package edu.wpi.aquamarine_axolotls.views.servicerequests;

import com.jfoenix.controls.JFXTabPane;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.db.*;
import edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST;
import edu.wpi.aquamarine_axolotls.db.enums.STATUS;
import edu.wpi.aquamarine_axolotls.db.enums.USERTYPE;
import edu.wpi.aquamarine_axolotls.views.GenericPage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.util.*;

import static edu.wpi.aquamarine_axolotls.Settings.*;
import static edu.wpi.aquamarine_axolotls.db.enums.SERVICEREQUEST.*;
import static edu.wpi.aquamarine_axolotls.db.enums.USERTYPE.*;

public class EmployeeRequests extends GenericPage { //TODO: please change the name of this class and page

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
    @FXML private TableColumn<CovidSurvey, String> covidLikelyColumn;
    @FXML private TableColumn<CovidSurvey, String> entryApprovedColumn;

    @FXML private JFXTabPane tabs;
    @FXML private Tab covidSurveys;

    DatabaseController db;

    private static final List<String> serviceRequestIndex;

    static {
        serviceRequestIndex = new ArrayList<String>();
        serviceRequestIndex.add("  floral");
        serviceRequestIndex.add("  food");
        serviceRequestIndex.add("  gift");
        serviceRequestIndex.add("  language");
        serviceRequestIndex.add("  facilities");
        serviceRequestIndex.add("  laundry");
        serviceRequestIndex.add("  sanitation");
        serviceRequestIndex.add("  external");
        serviceRequestIndex.add("  internal");
        serviceRequestIndex.add("  medicine");
    }

    static Map<String, String> icons = new HashMap<String,String>() {{
        put("  floral", "edu/wpi/aquamarine_axolotls/img/iconsWWords/FloralDeliverywWords.png");
        put("  food", "edu/wpi/aquamarine_axolotls/img/iconsWWords/FoodDeliverywWords.png");
        put("  gift", "edu/wpi/aquamarine_axolotls/img/iconsWWords/GiiftDeliverywWords.png");
        put("  language", "edu/wpi/aquamarine_axolotls/img/iconsWWords/LanguageInterpreterwWords.png");
        put("  facilities", "edu/wpi/aquamarine_axolotls/img/iconsWWords/FacilitiesMaintenancewWords.png");
        put("  laundry", "edu/wpi/aquamarine_axolotls/img/iconsWWords/LaundrywWords.png");
        put("  sanitation", "edu/wpi/aquamarine_axolotls/img/iconsWWords/SanitationwWords.png");
        put("  external", "edu/wpi/aquamarine_axolotls/img/iconsWWords/ExternalTransportwWords.png");
        put("  internal", "edu/wpi/aquamarine_axolotls/img/iconsWWords/InternalTransportwWords.png");
        put("  medicine", "edu/wpi/aquamarine_axolotls/img/iconsWWords/MedicineDeliverywWords.png");
    }};

