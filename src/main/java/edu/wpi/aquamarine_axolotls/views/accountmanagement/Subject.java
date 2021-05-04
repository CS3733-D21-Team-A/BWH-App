package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String newPassword;
    private String confirmPassword;
    private List<Observer> observers = new ArrayList<>();

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setState(String newPassword, String confirmPassword){
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public void attach(Observer newObserver) {
        this.observers.add(newObserver);
    }

}
