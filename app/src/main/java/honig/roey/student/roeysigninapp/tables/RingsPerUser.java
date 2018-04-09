package honig.roey.student.roeysigninapp.tables;

import java.util.ArrayList;

public class RingsPerUser {
    private ArrayList<String> userRings;

    public RingsPerUser(ArrayList<String> userRings) {
        this.userRings=userRings;
    }


    public ArrayList<String> getUserRings() {
        return userRings;
    }
}
