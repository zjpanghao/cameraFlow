package i221.entity;
import javax.persistence.*;
import java.io.Serializable;

@Entity(name = "detect_video_image")
public class VideoImage implements Serializable{
    //private byte [] data;
    @EmbeddedId
    private CaseData caseData;

    private String data;

    public VideoImage() {
    }

    public VideoImage(CaseData caseData) {
        this.caseData = caseData;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    private int x;
    private int y;
    private int width;
    private int height;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public CaseData getCaseData() {
        return caseData;
    }

    public void setCaseData(CaseData caseData) {
        this.caseData = caseData;
    }

}
