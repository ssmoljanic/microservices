package org.example.sessionservice.dto.iternal;

public class UpdateStatsRequest {

    private int reported;
    private int attended;
    private int missed;
    private int organized;

    public UpdateStatsRequest() {}

    public UpdateStatsRequest(int reported, int attended, int missed, int organized) {
        this.reported = reported;
        this.attended = attended;
        this.missed = missed;
        this.organized = organized;
    }

    public int getReported() {
        return reported;
    }

    public void setReported(int reported) {
        this.reported = reported;
    }

    public int getAttended() {
        return attended;
    }

    public void setAttended(int attended) {
        this.attended = attended;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    public int getOrganized() {
        return organized;
    }

    public void setOrganized(int organized) {
        this.organized = organized;
    }
}