    /**
     * Initializes the page for each user type
     */
    @FXML
    public void initialize() {
        try {
            db = DatabaseController.getInstance();

            ObservableList<String> names = FXCollections.observableArrayList();

            USERTYPE usertype = DatabaseUtil.USER_TYPE_NAMES.inverse().get(PREFERENCES.get(USER_TYPE,null));

            if (usertype != null) {
                switch (usertype) {
                    case PATIENT: // only display their service requests
                        tabs.getTabs().remove(covidSurveys);
                        break;
                    case ADMIN:
                        names = FXCollections.observableArrayList();
                        for (Map<String, String> user : db.getEmployees()) {
                            if (user.get("USERTYPE").equals("Employee")) {
                                names.add(user.get("FIRSTNAME") + " " + user.get("LASTNAME"));
                            }
                        }
                        covidSurveyTable.setEditable(true);
                        break;
                    case EMPLOYEE:
                        names = FXCollections.observableArrayList();
                        Map<String, String> usr = db.getUserByUsername(PREFERENCES.get(USER_NAME, null));
                        String name = usr.get("FIRSTNAME") + " " + usr.get("LASTNAME");
                        names.add(name);
                        covidSurveyTable.setEditable(true);
                        break;
                }
            }

            //COVID Status
            ObservableList<String> covidStatAndEntry = FXCollections.observableArrayList();
            covidStatAndEntry.add("true");
            covidStatAndEntry.add("false");

            //Service Request Status
            ObservableList<String> stats = FXCollections.observableArrayList(DatabaseUtil.STATUS_NAMES.get(STATUS.IN_PROGRESS),
                            DatabaseUtil.STATUS_NAMES.get(STATUS.DONE),
                            DatabaseUtil.STATUS_NAMES.get(STATUS.CANCELED));

            createTabs(names, stats);

            // COVID Table
            usernameColumn.setCellValueFactory(new PropertyValueFactory<CovidSurvey, String>("username"));
            covidLikelyColumn.setCellValueFactory(cellData -> cellData.getValue().isCovidLikelyProp());
            entryApprovedColumn.setCellValueFactory(cellData -> cellData.getValue().entryApprovedProp());
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

            covidLikelyColumn.setCellFactory(ComboBoxTableCell.forTableColumn(covidStatAndEntry));
            covidLikelyColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<CovidSurvey, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CovidSurvey, String> event) {
                    updateCovidTable("COVIDLIKELY", event);
                }
            });

            entryApprovedColumn.setCellFactory(ComboBoxTableCell.forTableColumn(covidStatAndEntry));
            entryApprovedColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<CovidSurvey, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<CovidSurvey, String> event) {
                    updateCovidTable("ENTRYAPPROVED", event);
                }
            });

            populateTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Populates the corresponding tables with requests and surveys
     */
    public void populateTable(){
        List<Map<String, String>> serviceRequests = null;
        List<Map<String, String>> covSurveys = null;
        try {
            switch (DatabaseUtil.USER_TYPE_NAMES.inverse().get(PREFERENCES.get(USER_TYPE,null))) {
                case ADMIN:
                case EMPLOYEE:
                    serviceRequests = db.getServiceRequests();
                    for (Map<String, String> req : serviceRequests) {
                        int index = serviceRequestIndex.indexOf(req.get("REQUESTTYPE"))+1;          // adds one to take into account of the COVID tab
                        TableView table = (TableView) ((tabs.getTabs().get(index).getContent()));
                        table.getItems().add(new Request(req));
                    }

                    covSurveys = db.getSurveys();
                    covidSurveyTable.getItems().clear();
                    for (Map<String, String> survey : covSurveys) {
                        covidSurveyTable.getItems().add(new CovidSurvey(survey));
                    }
                case PATIENT:
                    serviceRequests = db.getServiceRequestsByAuthor(PREFERENCES.get(USER_NAME,null));
                    for (Map<String, String> req : serviceRequests) {
                        int index = serviceRequestIndex.indexOf(req.get("REQUESTTYPE"))+1;          // adds one to take into account of the COVID tab
                        TableView table = (TableView) ((tabs.getTabs().get(index).getContent()));
                        table.getItems().add(new Request(req));
                    }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates tabs and tables for each service request type
     * @param names List of employee names to assign a service request to
     * @param stats List of statuses for each service request
     */
    public void createTabs(ObservableList names, ObservableList stats){
        USERTYPE usertype = DatabaseUtil.USER_TYPE_NAMES.inverse().get(PREFERENCES.get(USER_TYPE,null));

        for (String serReqType : serviceRequestIndex){
            String s = serReqType;
            if (usertype.equals(PATIENT) && s.equals("External Transport")){
                break;
            }

            TableView<Request> table = new TableView<Request>();
            table.setPrefSize(950.0, 750.0);

            TableColumn<Request, String> assignedColumn = new TableColumn<Request, String>();
            TableColumn<Request, String> assigneeColumn = new TableColumn<Request, String>();
            TableColumn<Request, String> statusColumn = new TableColumn<Request, String>();
            TableColumn<Request, String> locationColumn = new TableColumn<Request, String>();

            assignedColumn.setText("Assigned");
            assigneeColumn.setText("Assignee");
            statusColumn.setText("Status");
            locationColumn.setText("Location");

            table.getColumns().addAll(assignedColumn, assigneeColumn, statusColumn, locationColumn);

            assignedColumn.setCellValueFactory(cellData -> cellData.getValue().assignedProp());
            assigneeColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("assignee"));
            statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProp());
            locationColumn.setCellValueFactory(new PropertyValueFactory<Request, String>("location"));

            assignedColumn.setCellFactory(ComboBoxTableCell.forTableColumn(names));
            assignedColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Request, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Request, String> event) {
                    try {
                        int index = table.getSelectionModel().getFocusedIndex();

                        String employeeID = event.getNewValue();
                        table.getItems().get(index).setAssigned(event.getNewValue());

                        db.assignEmployee((table.getItems().get(index)).getRequestID(), employeeID);
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });

            statusColumn.setCellFactory(ComboBoxTableCell.forTableColumn(stats));
            statusColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<Request, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Request, String> event) {
                    try {
                        int index = table.getSelectionModel().getFocusedIndex();

                        String status = event.getNewValue();
                        table.getItems().get(index).setStatus(event.getNewValue());

                        db.changeStatus((table.getItems().get(index)).getRequestID(), DatabaseUtil.STATUS_NAMES.inverse().get(status));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            });

            if (usertype != null) {
                switch (usertype) {
                    case PATIENT: // only display their service requests
                        table.setEditable(false);
                        break;
                    case ADMIN:
                    case EMPLOYEE:
                        table.setEditable(true);
                        break;
                }
            }

            Tab tab = new Tab(s, table);
            Image img = new Image(icons.get(s));
            ImageView tabImg = new ImageView(img);
            tabImg.setFitWidth(256.0);
            tabImg.setPreserveRatio(true);
            tab.setGraphic(tabImg);

            tabs.getTabs().add(tab);
        }
    }

    /**
     * Updates the DB and the table when a change is made in COVID Table
     * @param colName Column name in DB
     * @param event the onEditCommit event
     */
    public void updateCovidTable(String colName, TableColumn.CellEditEvent<CovidSurvey, String> event){
        try {
            int index = covidSurveyTable.getSelectionModel().getFocusedIndex();

            Map<String, String> edits = new HashMap<>();
            edits.put(colName, event.getNewValue());

            covidSurveyTable.getItems().get(index).setEntryApproved(event.getNewValue());

            db.editUser(covidSurveyTable.getItems().get(index).username, edits);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Represents each service request in the tables
     */
    public class Request { //This needs to be public for things to work :(

        private StringProperty assigned;
        private String assignee;
        private StringProperty status;
        private String serviceRequest;
        private String location;
        private String requestID;

        public void setAssigned(String assigned) {
            this.assignedProp().set(assigned);
        }

        public void setStatus(String status) {
            this.statusProp().set(status);
        }

        public StringProperty assignedProp(){
            return assigned;
        }

        public StringProperty statusProp(){
            return status;
        }

        public String getAssigned() {
            return assigned.get();
        }

        public String getAssignee() {
            return assignee;
        }

        public String getStatus() {
            return status.get();
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
            this.assigned = new SimpleStringProperty(sr.get("EMPLOYEEID"));
            this.assignee = sr.get("FIRSTNAME") + " " + sr.get("LASTNAME");
            this.status = new SimpleStringProperty(sr.get("STATUS"));
            this.serviceRequest = sr.get("REQUESTTYPE");
            this.location = sr.get("LOCATIONID");
            this.requestID = sr.get("REQUESTID");
        }
    }

    /**
     * Represents each COVID Survey taken in the COVID Table
     */
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
        private StringProperty isCovidLikely;
        private StringProperty entryApproved;

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
                this.isCovidLikely = new SimpleStringProperty(db.getUserByUsername(survey.get("USERNAME")).get("COVIDLIKELY"));
                this.entryApproved = new SimpleStringProperty(db.getUserByUsername(survey.get("USERNAME")).get("ENTRYAPPROVED"));
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

        public StringProperty isCovidLikelyProp() {
            return isCovidLikely;
        }

        public String getIsCovidLikely() {
            return isCovidLikely.get();
        }

        public void setIsCovidLikely(String isCovidLikely) {
            this.isCovidLikelyProp().set(isCovidLikely);
        }

        public String getEntryApproved() {
            return entryApproved.get();
        }

        public StringProperty entryApprovedProp() {
            return entryApproved;
        }

        public void setEntryApproved(String entryApproved) {
            this.entryApprovedProp().set(entryApproved);
        }
    }

}
