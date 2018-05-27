package drawie;

import io.socket.client.Url;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

public class RoomController {

    private View view;
    private Model model;

    private Vector<int[]> mStroke = new Vector<>();

    @FXML
    private Canvas roomCanvas;

    @FXML
    private Slider paintbrushWidthSlider;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private void changeWidth() {
        GraphicsContext gc = roomCanvas.getGraphicsContext2D();
        gc.setLineWidth(paintbrushWidthSlider.getValue());
    }

    @FXML
    private void drawOnCanvas() {
        GraphicsContext gc = roomCanvas.getGraphicsContext2D();
        try {
            roomCanvas.setOnMousePressed(event -> {
                mStroke = new Vector<>();
                mStroke.add(new int[]{(int) event.getX(), (int) event.getY()});
                gc.setStroke(colorPicker.getValue());
                gc.beginPath();
                gc.lineTo(event.getX(), event.getY());
                gc.setLineCap(StrokeLineCap.ROUND);
                gc.stroke();
            });

            roomCanvas.setOnMouseDragged(event -> {
                mStroke.add(new int[]{(int) event.getX(), (int) event.getY()});
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();
            });

            roomCanvas.setOnMouseReleased(event ->{
                //TODO poprawne ustawianie koloru itp.
                model.sendStroke(model.hexColorToHashFormat(colorPicker.getValue()), "round", "solid", (int) gc.getLineWidth(), mStroke);
            });
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    @FXML
    private void undoButtonClicked() {
        model.handleUndoClick();
    }

    @FXML
    private void redoButtonClicked() {
        model.handleRedoClick();
    }

    @FXML
    private void goToMainMenu() {
        view.loadMainMenu(model);
    }

    @FXML
    private void copyURLToClipboard() {
        model.copyURLToClipboard();
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setView(View view) {
        this.view = view;
    }

    public void drawDumpBCOnCanvas(String dumpInBase64) {
        BASE64Decoder base64Decoder = new BASE64Decoder();
        ByteArrayInputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(base64Decoder.decodeBuffer(dumpInBase64));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Image dumpImg = new Image(inputStream);
        GraphicsContext gc = roomCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, roomCanvas.getWidth(), roomCanvas.getHeight());
        gc.drawImage(dumpImg, 0, 0);
    }

    public void drawStrokeBCOnCanvas(String color, String lineCap, String fillStyle, int lineWidth, JSONArray stroke){
        GraphicsContext gc = roomCanvas.getGraphicsContext2D();
        gc.setStroke(Color.web(color));

        StrokeLineCap slc;
        switch (lineCap){
            case "round":
                slc = StrokeLineCap.ROUND;
                break;
            case "square":
                slc = StrokeLineCap.SQUARE;
                break;
            default:
                slc = StrokeLineCap.BUTT;
        }
        gc.setLineCap(slc);

        //TODO setFillStyle?

        gc.setLineWidth(lineWidth);
        //drawStroke
        gc.beginPath();
        for (int i=0; i<stroke.length(); i++){
            JSONArray points;
            int x=0,y=0;
            try {
                points = stroke.getJSONArray(i);
                x = points.getInt(0);
                y = points.getInt(1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            gc.lineTo(x, y);
            gc.stroke();
        }
    }
}
