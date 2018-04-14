package honig.roey.student.roeysigninapp.tables;

public class RingGlobal {
    private String key;
    private String name;
    private int numPlayers;
    private boolean isPublicViewd;
    // FireBase Users UID
    private String p0;
    private String p1;
    private String p2;
    private String p3;
    private String p4;
    private String p5;


    //Ctors
    public RingGlobal() {
        // Default constructor required for calls to DataSnapshot.getValue(RingGlobal.class)
    }

    public RingGlobal(String key, String name, int numPlayers, boolean isPublicViewd, String p0, String p1, String p2, String p3, String p4, String p5) {
        this.key = key;
        this.name = name;
        this.numPlayers = numPlayers;
        this.isPublicViewd = isPublicViewd;
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.p4 = p4;
        this.p5 = p5;
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

    public String getP0() {
        return p0;
    }

    public String getP1() {
        return p1;
    }

    public String getP2() {
        return p2;
    }

    public String getP3() {
        return p3;
    }

    public String getP4() {
        return p4;
    }

    public String getP5() {
        return p5;
    }
}
