package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;


public class Node_Editing {
    @FXML
    public JFXButton deleteButton;

    @FXML
    private JFXButton addButton;

    @FXML
    private JFXButton editButton;

    @FXML
    private ComboBox nodeDropdown;

    @FXML
    private Label label;

    @FXML
    private Label submissionlabel;

    @FXML
    private JFXTextField longName;

    @FXML
    private JFXTextField shortName;

    @FXML
    private JFXTextField xCoor;

    @FXML
    private JFXTextField yCoor;

    @FXML
    private JFXButton findPathButton2;

    ObservableList<String> options = FXCollections.observableArrayList(
            "Parking Spot 1 45 Francis Street Lobby",
            "Parking Spot 45 Francis Street Lobby",
            "Parking Spot 3 45 Francis Street Lobby",
            "Parking Spot 4 45 Francis Street Lobby",
            "Parking Spot 5 45 Francis Street Lobby",
            "Parking Spot 6 45 Francis Street Lobby",
            "Parking Spot 7 45 Francis Street Lobby",
            "Parking Spot 8 45 Francis Street Lobby",
            "Parking Spot 9 45 Francis Street Lobby",
            "Parking Spot 10 45 Francis Street Lobby",
            "Parking Spot 11 45 Francis Street Lobby",
            "Parking Spot 12 45 Francis Street Lobby",
            "Parking Spot 13 45 Francis Street Lobby",
            "Parking Spot 14 45 Francis Street Lobby",
            "Parking Spot 15 45 Francis Street Lobby",
            "Parking Spot 1 80 Francis Parking",
            "Parking Spot 2 80 Francis Parking",
            "Parking Spot 3 80 Francis Parking",
            "Parking Spot 4 80 Francis Parking",
            "Parking Spot 5 80 Francis Parking",
            "Parking Spot 6 80 Francis Parking",
            "Parking Spot 7 80 Francis Parking",
            "Parking Spot 8 80 Francis Parking",
            "Parking Spot 9 80 Francis Parking",
            "Parking Spot 10 80 Francis Parking",
            "Entrance 75 Francis St",
            "Entrance ER 75 Francis Street");

    @FXML
    public void initialize(){
        nodeDropdown.setItems(options);
    }

public void clearfields(){
    longName.clear();
    shortName.clear();
    xCoor.clear();
    yCoor.clear();
}

    @FXML
    public void pressAddButton(){
        clearfields();
        nodeDropdown.setVisible(false);
        longName.setVisible(true);
        shortName.setVisible(true);
        xCoor.setVisible(true);
        yCoor.setVisible(true);
        label.setText("Add");


    }

    @FXML
    public  void PressdeleteButton(){
        clearfields();
        nodeDropdown.setVisible(true);
        longName.setVisible(false);
        shortName.setVisible(false);
        xCoor.setVisible(false);
        yCoor.setVisible(false);
        label.setText("Delete");

    }


    @FXML
    public void pressEditButton(){
        clearfields();
        nodeDropdown.setVisible(true);
        longName.setVisible(true);
        shortName.setVisible(true);
        xCoor.setVisible(true);
        yCoor.setVisible(true);
        label.setText("Edit");

    }

    @FXML
    public void submitfunction(){
        if(label.getText() == "Edit"){
            submissionlabel.setText("You have edited "+ nodeDropdown.getSelectionModel().getSelectedItem().toString());
        }

        else if(label.getText() == "Delete"){
            submissionlabel.setText("You have deleted "+ nodeDropdown.getSelectionModel().getSelectedItem().toString());
        }
        else if(label.getText() == "Add"){
            submissionlabel.setText("You have added "+ longName.getText());
        }
        else{
            submissionlabel.setText(  "You did not make a proper submission");
        }
        label.setText("Add Another Node?");
        clearfields();

        return;
    }

}
