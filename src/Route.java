
public class Route {

    public int Start, End;
    public double Distance, Capacity;
    public boolean Bidirectional;

    public double curr_pheromones;

    public Route(int start, int end, double distance, int bi, double capacity) {
        this.Start = start;
        this.End = end;
        this.Distance = distance;
        this.Bidirectional = bi == 1 ? true : false;
        this.Capacity = capacity;
    }

    public void resetPheromones() {
        curr_pheromones = 1;
    }

    public int getEnd(int Start) {
        if (Bidirectional) {
            return Start == this.Start ? this.End : this.Start;
        } else {
            return End;
        }
    }

    public int getStart(int end) {
        if (Bidirectional) {
            return this.End == end ? this.Start : this.End;
        } else {
            return this.Start;
        }
    }

}
