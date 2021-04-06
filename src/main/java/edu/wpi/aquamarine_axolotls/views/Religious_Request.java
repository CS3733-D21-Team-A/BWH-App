package edu.wpi.aquamarine_axolotls.views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class Religious_Request extends Service_Request{
    ObservableList<String> terminalList = FXCollections
            .observableArrayList("Yes", "No");

    @FXML
    private ComboBox terminal_illness;

    public void initialize(){
        terminal_illness.setItems(terminalList);
    }

    }
