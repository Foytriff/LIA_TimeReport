package se.pulsen.lia_timereportproject.Entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Timereport {
    @Id
    String TimerapportID;
    String RapportDate;
    String amountHours;
    String comment;
    String SubmitDate;

    String ActivityID;
    @ManyToOne
    @JoinColumn(name = "employeeID")
    Employee employee;

    public String getTimerapportID() {
        return TimerapportID;
    }

    public String getRapportDate() {
        return RapportDate;
    }

    public void setRapportDate(String rapportDate) {
        RapportDate = rapportDate;
    }

    public String getAmountHours() {
        return amountHours;
    }

    public void setAmountHours(String amountHours) {
        this.amountHours = amountHours;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSubmitDate() {
        return SubmitDate;
    }

    public void setSubmitDate(String submitDate) {
        SubmitDate = submitDate;
    }

    public String getActivityID() {
        return ActivityID;
    }

    public void setActivityID(String activityID) {
        ActivityID = activityID;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployeeID(Employee employee) {
        this.employee = employee;
    }
}
