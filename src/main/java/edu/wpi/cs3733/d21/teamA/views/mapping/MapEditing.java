package edu.wpi.cs3733.d21.teamA.views.mapping;

import com.jfoenix.controls.JFXComboBox;
import edu.wpi.cs3733.d21.teamA.db.CSVHandler;
import edu.wpi.cs3733.d21.teamA.db.enums.TABLES;
import edu.wpi.cs3733.d21.teamA.pathplanning.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.wpi.cs3733.d21.teamA.Settings.PREFERENCES;
import static edu.wpi.cs3733.d21.teamA.Settings.SEARCH_ALGORITHM;

public class MapEditing extends GenericMap {

	@FXML
	private JFXComboBox<String> algoSelectBox;

	@FXML
	public RadioMenuItem newCSVButton;
	@FXML
	public RadioMenuItem mergeCSVButton;
	@FXML
	public RadioMenuItem exportButton;
	@FXML
	public Pane helpPane;

	ObservableList<String> searchAlgorithms = FXCollections.observableArrayList();
	CSVHandler csvHandler = new CSVHandler(db);

	private Map<String, String> anchor1 = new HashMap<>();
	private Map<String, String> anchor2 = new HashMap<>();

	MenuItem newNode;
	MenuItem alignVertical;
	MenuItem alignHorizontal;
	MenuItem addAnchorPoint;
	MenuItem alignSlope;
	MenuItem makeEdge;
	MenuItem deleteNodes;
	MenuItem deleteEdges;
	MenuItem deselect;

