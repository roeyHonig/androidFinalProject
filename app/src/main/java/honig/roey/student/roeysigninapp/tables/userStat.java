package honig.roey.student.roeysigninapp.tables;

public class userStat {
    private String fullName;
    long los;
    long drw;
    long win;
    long numGames;
    float pct;

    public userStat(String fullName, long los, long drw, long win) {
        this.fullName = fullName;
        this.los = los;
        this.drw = drw;
        this.win = win;
        this.numGames = los + drw + win;
        this.pct = (drw+3*win)/(3*numGames);
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public float getPct() {
        return pct;
    }
}
