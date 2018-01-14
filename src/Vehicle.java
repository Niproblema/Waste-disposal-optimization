
import java.util.HashMap;
import java.util.LinkedList;

public class Vehicle {

    public double load;
    public int loc;
    public LinkedList<Node> path;
    public LinkedList<Route> routes;
    double cost;
    public double totalDistance;
    public double totalTime;

    public Vehicle() {
        load = 0;
        loc = 0;
        path = new LinkedList<>();
        routes = new LinkedList();
        cost = 0;
        totalDistance = (double)0;
        totalTime = (double)10;
    }

    public void addNode(Node n) {
        path.add(n);
        loc = n.id;
    }

    public void undoGarboCaused() {
        
    }
    
    public String pathToString(){
        StringBuffer sb = new StringBuffer();
        for(Node n:path){
            sb.append(n.id + " ");
        }        
        return sb.toString();
    }

}
