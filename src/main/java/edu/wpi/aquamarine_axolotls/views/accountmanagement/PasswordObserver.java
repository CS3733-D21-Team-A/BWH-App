package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import javafx.scene.control.Label;

public class PasswordObserver extends Observer{

    @Override
    public void update() {
        if(subject.getConfirmPassword().equals(subject.getNewPassword())) label.setText("The Passwords Match!");
        else label.setText("The Passwords Do Not Match");
    }

    public PasswordObserver(Subject subject, Label label){
        super(subject, label);
    }
}
