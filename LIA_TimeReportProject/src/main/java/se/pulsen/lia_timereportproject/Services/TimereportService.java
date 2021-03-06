package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Entities.Timereport;
import se.pulsen.lia_timereportproject.Repositories.TimereportRepo;

import java.util.List;

@Service
public class TimereportService {
    @Autowired
    TimereportRepo timereportRepo;
    @Autowired
    EmployeeService employeeService;

    public Timereport save(Timereport timereport){
        return timereportRepo.save(timereport);
    }

    public List<Timereport> findAll(){
        return timereportRepo.findAll();
    }

    public List<Timereport> getReportsForEmployee(Employee employee){
        return timereportRepo.findTimereportsByEmployee(employee);
    }

    public List<Timereport>getTimeSpentOnCustomer(String customerID, String employeeID){
        return timereportRepo.getTimeSpentOnCustomer(customerID, employeeID);
    }

    public List<Timereport> getReportsAdmin(){
        return timereportRepo.findAll();
    }

    public void deleteTimereport(Timereport tr) {
        timereportRepo.delete(tr);
    }
}
