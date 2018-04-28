package honig.roey.student.roeysigninapp.tables;

import android.os.Parcel;
import android.os.Parcelable;

public class UserStat implements Parcelable {
    private String uid;
    private String fullName;
    private String profileImage;
    private long los;
    private long drw;
    private long win;
    private long numGames;
    private double pct;
    private long goalsFor;
    private long goalsAgainst;
    private long winningStrike;
    private double goalsForAverage;
    private double goalsAgainstAverage;



    // Constractors
    public UserStat (){

    }

    /*
    public UserStat(String uid ,String fullName, String profileImage, long los, long drw, long win, long goalsFor, long goalsAgainst, long winningStrike, double goalsForAverage, double goalsAgainstAverage ) {
        this.uid = uid;
        this.fullName = fullName;
        this.profileImage = profileImage;
        this.los = los;
        this.drw = drw;
        this.win = win;
        this.numGames = los + drw + win;
        if (this.numGames == 0) {
            this.pct = 0;
        } else {
            this.pct = (double) (drw+3*win)/(double)(3*numGames);
        }
        this.goalsFor = goalsFor;
    }
    */

    public UserStat(String uid, String fullName, String profileImage, long los, long drw, long win, long goalsFor, long goalsAgainst, long winningStrike) {
        this.uid = uid;
        this.fullName = fullName;
        this.profileImage = profileImage;
        this.los = los;
        this.drw = drw;
        this.win = win;
        this.goalsFor = goalsFor;
        this.goalsAgainst = goalsAgainst;
        this.winningStrike = winningStrike;
        this.numGames = los + drw + win;
        if (this.numGames == 0) {
            this.pct = 0;
            this.goalsForAverage = 0;
            this.goalsAgainstAverage = 0;
        } else {
            this.pct = (double) (drw+3*win)/(double)(3*numGames);
            this.goalsForAverage = (double)(goalsFor)/(double)(this.numGames);
            this.goalsAgainstAverage = (double)(goalsAgainst)/(double)(this.numGames);

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

    public String getProfileImage() {
        return profileImage;
    }

    public long getGoalsFor() {
        return goalsFor;
    }

    public long getGoalsAgainst() {
        return goalsAgainst;
    }

    public long getWinningStrike() {
        return winningStrike;
    }

    public double getGoalsForAverage() {
        return goalsForAverage;
    }

    public double getGoalsAgainstAverage() {
        return goalsAgainstAverage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setGoalsFor(long goalsFor) {
        this.goalsFor = goalsFor;
    }

    public void setGoalsAgainst(long goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public void setWinningStrike(long winningStrike) {
        this.winningStrike = winningStrike;
    }

    // Constractor
    protected UserStat(Parcel in) {
        uid = in.readString();
        fullName = in.readString();
        profileImage = in.readString();
        los = in.readLong();
        drw = in.readLong();
        win = in.readLong();
        numGames = in.readLong();
        pct = in.readDouble();
        goalsFor = in.readLong();
        goalsAgainst = in.readLong();
        winningStrike = in.readLong();
        goalsForAverage = in.readDouble();
        goalsAgainstAverage = in.readDouble();
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
        parcel.writeString(profileImage);
        parcel.writeLong(los);
        parcel.writeLong(drw);
        parcel.writeLong(win);
        parcel.writeLong(numGames);
        parcel.writeDouble(pct);
        parcel.writeLong(goalsFor);
        parcel.writeLong(goalsAgainst);
        parcel.writeLong(winningStrike);
        parcel.writeDouble(goalsForAverage);
        parcel.writeDouble(goalsAgainstAverage);
    }
}
