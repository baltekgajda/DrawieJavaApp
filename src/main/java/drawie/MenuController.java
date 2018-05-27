package drawie;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class MenuController {

    private View view;
    private Model model;

    @FXML
    private TextField roomUrl;

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

    @FXML
    private void createNewRoom() {
        model.newRoom();
        view.loadRoom(model);
    }

    @FXML
    private void exitDrawie() {
        Platform.exit();
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public void setView(View view) {
        this.view = view;
    }
}
