package honig.roey.student.roeysigninapp.tables;

public class UserStat {
    String uid;
    private String fullName;
    long los;
    long drw;
    long win;
    long numGames;
    double pct;

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
}
