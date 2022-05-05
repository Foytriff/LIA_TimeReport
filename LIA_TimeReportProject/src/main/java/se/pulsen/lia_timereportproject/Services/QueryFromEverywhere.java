package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.Activity;
import se.pulsen.lia_timereportproject.Entities.Customer;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Entities.Project;
import se.pulsen.lia_timereportproject.Repositories.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

@Service
public class QueryFromEverywhere {

    @Autowired
    ActivityRepo activityRepo;
    @Autowired
    CustomerRepo customerRepo;
    @Autowired
    EmployeeRepo employeeRepo;
    @Autowired
    ProjectRepo projectRepo;
    @Autowired
    TimereportRepo timereportRepo;

    public String getCustomerNameFromActivityID(String activityID){
        Activity activity = activityRepo.findActivityByActivityID(activityID).orElseThrow();
        Project project = activity.getProject();
        Customer customer = customerRepo.findCustomerByCustomerID(project.getCustomerID()).orElseThrow();
        return customer.getCustomerName();
    }

    public String getNameForLoggedInUser(){
        Employee employee = employeeRepo.findEmployeeByUsername(PrincipalUtils.getUsername()).orElseThrow();
        return employee.getEmployeeName();
    }

    public Project getProjectNameFromActivity(String activityID){
        Activity activity = activityRepo.findActivityByActivityID(activityID).orElseThrow();
        return activity.getProject();
    }
    public String getProjectNameFromProjectID(String projectID){
        Project project = projectRepo.findProjectByProjectID(projectID).orElseThrow();
        return project.getProjectName();
    }

    public String getActivityName(String activityID){
        Activity activity = activityRepo.findActivityByActivityID(activityID).orElseThrow();
        return activity.getActivityName();
    }

}
