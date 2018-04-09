package honig.roey.student.roeysigninapp.tables;

import java.util.ArrayList;

public class RingsPerUser {
    private int numOfRings;
    private ArrayList<String> userRings;

    public RingsPerUser(int numOfRings, ArrayList<String> userRings) {
        this.numOfRings = numOfRings;
        this.userRings = userRings;
    }

    public int getNumOfRings() {
        return numOfRings;
    }

    public ArrayList<String> getUserRings() {
        return userRings;
    }
}
