package project.optics.jfkt.controllers;

import javafx.scene.Scene;
import project.optics.jfkt.utils.Util;
import project.optics.jfkt.views.CreateAccountView;

public class LoginController {
    private final Util util = new Util();
    public void onLinkClicked() {
        util.switchScene(new Scene(new CreateAccountView()));
    }
}
