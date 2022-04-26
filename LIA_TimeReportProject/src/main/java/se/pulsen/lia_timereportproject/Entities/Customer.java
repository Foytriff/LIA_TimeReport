package se.pulsen.lia_timereportproject.Entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Customer {
    @Id
    String customerID;
    String customerName;
    String customerlocation;

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerlocation() {
        return customerlocation;
    }

    public void setCustomerlocation(String customerlocation) {
        this.customerlocation = customerlocation;
    }

    @Override
    public String toString() {
        return customerName;
    }
}
