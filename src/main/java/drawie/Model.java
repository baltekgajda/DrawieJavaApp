package drawie;

import com.sun.jndi.toolkit.url.Uri;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.Url;
import io.socket.emitter.Emitter;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.Vector;

public class Model {

    private Socket socket;
    private RoomController roomController;
    private String roomURL;

    private String hostURL = "https://drawie.herokuapp.com";

    public boolean joinRoom(String text) {
        if(text.length()==0)
            return false;

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
        setRoomURL(text);
        return true;
    }

    private void configureSocket(){
        socket.on("dumpBC", args -> {
            JSONObject obj = (JSONObject) args[0];
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

    public void newRoom() {
        joinRoom(hostURL+"?room="+generateRandomUUID());
    }

    private String generateRandomUUID(){
        return UUID.randomUUID().toString();
    }

    public void setRoomController(RoomController rc){
        roomController = rc;
    }

    public void setRoomURL(String roomURL) {
        this.roomURL = roomURL;
    }

    public void copyURLToClipboard() {
        StringSelection selection = new StringSelection(this.roomURL);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    public String hexColorToHashFormat(Color color)
    {
        return "#"+(color.toString()).substring(2);
    }
}
