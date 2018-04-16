package honig.roey.student.roeysigninapp.tables;

import android.os.Parcel;
import android.os.Parcelable;

public class UserStat implements Parcelable {
    String uid;
    private String fullName;
    long los;
    long drw;
    long win;
    long numGames;
    double pct;

    // Constractors
    public UserStat (){

    }

    public UserStat(String uid ,String fullName, long los, long drw, long win) {
        this.uid = uid;
        this.fullName = fullName;
        this.los = los;
        this.drw = drw;
        this.win = win;
        this.numGames = los + drw + win;
        if (this.numGames == 0) {
            this.pct = 0;
        } else {
            this.pct = (double) (drw+3*win)/(double)(3*numGames);
        }
    }

    // Getters & Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getNumGames() {
        return numGames;
    }


    public long getLos() {
        return los;
    }

    public void setLos(long los) {
        this.los = los;
    }

    public long getDrw() {
        return drw;
    }

    public void setDrw(long drw) {
        this.drw = drw;
    }

    public long getWin() {
        return win;
    }

    public void setWin(long win) {
        this.win = win;
    }

    public double getPct() {
        return pct;
    }

    // Constractor
    protected UserStat(Parcel in) {
        uid = in.readString();
        fullName = in.readString();
        los = in.readLong();
        drw = in.readLong();
        win = in.readLong();
        numGames = in.readLong();
        pct = in.readDouble();
    }

    // final field (it's type is an interface - Creator<>) - requiered to implement Parcebale
    public static final Creator<UserStat> CREATOR = new Creator<UserStat>() {
        @Override
        public UserStat createFromParcel(Parcel in) {
            return new UserStat(in);
        }

        @Override
        public UserStat[] newArray(int size) {
            return new UserStat[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    // Parameter "int i" ? ---> Additional flags about how the object should be written. May be 0
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeString(fullName);
        parcel.writeLong(los);
        parcel.writeLong(drw);
        parcel.writeLong(win);
        parcel.writeLong(numGames);
        parcel.writeDouble(pct);
    }
}
