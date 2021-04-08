package edu.wpi.aquamarine_axolotls;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import com.opencsv.bean.CsvToBeanBuilder;
import edu.wpi.aquamarine_axolotls.views.pathplanning.Edge;
import edu.wpi.aquamarine_axolotls.views.pathplanning.FileDataReader;
import edu.wpi.aquamarine_axolotls.views.pathplanning.Node;
import edu.wpi.aquamarine_axolotls.views.pathplanning.SearchAlgorithm;


public class Main {

  public static void main(String[] args) throws IOException{
    //Aapp.launch(Aapp.class, args);

    //Declare file paths for node and edge CSVs
    String nodeFileName = "src/main/resources/edu/wpi/aquamarine_axolotls/L1Nodes.csv";
    String edgeFileName = "src/main/resources/edu/wpi/aquamarine_axolotls/L1Edges.csv";

    //Setup node and edge lists from the CSV files
    List<Node> nodes = new CsvToBeanBuilder(new FileReader(nodeFileName)).withType(Node.class).build().parse();
    List<Edge> edges = new CsvToBeanBuilder(new FileReader(edgeFileName)).withType(Edge.class).build().parse();

    //System.out.println(nodes);

    //Initialize the reader to get the data
    FileDataReader filedataReader = new FileDataReader();
    Edge newEdge = new Edge();
    Node newNode = new Node();

    //Initialize the search controller
    SearchAlgorithm newSearch = new SearchAlgorithm();
    filedataReader.setup(); //Setup currently doesn't do anything

    //List<Node> connectedNode = Node.getConnected(newNode.getNode("CDEPT003L1",nodes), edges, nodes);

    //System.out.println(connectedNode);

    //Get the start and end nodes from the list of nodes
    Node start = newNode.getNode("CLABS003L1",nodes);
    Node goal = newNode.getNode("WELEV00KL1",nodes);

    //Actually run the function to get the path from start to end
    List<Node> path = newSearch.getPath(edges,nodes,start,goal);
    System.out.println(path);
  }
}
