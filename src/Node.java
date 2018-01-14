
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Node {

    public int id;
    public double x, y;

    public double[] currGarb, originalGarb;

    public LinkedList<Route>[] inRoutes, outRoutes;
    public LinkedList<Route> allOutRoutes, allInRoutes;

    public Node(int id, double x, double y, double orgGarbo, double plasGarbo, double paperGarbo) {
        this.id = id;
        this.x = x;
        this.y = y;
        currGarb = new double[4];
        originalGarb = new double[4];
        currGarb[1] = orgGarbo;
        currGarb[2] = plasGarbo;
        currGarb[3] = paperGarbo;
        originalGarb[1] = orgGarbo;
        originalGarb[2] = plasGarbo;
        originalGarb[3] = paperGarbo;

        inRoutes = new LinkedList[main_ass6.nNodes + 1];
        outRoutes = new LinkedList[main_ass6.nNodes + 1];
        for (int l = 0; l <= main_ass6.nNodes; l++) {
            inRoutes[l] = new LinkedList<>();
            outRoutes[l] = new LinkedList<>();
        }
        allOutRoutes = new LinkedList<>();
        allInRoutes = new LinkedList<>();
    }

    public void addRoute(Route nRou) {
        if (nRou.Bidirectional) {
            allInRoutes.add(nRou);
            allOutRoutes.add(nRou);
            inRoutes[nRou.getStart(id)].add(nRou);
            outRoutes[nRou.getEnd(id)].add(nRou);
            sortIn(nRou.Start);
            sortOut(nRou.End);
        } else if (nRou.Start == id) {
            outRoutes[nRou.End].add(nRou);
            allOutRoutes.add(nRou);
            sortOut(nRou.End);
        } else if (nRou.End == id) {
            allInRoutes.add(nRou);
            inRoutes[nRou.Start].add(nRou);
            sortIn(nRou.Start);
        }
    }

    private void sortIn(int i) {
        Collections.sort(inRoutes[i], new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o1.Distance == o2.Distance ? 0 : (o1.Distance < o2.Distance ? -1 : 1);
            }
        });
    }

    private void sortOut(int i) {
        Collections.sort(outRoutes[i], new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                return o1.Distance == o2.Distance ? 0 : (o1.Distance < o2.Distance ? -1 : 1);
            }
        });
    }

    public boolean startAnt(Vehicle v, int garbIndex) {
        v.addNode(this);
        v.totalTime += 12;
        double spaceLeft = main_ass6.truckCapacity - v.load;
        if (spaceLeft > this.currGarb[garbIndex]) {
            v.load += this.currGarb[garbIndex];
            this.currGarb[garbIndex] = 0;
        } else {
            this.currGarb[garbIndex] -= spaceLeft;
            v.load += spaceLeft;
        }
        if (v.path.size() > main_ass6.MAX_STOPS) {
            return false;
        }
        if (v.loc == 1 && v.path.size() != 1) {
            return v.load != 0;
        }

        double sumPheromones = 0;
        for (Route r : allOutRoutes) {
            sumPheromones += r.curr_pheromones;
        }
        if (sumPheromones == 0) {
            int select = main_ass6.rng.nextInt(allOutRoutes.size());
            return main_ass6.nodes[allOutRoutes.get(select).getEnd(id)].startAnt(v, garbIndex);
        } else {
            while (true) {
                for (Route r : allOutRoutes) {
                    double chanceLimit = (r.curr_pheromones / sumPheromones) * 1000;
                    double rollDice = main_ass6.rng.nextDouble() * 1000;
                    if (rollDice <= chanceLimit) {
                        v.totalTime += r.Distance / main_ass6.VEHICLE_SPEED;
                        v.routes.add(r);
                        return main_ass6.nodes[r.getEnd(id)].startAnt(v, garbIndex);
                    }
                }
            }
        }
    }

}
