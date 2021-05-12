package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTabPane;
import edu.wpi.cs3733.d21.teamA.db.DatabaseController;
import edu.wpi.cs3733.d21.teamA.db.*;
import edu.wpi.cs3733.d21.teamA.db.enums.STATUS;
import edu.wpi.cs3733.d21.teamA.db.enums.USERTYPE;
import edu.wpi.cs3733.d21.teamA.views.GenericPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.wpi.cs3733.d21.teamA.Settings.*;

public class EmployeeRequests extends GenericPage { //TODO: please change the name of this class and page

    @FXML private TableView<Request> srTable;
    @FXML private TableColumn<Request, String> assignedColumn;
    @FXML private TableColumn<Request, String> assigneeColumn;
    @FXML private TableColumn<Request, String> statusColumn;
    @FXML private TableColumn<Request, String> serviceRequestColumn;
    @FXML private TableColumn<Request, String> locationColumn;

    @FXML private TableView<CovidSurvey> covidSurveyTable;
    @FXML private TableColumn<CovidSurvey, String> usernameColumn;
    @FXML private TableColumn<CovidSurvey, String> feverColumn;
    @FXML private TableColumn<CovidSurvey, String> coughColumn;
    @FXML private TableColumn<CovidSurvey, String> shortnessOfBreathColumn;
    @FXML private TableColumn<CovidSurvey, String> muscleAchesColumn;
    @FXML private TableColumn<CovidSurvey, String> tiredColumn;
    @FXML private TableColumn<CovidSurvey, String> nasalColumn;
    @FXML private TableColumn<CovidSurvey, String> soreThroatColumn;
    @FXML private TableColumn<CovidSurvey, String> nauseaDiarrheaColumn;
    @FXML private TableColumn<CovidSurvey, String> lossOfTasteSmellColumn;
    @FXML private TableColumn<CovidSurvey, String> chillsColumn;
    @FXML private TableColumn<CovidSurvey, String> quarantineColumn;
    @FXML private TableColumn<CovidSurvey, String> COVIDLikelyColumn;

    @FXML VBox srVbox;
    @FXML private JFXButton assignB;
    @FXML private JFXButton changeStatusB;
    @FXML private JFXComboBox assignD;
    @FXML JFXComboBox statusD;
    @FXML Line line;

    @FXML VBox covidVbox;
    @FXML JFXComboBox covidStatus;

    @FXML private JFXTabPane tabs;
    @FXML private Tab covidSurveys;


    DatabaseController db;


