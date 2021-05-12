package edu.wpi.cs3733.d21.teamA.views.servicerequests;

import com.jfoenix.controls.JFXTabPane;
import edu.wpi.cs3733.d21.teamA.db.DatabaseController;
import edu.wpi.cs3733.d21.teamA.db.*;
import edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST;
import edu.wpi.cs3733.d21.teamA.db.enums.STATUS;
import edu.wpi.cs3733.d21.teamA.db.enums.USERTYPE;
import edu.wpi.cs3733.d21.teamA.views.GenericPage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.util.*;

import static edu.wpi.cs3733.d21.teamA.Settings.*;
import static edu.wpi.cs3733.d21.teamA.db.DatabaseUtil.SERVICEREQUEST_NAMES;
import static edu.wpi.cs3733.d21.teamA.db.enums.SERVICEREQUEST.*;
import static edu.wpi.cs3733.d21.teamA.db.enums.USERTYPE.*;

public class EmployeeRequests extends GenericPage {

	@FXML
	private JFXTabPane tabs;

	private final DatabaseController db = DatabaseController.getInstance();

	private static final List<SERVICEREQUEST> SERVICE_REQUEST_INDEX = Arrays.asList(SERVICEREQUEST.values());

	private static final String COVID_ICON = "edu/wpi/cs3733/d21/teamA/img/iconsWWords/CovidSurveywWords.png";

