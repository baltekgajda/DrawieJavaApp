package drawie;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class View {

    private static Pane mainPane;
    private FXMLLoader loader;

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

    private void setDefaultStageSettings(Stage primaryStage) {

        //TODO sprawdzic czy cos usunac
        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/DrawieIcon.bmp")));
        primaryStage.setTitle("Drawie");
        primaryStage.centerOnScreen();
    }

    //TODO nie wiem czy nie usunac
    public void setImageView(ImageView imageView, String image) {
        imageView.setImage(new Image(this.getClass().getResourceAsStream("/main/resources/images/" + image)));
    }

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

    public void loadMainMenu(Model model) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/MainMenu.fxml"));
        setLoader(loader);
        MenuController menuController = loader.getController();
        menuController.setModel(model);
        menuController.setView(this);
    }

    public void loadRoom(Model model) {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/Room.fxml"));
        setLoader(loader);
        RoomController roomController = loader.getController();
        roomController.setModel(model);
        roomController.setView(this);
        model.setRoomController(roomController);
    }

    public FXMLLoader getLoader() {
        return this.loader;
    }

}

