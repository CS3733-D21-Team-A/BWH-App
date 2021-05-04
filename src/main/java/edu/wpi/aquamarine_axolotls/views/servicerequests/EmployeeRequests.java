package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import edu.wpi.aquamarine_axolotls.Aapp;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.db.enums.STATUS;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Line;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class EmployeeRequests extends GenericPage { //TODO: please change the name of this class and page

    @FXML private TableView<Request> srTable;
    @FXML private TableColumn<Request, String> assignedColumn;
    @FXML private TableColumn<Request, String> assigneeColumn;
    @FXML private TableColumn<Request, String> statusColumn;
    @FXML private TableColumn<Request, String> serviceRequestColumn;
    @FXML private TableColumn<Request, String> locationColumn;
    @FXML private JFXButton assignB;
    @FXML private JFXButton changeStatusB;
    @FXML private JFXComboBox assignD;
    @FXML JFXComboBox statusD;
    @FXML Line line;

    DatabaseController db;


    @FXML
    public void initialize() { // creds : http://tutorials.jenkov.com/javafx/tableview.html
        try {
            db = DatabaseController.getInstance();

            if(Aapp.userType.equals("Patient")){ // only display their service requests
                assignB.setVisible(false);
                changeStatusB.setVisible(false);
                statusD.setVisible(false);
                assignD.setVisible(false);
                line.setVisible(false);
            }
            else if(Aapp.userType.equals("Admin")){
                ObservableList<String> names = FXCollections.observableArrayList();
                List<Map<String, String>> users = db.getUsers();
                for(Map<String, String> user : users){
                    if(user.get("USERTYPE").equals("Employee")){
                        names.add(user.get("FIRSTNAME") + " " + user.get("LASTNAME"));
                    }
                }
                assignD.setItems(names);

            }
            else if(Aapp.userType.equals("Employee")){
                ObservableList<String> names = FXCollections.observableArrayList();
                Map<String, String> usr = db.getUserByUsername(Aapp.username);
                String name = usr.get("FIRSTNAME") + " " + usr.get("LASTNAME");
                names.add(name);
                assignD.setItems(names);
            }


            statusD.setItems(FXCollections
                    .observableArrayList(DatabaseUtil.STATUS_NAMES.get(STATUS.IN_PROGRESS),
                            DatabaseUtil.STATUS_NAMES.get(STATUS.DONE),
                            DatabaseUtil.STATUS_NAMES.get(STATUS.CANCELED)));
            assignedColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("assigned"));
            assigneeColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("assignee"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("status"));
            serviceRequestColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("serviceRequest"));
            locationColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("location"));

            refresh();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }

    }

    public void refresh(){
        List<Map<String, String>> serviceRequests = null;
        try {
            if(Aapp.userType.equals("Admin") || Aapp.userType.equals("Employee")){
                serviceRequests = db.getServiceRequests();
                srTable.getItems().clear();
                for(Map<String, String> req : serviceRequests){
                    srTable.getItems().add(new Request(req));
                }
            }
            else if(Aapp.userType.equals("Patient")){
                serviceRequests = db.getServiceRequestsByAuthor(Aapp.username);
                srTable.getItems().clear();
                for(Map<String, String> req : serviceRequests){
                    srTable.getItems().add(new Request(req));
                }
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

            db = DatabaseController.getInstance();
            db.assignEmployee(srTable.getItems().get(index).getRequestID(), assignD.getSelectionModel().getSelectedItem().toString());
            refresh();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void changeStatus(){
        try {
            if(statusD.getSelectionModel() == null) return;
            int index = srTable.getSelectionModel().getFocusedIndex();
            if(index == -1) return;

            db = DatabaseController.getInstance();
            String status = statusD.getSelectionModel().getSelectedItem().toString();
            db.changeStatus(srTable.getItems().get(index).getRequestID(), DatabaseUtil.STATUS_NAMES.inverse().get(status));
            refresh();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public class Request { //This needs to be public for things to work :(

        private String assigned;
        private String assignee;
        private String status;
        private String serviceRequest;
        private String location;
        private String requestID;

        public void setAssigned(String assigned) {
            this.assigned = assigned;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getAssigned() {
            return assigned;
        }

        public String getAssignee() {
            return assignee;
        }

        public String getStatus() {
            return status;
        }

        public String getServiceRequest() {
            return serviceRequest;
        }

        public String getLocation() {
            return location;
        }

        public String getRequestID() {
            return requestID;
        }

        public Request(Map<String, String> sr) {
            this.assigned = sr.get("EMPLOYEEID"); // TODO : update when DB database is here
            this.assignee = sr.get("FIRSTNAME") + " " + sr.get("LASTNAME");
            this.status = sr.get("STATUS");
            this.serviceRequest = sr.get("REQUESTTYPE");
            this.location = sr.get("LOCATIONID");
            this.requestID = sr.get("REQUESTID");
        }
    }

}
