
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class main_ass6 {

    public static int nNodes;
    public static int truckCapacity;
    public static Node[] nodes;
    public static LinkedList<Route> routes;
    public static double[] sumGarbage;
    public final static double MAX_NO_IMPROVEMENT = 696;
    public final static double VEHICLE_SPEED = 50;
    public static int MAX_STOPS;
    public static Random rng;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Path filePath = Paths.get(args[0]);
        Scanner sc = new Scanner(System.in);
        try {
            sc = new Scanner(filePath);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        String line = sc.nextLine();
        String[] pLine = line.split(",");
        sumGarbage = new double[4];
        rng = new Random();

        //////////////////////////////////////////////////// 
        nNodes = Integer.parseInt(pLine[0]);
        truckCapacity = Integer.parseInt(pLine[1]);
        nodes = new Node[nNodes + 1];
        MAX_STOPS = nNodes + 1;
        routes = new LinkedList<>();
        sumGarbage[1] = 0;
        sumGarbage[2] = 0;
        sumGarbage[3] = 0;
        ////////////////////////////////////////////////////

        for (int i = 1; i <= nNodes; i++) {
            line = sc.nextLine();
            pLine = line.split(",");
            nodes[i] = new Node(Integer.parseInt(pLine[0]), Double.parseDouble(pLine[1]), Double.parseDouble(pLine[2]), Double.parseDouble(pLine[3]), Double.parseDouble(pLine[4]), Double.parseDouble(pLine[5]));
            sumGarbage[1] += nodes[i].currGarb[1];
            sumGarbage[2] += nodes[i].currGarb[2];
            sumGarbage[3] += nodes[i].currGarb[3];
        }
        while (sc.hasNext()) {
            line = sc.nextLine();
            pLine = line.split(",");
            Route nRou = new Route(Integer.parseInt(pLine[0]), Integer.parseInt(pLine[1]), Double.parseDouble(pLine[2]), Integer.parseInt(pLine[3]), Double.parseDouble(pLine[4]));
            routes.add(nRou);
            nodes[nRou.Start].addRoute(nRou);
            nodes[nRou.End].addRoute(nRou);
        }
        ///////////////////////////////////////////////////// 
        ////First garbage type////
        long nTripsType1 = Math.round(Math.ceil(sumGarbage[1] / (double) truckCapacity));
        long nTripsType2 = Math.round(Math.ceil(sumGarbage[2] / (double) truckCapacity));
        long nTripsType3 = Math.round(Math.ceil(sumGarbage[3] / (double) truckCapacity));
        Solver G1 = new Solver(nTripsType1, 1);
        LinkedList<Vehicle> S1 = G1.solveAntColony(1, 100000);
        Solver G2 = new Solver(nTripsType2, 2);
        LinkedList<Vehicle> S2 = G2.solveAntColony(1, 100000);
        Solver G3 = new Solver(nTripsType3, 3);
        LinkedList<Vehicle> S3 = G3.solveAntColony(1, 100000);

        printSolution(S1, 1);
        printSolution(S2, 2);
        printSolution(S3, 3);
        
        System.out.println("\nTotal cost: "+ Double.toString(G1.bestSolCost + G2.bestSolCost + G3.bestSolCost ));

    }

    public static void hardResetPheromones() {
        Iterator<Route> routeIte = routes.iterator();
        while (routeIte.hasNext()) {
            Route r = routeIte.next();
            r.resetPheromones();
        }
    }

    public static void printSolution(LinkedList<Vehicle> sol, int gType) {
        for (Vehicle v : sol) {
            StringBuffer sb = new StringBuffer();
            sb.append(Integer.toString(gType) + "," + v.pathToString());
            System.out.println(sb.toString());
        }
    }
}
