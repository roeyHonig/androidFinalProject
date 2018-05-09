package honig.roey.student.roeysigninapp.tables;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RingGlobal implements Parcelable {
    private String key;
    private String name;
    private int numPlayers;
    private boolean isPublicViewd;
    private  String superUser;
    private ArrayList<UserStat> userStats;

    public static final Creator<RingGlobal> CREATOR = new Creator<RingGlobal>() {
        @Override
        public RingGlobal createFromParcel(Parcel in) {
            return new RingGlobal(in);
        }

        @Override
        public RingGlobal[] newArray(int size) {
            return new RingGlobal[size];
        }
    };



    //Ctors
    public RingGlobal() {
        // Default constructor required for calls to DataSnapshot.getValue(RingGlobal.class)
    }

    public RingGlobal(String key, String name, boolean isPublicViewd, String superUser, ArrayList<UserStat> userStats) {
        this.key = key;
        this.name = name;
        this.numPlayers = userStats.size();
        this.isPublicViewd = isPublicViewd;
        this.superUser = superUser;
        this.userStats = userStats;

    }

    protected RingGlobal(Parcel in) {
        key = in.readString();
        name = in.readString();
        numPlayers = in.readInt();
        isPublicViewd = in.readByte() != 0;
        superUser = in.readString();
    }



    // public getters
    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public boolean isPublicViewd() {
        return isPublicViewd;
    }

    public String getSuperUser() {
        return superUser;
    }

    public ArrayList<UserStat> getUserStats() {
        return userStats;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = getUserStats().size();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeInt(numPlayers);
        parcel.writeByte((byte) (isPublicViewd ? 1 : 0));
        parcel.writeString(superUser);

    }

    // public setters


    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublicViewd(boolean publicViewd) {
        isPublicViewd = publicViewd;
    }

    public void setSuperUser(String superUser) {
        this.superUser = superUser;
    }

    public void setUserStats(ArrayList<UserStat> userStats) {
        this.userStats = userStats;
    }

    public String toShortString(){
        String superUserName="";
        String description = "Arena Id is: "+ this.getKey() +", "+
                "Arena's Name is: "+ this.getName() +", "+
                "There are: "+ this.getNumPlayers() +" Players in this Arena, "+
                "Is this Arena visiable to all App users? "+ this.isPublicViewd() +", ";


        for (UserStat player: this.getUserStats()) {
            if (player.getUid().equals(this.getSuperUser())){superUserName = player.getFullName() ;}
        }

        description += "and the super User is "+ superUserName;

        return description ;
    }
}
