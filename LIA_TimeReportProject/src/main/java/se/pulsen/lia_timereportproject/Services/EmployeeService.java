package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Repositories.EmployeeRepo;

import javax.annotation.security.PermitAll;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;

    public Employee saveEmployee(Employee employee){
        return employeeRepo.save(employee);
    }

    public String getEmployeeNameFromID(UUID employeeID){
        Employee employee = employeeRepo.findEmployeeByEmployeeID(employeeID).orElseThrow();
        return employee.getEmployeeName();
    }

    public Employee findEmployeeByUsername(String username){
        return employeeRepo.findEmployeeByUsername(username).orElseThrow();
    }

    public Employee getEmployeeFromID(UUID employeeID){
        return employeeRepo.findEmployeeByEmployeeID(employeeID).orElseThrow();
    }

    public void deleteEmployee(Employee employee){
        employeeRepo.delete(employee);
    }

    public UUID getIDfromUsername(String username){
        Employee employee = this.findEmployeeByUsername(username);
        return employee.getEmployeeID();
    }

    public List<Employee> findAll() {
        return employeeRepo.findAll();
    }
}
