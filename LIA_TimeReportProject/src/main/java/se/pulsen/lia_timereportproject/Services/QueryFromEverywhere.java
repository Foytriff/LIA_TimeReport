package se.pulsen.lia_timereportproject.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.pulsen.lia_timereportproject.Entities.*;
import se.pulsen.lia_timereportproject.Repositories.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import javax.lang.model.element.AnnotationMirror;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
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

    @Autowired
    TimereportService timereportService;

    @Autowired
    ProjectService projectService;
    @Autowired
    CustomerService customerService;

    @Autowired
    ActivityService activityService;

    // Refractor methods used in statcard?

    public TimereportService getTimereportService(){
        return this.timereportService;
    }

    public List<Customer> getCustomersForEmployee(Employee employee){
        List<Timereport> reportsToFilterFrom = timereportService.findAll();
        reportsToFilterFrom = reportsToFilterFrom
                .stream()
                .filter(tr -> tr.getEmployeeID().equals(employee.getEmployeeID()))
                .collect(Collectors.toList());

        List<Customer> customers = reportsToFilterFrom
                .stream()
                .map(tr -> activityRepo.findActivityByActivityID(tr.getActivityID()).orElseThrow())
                .map(activity -> projectRepo.findProjectByProjectID(activity.getProject().getProjectID()).orElseThrow())
                .map(project -> customerRepo.findCustomerByCustomerID(project.getCustomerID()).orElseThrow())
                .distinct()
                .collect(Collectors.toList());
        return customers;
    }

    public List<Project> getProjectsForEmployee(Employee employee){
        List<Timereport> reportsToFilterFrom = timereportService.findAll();
        reportsToFilterFrom = reportsToFilterFrom
                .stream()
                .filter(tr -> tr.getEmployeeID().equals(employee.getEmployeeID()))
                .collect(Collectors.toList());

        List<Project> projects = reportsToFilterFrom
                .stream()
                .map(tr -> activityRepo.findActivityByActivityID(tr.getActivityID()).orElseThrow())
                .map(activity -> projectRepo.findProjectByProjectID(activity.getProject().getProjectID()).orElseThrow())
                .distinct()
                .collect(Collectors.toList());

        return projects;
    }

    public List<Activity> getActivitiesForEmployee(Employee employee){
        List<Timereport> reportsToFilterFrom = timereportService.findAll();
        reportsToFilterFrom = reportsToFilterFrom
                .stream()
                .filter(tr -> tr.getEmployeeID().equals(employee.getEmployeeID()))
                .collect(Collectors.toList());

        List<Activity> activities = reportsToFilterFrom
                .stream()
                .map(tr -> activityRepo.findActivityByActivityID(tr.getActivityID()).orElseThrow())
                .distinct()
                .collect(Collectors.toList());

        return activities;
    }

    public Customer getCustomerFromTimereport(Timereport tr){
        Activity activity = activityRepo.findActivityByActivityID(tr.getActivityID()).orElseThrow();
        Project project = projectRepo.findProjectByProjectID(activity.getProject().getProjectID()).orElseThrow();
        return customerRepo.findCustomerByCustomerID(project.getCustomerID()).orElseThrow();
    }

    public Project getProjectFromTimereport(Timereport tr){
        Activity activity = activityRepo.findActivityByActivityID(tr.getActivityID()).orElseThrow();
        return  projectRepo.findProjectByProjectID(activity.getProject().getProjectID()).orElseThrow();
    }

    public Activity getActivityFromTimereport(Timereport tr){
        return  activityRepo.findActivityByActivityID(tr.getActivityID()).orElseThrow();
    }

    public List<Timereport> getTimereportsForCustomer(String customerID){

        List<Project> projectsForCustomer = projectRepo.findProjectsByCustomerID(customerID);
        List<Activity> activitiesForCustomer = new ArrayList<>();
        List<Timereport> timereportsForActivity = new ArrayList<>();

        projectsForCustomer.forEach(p -> {
            List<Activity> temp = activityRepo.findActivitiesByProject(p);
            for(Activity a : temp){
                activitiesForCustomer.add(a);
            }
        });

        activitiesForCustomer.forEach(a -> {
            List<Timereport> temp = timereportRepo.findTimereportsByActivityID(a.getActivityID());
            temp.forEach(tr -> {
                timereportsForActivity.add(tr);
            });
        });

        return timereportsForActivity;
    }

    public List<Timereport> getTimereportsForProject(Project project){
        List<Activity> activitiesForProject = activityRepo.findActivitiesByProject(project);
        List<Timereport> timereportsForActivity = new ArrayList<>();

        activitiesForProject.forEach(a -> {
            List<Timereport> reportPerActivity = timereportRepo.findTimereportsByActivityID(a.getActivityID());
            reportPerActivity.forEach(tr -> {
                timereportsForActivity.add(tr);
            });
        });
        return timereportsForActivity;
    }

    public List<Timereport> getTimereportsForActivity(Activity activity){
        return timereportRepo.findTimereportsByActivityID(activity.getActivityID());
    }

    public List<Employee> getEmployeesForCustomer(String customerID){

        List<Employee> employeesForCustomer = new ArrayList<>();
        List<Timereport> timereportsForCustomer = getTimereportsForCustomer(customerID);
        timereportsForCustomer.forEach(tr -> System.out.println(tr.getComment()));

        AtomicInteger count = new AtomicInteger(timereportsForCustomer.size());
        AtomicReferenceArray<Employee> empRef = new AtomicReferenceArray<>(count.get());

        // Don't use streams and no atomic vars are needed?
        timereportsForCustomer.forEach(tr -> {
            if(count.get() == 0)
                return;
            Employee employee = employeeRepo.findEmployeeByEmployeeID(tr.getEmployeeID()).orElseThrow();
            empRef.set(empRef.length() - count.get(), employee);
            count.set(count.get() - 1);
        });

        if(empRef.length() != 0){
            for (int i = 0; i < empRef.length(); i++){
                employeesForCustomer.add(empRef.get(i));
            }
        }

        employeesForCustomer = employeesForCustomer
                .stream()
                .distinct()
                .collect(Collectors.toList());

        return employeesForCustomer;
    }

    public List<Employee> getEmployeesForProject(Project project) {
        List<Timereport> timereportsForProject = getTimereportsForProject(project);
        List<Employee> employeesForProject = new ArrayList<>();

        for(Timereport tr : timereportsForProject){
            Employee employee = employeeRepo.findEmployeeByEmployeeID(tr.getEmployeeID()).orElseThrow();
            employeesForProject.add(employee);
        }

        return employeesForProject;
    }

    public List<Employee> getEmployeesForActivity(Activity activity){
        List<Timereport> timereportsForActivity = getTimereportsForActivity(activity);
        List<Employee> employeesForActivity = new ArrayList<>();

        for(Timereport tr : timereportsForActivity){
            Employee employee = employeeRepo.findEmployeeByEmployeeID(tr.getEmployeeID()).orElseThrow();
            employeesForActivity.add(employee);
        }

        return employeesForActivity;
    }

    public Customer getCustomerForActivity(Activity activity){
        Project project = projectRepo.findProjectByProjectID(activity.getProject().getProjectID()).orElseThrow();
        return customerRepo.findCustomerByCustomerID(project.getCustomerID()).orElseThrow();
    }

    public Project getProjectForActivity(Activity activity){
        return projectRepo.findProjectByProjectID(activity.getProject().getProjectID()).orElseThrow();
    }

    public double totalTimeForCustomer(String customerID){
        List<Timereport> timereportsForCustomer = getTimereportsForCustomer(customerID);
        double totalTime = 0;

        // Testing without stream
        for(Timereport tr : timereportsForCustomer){
            totalTime += tr.getAmountHours();
        }

        return  totalTime;
    }

    public double totalTimeForProject(Project project){
        List<Timereport> timereportsForProject = getTimereportsForProject(project);
        double totalTime = 0;

        for (Timereport tr : timereportsForProject){
            totalTime += tr.getAmountHours();
        }

        return totalTime;
    }

    public double totalTimeForActivity(Activity activity){
        List<Timereport> timereportsForActivity = getTimereportsForActivity(activity);
        double totalTime = 0;

        for (Timereport tr : timereportsForActivity){
            totalTime += tr.getAmountHours();
        }

        return totalTime;
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
