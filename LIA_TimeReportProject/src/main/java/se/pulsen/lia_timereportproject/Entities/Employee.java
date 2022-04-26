package se.pulsen.lia_timereportproject.Entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Employee")
public class Employee {
    @Id
    String employeeID;
    @Column
    String employeeName;
    @Column
    String phone;
    @Column
    String username;
    @Column
    String pass;
    @OneToMany(mappedBy = "timereportID")
    List<Timereport> myRapports;

    public Employee(String employeeName, String phone, String username){
        this.employeeName = employeeName;
        this.phone = phone;
        this.username = username;
        this.pass = "generated";
    }

    public Employee() {

    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
