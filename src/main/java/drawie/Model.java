package drawie;

import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Vector;

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

    public void sendStroke(String color, String lineCap, String fillStyle, int lineWidth, Vector<int[]> mStroke) {
        JSONObject strokeObj = new JSONObject();
        JSONObject options = new JSONObject();
        JSONArray stroke = new JSONArray();
        try {
            for(int[] points : mStroke){
                stroke.put(points);
            }

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
