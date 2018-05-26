package drawie;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Model {

    private Socket socket;
    private RoomController roomController;

    public void joinRoom(String text) {
        try {
            socket = IO.socket(text);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        configureSocket();
        socket.connect();
    }

    private void configureSocket(){
        socket.on("dumpBC", args -> {
            JSONObject obj = (JSONObject) args[0];
            System.out.println(obj.toString());
            String[] imgInB64 =null;
            try {
                 imgInB64 = obj.getString("snapshot").split(",");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            roomController.drawDumpBCOnCanvas(imgInB64[imgInB64.length-1]);
        });
    }

    public void handleRedoClick() {
        socket.emit("redo");
    }

    public void handleUndoClick() {
        socket.emit("undo");
    }

    public void newRoom() { }

    public void setRoomController(RoomController rc){
        roomController = rc;
    }
}
