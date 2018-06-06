package drawie;

import io.socket.client.Url;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
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

/**
 * Controller for room view
 */
public class RoomController {

    private View view;
    private Model model;

    @FXML
    private Canvas roomCanvas;

    @FXML
    private Canvas serverCanvas;

    @FXML
    private Slider paintbrushWidthSlider;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private StackPane loadingPane;

    @FXML
    private ToggleButton bucketFill;

    @FXML
    private void changeWidth() {
        //GraphicsContext gc = roomCanvas.getGraphicsContext2D();
        //gc.setLineWidth(paintbrushWidthSlider.getValue());
    }

    /**
     * Function adding mouse listeners to roomCanvas allowing user to draw on canvas
     */
    @FXML
    private void drawOnCanvas() {
        GraphicsContext gc = roomCanvas.getGraphicsContext2D();
        try {
            roomCanvas.setOnMousePressed(event -> {
                model.manageOnMousePressed(bucketFill.isSelected(), (int) event.getX(), (int) event.getY(), colorPicker.getValue());
            });

            roomCanvas.setOnMouseDragged(event -> {
                model.manageOnMouseDragged(bucketFill.isSelected(), (int) event.getX(), (int) event.getY());
            });

            roomCanvas.setOnMouseReleased(event ->{
                model.manageOnMouseReleased(bucketFill.isSelected(), colorPicker.getValue(), "round", "solid", (int) paintbrushWidthSlider.getValue());
            });
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    /**
     * Passing to model information that undo button was clicked. Handler
     */
    @FXML
    private void undoButtonClicked() {
        model.handleUndoClick();
    }

    /**
     * Passing to model information that redo button was clicked. Handler
     */
    @FXML
    private void redoButtonClicked() {
        model.handleRedoClick();
    }

    /**
     * Handler for main menu button. Changing view to main menu
     */
    @FXML
    private void goToMainMenu() {
        loadingPane.setVisible(true);
        view.loadMainMenu(model);
    }

    /**
     * Handler of copy to clipboard button. Invoking copying in model
     */
    @FXML
    private void copyURLToClipboard() {
        model.copyURLToClipboard();
    }

    /**
     * Setting model of the application
     * @param model model to be set
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Setting view of the application
     * @param view
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Passing parameters of dump from model to view
     * @param dumpImg dump image to be drawn
     */
    public void drawDumpBCOnCanvas(Image dumpImg) {
        view.drawDumpBCOnCanvas(serverCanvas.getGraphicsContext2D(), dumpImg, serverCanvas.getWidth(), serverCanvas.getHeight() );
        loadingPane.setVisible(false);
    }

    /**
     * Passing parameters of stroke from model to view
     * @param color color of stroke
     * @param lineCap lineCap of stroke
     * @param fillStyle fillStyle of stroke
     * @param lineWidth lineWidth of stroke
     * @param stroke array of stroke points
     */
    public void drawStrokeBCOnCanvas(String color, String lineCap, String fillStyle, int lineWidth, int[] stroke){
        roomCanvas.getGraphicsContext2D().clearRect(0,0,roomCanvas.getWidth(),roomCanvas.getHeight());
        view.drawStrokeOnCanvas(serverCanvas.getGraphicsContext2D(), color, lineCap, fillStyle, lineWidth, stroke);
    }

    /**
     * Beginning users stroke
     * @param x x coordinate showing where the mouse was pressed
     * @param y y coordinate showing where the mouse was pressed
     */
    public void beginUserStroke(double x, double y) {
        view.beginUserStroke(roomCanvas.getGraphicsContext2D(), x, y,(int) paintbrushWidthSlider.getValue(),colorPicker.getValue());
    }

    /**
     * Drawing users stroke
     * @param x x coordinate showing where the mouse was dragged
     * @param y y coordinate showing where the mouse was dragged
     */
    public  void drawUserStroke(double x, double y){
        view.drawUserStroke(roomCanvas.getGraphicsContext2D(), x, y, (int) paintbrushWidthSlider.getValue());
    }
}
