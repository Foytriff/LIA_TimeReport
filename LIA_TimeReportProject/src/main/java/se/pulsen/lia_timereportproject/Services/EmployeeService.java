package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Repositories.EmployeeRepo;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepo employeeRepo;

    public Employee saveEmployee(Employee employee){
        return employeeRepo.save(employee);
    }

    public Employee findEmployeeByName(String name){
        return employeeRepo.findEmployeeByEmployeeName(name).orElseThrow();
    }
}
