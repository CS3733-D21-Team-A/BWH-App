package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.DatabaseInfo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeRequests extends SServiceRequest{



    @FXML
    private TableView<EmployeeRequest> srTable;

    @FXML
    TableColumn<EmployeeRequest, String> assignedColumn;

    @FXML
    TableColumn<EmployeeRequest, String> assigneeColumn;

    @FXML
    TableColumn<EmployeeRequest, String> statusColumn;

    @FXML
    TableColumn<EmployeeRequest, String> serviceRequestColumn;

    @FXML
    TableColumn<EmployeeRequest, String> locationColumn;

    @FXML
    JFXButton assignB;

    @FXML
    JFXButton changeStatusB;

    @FXML
    JFXButton cancelB;

    @FXML
    JFXComboBox assignD;

    @FXML
    JFXComboBox statusD;

    @FXML
    JFXComboBox filterD;
    
    DatabaseController db;


    @FXML
    public void initialize() { // creds : http://tutorials.jenkov.com/javafx/tableview.html
        assignD.setItems(FXCollections
                .observableArrayList("Sai", "Samantha", "Imani"));

        statusD.setItems(FXCollections
                .observableArrayList(DatabaseInfo.TABLES.SERVICEREQUESTS.STATUS.IN_PROGRESS.text,
                        DatabaseInfo.TABLES.SERVICEREQUESTS.STATUS.DONE.text,
                        DatabaseInfo.TABLES.SERVICEREQUESTS.STATUS.CANCELED.text));
        assignedColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("assigned"));
        assigneeColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("assignee"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("status"));
        serviceRequestColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("serviceRequest"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("location"));

        refresh();
    }

    public void refresh(){
        try {
            db = new DatabaseController();
            List<Map<String, String>> serviceRequests = db.getServiceRequests();
            srTable.getItems().clear();
            for(Map<String, String> req : serviceRequests){
                srTable.getItems().add(new EmployeeRequest(req));
            }
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void goToService() {
        sceneSwitch("DefaultServicePage");
    }

    @FXML
    public void assign(){
        try {
            if(assignD.getSelectionModel() == null) return;
            int index = srTable.getSelectionModel().getFocusedIndex();
            if(index == -1) return;

            db = new DatabaseController();
            db.assignEmployee(srTable.getItems().get(index).getRequestID(), assignD.getSelectionModel().getSelectedItem().toString());
            refresh();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void changeStatus(){
        try {
            if(statusD.getSelectionModel() == null) return;
            int index = srTable.getSelectionModel().getFocusedIndex();
            if(index == -1) return;

            db = new DatabaseController();
            String status = statusD.getSelectionModel().getSelectedItem().toString();
            db.changeStatus(srTable.getItems().get(index).getRequestID(), DatabaseInfo.TABLES.SERVICEREQUESTS.STATUS.stringToStatus(status));
            refresh();
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

}