package drawie;

import com.sun.jndi.toolkit.url.Uri;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class Model {

    private Socket socket;
    private RoomController roomController;

    public boolean joinRoom(String text) {
        try {
            socket = IO.socket(text);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (RuntimeException e) {
            return false;
        }
        configureSocket();
        socket.connect();
        return true;
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

        socket.on("strokeBC", args -> {
            JSONObject obj = (JSONObject) args[0];
            System.out.println(obj.toString());
            try {
                JSONObject opt = obj.getJSONObject("options");
                roomController.drawStrokeBCOnCanvas(opt.getString("strokeStyle"), opt.getString("lineCap"), opt.getString("fillStyle"), opt.getInt("lineWidth"), obj.getJSONArray("stroke"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public void handleRedoClick() {
        socket.emit("redo");
    }

    public void handleUndoClick() {
        socket.emit("undo");
    }

    public void sendStroke(String color, String lineCap, String fillStyle, int lineWidth, JSONArray stroke) {
        JSONObject strokeObj = new JSONObject();
        JSONObject options = new JSONObject();
        try {
            options.put("strokeStyle", color);
            options.put("lineCap", lineCap);
            options.put("fillStyle", fillStyle);
            options.put("lineWidth", lineWidth);
            strokeObj.put("options", options);
            strokeObj.put("stroke", stroke);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("stroke", strokeObj);
    }

    public void newRoom() { }

    public void setRoomController(RoomController rc){
        roomController = rc;
    }
}
