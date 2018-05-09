package honig.roey.student.roeysigninapp.tables;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class MatchUp implements Parcelable {
    private String key;
    private ArrayList<UserStat> players;

    public MatchUp(String key, ArrayList<UserStat> players) {
        this.key = key;
        this.players = players;
    }

    protected MatchUp(Parcel in) {
        key = in.readString();
        players = in.createTypedArrayList(UserStat.CREATOR);
    }

    public static final Creator<MatchUp> CREATOR = new Creator<MatchUp>() {
        @Override
        public MatchUp createFromParcel(Parcel in) {
            return new MatchUp(in);
        }

        @Override
        public MatchUp[] newArray(int size) {
            return new MatchUp[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<UserStat> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<UserStat> players) {
        this.players = players;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(key);
        parcel.writeTypedList(players);
    }
}
