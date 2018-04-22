package honig.roey.student.roeysigninapp.tables;

import android.os.Parcelable;

import java.util.ArrayList;

public class RequestsPerUser {
    private ArrayList<Request> userRequest;
    private ArrayList<Request> userInvites;
    private ArrayList<Request> userAproves;

    // constractor
    public RequestsPerUser(ArrayList<Request> userRequest, ArrayList<Request> userInvites, ArrayList<Request> userAproves) {

        this.userRequest = userRequest;
        this.userInvites = userInvites;
        this.userAproves = userAproves;
    }

    // Getter
    public ArrayList<Request> getUserRequest() {
        return userRequest;
    }

    public ArrayList<Request> getUserInvites() {
        return userInvites;
    }

    public ArrayList<Request> getUserAproves() {
        return userAproves;
    }


    // Setter
    public void setUserRequest(ArrayList<Request> userRequest) {
        this.userRequest = userRequest;
    }

    public void setUserInvites(ArrayList<Request> userInvites) {
        this.userInvites = userInvites;
    }

    public void setUserAproves(ArrayList<Request> userAproves) {
        this.userAproves = userAproves;
    }
}
