package drawie;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class for starting Drawie Application
 */
public class Start extends Application {
    /**
     * Launching application
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Overriden method from Application for starting the javafx app
     * @param primaryStage view to display at the beginning
     */
    @Override
    public void start(Stage primaryStage) {
        View view = new View(primaryStage);
        Model model = new Model();
        MenuController menuController = view.getLoader().getController();
        menuController.setView(view);
        menuController.setModel(model);
    }
}
