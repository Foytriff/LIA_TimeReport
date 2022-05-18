package se.pulsen.lia_timereportproject.Entities;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Project {
    @Id
    String projectID;
    String projectName;
    String projectDescription;

    String customerID;
    @OneToMany(mappedBy = "activityID")
    List<Activity> activities;

    public String getProjectID() {
        return projectID;
    }

    public void setProjectIDForHomeUse(String id){
        this.projectID = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public void setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    @Override
    public String toString() {
        return projectName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return projectID.equals(project.projectID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectID);
    }
}
