package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import javafx.scene.control.Label;

public class PasswordObserver extends Observer {

    public PasswordObserver(Subject subject, Label label) {
        super(subject, label);
    }

    public void update() {
        if(subject.getConfirmPassword().equals(subject.getNewPassword())) label.setText("Passwords match");
        else label.setText("Passwords do not match");
    }
}
