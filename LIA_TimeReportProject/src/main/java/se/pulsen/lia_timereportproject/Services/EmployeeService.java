package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Repositories.EmployeeRepo;

import javax.annotation.security.PermitAll;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;

    public Employee saveEmployee(Employee employee){
        return employeeRepo.save(employee);
    }

    public Employee findEmployeeByID(String username){
        return employeeRepo.findEmployeeByEmployeeID(username).orElseThrow();
    }

    public Employee findEmployeeByUsername(String username){
        return employeeRepo.findEmployeeByUsername(username).orElseThrow();
    }

    public String getIDfromUsername(String username){
        Employee employee = this.findEmployeeByUsername(username);
        return employee.getEmployeeID();
    }
}
