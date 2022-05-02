package se.pulsen.lia_timereportproject.Entities;

import org.hibernate.id.GUIDGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Entity
public class Timereport {
    @Id
    UUID timereportID;
    @Column(name = "RapportDate")
    String reportDate;
    double amountHours;
    String comment;
    String submitDate;

    String activityID;
    @ManyToOne
    @JoinColumn(name = "employeeID")
    Employee employee;


    public Timereport(Employee employee, double amountHours, String reportDate, String comment, String activityID){
        this.employee = employee;
        this.amountHours = amountHours;
        this.reportDate = reportDate;
        this.comment = comment;
        this.activityID = activityID;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();

        this.submitDate = dtf.format(now);

        this.comment = comment;

        this.timereportID = UUID.randomUUID();

    }
    public Timereport(){

    }

    public String getTimereportID() {
        return timereportID.toString();
    }

    public String getReportDate() {
        return reportDate;
    }

    public void setReportDate(String rapportDate) {
        this.reportDate = rapportDate;
    }

    public double getAmountHours() {
        return amountHours;
    }

    public void setAmountHours(double amountHours) {
        this.amountHours = amountHours;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(String submitDate) {
        this.submitDate = submitDate;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public UUID getEmployeeID() {
        return employee.getEmployeeID();
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
