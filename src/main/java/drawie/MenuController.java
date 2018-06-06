package drawie;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * Controller for main menu view
 */
public class MenuController {

    private View view;
    private Model model;

    @FXML
    private TextField roomUrl;

    /**
     * Handler for joining existing room when the button is pressed
     */
    @FXML
    private void joinExistingRoom() {
        if(model.joinRoom(roomUrl.getText()))
            view.loadRoom(model);
        else
        {
            roomUrl.clear();
            roomUrl.setPromptText("wrong URL");
        }
    }

    /**
     * Handler for creating new room when the button is pressed
     */
    @FXML
    private void createNewRoom() {
        model.newRoom();
        view.loadRoom(model);
    }

    /**
     * Handler for exiting application
     */
    @FXML
    private void exitDrawie() {
        Platform.exit();
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
     * @param view view to be set
     */
    public void setView(View view) {
        this.view = view;
    }
}
