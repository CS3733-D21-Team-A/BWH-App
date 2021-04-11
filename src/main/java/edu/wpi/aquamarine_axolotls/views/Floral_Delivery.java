package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;

import javafx.fxml.FXMLLoader;
import java.util.Optional;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;

import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import edu.wpi.aquamarine_axolotls.Aapp;


public class Floral_Delivery extends Service_Request{

    @FXML
    private Button back_button;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField deliveryTime;

    @FXML
    private TextField roomNumber;

    @FXML
    private TextField persMessage;

    @FXML
    private AnchorPane myAnchorPane;

    @FXML
    private Button submitButton;

    @FXML
    private Button helpButton;

    @FXML
    private StackPane stackPane;




    @FXML
    public void handleButtonAction(javafx.event.ActionEvent actionEvent) {

        Stage stage = (Stage) myAnchorPane.getScene().getWindow();

        Alert.AlertType type = Alert.AlertType.CONFIRMATION;

        Alert alert = new Alert(type, "");

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);
        alert.getDialogPane().setContentText("Click OK to confirm submission");

        alert.getDialogPane().setHeaderText("Are you sure you would like to submit?");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK)
        {
            try {
                Object root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Default_Service_Page.fxml"));
                Aapp.getPrimaryStage().getScene().setRoot((Parent) root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }        }

    }

    @FXML
    public void return_home(javafx.event.ActionEvent actionEvent) {

        Stage stage = (Stage) myAnchorPane.getScene().getWindow();

        Alert.AlertType type = Alert.AlertType.CONFIRMATION;

        JFXAlert<String> alert = new JFXAlert<>((Stage) back_button.getScene().getWindow());

        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(stage);
        alert.getDialogPane().setContentText("Your changes will not be saved.");

        alert.getDialogPane().setHeaderText("Are you sure you would like to return to the previous screen?");

        Optional result = alert.showAndWait();
        if(result.get() == ButtonType.OK)
        {
            try {
                Object root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Default_Service_Page.fxml"));
                Aapp.getPrimaryStage().getScene().setRoot((Parent) root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }        }

    }
    @FXML
    public void loadHelp(javafx.event.ActionEvent event){
        JFXDialogLayout content = new JFXDialogLayout();

    JFXDialog help = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
    content.setHeading(new Text("Help Page"));
    content.setBody(new Text("here is information that the help page would show"));

        JFXButton exit_button = new JFXButton("Close");
        JFXButton other = new JFXButton("other option");
        exit_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                help.close();
            }
        });

    content.setActions(exit_button,other);
    help.show();
}



}


