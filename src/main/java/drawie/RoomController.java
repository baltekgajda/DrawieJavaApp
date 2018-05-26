package drawie;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RoomController {

    private View view;
    private Model model;

    private JSONArray mStroke;

    @FXML
    private Canvas roomCanvas;

    @FXML
    private Slider paintbrushWidthSlider;

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
                mStroke = new JSONArray();
//                mStroke.put(new JSONArray().put((int) event.getX()).put((int) event.getX())); TODO dodawanie tego JSONA
                gc.beginPath();
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();
            });

            roomCanvas.setOnMouseDragged(event -> {
                /*JSONArray points = new JSONArray(); //TODO WRZUCANIE DO TABLICY
                points.put((int) event.getX());
                points.put((int) event.getX());
                mStroke.put(points);*/
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();
            });

            roomCanvas.setOnMouseReleased(event ->{
                model.sendStroke("#00ff00", "round", "solid", (int) gc.getLineWidth(), mStroke);
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
        gc.setStroke(new Color(
                (float)Integer.valueOf( color.substring( 1, 3 ), 16 )/256f,
                (float)Integer.valueOf( color.substring( 3, 5 ), 16 )/256f,
                (float)Integer.valueOf( color.substring( 5, 7 ), 16 )/256f, 1));


        StrokeLineCap slc;
        switch (lineCap){
            case "round":
                slc = StrokeLineCap.ROUND;
                break;
            case "square":
                slc = StrokeLineCap.SQUARE;
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
