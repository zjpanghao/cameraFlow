package i221.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class CaseData implements Serializable {
    @Column(name = "case_id")
    private int caseId;
    private int id;

    public CaseData() {
    }

    public CaseData(int caseId, int id) {
        this.caseId = caseId;
        this.id = id;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof CaseData) {
            if (object == this) {
                return true;
            }
            if (((CaseData) object).caseId == caseId && ((CaseData) object).getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return id << 3 & caseId;
    }
}
