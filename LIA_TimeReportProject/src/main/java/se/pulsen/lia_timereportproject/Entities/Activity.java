package se.pulsen.lia_timereportproject.Entities;

import javax.persistence.*;
import java.util.Objects;

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
    @ManyToOne
    @JoinColumn(name = "projectID")
    Project project;

    public String getActivityID() {
        return activityID;
    }

    public void setActivityIDForHomeUse(String id){
        activityID = id;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return activityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return activityID.equals(activity.activityID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityID);
    }
}
