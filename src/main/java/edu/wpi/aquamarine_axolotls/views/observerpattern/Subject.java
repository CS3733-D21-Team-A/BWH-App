package edu.wpi.aquamarine_axolotls.views.observerpattern;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Subject {
    private ArrayList<String> watchList = new ArrayList<>();
    private List<Observer> observers = new ArrayList<>();
    private int size;


    public Subject(int size){
        this.size = size;
    }

    public String getItem(int index){
        return watchList.get(index);
    }

    public void setItem(int index, String value){
        if(watchList.size() > index) watchList.set(index, value);
        else watchList.add(value);
        notifyAllObservers();
    }

    public ArrayList<String> getItems(){
        return watchList;
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
