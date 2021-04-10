package edu.wpi.aquamarine_axolotls;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.views.pathplanning.Edge;
import edu.wpi.aquamarine_axolotls.views.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.views.pathplanning.SearchAlgorithm;


import java.io.IOException;
public class Main {
  public static void main(String[] args) throws IOException{
    //Aapp.launch(Aapp.class, args);
    Adb.main(args);

    //Declare file paths for node and edge CSVs
    String nodeFileName = "src/main/resources/edu/wpi/aquamarine_axolotls/MapAnodes.csv";
    String edgeFileName = "src/main/resources/edu/wpi/aquamarine_axolotls/MapAedges.csv";

    //Setup node and edge lists from the CSV files
    //List<Node> nodes = new CsvToBeanBuilder(new FileReader(nodeFileName)).withType(Node.class).build().parse();
    //List<Edge> edges = new CsvToBeanBuilder(new FileReader(edgeFileName)).withType(Edge.class).build().parse();

    DatabaseController dbControl = new DatabaseController();

    List<Map<String, String>> nodeMap = dbControl.getNodes();
    List<Map<String, String>> edgeMap = dbControl.getEdges();

    //System.out.println(nodes);

    //Initialize the reader to get the data
    //Edge newEdge = new Edge();
    //Node newNode = new Node();

    //Initialize the search controller
    SearchAlgorithm newSearch = new SearchAlgorithm(dbControl.getNodes(), dbControl.getEdges());

    //List<Node> connectedNode = Node.getConnected(newNode.getNode("CDEPT003L1",nodes), edges, nodes);

    //Get the start and end nodes from the list of nodes
    //Node start = newNode.getNode("aPARK016GG",nodes);
    //Node goal = newNode.getNode("aPARK007GG",nodes);

    Node start = newSearch.getNode("aPARK016GG");
    Node goal = newSearch.getNode("aPARK007GG");

    //Actually run the function to get the path from start to end
    List<Node> path = newSearch.getPath(start,goal);
    System.out.println(path);
  }
}
