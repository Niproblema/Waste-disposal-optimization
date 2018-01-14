
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Solver {

    public final static double pheromonStrength = 10000;
    public final static double newPheromonShare = 0.2;
    public final static double pheromonExpiryTime = 1000; 

    public Vehicle[] solution, bestSol;
    public long nRuns;
    public int garbIndex;
    Random rand;

    public Solver(long nRuns, int garbIndex) {
        solution = new Vehicle[(int) nRuns];
        this.nRuns = nRuns;
        this.garbIndex = garbIndex;
        rand = new Random();
    }

    public LinkedList<Vehicle> solveAntColony(int startId, int maxIterations) {
        confirmAndResetCurrGarb();
        main_ass6.resetAllPheromonePaths();
        int noImpoveCounter = 0;
        double bestSolValue = Double.MAX_VALUE;
        LinkedList<Vehicle> optiSol = new LinkedList();
        for (int iterationCounter = 0; iterationCounter <= maxIterations && noImpoveCounter <= main_ass6.MAX_NO_IMPROVEMENT; iterationCounter++) {
            confirmAndResetCurrGarb();
            if (iterationCounter % 1000 == 0) {
                System.out.println("AntColony ite:" + iterationCounter + " Best sol:" + Double.toString(bestSolValue));
            }
            LinkedList<Vehicle> newSol = new LinkedList<>();
            for (int vechAmount = 0; vechAmount < 2 * nRuns; vechAmount++) {

                Vehicle newAnt = new Vehicle();
                boolean passTest = false;
                int noImprovementCounter2 = 0;
                while (!passTest && noImprovementCounter2 <= main_ass6.MAX_NO_IMPROVEMENT) {
                    Node[] state_before = main_ass6.nodes.clone();
                    if (main_ass6.nodes[startId].startAnt(newAnt, garbIndex)) {
                        passTest = true;
                        newSol.add(newAnt);
                        noImprovementCounter2 = 0;
                        updatePheromones(newAnt);
                    } else {
                        main_ass6.nodes = state_before;
                        noImprovementCounter2++;
                    }
                }
            }
            if (evaluateSolution(newSol)) {
                double cost = getSolutionCost(newSol);
                if (cost < bestSolValue) {
                    bestSolValue = cost;
                    optiSol = newSol;
                    noImpoveCounter = 0;
                } else {
                    noImpoveCounter++;
                }
            }

        }
        return optiSol;
    }

    public Vehicle[] solveTabu(int startId) {
        boolean keepRunning = true;
        for (int i = 1; i <= nRuns; i++) {
            solution[i].addNode(main_ass6.nodes[startId]);
        }
        // Find original solution;
        int[] permutation = new int[main_ass6.nNodes + 2];
        permutation[1] = 1;
        permutation[main_ass6.nNodes + 1] = 1;
        for (int i = 2; i <= main_ass6.nNodes; i++) {
            permutation[i] = i;
        }
        do {
            for (int i = 2; i <= main_ass6.nNodes; i++) {
                int rng = (int) (Math.random() * main_ass6.nNodes + 1);
                int tem = permutation[rng];
                permutation[rng] = permutation[i];
                permutation[i] = tem;
            }
        } while (!evaluateSolution(solution));
        //
        bestSol = solution.clone();

        int iteration = 0;
        while (keepRunning) {
            iteration++;
            for (int k = 0; k < nRuns; k++) {
                LinkedList<Node> pathSoFar = solution[k].path;

            }
        }
        return bestSol;
    }

    public boolean evaluatePath(Vehicle solPath) {
        confirmAndResetCurrGarb();
        double cost = 10;
        double load = 0;
        double distance = 0;
        double time = 0;
        LinkedList<Node> path = solPath.path;
        if (path.getFirst() != path.getLast() || path.size() <= 1) {
            return false;
        } else {

            Iterator<Node> itee = path.iterator();
            Node prev = itee.next();
            while (itee.hasNext()) {
                Node dis = itee.next();

                LinkedList<Route> ll = prev.outRoutes[dis.id];
                boolean rtn = false;
                for (Route r : ll) {
                    if (r.Capacity >= load && rtn == false) {
                        rtn = true;
                        distance += r.Distance;
                        solPath.routes.add(r);
                    }
                }
                if (!rtn) {
                    return false;
                }

                if (dis.id == 1) {
                    time += 30;
                } else {
                    double spaceLeft = main_ass6.truckCapacity - load;
                    if (spaceLeft > dis.currGarb[garbIndex]) {
                        load += dis.currGarb[garbIndex];
                        dis.currGarb[garbIndex] = 0;
                    } else {
                        dis.currGarb[garbIndex] -= spaceLeft;
                        load += spaceLeft;
                    }
                    time += 12;

                }
                prev = dis;
            }
            cost += distance * 0.1 + (time > 8 * 60 ? 20 * time : 10 * time);
        }
        solPath.cost = cost;
        solPath.totalDistance = distance;
        solPath.totalTime = time;
        return true;
    }

    public boolean evaluateSolution(Vehicle[] sol) {
        for (Vehicle v : sol) {
            if (!evaluatePath(v)) {
                return false;
            }
        }
        return confirmAndResetCurrGarb();
    }

    public boolean evaluateSolution(LinkedList<Vehicle> sol) {
        for (Vehicle v : sol) {
            if (!evaluatePath(v)) {
                return false;
            }
        }
        return confirmAndResetCurrGarb();
    }

    public boolean confirmAndResetCurrGarb() {
        boolean rtn = true;
        for (int i = 1; i <= main_ass6.nNodes; i++) {
            if (main_ass6.nodes[i].currGarb[garbIndex] != 0) {
                rtn = false;
                main_ass6.nodes[i].currGarb[garbIndex] = main_ass6.nodes[i].originalGarb[garbIndex];
            }
        }
        return rtn;
    }

    private double getSolutionCost(LinkedList<Vehicle> newSol) {
        double rtn = 0;
        for (Vehicle v : newSol) {
            rtn += v.cost;
        }
        return rtn;
    }

    private void updatePheromones(Vehicle newAnt) {
        double tTime = newAnt.totalTime;

        Iterator<Route> itee = newAnt.routes.iterator();
        while (itee.hasNext()) {
            Route selectedRoute = itee.next();
            selectedRoute.curr_pheromones = (1 - newPheromonShare) * selectedRoute.curr_pheromones + newPheromonShare * (pheromonStrength);
        }

        LinkedList<Route> allRoutes = main_ass6.routes;
        for (Route r : allRoutes) {
            r.curr_pheromones =  Math.max(1, r.curr_pheromones*( 1-(tTime/pheromonExpiryTime)));
        }

    }
}
