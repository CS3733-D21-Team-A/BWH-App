package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.CSVHandler;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javafx.stage.Modality;
import javafx.util.Duration;

public class TemplateServiceRequest {
     ObservableList<String> foodOptionList = FXCollections
                .observableArrayList("Vegetarian", "Salad", "Pizza");

        DatabaseController db;
        CSVHandler csvHandler;

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
        private ComboBox locationDropdown;
        @FXML
        private AnchorPane myAnchorPane;

        @FXML
        private AnchorPane anchor1 ;

        @FXML
        private ToggleButton toggleButton;

        @FXML
        public void initialize() {
            ObservableList<String> options = FXCollections.observableArrayList();
            foodOptions.setItems(foodOptionList);

            try {
                db = new DatabaseController();
                csvHandler = new CSVHandler(db);
                List<Map<String, String>> nodes = db.getNodes();
                for (Map<String, String> node : nodes) {
                    options.add(node.get("LONGNAME"));
                }
                locationDropdown.setItems(options);
            } catch (SQLException | IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }


        @FXML
        public void loadHelp (javafx.event.ActionEvent event) {
            final Stage myDialog = new Stage();
            myDialog.initModality(Modality.APPLICATION_MODAL);
            myDialog.centerOnScreen();
            myDialog.setTitle("Help Page");
            Text text1 = new Text("\nHelp Page Information:\n");
            Text text2 = new Text("\n blahblahblah");
            text1.setStyle("-fx-font-size: 20; -fx-fill: black; -fx-font-family: Times; -fx-alignment: center");
            TextFlow textFlow = new TextFlow(text1, text2);
            textFlow.setTextAlignment(TextAlignment.CENTER);
            myDialog.setScene(new Scene(textFlow, 300 , 200));
            myDialog.show();
        }

    @FXML
    public void hammy() {
        boolean isSelected = toggleButton.isSelected();
        if(isSelected){
            anchor1.setVisible(true);
            TranslateTransition translateTransition = new TranslateTransition((Duration.seconds(.25)), anchor1);
            translateTransition.setFromX(150);
            translateTransition.setToX(0);

            translateTransition.play();
        }
        if(!isSelected) {
            TranslateTransition translateTransition1 = new TranslateTransition((Duration.seconds(1.5)), anchor1);
            translateTransition1.setFromX(0);
            translateTransition1.setToX(150);
            translateTransition1.play();
        }
    }


        @FXML
        public void handleButtonAction(javafx.event.ActionEvent actionEvent) throws IOException {
            final Stage myDialog = new Stage();
            myDialog.initModality(Modality.APPLICATION_MODAL);
            myDialog.centerOnScreen();
            myDialog.setTitle("Submission Success");
            Text text1 = new Text("\nYour service request has been successfully submitted!\n");
            text1.setStyle("-fx-font-size: 20; -fx-fill: black; -fx-font-family: Times; -fx-alignment: center");
            TextFlow textFlow = new TextFlow(text1);
            textFlow.setTextAlignment(TextAlignment.CENTER);
            myDialog.setScene(new Scene(textFlow, 300, 100));
            myDialog.show();

        }

    }

