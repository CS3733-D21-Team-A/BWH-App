package edu.wpi.aquamarine_axolotls.views;

import edu.wpi.aquamarine_axolotls.Aapp;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
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
    public void initialize() { // creds : http://tutorials.jenkov.com/javafx/tableview.html
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

}
