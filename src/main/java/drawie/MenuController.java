package drawie;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MenuController {

    private View view;
    private Model model;

    @FXML
    private TextField roomUrl;

    @FXML
    private void joinExistingRoom() {
        model.joinRoom(roomUrl.getText());
        System.out.println("joinExistingRoom" + roomUrl.getText());
        view.loadRoom(model);
    }

    @FXML
    private void createNewRoom() {
        model.newRoom();
        System.out.println("createNewRoom");
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
