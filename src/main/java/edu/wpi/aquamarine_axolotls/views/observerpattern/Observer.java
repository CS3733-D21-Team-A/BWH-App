package edu.wpi.aquamarine_axolotls.views.observerpattern;

import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Predicate;

public class Observer {
    private Subject subject;
    private Label label;
    private Predicate<ArrayList<String>> syntaxChecker;
    Function<ArrayList<String> ,String> valueGetter;
    private String pass;
    private String fail;


    public Observer(Subject subject, Label label, Predicate<ArrayList<String>> syntaxChecker, String pass, String fail) {
        this.subject = subject;
        subject.attach(this);
        this.label = label;
        this.syntaxChecker = syntaxChecker;
        this.pass = pass;
        this.fail = fail;
    }

    public Observer(Subject subject, Label label, Predicate<ArrayList<String>> syntaxChecker, Function<ArrayList<String>,String> valueGetter, String fail) {
        this.subject = subject;
        subject.attach(this);
        this.label = label;
        this.syntaxChecker = syntaxChecker;
        this.valueGetter = valueGetter;
        this.fail = fail;
    }

    public void update(){
        if(valueGetter != null){
            if(syntaxChecker.test(subject.getItems())) label.setText(valueGetter.apply(subject.getItems()));
            else label.setText(fail);
        }
        else {
            if(syntaxChecker.test(subject.getItems())) label.setText(pass);
            else label.setText(fail);
        }

    }

}
