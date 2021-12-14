package cs451;

import java.util.Arrays;
import java.util.List;

public class VectorClock {

    private final int [] clock;

    public VectorClock(int numberOfDependencies){
        clock = new int [numberOfDependencies];
        Arrays.fill(clock, 0);
    }

    public void increment(int senderId){
        clock[senderId - 1] ++;
    }

    public int get(int senderId){
        return clock[senderId - 1];
    }

    public void set(int senderId, int value){
        clock[senderId - 1] = value;
    }

    /**
     * @param other
     * @param dependencies
     * @return
     */
    public boolean smallerThan(VectorClock other, int [] dependencies){
        for(var hostId : dependencies){
            if(this.get(hostId) >= other.get(hostId)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VectorClock that = (VectorClock) o;
        return Arrays.equals(clock, that.clock);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(clock);
    }

    @Override
    public String toString(){
        return Arrays.toString(clock);
    }
}