    @FXML
    public void initialize() { // creds : http://tutorials.jenkov.com/javafx/tableview.html
        try {
            db = DatabaseController.getInstance();

            ObservableList<String> names;

            USERTYPE usertype = DatabaseUtil.USER_TYPE_NAMES.inverse().get(PREFERENCES.get(USER_TYPE,null));

            if (usertype != null) {
                switch (usertype) {
                    case PATIENT: // only display their service requests
                        assignB.setVisible(false);
                        changeStatusB.setVisible(false);
                        statusD.setVisible(false);
                        assignD.setVisible(false);
                        line.setVisible(false);
                        tabs.getTabs().remove(covidSurveys);
                        break;
                    case ADMIN:
                        names = FXCollections.observableArrayList();
                        for (Map<String, String> user : db.getEmployees()) {
                            if (user.get("USERTYPE").equals("Employee")) {
                                names.add(user.get("FIRSTNAME") + " " + user.get("LASTNAME"));
                            }
                        }
                        assignD.setItems(names);
                        break;
                    case EMPLOYEE:
                        names = FXCollections.observableArrayList();
                        Map<String, String> usr = db.getUserByUsername(PREFERENCES.get(USER_NAME, null));
                        String name = usr.get("FIRSTNAME") + " " + usr.get("LASTNAME");
                        names.add(name);
                        assignD.setItems(names);
                        break;
                }
            }

            //COVID Status
            ObservableList<String> covidStat = FXCollections.observableArrayList();
            covidStat.add("true");
            covidStat.add("false");
            covidStatus.setItems(covidStat);

            //Service Request Table
            statusD.setItems(FXCollections
                    .observableArrayList(DatabaseUtil.STATUS_NAMES.get(STATUS.IN_PROGRESS),
                            DatabaseUtil.STATUS_NAMES.get(STATUS.DONE),
                            DatabaseUtil.STATUS_NAMES.get(STATUS.CANCELED)));
            assignedColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("assigned"));
            assigneeColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("assignee"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("status"));
            serviceRequestColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("serviceRequest"));
            locationColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("location"));

            // COVID Table
            usernameColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("username"));
            COVIDLikelyColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("isCovidLikely"));
            feverColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("fever"));
            coughColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("cough"));
            shortnessOfBreathColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("shortnessOfBreath"));
            muscleAchesColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("muscleAches"));
            tiredColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("tired"));
            nasalColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("nasalCongestion"));
            lossOfTasteSmellColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("lossOfTasteSmell"));
            chillsColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("chills"));
            quarantineColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("quarantine"));
            soreThroatColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("soreThroat"));
            nauseaDiarrheaColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("nauseaDiarrhea"));

            srVbox.setVisible(true);
            covidVbox.setVisible(false);
            srVbox.toFront();

            refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void refresh(){
        List<Map<String, String>> serviceRequests = null;
        List<Map<String, String>> covSurveys = null;
        try {
            switch (DatabaseUtil.USER_TYPE_NAMES.inverse().get(PREFERENCES.get(USER_TYPE,null))) {
                case ADMIN:
                case EMPLOYEE:
                    serviceRequests = db.getServiceRequests();
                    srTable.getItems().clear();
                    for (Map<String, String> req : serviceRequests) {
                        srTable.getItems().add(new Request(req));
                    }

                    covSurveys = db.getSurveys();
                    covidSurveyTable.getItems().clear();
                    for (Map<String, String> survey : covSurveys) {
                        covidSurveyTable.getItems().add(new CovidSurvey(survey));
                    }
                case PATIENT:
                    serviceRequests = db.getServiceRequestsByAuthor(PREFERENCES.get(USER_NAME,null));
                    srTable.getItems().clear();
                    for (Map<String, String> req : serviceRequests) {
                        srTable.getItems().add(new Request(req));
                    }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }

    }


    @FXML
    public void assign(){
        try {
            if(assignD.getSelectionModel() == null) return;
            int index = srTable.getSelectionModel().getFocusedIndex();
            if(index == -1) return;
            db.assignEmployee(srTable.getItems().get(index).getRequestID(), assignD.getSelectionModel().getSelectedItem().toString());
            refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void changeStatus(){
        try {
            if(statusD.getSelectionModel() == null) return;
            int index = srTable.getSelectionModel().getFocusedIndex();
            if(index == -1) return;

            String status = statusD.getSelectionModel().getSelectedItem().toString();
            db.changeStatus(srTable.getItems().get(index).getRequestID(), DatabaseUtil.STATUS_NAMES.inverse().get(status));
            refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changeCovidStatus() {
        // TODO: add db integration
        try {
            if(covidStatus.getSelectionModel() == null) return;
            int index = covidSurveyTable.getSelectionModel().getFocusedIndex();
            if(index == -1) return;

            String status = covidStatus.getSelectionModel().getSelectedItem().toString();

            Map<String, String> isCovidLikely = new HashMap<>();
            isCovidLikely.put("COVIDLIKELY", status);

            db.editUser(covidSurveyTable.getItems().get(index).username, isCovidLikely);
            refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showCovidBox() {
        srVbox.setVisible(false);
        covidVbox.setVisible(true);
        covidVbox.toFront();
    }

    public void showSRBox() {
        srVbox.setVisible(true);
        covidVbox.setVisible(false);
        srVbox.toFront();
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

    public class CovidSurvey { //This needs to be public for things to work :(
        private String username;
        private String fever;
        private String cough;
        private String shortnessOfBreath;
        private String muscleAches;
        private String tired;
        private String nasalCongestion;
        private String soreThroat;
        private String nauseaDiarrhea;
        private String lossOfTasteSmell;
        private String chills;
        private String quarantine;
        private String isCovidLikely;

        public CovidSurvey(Map<String, String> survey) {
            this.username = survey.get("USERNAME");
            this.quarantine = survey.get("AREQUAR");
            this.fever = survey.get("HASFEVER");
            this.cough = survey.get("HASCOUGH");
            this.shortnessOfBreath = survey.get("SHORTBREATH");
            this.muscleAches = survey.get("MUSCLEACHES");
            this.tired = survey.get("MORETIRED");
            this.nasalCongestion = survey.get("NASALCONGEST");
            this.soreThroat = survey.get("SORETHROAT");
            this.nauseaDiarrhea = survey.get("NAUSEADIARRHEA");
            this.lossOfTasteSmell = survey.get("LOSSTASTESMELL");
            this.chills = survey.get("NEWCHILLS");
            try {
                this.isCovidLikely = db.getUserByUsername(survey.get("USERNAME")).get("COVIDLIKELY");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getFever() {
            return fever;
        }

        public void setFever(String fever) {
            this.fever = fever;
        }

        public String getCough() {
            return cough;
        }

        public void setCough(String cough) {
            this.cough = cough;
        }

        public String getShortnessOfBreath() {
            return shortnessOfBreath;
        }

        public void setShortnessOfBreath(String shortnessOfBreath) {
            this.shortnessOfBreath = shortnessOfBreath;
        }

        public String getMuscleAches() {
            return muscleAches;
        }

        public void setMuscleAches(String muscleAches) {
            this.muscleAches = muscleAches;
        }

        public String getTired() {
            return tired;
        }

        public void setTired(String tired) {
            this.tired = tired;
        }

        public String getNasalCongestion() {
            return nasalCongestion;
        }

        public void setNasalCongestion(String nasalCongestion) {
            this.nasalCongestion = nasalCongestion;
        }

        public String getSoreThroat() {
            return soreThroat;
        }

        public void setSoreThroat(String soreThroat) {
            this.soreThroat = soreThroat;
        }

        public String getNauseaDiarrhea() {
            return nauseaDiarrhea;
        }

        public void setNauseaDiarrhea(String nauseaDiarrhea) {
            this.nauseaDiarrhea = nauseaDiarrhea;
        }

        public String getLossOfTasteSmell() {
            return lossOfTasteSmell;
        }

        public void setLossOfTasteSmell(String lossOfTasteSmell) {
            this.lossOfTasteSmell = lossOfTasteSmell;
        }

        public String getChills() {
            return chills;
        }

        public void setChills(String chills) {
            this.chills = chills;
        }

        public String getQuarantine() {
            return quarantine;
        }

        public void setQuarantine(String quarantine) {
            this.quarantine = quarantine;
        }

        public String getIsCovidLikely() {
            return isCovidLikely;
        }

        public void setIsCovidLikely(String isCovidLikely) {
            this.isCovidLikely = isCovidLikely;
        }

    }

}
