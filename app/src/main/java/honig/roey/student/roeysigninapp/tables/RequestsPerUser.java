package honig.roey.student.roeysigninapp.tables;

import java.util.ArrayList;

public class RequestsPerUser {
    private ArrayList<Request> userRequest;

    // constractor
    public RequestsPerUser(ArrayList<Request> userRequest) {
        this.userRequest = userRequest;
    }

    // Getter
    public ArrayList<Request> getUserRequest() {
        return userRequest;
    }

}
