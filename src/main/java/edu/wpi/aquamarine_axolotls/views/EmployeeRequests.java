package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

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


    @FXML
    public void initialize() { // creds : http://tutorials.jenkov.com/javafx/tableview.html
        assignD.setItems(FXCollections
                .observableArrayList("Sai", "Samantha", "Imani"));
        statusD.setItems(FXCollections
                .observableArrayList("Unassigned", "Assigned","In Progress", "Done", "Canceled"));
        assignedColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("assigned"));
        assigneeColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("assignee"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("status"));
        serviceRequestColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("serviceRequest"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("location"));

        for(Map<String, String> sq : Aapp.serviceRequests) {
            srTable.getItems().add(new EmployeeRequest(sq));
        }
    }

    @FXML
    public void goToService() {
        sceneSwitch("DefaultServicePage");
    }

    // TODO: change all methods to use DB and update it and not the table
    @FXML
    public void assign(){
        if(assignD.getSelectionModel() == null) return;
        int index = srTable.getSelectionModel().getFocusedIndex();
        if(index == -1) return;
        srTable.getItems().get(index).setAssigned(assignD.getSelectionModel().getSelectedItem().toString());
        assignD.getSelectionModel().clearSelection();
        srTable.refresh();
    }

    @FXML
    public void changeStatus(){
        if(statusD.getSelectionModel() == null) return;
        int index = srTable.getSelectionModel().getFocusedIndex();
        if(index == -1) return;
       // Aapp.serviceRequests.get(index).replace();
        srTable.getItems().get(index).setStatus(statusD.getSelectionModel().getSelectedItem().toString());
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
