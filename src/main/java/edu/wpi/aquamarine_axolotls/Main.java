package edu.wpi.aquamarine_axolotls;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import com.opencsv.bean.CsvToBeanBuilder;
import edu.wpi.aquamarine_axolotls.views.pathplanning.Edge;
import edu.wpi.aquamarine_axolotls.views.pathplanning.FileDataReader;
import edu.wpi.aquamarine_axolotls.views.pathplanning.Node;


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
    filedataReader.setup();
    List<Node> connectedNode = Edge.getConnected("GHALL006L1", edges, nodes);

    System.out.println(connectedNode);

  }
}
