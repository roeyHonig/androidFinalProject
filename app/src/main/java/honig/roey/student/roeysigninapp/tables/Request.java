package honig.roey.student.roeysigninapp.tables;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable{

    private String key;
    private String requestingUID;
    private String requestingName;
    private String approvingUID;
    private String approvingName;
    private String arenaName;
    private String arenaID;
    private int status;      // 0 - Approved , 1 - Denied, 2- Pending Approval, 3- Canceled

    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

    // Ctor
    protected Request(Parcel in) {
        key = in.readString();
        requestingUID = in.readString();
        requestingName = in.readString();
        approvingUID = in.readString();
        approvingName = in.readString();
        arenaName = in.readString();
        arenaID = in.readString();
        status = in.readInt();
    }

    public Request(String key,String requestingUID ,String requestingName,String approvingUID ,String approvingName, String arenaName, String arenaID ,int status) {
        this.key = key;
        this.requestingUID = requestingUID;
        this.requestingName = requestingName;
        this.approvingUID = approvingUID;
        this.approvingName = approvingName;
        this.arenaName = arenaName;
        this.arenaID = arenaID;
        this.status = status;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(requestingUID);
        parcel.writeString(requestingName);
        parcel.writeString(approvingUID);
        parcel.writeString(approvingName);
        parcel.writeString(arenaName);
        parcel.writeString(arenaID);
        parcel.writeInt(status);
    }






    public String getKey() {
        return key;
    }

    public String getRequestingUID() {
        return requestingUID;
    }

    public String getRequestingName() {
        return requestingName;
    }

    public String getApprovingUID() {
        return approvingUID;
    }

    public String getApprovingName() {
        return approvingName;
    }

    public String getArenaName() {
        return arenaName;
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

    public void setRequestingName(String requestingUID) {
        this.requestingName = requestingUID;
    }

    public void setApprovingUID(String approvingUID) {
        this.approvingUID = approvingUID;
    }

    public void setApprovingName(String approvingUID) {
        this.approvingName = approvingUID;
    }

    public void setArenaName(String arenaID) {
        this.arenaName = arenaID;
    }

    public void setArenaID(String arenaID) {
        this.arenaID = arenaID;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
