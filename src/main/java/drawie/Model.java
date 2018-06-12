package drawie;

import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.scene.paint.Color;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayInputStream;
import java.net.URISyntaxException;
import java.util.Base64;
import java.util.UUID;
import java.util.Vector;

/**
 * Model class of the application containing web communication and application logic
 */
public class Model {

    private Socket socket;
    private RoomController roomController;
    private String roomURL;

    private String hostURL = "https://drawie.herokuapp.com";

    private Vector<int[]> mStroke = new Vector<>();

    /**
     * Joining the room of Drawie App with given URL
     *
     * @param text room URL
     * @return true if entered the room, false otherwise
     */
    public boolean joinRoom(String text) {
        if (text.length() == 0)
            return false;

        try {
            socket = IO.socket(text);
        } catch (URISyntaxException e) {
            // e.printStackTrace();
            return false;
        } catch (RuntimeException e) {
            return false;
        }
        configureSocket();
        socket.connect();
        setRoomURL(text);
        return true;
    }

    /**
     * Configuring communication socket for reacting to events.
     */
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

    /**
     * Emmiting redo event
     */
    public void handleRedoClick() {
        socket.emit("redo");
    }

    /**
     * Emmiting undo event
     */
    public void handleUndoClick() {
        socket.emit("undo");
    }

    /**
     * Sending stroke to the server
     *
     * @param color     color of stroke
     * @param lineCap   lineCap of stroke
     * @param fillStyle fillStyle of stroke
     * @param lineWidth width of the stroke
     * @param mStroke   array of stroke points
     */
    private void sendStroke(Color color, String lineCap, String fillStyle, int lineWidth, Vector<int[]> mStroke) {
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

    /**
     * Creating new room on the server and joining it
     */
    public void newRoom() {
        joinRoom(hostURL + "?room=" + generateRandomUUID());
    }

    /**
     * Generating random user id for room id
     *
     * @return random user id
     */
    private String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Setting room controller
     *
     * @param rc room controller to be set
     */
    public void setRoomController(RoomController rc) {
        roomController = rc;
    }

    /**
     * Setting rooomURL
     *
     * @param roomURL roomURL to be set
     */
    private void setRoomURL(String roomURL) {
        this.roomURL = roomURL;
    }

    /**
     * Copying roomURL to the clipboard
     */
    public void copyURLToClipboard() {
        StringSelection selection = new StringSelection(this.roomURL);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    /**
     * Changing color from Color class to hex format
     *
     * @param color color in Color class format
     * @return color in hex format
     */
    private String hexColorToHashFormat(Color color) {
        String colorToReturn = "#" + (color.toString()).substring(2, 8);
        if (colorToReturn.equals("#000000")) return "#050505";
        return colorToReturn;
    }

    /**
     * Sending floodFill event to the server
     *
     * @param x     where the mouse was pressed x
     * @param y     where the mouse was pressed y
     * @param color color that is selected by the user
     */
    private void bucketFill(int x, int y, Color color) {
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


    /**
     * Mangaing dump received by socket
     *
     * @param obj JSONObject received by socket
     */
    public void manageReceivedDumpBC(JSONObject obj) {
        String[] imgInB64;
        try {
            imgInB64 = obj.getString("snapshot").split(",");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(imgInB64[imgInB64.length - 1]));
        roomController.drawDumpBCOnCanvas(new javafx.scene.image.Image(inputStream));
    }

    /**
     * Mangaging stroke received by socket
     *
     * @param obj JSONObject received by socket
     */
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

    /**
     * Managing logic when mouse was pressed on canvas
     *
     * @param fillSelected is the flood fill selected?
     * @param x            x coordinate where the mouse was pressed
     * @param y            coordinate where the mouse was pressed
     * @param color        color selected by user
     */
    public void manageOnMousePressed(boolean fillSelected, int x, int y, Color color) {
        if (fillSelected) {
            bucketFill(x, y, color);
            return;
        }

        mStroke = new Vector<>();
        mStroke.add(new int[]{x, y});
        roomController.beginUserStroke(x, y);
    }

    /**
     * Managing logic when mouse was dragged on canvas
     *
     * @param fillSelected is the flood fill selected?
     * @param x            x coordinate where the mouse was pressed
     * @param y            coordinate where the mouse was pressed
     */
    public void manageOnMouseDragged(boolean fillSelected, int x, int y) {
        if (fillSelected) return;
        mStroke.add(new int[]{x, y});
        roomController.drawUserStroke(x, y);
    }

    /**
     * Managing logic when mouse was released on canvas
     *
     * @param fillSelected is the flood fill selected?
     * @param color        color selected by user
     * @param lineCap      lineCap of the stroke
     * @param fillStyle    fillStyle of the stroke
     * @param lineWidth    width of the stroke set by user
     */
    public void manageOnMouseReleased(boolean fillSelected, Color color, String lineCap, String fillStyle, int lineWidth) {
        if (fillSelected) return;
        sendStroke(color, lineCap, fillStyle, lineWidth, mStroke);
    }

    public void closeSocket() {
        socket.close();
    }
}
