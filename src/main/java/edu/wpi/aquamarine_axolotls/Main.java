package edu.wpi.aquamarine_axolotls;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import edu.wpi.aquamarine_axolotls.db.DatabaseController;
import edu.wpi.aquamarine_axolotls.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.pathplanning.SearchAlgorithm;

public class Main {
  public static void main(String[] args) throws IOException{
    //Aapp.launch(Aapp.class, args);
    Adb.main(args);

    DatabaseController dbControl = new DatabaseController();

    try {
      List<Map<String, String>> nodeMap = dbControl.getNodes();
      List<Map<String, String>> edgeMap = dbControl.getEdges();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }

    //Initialize the search controller
    SearchAlgorithm newSearch = new SearchAlgorithm();

    //List<Node> connectedNode = Node.getConnected(newNode.getNode("CDEPT003L1",nodes), edges, nodes);

    String start = "aPARK016GG";
    String goal = "aPARK007GG";

    //Actually run the function to get the path from start to end
    List<Node> path = newSearch.getPath(start,goal);
    System.out.println(path);
  }
}
