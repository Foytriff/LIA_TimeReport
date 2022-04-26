package se.pulsen.lia_timereportproject.Entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Project {
    @Id
    String projectID;
    String projectName;
    String projectDescription;

    String customerID;

    public String getProjectID() {
        return projectID;
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
}
