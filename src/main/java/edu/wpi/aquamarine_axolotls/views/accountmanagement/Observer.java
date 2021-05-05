package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import javafx.scene.control.Label;

public abstract class Observer {
    protected Subject subject;
    protected Label label;
    public abstract void update();
    public Observer(Subject subject, Label label) {
        this.subject = subject;
        subject.attach(this);
        this.label = label;
    }

}
