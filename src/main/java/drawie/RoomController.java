package drawie;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import sun.misc.BASE64Decoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class RoomController {

    private View view;
    private Model model;

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
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);

        try {
            roomCanvas.setOnMousePressed(event -> {
                gc.beginPath();
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();
            });

            roomCanvas.setOnMouseDragged(event -> {
                gc.lineTo(event.getX(), event.getY());
                gc.stroke();
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
        System.out.println("Image drawn");
    }
}
