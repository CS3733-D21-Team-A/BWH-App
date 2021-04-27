package edu.wpi.aquamarine_axolotls.views;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeRequests extends SPage{

    @FXML private TableView<EmployeeRequest> srTable;
    @FXML private TableColumn<EmployeeRequest, String> assignedColumn;
    @FXML private TableColumn<EmployeeRequest, String> assigneeColumn;
    @FXML private TableColumn<EmployeeRequest, String> statusColumn;
    @FXML private TableColumn<EmployeeRequest, String> serviceRequestColumn;
    @FXML private TableColumn<EmployeeRequest, String> locationColumn;
    @FXML private JFXButton assignB;
    @FXML private JFXButton changeStatusB;
    @FXML private JFXComboBox assignD;
    @FXML JFXComboBox statusD;
    @FXML JFXComboBox filterD;
    
    DatabaseController db;


    @FXML
    public void initialize() { // creds : http://tutorials.jenkov.com/javafx/tableview.html
        try {
            db = new DatabaseController();
            Map<String, String> user1 = new HashMap<>();
/*            user1.put("USERNAME", "poophead");
            user1.put("FIRSTNAME","poo");
            user1.put("LASTNAME","head");
            user1.put("EMAIL","phead@gmail.com");
            user1.put("USERTYPE", "Employee");
            user1.put("PASSWORD", "123");*/


           // db.addUser(user1);
            ObservableList<String> employeeNames = FXCollections.observableArrayList();
            List<Map<String, String>> users = db.getUsers();
            for(Map<String, String> user : users){
                if(user.get("USERTYPE").equals("Employee")){
                    employeeNames.add(user.get("FIRSTNAME") + user.get("LASTNAME"));
                }
            }
            assignD.setItems(employeeNames);
            statusD.setItems(FXCollections
                    .observableArrayList(DatabaseUtil.STATUS_NAMES.get(STATUS.IN_PROGRESS),
                            DatabaseUtil.STATUS_NAMES.get(STATUS.DONE),
                            DatabaseUtil.STATUS_NAMES.get(STATUS.CANCELED)));
            assignedColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("assigned"));
            assigneeColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("assignee"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("status"));
            serviceRequestColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("serviceRequest"));
            locationColumn.setCellValueFactory(new PropertyValueFactory<EmployeeRequest, String>("location"));

            refresh();
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void goHome(ActionEvent actionEvent) {
        sceneSwitch("AdminMainPage");
    }

    public void refresh(){
        List<Map<String, String>> serviceRequests = null;
        try {
            serviceRequests = db.getServiceRequests();
            srTable.getItems().clear();
            for(Map<String, String> req : serviceRequests){
                srTable.getItems().add(new EmployeeRequest(req));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void goToService() {
        sceneSwitch("AdminMainPage");
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
        } catch (SQLException | IOException | URISyntaxException e) {
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
            db.changeStatus(srTable.getItems().get(index).getRequestID(), DatabaseUtil.STATUS_NAMES.inverse().get(status));
            refresh();
            db.close();
        } catch (SQLException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
