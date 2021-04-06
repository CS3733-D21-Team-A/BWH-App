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

    String nodeFileName = "src/main/resources/edu/wpi/aquamarine_axolotls/L1Nodes.csv";
    String edgeFileName = "src/main/resources/edu/wpi/aquamarine_axolotls/L1Edges.csv";

    List<Node> nodes = new CsvToBeanBuilder(new FileReader(nodeFileName)).withType(Node.class).build().parse();
    List<Edge> edges = new CsvToBeanBuilder(new FileReader(edgeFileName)).withType(Edge.class).build().parse();

    //nodes.forEach(System.out::println);
    //edges.forEach(System.out::println);

    FileDataReader filedataReader = new FileDataReader();
    Edge newEdge = new Edge();
    Node newNode = new Node();
    SearchAlgorithm newSearch = new SearchAlgorithm();
    filedataReader.setup();
    //List<Node> connectedNode = Node.getConnected(newNode.getNode("CDEPT003L1",nodes), edges, nodes);

    //System.out.println(connectedNode);

    Node start = newNode.getNode("CREST004L1",nodes);
    Node goal = newNode.getNode("CHALL004L1",nodes);
    List<Node> path = newSearch.getPath(edges,nodes,start,goal);
    System.out.println(path);

  }
}
