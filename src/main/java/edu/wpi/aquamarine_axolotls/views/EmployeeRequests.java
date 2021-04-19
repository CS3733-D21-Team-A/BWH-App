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
                .observableArrayList(DatabaseInfo.TABLES.SERVICEREQUESTS.STATUSES.UNASSIGNED,
                        DatabaseInfo.TABLES.SERVICEREQUESTS.STATUSES.ASSIGNED,
                        DatabaseInfo.TABLES.SERVICEREQUESTS.STATUSES.IN_PROGRESS,
                        DatabaseInfo.TABLES.SERVICEREQUESTS.STATUSES.DONE,
                        DatabaseInfo.TABLES.SERVICEREQUESTS.STATUSES.CANCELED));
        assignedColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("assigned"));
        assigneeColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("assignee"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("status"));
        serviceRequestColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("serviceRequest"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("location"));

        try {
            db = new DatabaseController();
            List<Map<String, String>> serviceRequests = db.getServiceRequests();
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

    // TODO: change all methods to use DB and update it and not the table
    @FXML
    public void assign(){
        try {
            if(assignD.getSelectionModel() == null) return;
            int index = srTable.getSelectionModel().getFocusedIndex();
            if(index == -1) return;

            db = new DatabaseController();
            EmployeeRequest r = srTable.getItems().get(index);
            String employee = assignD.getSelectionModel().getSelectedItem().toString();

            r.setAssigned(employee);
            db.changeEmployee(r.getReqId(), employee);
            db.close();
            srTable.refresh();
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
        if(statusD.getSelectionModel() == null) return;
        int index = srTable.getSelectionModel().getFocusedIndex();
        if(index == -1) return;
       // Aapp.serviceRequests.get(index).replace();
        EmployeeRequest r = srTable.getItems().get(index);
        r.setStatus(statusD.getSelectionModel().getSelectedItem().toString());

        //b.changeStatus(r.getReqId(), statusD.getSelectionModel().getSelectedItem());
        statusD.getSelectionModel().clearSelection();
        srTable.refresh();
    }

    @FXML
    public void cancel(){
        int index = srTable.getSelectionModel().getFocusedIndex();
        if(index == -1) return;
        //Aapp.serviceRequests.remove(index);
        srTable.getItems().remove(index);
        srTable.refresh();
    }

}
