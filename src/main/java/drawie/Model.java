package drawie;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class Model {
    private Socket socket;
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
        });
    }

    private void handleRedoClick() {
        socket.emit("redo");
    }

    private void handleUndoClick() {
        socket.emit("undo");
    }

    public void newRoom() {

    }
}
