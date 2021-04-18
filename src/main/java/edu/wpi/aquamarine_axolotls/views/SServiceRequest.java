package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class SServiceRequest extends SPage{
    @FXML
    public JFXButton backB;
    
    @FXML
    public JFXButton submitB;

    @FXML
    public JFXButton helpB;

    @FXML
    public JFXButton homeB;

    @FXML
    public StackPane stackPane;
    
    public void goHome(ActionEvent actionEvent) {
        sceneSwitch("DefaultServicePage");
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
                    Object root = FXMLLoader.load(getClass().getResource("/edu/wpi/aquamarine_axolotls/fxml/DefaultServicePage.fxml"));
                    Aapp.getPrimaryStage().getScene().setRoot((Parent) root);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }        }
        });

        content.setActions(exit_button);
        help.show();
    }


    @FXML
    public void loadHelp(javafx.event.ActionEvent event) {
        final Stage myDialog = new Stage();
        myDialog.initModality(Modality.APPLICATION_MODAL);
        myDialog.centerOnScreen();
        myDialog.setTitle("Help Page");
        Text text1 = new Text("\nHelp Page Information:\n");
        Text text2 = new Text("\n blahblahblah");
        text1.setStyle("-fx-font-size: 20; -fx-fill: black; -fx-font-family: Times; -fx-alignment: center");
        TextFlow textFlow = new TextFlow(text1,text2);
        textFlow.setTextAlignment(TextAlignment.CENTER);
        myDialog.setScene(new Scene(textFlow, 400, 300));
        myDialog.show();
    }
}