	@FXML
	public void initialize() throws SQLException, IOException {
		startUp();
		helpPane.setVisible(false);

		//====SET UP SEARCH ALGORITHM SELECTION====//

		if (searchAlgorithms.size() == 0) {
			searchAlgorithms.add("A Star");
			searchAlgorithms.add("Dijkstra");
			searchAlgorithms.add("Breadth First");
			searchAlgorithms.add("Depth First");
			searchAlgorithms.add("Best First");
			algoSelectBox.setItems(searchAlgorithms);
		}

		if (PREFERENCES.get(SEARCH_ALGORITHM, null) == null) {
			PREFERENCES.put(SEARCH_ALGORITHM, "A Star");
		}

		switch (PREFERENCES.get(SEARCH_ALGORITHM,null)) {
			case "A Star":
				algoSelectBox.getSelectionModel().select(0);
				break;
			case "Dijkstra":
				algoSelectBox.getSelectionModel().select(1);
				break;
			case "Breadth First":
				algoSelectBox.getSelectionModel().select(2);
				break;
			case "Depth First":
				algoSelectBox.getSelectionModel().select(3);
				break;
			case "Best First":
				algoSelectBox.getSelectionModel().select(4);
				break;
		}

		selectAlgorithm();



		//====CONTEXT MENU SETUP====//

		// TODO: Rewrite context menu stuff
		newNode = new MenuItem(("New Node Here"));
		alignVertical = new MenuItem(("Align Vertical"));
		alignHorizontal = new MenuItem(("Align Horizontal"));
		addAnchorPoint = new MenuItem(("Add Anchor Point"));
		alignSlope = new MenuItem("Align w/ Anchors 1 and 2");
		makeEdge = new MenuItem("Make Edge Between Selection");
		deleteNodes = new MenuItem("Delete Selected Nodes");
		deleteEdges = new MenuItem("Delete Selected Edges");
		deselect = new MenuItem(("Deselect All"));

		//Handler for the button that adds a new node
		newNode.setOnAction((ActionEvent e) -> {
			state = "New";
			currentID = "";
			editPopUp();
		});

		//Handler for the button that adds an anchor point
		addAnchorPoint.setOnAction((ActionEvent e) -> {
			Map<String, String> selectedNode = null;
			try {
				selectedNode = getNearestNode(contextMenuX, contextMenuY);
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
			if (selectedNode == null) return;
			if (anchor1.isEmpty()) {
				anchor1 = selectedNode;
				if (selectedNodesList.contains(selectedNode)) selectedNodesList.remove(selectedNode);
				addAnchorPoint.setText("Add 2nd Anchor Point");
			} else {
				anchor2 = selectedNode;
				if (selectedNodesList.contains(selectedNode)) selectedNodesList.remove(selectedNode);
				addAnchorPoint.setText("Change 2nd Anchor Point");
			}
			nodesOnImage.get(selectedNode.get("NODEID")).setFill(Color.PURPLE);
//            changeNodeColorOnImage(selectedNode.get("NODEID"), Color.PURPLE);

		});

		//Handler for the button that deselects everything
		deselect.setOnAction((ActionEvent e) -> {
			alignHorizontal.setVisible(false);
			alignVertical.setVisible(false);
			alignSlope.setVisible(false);
			makeEdge.setVisible(false);
			deleteNodes.setVisible(false);
			deleteEdges.setVisible(false);
			addAnchorPoint.setVisible(false);
			for (Map<String, String> node : selectedNodesList) {
				try {
					nodesOnImage.get(node.get("NODEID")).setFill(darkBlue);
					if (!db.getNode(node.get("NODEID")).get("FLOOR").equals(FLOOR)) {
						removeNodeOnImage(node.get("NODEID"));
					}
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
			}
			for (Map<String, String> edge : selectedEdgesList) {
				try {
					String edgeID = edge.get("EDGEID");
					linesOnImage.get(edgeID).setStroke(Color.BLACK);
					if (!db.getNode(db.getEdge(edgeID).get("STARTNODE")).get("FLOOR").equals(FLOOR)) {
						removeEdgeOnImage(edgeID);
					}
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
			}
			selectedNodesList.clear();
			selectedEdgesList.clear();
			addAnchorPoint.setText("Add Anchor Point");
			if (!anchor1.isEmpty()) changeNodeColorOnImage(anchor1.get("NODEID"), darkBlue);
			if (!anchor2.isEmpty()) changeNodeColorOnImage(anchor2.get("NODEID"), darkBlue);
			anchor1.clear();
			anchor2.clear();
			deselect.setVisible(false);
		});

		//Handler for the button that aligns the selection vertically
		alignVertical.setOnAction((ActionEvent e) -> {
			try {
				if (!selectedNodesList.isEmpty()) {
					alignNodesVertical(selectedNodesList, anchor1);
					deselect.fire();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}

		});

		//Handler for the button that aligns the selection horizontally
		alignHorizontal.setOnAction((ActionEvent e) -> {
			try {
				if (!selectedNodesList.isEmpty()) {
					alignNodesHorizontal(selectedNodesList, anchor1);
					deselect.fire();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		});

		//Handler for the button that aligns everything on a slope between two points
		alignSlope.setOnAction((ActionEvent e) -> {
			try {
				if (!selectedNodesList.isEmpty()) {
					alignNodesBetweenTwoNodes(selectedNodesList, anchor1, anchor2);
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		});

		//Handler for the button that makes a new edge
		makeEdge.setOnAction((ActionEvent e) -> {

			Map<String, String> edge = new HashMap<>();

			String startNodeID = selectedNodesList.get(0).get("NODEID");
			String endNodeID = selectedNodesList.get(1).get("NODEID");
			String edgeID = startNodeID + "_" + endNodeID;

			edge.put("STARTNODE", startNodeID);
			edge.put("ENDNODE", endNodeID);
			edge.put("EDGEID", edgeID);

			try {
				if (!db.edgeExists(edge.get("EDGEID")) &&
					!db.edgeExists(edge.get("ENDNODE") + "_" + edge.get("STARTNODE"))) {
					db.addEdge(edge);
				} else {
					popUp("ERROR", "That edge already exists.");
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}

			drawSingleEdge(startNodeID, endNodeID, Color.BLACK);
			deselect.fire();

		});

		deleteNodes.setOnAction((ActionEvent e) -> {
			for (Map<String, String> node : selectedNodesList) {
				try {
					String nodeID = node.get("NODEID");
					removeNodeOnImage(nodeID);
					for (Map<String, String> edge : db.getEdgesConnectedToNode(nodeID)) {
						selectedEdgesList.remove(edge);
						removeEdgeOnImage(edge.get("EDGEID"));
					}
					db.deleteNode(nodeID);
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
			}
			selectedNodesList.clear();
			//drawFloor(FLOOR);
		});

		deleteEdges.setOnAction((ActionEvent e) -> {
			for (Map<String, String> edge : selectedEdgesList) {
				try {
					String edgeID = edge.get("EDGEID");
					removeEdgeOnImage(edgeID);
					db.deleteEdge(edgeID);
				} catch (SQLException throwables) {
					throwables.printStackTrace();
				}
			}
			selectedEdgesList.clear();
			//drawFloor(FLOOR);
		});

		//Aligning and deselecting shouldn't be initially visible since they require a selection to work
		newNode.setVisible(false);
		addAnchorPoint.setVisible(false);
		alignHorizontal.setVisible(false);
		alignVertical.setVisible(false);
		alignSlope.setVisible(false);
		makeEdge.setVisible(false);
		deleteNodes.setVisible(false);
		deleteEdges.setVisible(false);
		deselect.setVisible(false);

		//Add everything to the context menu
		contextMenu.getItems().clear();
		contextMenu.getItems().addAll(newNode, addAnchorPoint, alignHorizontal, alignVertical, alignSlope, makeEdge, deleteNodes, deleteEdges, deselect);

		//Update the context menu when it's requested
		mapView.setOnContextMenuRequested(event -> {
			contextMenu.show(mapView, event.getScreenX(), event.getScreenY()); //Show the menu
			contextMenuX = event.getX(); //Update X and Y coords of contextmenu
			contextMenuY = event.getY();

			Map<String, String> nearestNode = null;
			try { //Get the nearest node to the context menu -- will be used in subsequent operatinos
				nearestNode = getNearestNode(contextMenuX, contextMenuY);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			newNode.setVisible(true);

			if (nearestNode != null) { //If there's nothing nearby, set add anchor point to be invisible
				addAnchorPoint.setVisible(true);
			} else {
				addAnchorPoint.setVisible(false);
			}

			if (!anchor1.isEmpty() && anchor2.isEmpty()) {
				alignHorizontal.setVisible(true);
			} else {
				alignHorizontal.setVisible(false);
			}

			if (!anchor1.isEmpty() && anchor2.isEmpty()) {
				alignVertical.setVisible(true);
			} else {
				alignVertical.setVisible(false);
			}

			if (!anchor1.isEmpty() && !anchor2.isEmpty()) {
				alignSlope.setVisible(true);
			} else {
				alignSlope.setVisible(false);
			}

			if (selectedNodesList.size() == 2) {
				makeEdge.setVisible(true);
			} else {
				makeEdge.setVisible(false);
			}

			if (!selectedNodesList.isEmpty()) {
				deleteNodes.setVisible(true);
			} else {
				deleteNodes.setVisible(false);
			}

			if (!selectedEdgesList.isEmpty()) {
				deleteEdges.setVisible(true);
			} else {
				deleteEdges.setVisible(false);
			}

			if (!selectedNodesList.isEmpty() || !anchor1.isEmpty() || !anchor2.isEmpty() || !selectedEdgesList.isEmpty()) { //If there's nothing selected, set deselect to be invisible
				deselect.setVisible(true);
			} else {
				deselect.setVisible(false);
			}

		});

		//Event handler for when the mouse is clicked and dragged, used for dragging nodes around
		mapView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
			if (nodeBeingDragged != null) { //If we've double-clicked and dragged, move the node
				changeNodeCoordinatesOnImage(nodeBeingDragged, event.getX(), event.getY());
				mapScrollPane.setPannable(false);
			}
		});

		//Event handler for when the mouse is released
		mapView.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
			if (nodeBeingDragged != null) { //If we were dragging a node, update the database with its new position
				try {
					Map<String, String> newCoords = new HashMap<>();
					newCoords.put("XCOORD", String.valueOf(Math.round(inverseXScale(event.getX()))));
					newCoords.put("YCOORD", String.valueOf(Math.round(inverseYScale(event.getY()))));
					db.editNode(nodeBeingDragged, newCoords);
					deselect.fire();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				updateEdgesConnectedToNode(nodeBeingDragged); //Visually update the edges connected to that node
				nodeBeingDragged = null; //We're no longer dragging anything
				mapScrollPane.setPannable(true);
			}
		});
	}


	public void setUpEdgeEventHandler(Line edge, String edgeID) {
		//Opening the popup menu
		edge.setOnMouseClicked((MouseEvent e) -> {

			try {
				if (selectedEdgesList.contains(db.getEdge(edgeID))) {
					selectedEdgesList.remove(db.getEdge(edgeID));
					edge.setStroke(Color.BLACK);
					linesOnImage.get(edgeID).setStroke(Color.BLACK);
					if (!db.getNode(db.getEdge(edgeID).get("STARTNODE")).get("FLOOR").equals(FLOOR)) {
						removeEdgeOnImage(edgeID);
					}
				} else {
					selectedEdgesList.add(db.getEdge(edgeID));
					edge.setStroke(yellow);
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
		});

		// Hover over edge to make it thicker
		edge.setOnMouseEntered((MouseEvent e) -> {
			edge.setStrokeWidth(5);
			edge.toBack();
		});

		//Moving mouse off edge will make it stop highlighting
		edge.setOnMouseExited((MouseEvent e) -> {
			edge.setStrokeWidth(magicNumber);
		});
	}


	/**
	 * Brings up the editing popup for users to change values of an edge/node
	 */
	public void editPopUp() {
		final Stage myDialog = new Stage();
		myDialog.initModality(Modality.APPLICATION_MODAL);
		myDialog.centerOnScreen();
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/edu/wpi/cs3733/d21/teamA/fxml/NodePopUp.fxml"));

			NodePopUp controller = new NodePopUp(this);
			loader.setController(controller);

			myDialog.setScene(new Scene(loader.load()));
			myDialog.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Switches to a given floor, then draws all nodes and edges on it
	 *
	 * @param floor Floor to move to
	 */
	@Override
	public void drawFloor(String floor) {
		changeFloorImage(floor);
		drawEdges();
		drawNodes(darkBlue);
        /*if(contextMenu.getItems().contains(deselect)) {
            ObservableList<MenuItem> items = contextMenu.getItems();
            items.get(items.indexOf(deselect)).fire(); // targets deselect
        }*/
	}

	public void drawNodes(Color colorOfNodes) {
		try {
			for (Map<String, String> node : db.getNodesByValue("FLOOR", FLOOR)) {
				drawSingleNode(node.get("NODEID"), colorOfNodes);
			}
			for (Map<String, String> node : selectedNodesList) {
				drawSingleNode(node.get("NODEID"), yellow);
			}
			if (!anchor1.isEmpty()) drawSingleNode(anchor1.get("NODEID"), Color.PURPLE);
			if (!anchor2.isEmpty()) drawSingleNode(anchor2.get("NODEID"), Color.PURPLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Draws all edges on the current floor, check for floor is in drawTwoNodesWithEdge
	 */
	public void drawEdges() {
		try {
			for (Map<String, String> edge : db.getEdges())
				drawSingleEdge(edge.get("STARTNODE"), edge.get("ENDNODE"), Color.BLACK);
			for (Map<String, String> edge : selectedEdgesList)
				drawSingleEdge(edge.get("STARTNODE"), edge.get("ENDNODE"), yellow);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Circle setEventHandler(Circle node, String nodeID) {
		node.setOnMousePressed((MouseEvent e) -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {
				if (e.getClickCount() == 2) nodeBeingDragged = nodeID;
			}
		});

		node.setOnMouseClicked((MouseEvent e) -> {
			if (e.getButton().equals(MouseButton.PRIMARY)) {

				//System.out.println(e.getClickCount());
				if (e.getClickCount() == 2) {
					if (e.isStillSincePress()) {
//                        node.setFill(yellow);
						System.out.println("Successfully clicked node");
						currentID = nodeID;
						state = "Edit";
						editPopUp();
					}
				}
				//Otherwise, single clicks will select/deselect nodes
				else {
					Circle currentCircle = nodesOnImage.get(nodeID);
					Map<String, String> nodeMap = new HashMap<String, String>();
					try {
						nodeMap = db.getNode(nodeID);
					} catch (SQLException throwables) {
						throwables.printStackTrace();
					}
					if (selectedNodesList.contains(nodeMap)) {
						selectedNodesList.remove(nodeMap);
						if (!nodeMap.get("FLOOR").equals(FLOOR)) {
							removeNodeOnImage(nodeID);
						}
						node.setFill(darkBlue);
					} else if (!anchor1.equals(nodeMap) && !anchor2.equals(nodeMap)) {
						selectedNodesList.add(nodeMap);
						currentCircle.setFill(yellow);
					}
				}
			}
		});

		node.setOnMouseEntered((MouseEvent e) -> node.setRadius(5));

		node.setOnMouseExited((MouseEvent e) -> node.setRadius(magicNumber));

		return node;
	}


	/**
	 * Sets the strategy to be used when pathfinding based on the current state of the algorithm selectio dropdown box
	 */
	@FXML
	public void selectAlgorithm() {
		String algo = algoSelectBox.getSelectionModel().getSelectedItem();
		PREFERENCES.put(SEARCH_ALGORITHM,algo);
		switch (algo) {
			case "A Star":
				SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new AStar());
				break;
			case "Dijkstra":
				SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new Dijkstra());
				break;
			case "Breadth First":
				SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new BreadthFirstSearch());
				break;
			case "Depth First":
				SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new DepthFirstSearch());
				break;
			case "Best First":
				SearchAlgorithmContext.getSearchAlgorithmContext().setContext(new BestFirstSearch());
				break;
		}
	}


	/**
	 * Aligns the current selection horizontally relative to a selected anchor point
	 *
	 * @param nodes       List of nodes to align
	 * @param anchorPoint Anchor point to align relative to
	 * @throws SQLException
	 */
	public void alignNodesHorizontal(List<Map<String, String>> nodes, Map<String, String> anchorPoint) throws SQLException {
		String anchorY = anchorPoint.get("YCOORD");
		updateNodeOnImage(anchorPoint.get("NODEID"));
		for (Map<String, String> node : nodes) {
			node.replace("YCOORD", anchorY); // meh it works CHANGE TO NEW NODE IF NOT WORKING
			db.editNode(node.get("NODEID"), node); // IF THIS ISNT WORKING YOU HAVE TO CHANGE TO new node
//            changeNodeCoordinatesOnImage(node.get("NODEID"), Integer.parseInt(node.get("XCOORD")), Integer.parseInt(anchorY));
//            changeNodeColorOnImage(node.get("NODEID"), darkBlue);
			updateNodeOnImage(node.get("NODEID"));
		}
		selectedNodesList.clear();
		anchor1.clear();
		drawFloor(FLOOR);
	}


	/**
	 * Aligns the current selection in a vertical line relative to a selected anchor point
	 *
	 * @param nodes       List of nodes to align
	 * @param anchorPoint Anchor point to align relative to
	 * @throws SQLException
	 */
	public void alignNodesVertical(List<Map<String, String>> nodes, Map<String, String> anchorPoint) throws SQLException {
		String anchorX = anchorPoint.get("XCOORD");
		updateNodeOnImage(anchorPoint.get("NODEID"));
		for (Map<String, String> node : nodes) {
			node.replace("XCOORD", anchorX);
			db.editNode(node.get("NODEID"), node);
			updateNodeOnImage(node.get("NODEID"));
		}

		selectedNodesList.clear();
		anchor1.clear();
		drawFloor(FLOOR);
	}


	/**
	 * Aligns the current selection in a line between two anchors
	 *
	 * @param nodes        Nodes to align
	 * @param anchorPoint1 First anchor
	 * @param anchorPoint2 Second anchor
	 * @throws SQLException
	 */
	//TODO : loss of accuarcy causing errors
	public void alignNodesBetweenTwoNodes(List<Map<String, String>> nodes, Map<String, String> anchorPoint1, Map<String, String> anchorPoint2) throws SQLException {

		double anchorX1 = Integer.parseInt(anchorPoint1.get("XCOORD"));
		double anchorY1 = Integer.parseInt(anchorPoint1.get("YCOORD"));
		double anchorX2 = Integer.parseInt(anchorPoint2.get("XCOORD"));
		double anchorY2 = Integer.parseInt(anchorPoint2.get("YCOORD"));

		double anchorSlope = (anchorX2 - anchorX1) / (anchorY2 - anchorY1);

		for (Map<String, String> node : nodes) {

			double originalX = Integer.parseInt(node.get("XCOORD"));
			double originalY = Integer.parseInt(node.get("YCOORD"));

			double newY = (originalY + Math.pow(anchorSlope, 2) * anchorY1 - anchorSlope * anchorX1 + anchorSlope * originalX) / (1 + Math.pow(anchorSlope, 2));
			double newX = (anchorSlope * newY + anchorX1 - anchorSlope * anchorY1);

			node.replace("XCOORD", String.valueOf((int) Math.round(newX)));
			node.replace("YCOORD", String.valueOf((int) Math.round(newY)));
			db.editNode(node.get("NODEID"), node);
			updateNodeOnImage(node.get("NODEID"));
		}
		selectedNodesList.clear();
		anchor1.clear();
		anchor2.clear();
		drawFloor(FLOOR);
	}


	/** CSV Stuff **/

	/**
	 * Overwrites current csv with the selected csv
	 */
	public void newCSV() { //still in the works
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(
			new FileChooser.ExtensionFilter("CSV Files", "*.csv")
		);
		File csv = fileChooser.showOpenDialog(algoSelectBox.getScene().getWindow());
		if (csv == null) return;

		TABLES table = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv)))) {
			List<String> columns = Arrays.asList(br.readLine().split(","));
			System.out.println(columns);
			if (columns.size() == 3) {
				boolean hasEdgeCols = true;
				List<String> edgeTabCols = db.getEdgeColumns();
				for (String col : columns) {
					if (!edgeTabCols.contains(col.toUpperCase())) {
						hasEdgeCols = false;
						break;
					}
				}
				if (hasEdgeCols) table = TABLES.EDGES;
				else {
					popUp("CSV Importing","Error: Malformed CSV. Import halted");
					return;
				}
			} else if (columns.size() == 8) {
				boolean hasNodeCols = true;
				List<String> nodeTabCols = db.getNodeColumns();
				System.out.println(nodeTabCols);
				for (String col : columns) {
					if (!nodeTabCols.contains(col.toUpperCase())) {
						System.out.println(col.toUpperCase());
						hasNodeCols = false;
						break;
					}
				}
				if (hasNodeCols) table = TABLES.NODES;
				else {
					popUp("CSV Importing","Error: Malformed CSV. Import halted");
					return;
				}
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return;
		}

		if (table == null) {
			popUp("CSV Importing","Error: Something went wrong. Import halted");
			return;
		}


		try {
			csvHandler.importCSV(csv, table, true);
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}

		try {
			initialize(); //REFRESH TABLE
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Merges the selected CSV together with the existing one
	 */
	public void mergeCSV() { //still in the works
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(
			new FileChooser.ExtensionFilter("CSV Files", "*.csv")
		);
		File csv = fileChooser.showOpenDialog(algoSelectBox.getScene().getWindow());
		if (csv == null) return;

		TABLES table = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csv)))) {
			List<String> columns = Arrays.asList(br.readLine().split(","));
			System.out.println(columns);
			if (columns.size() == 3) {
				boolean hasEdgeCols = true;
				List<String> edgeTabCols = db.getEdgeColumns();
				for (String col : columns) {
					if (!edgeTabCols.contains(col.toUpperCase())) {
						hasEdgeCols = false;
						break;
					}
				}
				if (hasEdgeCols) table = TABLES.EDGES;
				else {
					popUp("CSV Importing","Error: Malformed CSV. Import halted");
					return;
				}
			} else if (columns.size() == 8) {
				boolean hasNodeCols = true;
				List<String> nodeTabCols = db.getNodeColumns();
				System.out.println(nodeTabCols);
				for (String col : columns) {
					if (!nodeTabCols.contains(col.toUpperCase())) {
						System.out.println(col.toUpperCase());
						hasNodeCols = false;
						break;
					}
				}
				if (hasNodeCols) table = TABLES.NODES;
				else {
					popUp("CSV Importing","Error: Malformed CSV. Import halted");
					return;
				}
			}
		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return;
		}

		if (table == null) {
			popUp("CSV Importing","Error: Something went wrong. Import halted");
			return;
		}

		try {
			csvHandler.importCSV(csv, table, false);
		} catch (IOException | SQLException ie) {
			ie.printStackTrace();
		}

		try {
			initialize(); //REFRESH TABLE
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Downloads the current list of edges to a CSV
	 */
	public void exportCSV() { //still in the works
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(
			new FileChooser.ExtensionFilter("CSV Files", "*.csv")
		);

		File nodeCsv = fileChooser.showSaveDialog(algoSelectBox.getScene().getWindow());
		if (nodeCsv != null) {
			try {
				csvHandler.exportCSV(nodeCsv, TABLES.NODES);
			} catch (IOException | SQLException ie) {
				ie.printStackTrace();
			}
		}

		File edgeCsv = fileChooser.showSaveDialog(algoSelectBox.getScene().getWindow());
		if (edgeCsv != null) {
			try {
				csvHandler.exportCSV(edgeCsv, TABLES.EDGES);
			} catch (IOException | SQLException ie) {
				ie.printStackTrace();
			}
		}
	}

	public void helpButton() {
		helpPane.setVisible(true);
	}

	public void cancelHelp() {
		helpPane.setVisible(false);
	}

}