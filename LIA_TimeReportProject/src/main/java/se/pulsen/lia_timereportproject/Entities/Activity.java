package se.pulsen.lia_timereportproject.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Activity {
    @Id
    @Column
    String activityID;
    @Column
    String activityName; // Problem med denna, versal i början fkar allt. Verkar som problemet uppstår för att java är case sensitive medan sql inte är det
    @Column
    String startDate;
    @Column
    String endDate;
    @Column
    String projectID;

    public String getActivityID() {
        return activityID;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    @Override
    public String toString() {
        return activityName;
    }
}
