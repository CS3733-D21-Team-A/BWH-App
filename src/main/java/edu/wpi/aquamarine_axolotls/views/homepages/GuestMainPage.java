package edu.wpi.aquamarine_axolotls.views.homepages;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.sql.SQLException;


public class GuestMainPage extends GenericPage {

    @FXML
    StackPane stackPane;

    public DatabaseController db;

    @FXML Text userNameText;

    @FXML
    public void initialize() throws SQLException, IOException{
        db = DatabaseController.getInstance();
    }
    @FXML
    public void signInP(ActionEvent actionEvent) {
        sceneSwitch("LogIn");
    }

    @FXML
    public void mapP(ActionEvent actionEvent) {
        try {
            if ( db.hasUserTakenCovidSurvey ( Aapp.username ) ) {
                sceneSwitch ( "Navigation" );
            }
            else {
                popUp ( "Covid Survey" ,"\n\n\n\n\nTaking the Covid-19 Survey is necessary before completing this action" );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace ( );
        }
    }

    @FXML
    public void stop(javafx.event.ActionEvent event){
        JFXDialogLayout content = new JFXDialogLayout();
        JFXDialog help = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.BOTTOM);
        content.setHeading(new Text("Shut Down Application"));
        content.setBody(new Text("Clicking the Shut Down button will close the application."));
        JFXButton shutDownB = new JFXButton("Shut Down");
        JFXButton cancel_button = new JFXButton( "Cancel");
        shutDownB.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                help.close();
                Platform.exit();
            }
        });
        cancel_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                help.close();
            }
        });
        content.setActions(cancel_button, shutDownB);
        help.show();
    }

    @FXML
    public void covidSurveyPage(ActionEvent actionEvent) {
        sceneSwitch("CovidSurvey");
    }



}