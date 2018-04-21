package honig.roey.student.roeysigninapp.tables;

public class Request {
    private String key;
    private String requestingUID;
    private String approvingUID;
    private String arenaID;
    private int status;      // 0 - Approved , 1 - Denied, 2- Pending Approval, 3- Canceled


    public Request(String key, String requestingUID, String approvingUID, String arenaID, int status) {
        this.key = key;
        this.requestingUID = requestingUID;
        this.approvingUID = approvingUID;
        this.arenaID = arenaID;
        this.status = status;
    }

    public String getKey() {
        return key;
    }

    public String getRequestingUID() {
        return requestingUID;
    }

    public String getApprovingUID() {
        return approvingUID;
    }

    public String getArenaID() {
        return arenaID;
    }

    public int getStatus() {
        return status;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setRequestingUID(String requestingUID) {
        this.requestingUID = requestingUID;
    }

    public void setApprovingUID(String approvingUID) {
        this.approvingUID = approvingUID;
    }

    public void setArenaID(String arenaID) {
        this.arenaID = arenaID;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
