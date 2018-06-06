package drawie;

import com.sun.jndi.toolkit.url.Uri;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.client.Url;
import io.socket.emitter.Emitter;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import java.awt.*;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.Vector;

public class Model {

    private Socket socket;
    private RoomController roomController;
    private String roomURL;

    private String hostURL = "https://drawie.herokuapp.com";

    private Vector<int[]> mStroke = new Vector<>();

    public boolean joinRoom(String text) {
        if (text.length() == 0)
            return false;

        try {
            socket = IO.socket(text);
        } catch (URISyntaxException e) {
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

    private void configureSocket() {
        socket.on("dumpBC", args -> {
            JSONObject obj = (JSONObject) args[0];
            manageReceivedDumpBC(obj);
        });

        socket.on("strokeBC", args -> {
            JSONObject obj = (JSONObject) args[0];
            manageReceiveStrokeBC(obj);
        });

    }

    public void handleRedoClick() {
        socket.emit("redo");
    }

    public void handleUndoClick() {
        socket.emit("undo");
    }

    public void sendStroke(Color color, String lineCap, String fillStyle, int lineWidth, Vector<int[]> mStroke) {
        JSONObject strokeObj = new JSONObject();
        JSONObject options = new JSONObject();
        JSONArray stroke = new JSONArray();
        try {
            for (int[] points : mStroke) {
                stroke.put(points);
            }

            options.put("strokeStyle", hexColorToHashFormat(color));
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
        joinRoom(hostURL + "?room=" + generateRandomUUID());
    }

    private String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public void setRoomController(RoomController rc) {
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

    private String hexColorToHashFormat(Color color) {
        return "#" + (color.toString()).substring(2, 8);
    }

    public void bucketFill(int x, int y, Color color) {
        JSONObject floodFillObj = new JSONObject();
        try {
            floodFillObj.put("x", x);
            floodFillObj.put("y", y);
            floodFillObj.put("color", hexColorToHashFormat(color));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("floodFill", floodFillObj);
    }


    public void manageReceivedDumpBC(JSONObject obj) {
        String[] imgInB64;
        try {
            imgInB64 = obj.getString("snapshot").split(",");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        ByteArrayInputStream inputStream;
        try {
            inputStream = new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(imgInB64[imgInB64.length - 1]));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        roomController.drawDumpBCOnCanvas(new javafx.scene.image.Image(inputStream));
    }

    public void manageReceiveStrokeBC(JSONObject obj) {
        String color, lineCap, fillStyle;
        int lineWidth;
        JSONArray jsonStroke;

        try {
            JSONObject opt = obj.getJSONObject("options");
            color = opt.getString("strokeStyle");
            lineCap = opt.getString("lineCap");
            fillStyle = opt.getString("fillStyle");
            lineWidth = opt.getInt("lineWidth");
            jsonStroke = obj.getJSONArray("stroke");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        int[] stroke = new int[jsonStroke.length() * 2];
        for (int i = 0; i < jsonStroke.length(); i++) {
            try {
                stroke[i * 2] = jsonStroke.getJSONArray(i).getInt(0);
                stroke[i * 2 + 1] = jsonStroke.getJSONArray(i).getInt(1);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        roomController.drawStrokeBCOnCanvas(color, lineCap, fillStyle, lineWidth, stroke);
    }

    public void manageOnMousePressed(boolean fillSelected, int x, int y, Color color) {
        if (fillSelected) {
            bucketFill(x, y, color);
            return;
        }

        mStroke = new Vector<>();
        mStroke.add(new int[]{x, y});
        roomController.beginUserStroke(x, y);
    }

    public void manageOnMouseDragged(boolean fillSelected, int x, int y) {
        if (fillSelected) return;
        mStroke.add(new int[]{x, y});
        roomController.drawUserStroke(x, y);
    }

    public void manageOnMouseReleased(boolean fillSelected, Color color, String lineCap, String fillStyle, int lineWidth) {
        if (fillSelected) return;
        sendStroke(color, lineCap, fillStyle, lineWidth, mStroke);
    }
}
