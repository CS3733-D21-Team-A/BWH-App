package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;





public class Food_Delivery extends Service_Request {

    ObservableList<String> foodOptionList = FXCollections
            .observableArrayList("Mac and Cheese", "Salad", "Pizza");

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField deliveryTime;

    @FXML
    private TextField roomNumber;

    @FXML
    private ComboBox foodOptions;

    @FXML
    private Button submitButton;

    @FXML
    private AnchorPane myAnchorPane;

    @FXML
    private StackPane stackPane;

    @FXML
    public void initialize() {
        foodOptions.setItems(foodOptionList);
    }

    @FXML
    public void handleButtonAction(javafx.event.ActionEvent actionEvent) throws IOException {
        JFXDialogLayout content = new JFXDialogLayout();

        JFXDialog help = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
        content.setHeading(new Text("Submission Success!"));
        content.setBody(new Text("Your information has successfully been submitted."));

        JFXButton exit_button = new JFXButton("Close");
        exit_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                help.close();
                try {
                    Object root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Default_Service_Page.fxml"));
                    Aapp.getPrimaryStage().getScene().setRoot((Parent) root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        content.setActions(exit_button);
        help.show();
    }

    @FXML
    public void return_home(javafx.event.ActionEvent actionEvent) {
        try {
            Object root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/Default_Service_Page.fxml"));
            Aapp.getPrimaryStage().getScene().setRoot((Parent) root);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void loadHelp(javafx.event.ActionEvent event) {
        JFXDialogLayout content = new JFXDialogLayout();

        JFXDialog help = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
        content.setHeading(new Text("Help Page"));
        content.setBody(new Text("Help Page Information:"));

        JFXButton exit_button = new JFXButton("Close");
        exit_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                help.close();
            }
        });

        content.setActions(exit_button);
        help.show();

    }
}
