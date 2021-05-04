package edu.wpi.aquamarine_axolotls.views.accountmanagement;

import com.jfoenix.controls.JFXTextField;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Account extends GenericPage {
    @FXML HBox saveHbox;
    @FXML HBox editHbox;

    @FXML JFXTextField firstName;
    @FXML JFXTextField lastName;
    @FXML JFXTextField email;
    @FXML JFXTextField pronouns;
    @FXML DatePicker dob;

    private void initialize(){
        editHbox.setVisible(true);
        saveHbox.setVisible(false);
        editHbox.toFront();

//        firstName.setText();      // TODO: connect with DB user account
//        lastName.setText();
//        email.setText();
//        pronouns.setText();
//        dob.setValue();

    }

    public void editAccount() {
        editHbox.setVisible(false);
        saveHbox.setVisible(true);
        saveHbox.toFront();

        firstName.setEditable(true);
        lastName.setEditable(true);
        email.setEditable(true);
        pronouns.setEditable(true);
        dob.setEditable(true);
    }

    public void saveEdits(ActionEvent actionEvent) {
        editHbox.setVisible(true);
        saveHbox.setVisible(false);
        editHbox.toFront();

        firstName.setEditable(false);
        lastName.setEditable(false);
        email.setEditable(false);
        pronouns.setEditable(false);
        dob.setEditable(false);


    }
}
