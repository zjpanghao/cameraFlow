package i221.entity;

public class TrackSync {
    private int caseId;
    private int id;
    private GrabTaskState grabTaskState;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public GrabTaskState getGrabTaskState() {
        return grabTaskState;
    }

    public void setGrabTaskState(GrabTaskState grabTaskState) {
        this.grabTaskState = grabTaskState;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }
}
