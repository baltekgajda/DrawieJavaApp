package drawie;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * View class of the application
 */
public class View {

    private static Pane mainPane;
    private FXMLLoader loader;

    /**
     * Creating view object and displaying main menu
     * @param primaryStage stage of the application
     */
    public View(Stage primaryStage) {
        setDefaultStageSettings(primaryStage);
        this.loader = new FXMLLoader(this.getClass().getResource("/fxml/MainMenu.fxml"));
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainPane = pane;
        Scene scene = new Scene(pane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Setting stage settings
     * @param primaryStage stage of the application
     */
    private void setDefaultStageSettings(Stage primaryStage) {

        //TODO sprawdzic czy cos usunac
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/DrawieIcon.bmp")));
        primaryStage.setTitle("Drawie");
        primaryStage.centerOnScreen();
    }

    //TODO nie wiem czy nie usunac

    /**
     * Setting image on the imageView of the application
     * @param imageView imageView
     * @param image image to be set
     */
    public void setImageView(ImageView imageView, String image) {
        imageView.setImage(new Image(this.getClass().getResourceAsStream("/main/resources/images/" + image)));
    }

    /**
     * Setting loader of the application while changing screens
     * @param loader loader to be set
     */
    private void setLoader(FXMLLoader loader) {
        this.loader = loader;
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainPane.getChildren().clear();
        mainPane.getChildren().add(pane);
    }

    /**
     * Loading main menu view
     * @param model model of the application
     */
    public void loadMainMenu(Model model) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/MainMenu.fxml"));
        setLoader(loader);
        MenuController menuController = loader.getController();
        menuController.setModel(model);
        menuController.setView(this);
    }

    /**
     * Looading room of the application
     * @param model model of the application
     */
    public void loadRoom(Model model) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/Room.fxml"));
        setLoader(loader);
        RoomController roomController = loader.getController();
        roomController.setModel(model);
        roomController.setView(this);
        model.setRoomController(roomController);
    }

    /**
     * Returning loader
     * @return loader
     */
    public FXMLLoader getLoader() {
        return this.loader;
    }

    /**
     *
     * @param gc graphicsContext of the canvas
     * @param color color of the stroke
     * @param lineCap lineCap of the stroke
     * @param fillStyle fillStyle of the stroke
     * @param lineWidth width of the line of the stroke
     * @param stroke stroke points
     */
    public void drawStrokeOnCanvas(GraphicsContext gc, String color, String lineCap, String fillStyle, int lineWidth, int[] stroke) {
        gc.setStroke(Color.web(color));

        switch (lineCap) {
            case "round":
                gc.setLineCap(StrokeLineCap.ROUND);
                break;
            case "square":
                gc.setLineCap(StrokeLineCap.SQUARE);
                break;
            default:
                gc.setLineCap(StrokeLineCap.BUTT);
        }
        //TODO setFillStyle?

        gc.setLineWidth(lineWidth);

        //drawStroke
        gc.beginPath();
        for (int i = 0; i < stroke.length; i += 2) {
            gc.lineTo(stroke[i], stroke[i + 1]);
            gc.stroke();
        }
    }

    /**
     * Drawing image dump on canvas
     * @param gc graphicsContext of the canvas
     * @param dumpImg image of the dump
     * @param width width of the image
     * @param height height of the image
     */
    public void drawDumpBCOnCanvas(GraphicsContext gc, Image dumpImg, double width, double height) {
        gc.clearRect(0, 0, width, height);
        gc.drawImage(dumpImg, 0, 0);
    }

    /**
     * Drawing stroke made by user
     * @param gc graphicsContext of the canvas
     * @param x x coordinate where the event of drawing was executed
     * @param y y coordinate where the event of drawing was executed
     * @param lineWidth width of the line of the stroke
     */
    public void drawUserStroke(GraphicsContext gc, double x, double y, int lineWidth) {
        gc.setLineWidth(lineWidth);
        gc.lineTo(x, y);
        gc.stroke();
    }

    /**
     *
     * @param gc graphicsContext of the canvas
     * @param x x coordinate where the event of drawing was executed
     * @param y y coordinate where the event of drawing was executed
     * @param lineWidth width of the line of the stroke
     * @param color color of the stroke chosen by user
     */
    public void beginUserStroke(GraphicsContext gc, double x, double y, int lineWidth, Paint color) {
        gc.setStroke(color);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.beginPath();
        drawUserStroke(gc, x, y, lineWidth);
    }
}

