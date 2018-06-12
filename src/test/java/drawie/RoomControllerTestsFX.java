package drawie;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.testfx.framework.junit5.ApplicationTest;

public class RoomControllerTestsFX extends ApplicationTest {

    View view;
    Model model;
    MenuController menuController;
    RoomController roomController;
    private String roomUrlTest = "https://drawie.herokuapp.com/?room=3332b08b-a9f5-4c40-32s21c-9ea26a3c5ef1";

    @Override
    public void start(Stage primaryStage) {
        view = Mockito.spy(new View(primaryStage));
        model = new Model();
        MenuController menuController = view.getLoader().getController();
        menuController.setView(view);
        menuController.setModel(model);
        model.joinRoom(roomUrlTest);
        view.loadRoom(model);
    }

    @Test
    public void drawUserStrokeExecutesDrawUserStrokeOnView() {
        roomController = (RoomController) Whitebox.getInternalState(model, "roomController");
        roomController.drawUserStroke(0, 0);
        GraphicsContext gc = ((Canvas) Whitebox.getInternalState(roomController, "roomCanvas")).getGraphicsContext2D();
        Mockito.verify(view, Mockito.atLeastOnce()).drawUserStroke(gc, 0, 0, 1);
    }

    @Test
    public void beginUserStrokeExecutesDrawUserStrokeOnView() {
        roomController = (RoomController) Whitebox.getInternalState(model, "roomController");
        roomController.beginUserStroke(0, 0);
        GraphicsContext gc = ((Canvas) Whitebox.getInternalState(roomController, "roomCanvas")).getGraphicsContext2D();
        Color color = ((ColorPicker) Whitebox.getInternalState(roomController, "colorPicker")).getValue();
        Mockito.verify(view, Mockito.atLeastOnce()).beginUserStroke(gc, 0, 0, 1, color);
    }

    @Test
    public void drawDumpOnCanvasExecutesDrawDumpOnView() {
        roomController = (RoomController) Whitebox.getInternalState(model, "roomController");
        Image img = null;
        Canvas serverCanvas = ((Canvas) Whitebox.getInternalState(roomController, "serverCanvas"));
        roomController.drawDumpBCOnCanvas(img);
        Mockito.verify(view, Mockito.atLeastOnce()).drawDumpBCOnCanvas(serverCanvas.getGraphicsContext2D(), img, serverCanvas.getWidth(), serverCanvas.getHeight());
    }

    @Test
    public void drawStrokeOnCanvasExecutesDrawDumpOnView() {
        roomController = (RoomController) Whitebox.getInternalState(model, "roomController");
        Canvas serverCanvas = ((Canvas) Whitebox.getInternalState(roomController, "serverCanvas"));
        Color color = new Color(0, 0, 0, 0);
        int[] stroke = {1, 2};
        roomController.drawStrokeBCOnCanvas(color.toString(), "", "", 1, stroke);
        Mockito.verify(view, Mockito.atLeastOnce()).drawStrokeOnCanvas(serverCanvas.getGraphicsContext2D(), color.toString(), "", "", 1, stroke);
    }
}
