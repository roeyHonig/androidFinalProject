package honig.roey.student.roeysigninapp.arena;

public class SpinnerMatchUp {
    private String matchupid;
    private String p1Name;
    private String p2Name;
    private String p1ImageUrl;
    private String p2ImageUrl;

    public SpinnerMatchUp(String matchupid, String p1Name, String p2Name, String p1ImageUrl, String p2ImageUrl) {
        this.matchupid = matchupid;
        this.p1Name = p1Name;
        this.p2Name = p2Name;
        this.p1ImageUrl = p1ImageUrl;
        this.p2ImageUrl = p2ImageUrl;
    }

    public String getMatchupid() {
        return matchupid;
    }

    public String getP1Name() {
        return p1Name;
    }

    public String getP2Name() {
        return p2Name;
    }

    public String getP1ImageUrl() {
        return p1ImageUrl;
    }

    public String getP2ImageUrl() {
        return p2ImageUrl;
    }

    public void setMatchupid(String matchupid) {
        this.matchupid = matchupid;
    }

    public void setP1Name(String p1Name) {
        this.p1Name = p1Name;
    }

    public void setP2Name(String p2Name) {
        this.p2Name = p2Name;
    }

    public void setP1ImageUrl(String p1ImageUrl) {
        this.p1ImageUrl = p1ImageUrl;
    }

    public void setP2ImageUrl(String p2ImageUrl) {
        this.p2ImageUrl = p2ImageUrl;
    }
}
