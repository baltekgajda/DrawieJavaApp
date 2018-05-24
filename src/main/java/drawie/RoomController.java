package drawie;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RoomController {

    private View view;
    private Model model;

    @FXML
    private Canvas roomCanvas;

    @FXML
    private void drawOnCanvas()
    {
        GraphicsContext gc = roomCanvas.getGraphicsContext2D();
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        System.out.println("drawCanvas");

        try {
            roomCanvas.setOnMousePressed(event -> {
                System.out.println("Mouse click");
                gc.beginPath();
                gc.lineTo(event.getSceneX(), event.getSceneY());
                gc.stroke();
            });

            roomCanvas.setOnMouseDragged(event -> {
                System.out.println("Mouse dragged");
                gc.lineTo(event.getSceneX(), event.getSceneY());
                gc.stroke();
            });
        }catch (Exception e){
            System.out.println(e);
            System.exit(0);
        }
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
}