	static Map<SERVICEREQUEST, String> icons = new HashMap<SERVICEREQUEST, String>() {{
		put(FLORAL_DELIVERY, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/FloralDeliverywWords.png");
		put(FOOD_DELIVERY, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/FoodDeliverywWords.png");
		put(GIFT_DELIVERY, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/GiiftDeliverywWords.png");
		put(LANGUAGE_INTERPRETER, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/LanguageInterpreterwWords.png");
		put(FACILITIES_MAINTENANCE, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/FacilitiesMaintenancewWords.png");
		put(LAUNDRY, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/LaundrywWords.png");
		put(SANITATION, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/SanitationwWords.png");
		put(EXTERNAL_TRANSPORT, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/ExternalTransportwWords.png");
		put(INTERNAL_TRANSPORT, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/InternalTransportwWords.png");
		put(MEDICINE_DELIVERY, "edu/wpi/cs3733/d21/teamA/img/iconsWWords/MedicineDeliverywWords.png");
	}};

	/**
	 * Initializes the page for each user type
	 */
	@FXML
	public void initialize() {
		try {
			ObservableList<String> names = FXCollections.observableArrayList();

			USERTYPE usertype = DatabaseUtil.USER_TYPE_NAMES.inverse().get(PREFERENCES.get(USER_TYPE, null));


			switch (usertype) {
				case ADMIN:
					for (Map<String, String> user : db.getEmployees()) {
						if (user.get("USERTYPE").equals("Employee")) {
							names.add(user.get("FIRSTNAME") + " " + user.get("LASTNAME"));
						}
					}
					break;
				case EMPLOYEE:
					Map<String, String> usr = db.getUserByUsername(PREFERENCES.get(USER_NAME, null));
					String name = usr.get("FIRSTNAME") + " " + usr.get("LASTNAME");
					names.add(name);
					break;
			}


			//COVID Status
			ObservableList<String> covidStatAndEntry = FXCollections.observableArrayList();
			covidStatAndEntry.add("true");
			covidStatAndEntry.add("false");

			//Service Request Status
			ObservableList<String> stats = FXCollections.observableArrayList(
				DatabaseUtil.STATUS_NAMES.get(STATUS.IN_PROGRESS),
				DatabaseUtil.STATUS_NAMES.get(STATUS.DONE),
				DatabaseUtil.STATUS_NAMES.get(STATUS.CANCELED)
			);

			if (usertype != PATIENT) {
				TableView<Map> covidSurveyTable = new TableView<>();
				covidSurveyTable.setPrefSize(950.0, 750.0);


				//ADD COVID LIKELY COLUMN
				TableColumn<Map, String> covLikelyColumn = new TableColumn<>("COVIDLIKELY");
				covLikelyColumn.setCellValueFactory(new MapValueFactory<>("COVIDLIKELY"));
				covLikelyColumn.setCellFactory(ComboBoxTableCell.forTableColumn(covidStatAndEntry));
				covLikelyColumn.setOnEditCommit(event -> {
					try {
						int index = covidSurveyTable.getSelectionModel().getFocusedIndex();

						String covLikely = event.getNewValue();
						Map<String, String> survMap = covidSurveyTable.getItems().get(index);
						survMap.put("COVIDLIKELY", covLikely);

						db.editUser(survMap.get("USERNAME"), Collections.singletonMap("COVIDLIKELY", covLikely));
					} catch (SQLException throwables) {
						throwables.printStackTrace();
					}
				});
				covidSurveyTable.getColumns().add(covLikelyColumn);


				// ADD ENTRY APPROVED COLUMN
				TableColumn<Map, String> entryColumn = new TableColumn<>("ENTRYAPPROVED");
				entryColumn.setCellValueFactory(new MapValueFactory<>("ENTRYAPPROVED"));
				entryColumn.setCellFactory(ComboBoxTableCell.forTableColumn(covidStatAndEntry));
				entryColumn.setOnEditCommit(event -> {
					try {
						int index = covidSurveyTable.getSelectionModel().getFocusedIndex();

						String approval = event.getNewValue();
						Map<String, String> survMap = covidSurveyTable.getItems().get(index);
						survMap.put("ENTRYAPPROVED", approval);

						db.editUser(survMap.get("USERNAME"), Collections.singletonMap("ENTRYAPPROVED", approval));
					} catch (SQLException throwables) {
						throwables.printStackTrace();
					}
				});
				covidSurveyTable.getColumns().add(entryColumn);


				//ADD COVID SURVEY COLUMNS
				List<String> survCols = db.getSurveyColumns();
				Collections.swap(survCols, survCols.indexOf("USERNAME"), 0); //move username to the front
				for (String col : survCols) {
					TableColumn<Map, String> tableColumn = new TableColumn<>(col);
					tableColumn.setCellValueFactory(new MapValueFactory<>(col));
					covidSurveyTable.getColumns().add(tableColumn);
				}
				covidSurveyTable.setEditable(true);

				//add covid survey tab
				Tab tab = new Tab("COVID Survey", covidSurveyTable);
				Image img = new Image(COVID_ICON);
				ImageView tabImg = new ImageView(img);
				tabImg.setFitWidth(256.0);
				tabImg.setPreserveRatio(true);
				tab.setGraphic(tabImg);

				tabs.getTabs().add(tab);
			}


			//CREATE SERVICE REQUEST TABS
			createTabs(names, stats);


			populateTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Populates the corresponding tables with requests and surveys
	 */
	public void populateTables() {
		try {
			switch (DatabaseUtil.USER_TYPE_NAMES.inverse().get(PREFERENCES.get(USER_TYPE, null))) {
				case ADMIN:
				case EMPLOYEE:
					for (SERVICEREQUEST servReq : SERVICEREQUEST.values()) {
						int index = SERVICE_REQUEST_INDEX.indexOf(servReq) + 1;
						TableView<Map> table = (TableView) tabs.getTabs().get(index).getContent();
						table.getItems().addAll(db.getServiceRequestsByType(servReq));
					}

					/*for (Map<String, String> req : db.getServiceRequests()) {
						int index = SERVICE_REQUEST_INDEX.indexOf(SERVICEREQUEST_NAMES.inverse().get(req.get("REQUESTTYPE"))) + 1;          // adds one to take into account of the COVID tab
						TableView<Map> table = (TableView) tabs.getTabs().get(index).getContent();
						table.getItems().add(req);
					}*/

					TableView<Map> covidSurveyTable = (TableView) tabs.getTabs().get(0).getContent();
					covidSurveyTable.getItems().clear();
					for (Map<String, String> survey : db.getSurveys()) {
						Map<String,String> user = db.getUserByUsername(survey.get("USERNAME"));
						survey.put("COVIDLIKELY",user.get("COVIDLIKELY"));
						survey.put("ENTRYAPPROVED",user.get("ENTRYAPPROVED"));
						covidSurveyTable.getItems().add(survey);
					}
					break;
				case PATIENT:
					for (SERVICEREQUEST servReq : SERVICEREQUEST.values()) {
						if (servReq == EXTERNAL_TRANSPORT || servReq == INTERNAL_TRANSPORT || servReq == MEDICINE_DELIVERY) continue; //skip these forms for patients
						int index = SERVICE_REQUEST_INDEX.indexOf(servReq);
						TableView<Map> table = (TableView) tabs.getTabs().get(index).getContent();
						List<Map<String,String>> requests = db.getServiceRequestsByType(servReq);
						List<Map> tableItems = table.getItems();
						for (Map<String,String> req : requests) {
							if (PREFERENCES.get(USER_NAME,null).equals(req.get("AUTHORID"))) {
								tableItems.add(req);
							}
						}
					}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates tabs and tables for each service request type
	 *
	 * @param names List of employee names to assign a service request to
	 * @param stats List of statuses for each service request
	 */
	public void createTabs(ObservableList<String> names, ObservableList<String> stats) throws SQLException {
		USERTYPE usertype = DatabaseUtil.USER_TYPE_NAMES.inverse().get(PREFERENCES.get(USER_TYPE, null));

		for (SERVICEREQUEST serReqType : SERVICE_REQUEST_INDEX) {
			if (usertype == PATIENT && (serReqType == EXTERNAL_TRANSPORT || serReqType == INTERNAL_TRANSPORT || serReqType == MEDICINE_DELIVERY)) continue; //skip these forms for patients

			TableView<Map> table = new TableView<>();
			table.setPrefSize(950.0, 750.0);

			List<String> reqCols = db.getServiceRequestColumns(serReqType);
			reqCols.remove("REQUESTID"); //there are two request id columns
			reqCols.remove("REQUESTTYPE"); //request type is redundant
			Collections.swap(reqCols, reqCols.indexOf("REQUESTID"),0); //move request id to the front
			Collections.swap(reqCols, reqCols.indexOf("AUTHORID"),1); //move author id to the front
			Collections.swap(reqCols, reqCols.indexOf("EMPLOYEEID"),2); //move assigned employee id to the front
			Collections.swap(reqCols, reqCols.indexOf("STATUS"),3); //move status to the front
			for (String col : reqCols) {
				TableColumn<Map, String> tableColumn = new TableColumn<>(col);
				tableColumn.setCellValueFactory(new MapValueFactory<>(col));
				if (col.equals("EMPLOYEEID")) {
					tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(names));
					tableColumn.setOnEditCommit(event -> {
						try {
							int index = table.getSelectionModel().getFocusedIndex();

							String employeeID = event.getNewValue();
							Map<String, String> reqMap = table.getItems().get(index);
							reqMap.put("EMPLOYEEID", employeeID);

							db.assignEmployee(reqMap.get("REQUESTID"), employeeID);
						} catch (SQLException throwables) {
							throwables.printStackTrace();
						}
					});
				} else if (col.equals("STATUS")) {
					tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(stats));
					tableColumn.setOnEditCommit(event -> {
						try {
							int index = table.getSelectionModel().getFocusedIndex();

							String status = event.getNewValue();
							Map<String, String> reqMap = table.getItems().get(index);
							reqMap.put("STATUS", status);

							db.changeStatus(reqMap.get("REQUESTID"), DatabaseUtil.STATUS_NAMES.inverse().get(status));
						} catch (SQLException throwables) {
							throwables.printStackTrace();
						}
					});
				}

				table.getColumns().add(tableColumn);
			}

			switch (usertype) {
				case PATIENT: // only display their service requests
					table.setEditable(false);
					break;
				case ADMIN:
				case EMPLOYEE:
					table.setEditable(true);
					break;
			}

			Tab tab = new Tab(SERVICEREQUEST_NAMES.get(serReqType), table);
			Image img = new Image(icons.get(serReqType));
			ImageView tabImg = new ImageView(img);
			tabImg.setFitWidth(256.0);
			tabImg.setPreserveRatio(true);
			tab.setGraphic(tabImg);

			tabs.getTabs().add(tab);
		}
	}
}
