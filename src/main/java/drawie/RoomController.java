package main.java.drawie;

import javafx.fxml.FXML;

public class RoomController {

    private View view;
    private Model model;

    public void setModel(Model model) {
        this.model = model;
    }

    public void setView(View view) {
        this.view = view;
    }

    @FXML
    private void goToMainMenu() {
        System.out.println("Going to main menu");
        view.loadMainMenu(model);
    }
}
