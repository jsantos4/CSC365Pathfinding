package YelpDataPathfinding;

import java.io.*;
import java.util.*;
import javafx.collections.*;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static java.lang.Math.abs;

public class Main extends Application {

    //Buttons-----------------------------------------
    private Button search = new Button();
    private Button findPath = new Button();

    //List views--------------------------------------
    private ListView businessMenu;
    private ListView spanningTree;

    //Graph Visualization-----------------------------
    private NumberAxis xAxis = new NumberAxis();
    private NumberAxis yAxis = new NumberAxis();
    private LineChart<?, ?> pathGraph = new LineChart<>(xAxis, yAxis);

    //Generic objects---------------------------------
    private Graph graph = new Graph();
    private ArrayList<String> businesses = new ArrayList<>();
    private ArrayList<String> sTree;
    private ArrayList<Node> nodes = new ArrayList<>();
    private int i = 0;
    private String dir;

    public static void main(String[] args) {
        launch(args);
    }

    //Sets second list view to minimum spanning tree of selected business
    private void setSearch() {
        String selectedBusiness = businessMenu.getSelectionModel().getSelectedItem().toString();
        for (Node node : nodes) {
            if (node.business.toString().equals(selectedBusiness)) {
                i = node.id;
            }
        }
        sTree = graph.findSpanningTree(nodes.get(i));
        ObservableList<String> list = FXCollections.observableArrayList(sTree);
        spanningTree.setItems(list);
    }

    //Displays shortest path between two businesses as well as surrounding businesses in a line chart
    private void setFindPathGraph() {
        XYChart.Series pathSeries = new XYChart.Series();
        pathSeries.setName("Businesses in path");
        String selectedBusiness = businessMenu.getSelectionModel().getSelectedItem().toString();

        pathGraph.getData().clear();

        for (Node node : nodes) {
            if (node.business.toString().equals(selectedBusiness)) {
                i = node.id;
            }
        }

        ArrayList<XYChart.Series> streeSeries = new ArrayList<>();
        String destination = spanningTree.getSelectionModel().getSelectedItem().toString();

        double[] graphSize = new double[4];
        graphSize[0] = Double.MAX_VALUE;
        graphSize[1] = Double.MIN_VALUE;
        graphSize[2] = Double.MAX_VALUE;
        graphSize[3] = Double.MIN_VALUE;

        for (String node : sTree) {
            for (Node node1 : nodes) {
                if (node.equals(node1.business.toString())) {
                    XYChart.Series temp = new XYChart.Series();
                    temp.getData().add(new XYChart.Data(node1.business.getLongitude(), node1.business.getLatitude()));
                    streeSeries.add(temp);
                }
            }
        }


        int j = 0;
        for (Node node : nodes) {
            if (node.business.toString().equals(destination)) {
                j = node.id;
            }
        }
        LinkedList<Node> path = graph.dijkstra(nodes.get(i), nodes.get(j));


        for (Node node : path) {
            pathSeries.getData().add(new XYChart.Data(node.business.getLongitude(), node.business.getLatitude()));

            if (node.business.getLongitude() < graphSize[0]) {
                graphSize[0] = node.business.getLongitude() - 0.2;
            }
            if (abs(node.business.getLongitude()) > graphSize[1]) {
                graphSize[1] = node.business.getLongitude() + 0.2;
            }
            if (node.business.getLatitude() < graphSize[2]) {
                graphSize[2] = node.business.getLatitude() - 0.2;
            }
            if (node.business.getLatitude() > graphSize[3]) {
                graphSize[3] = node.business.getLatitude() + 0.2;
            }
        }

        xAxis.setLowerBound(graphSize[0]);
        xAxis.setUpperBound(graphSize[1]);
        yAxis.setLowerBound(graphSize[2]);
        yAxis.setUpperBound(graphSize[3]);


        for (XYChart.Series series : streeSeries) {
            pathGraph.getData().add(series);
        }
        pathGraph.getData().add(pathSeries);

    }


    @Override
    public void start(Stage stage) {


        stage.setTitle("YelpDataComparing");

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);


        //Parse json
        try {
            nodes = Parser.parse(dir, businesses); //path = directory path of json file
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Initialize graph
        graph.init(nodes);

        //Put array list of businesses into menu list
        businessMenu = new javafx.scene.control.ListView<String>();
        ObservableList<String> list = FXCollections.observableArrayList(businesses);
        businessMenu.setItems(list);
        search.setText("Search");
        findPath.setText("Find Path");

        //create new list view for minimum spanning tree
        spanningTree = new javafx.scene.control.ListView<String>();

        //call method to select business in menu on button press
        search.setOnAction(event -> setSearch());
        findPath.setOnAction(event -> setFindPathGraph());


        //display gui
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20, 20, 20, 20));
        layout.getChildren().addAll(businessMenu, spanningTree, search, pathGraph, findPath);
        Scene scene = new Scene(layout, 1200, 900  );
        stage.setScene(scene);
        stage.show();



    }
}