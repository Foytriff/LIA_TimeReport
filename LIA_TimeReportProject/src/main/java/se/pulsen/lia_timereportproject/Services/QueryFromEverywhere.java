package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.*;
import se.pulsen.lia_timereportproject.Repositories.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import javax.lang.model.element.AnnotationMirror;
import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.Collectors;

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


    public List<Timereport> getTimereportsForCustomer(String customerID){

        // Customer customer = customerRepo.findCustomerByCustomerID(customerID).orElseThrow();
        List<Project> projectsForCustomer = projectRepo.findProjectsByCustomerID(customerID);

        List<Activity> activitiesForCustomer = List.of();
        List<Timereport> timereportsForActivity = List.of();

        projectsForCustomer.forEach(p -> {
            List<Activity> temp = activityRepo.findActivitiesByProject(p);
            temp.forEach(a -> {
                activitiesForCustomer.add(a);
            });
        });

        activitiesForCustomer.forEach(a -> {
            List<Timereport> temp = timereportRepo.findTimereportsByActivityID(a.getActivityID());
            temp.forEach(tr -> {
                timereportsForActivity.add(tr);
            });
        });
        return timereportsForActivity;
    }

    public List<Employee> getEmployeesForCustomer(String customerID){

        List<Employee> employeesForCustomer = List.of();

        List<Timereport> timereportsForCustomer = getTimereportsForCustomer(customerID);

        timereportsForCustomer.forEach(tr -> {
            Employee employee = employeeRepo.findEmployeeByEmployeeID(tr.getEmployeeID()).orElseThrow();
            employeesForCustomer.add(employee);
        });

        employeesForCustomer = employeesForCustomer
                .stream()
                .distinct()
                .collect(Collectors.toList());

        return List.of();
    }

    public double totalTimeForCustomer(String customerID){
        List<Timereport> timereportsForCustomer = getTimereportsForCustomer(customerID);
        double totalTime = 0;

        timereportsForCustomer.forEach(tr -> {
            totalTime += tr.getAmountHours();
        });

        return  totalTime;
    }

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
