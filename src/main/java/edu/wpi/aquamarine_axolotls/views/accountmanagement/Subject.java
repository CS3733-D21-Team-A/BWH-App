package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private String newPassword = "";
    private String confirmPassword = "";
    private List<Observer> observers = new ArrayList<>();

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setNewPassword(String newPassword){
        this.newPassword = newPassword;
        notifyAllObservers();
    }

    public void setConfirmPassword(String confirmPassword){
        this.confirmPassword = confirmPassword;
        notifyAllObservers();
    }

    private void notifyAllObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public void attach(Observer newObserver) {
        this.observers.add(newObserver);
    }

}
