package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.LoginView;

public class CreateAccountController {
    private final Util util = new Util();

    public void onBackButtonPressed() {
        util.switchScene(new Scene(new LoginView()));
    }
}
