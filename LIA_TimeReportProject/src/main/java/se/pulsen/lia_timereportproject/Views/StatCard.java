package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import se.pulsen.lia_timereportproject.Entities.*;
import se.pulsen.lia_timereportproject.Services.*;

import java.util.List;
import java.util.stream.Collectors;

public class StatCard extends Div{

    StatisticsView statisticsView;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    TimereportService timereportService;

    QueryFromEverywhere qfew;

    public<E> StatCard(StatisticsView statisticsView, QueryFromEverywhere queryFromEverywhere, E thing){
        // Initialising services
        this.qfew = queryFromEverywhere;
        this.statisticsView = statisticsView;

        // Formatting statistics and adding to the div-component
        this.add(stats(thing));

    }

    public<E> Div stats(E thing){
        // Creating div to return
        Div statsContainer = new Div();
        // Parameter control
        Class param = thing.getClass();

        if(param.equals(Customer.class)){
            // Extracting customer from parameter
            Customer customer = (Customer) thing;

            VerticalLayout content = new VerticalLayout();

            // Layout to contain projects and employees
            HorizontalLayout detailsContainer = new HorizontalLayout();

            Label label = new Label("Customer: " + customer.getCustomerName());

            // Layout for holding projects for given customer
            VerticalLayout projectsContainer = new VerticalLayout();
            statisticsView.projectService.projectsForCustomer(customer.getCustomerID()).forEach(c ->{
                Span project = new Span(c.getProjectName());
                projectsContainer.add(project);
            });
            Details projectsDetails = new Details("Projects", projectsContainer);

            // Layout for holding employees for given customer
            VerticalLayout employeesForCustomer = new VerticalLayout();
            qfew.getEmployeesForCustomer(customer.getCustomerID()).forEach(emp -> {
                Span employeeName = new Span(emp.getEmployeeName());
                employeesForCustomer.add(employeeName);
            });

            Details employeesDetails = new Details("Employees", employeesForCustomer);

            detailsContainer.add(projectsDetails, employeesDetails);

            Span totTime = new Span("Total Time Spent: " + qfew.totalTimeForCustomer(customer.getCustomerID()));

            content.add(label, detailsContainer, totTime);

            statsContainer.add(content);

        } else if(param.equals(Project.class)){
            // For project
            Project project = (Project) thing;

            VerticalLayout content = new VerticalLayout();
            HorizontalLayout detailsContainer = new HorizontalLayout();
            VerticalLayout activities = new VerticalLayout();
            VerticalLayout employees = new VerticalLayout();
            Label label = new Label("Project: " + project.getProjectName());
            Span totTime = new Span("Time spent on project: " + qfew.totalTimeForProject(project));

            // Activities
            statisticsView.activityService.findActivitiesForProject(project).forEach(c -> {
                Span activityName = new Span(c.getActivityName());
                activities.add(activityName);
            });
            Details activitiesDetails = new Details("Activities", activities);

            // Employees
            qfew.getEmployeesForProject(project).forEach(emp -> {
                Span employeeName = new Span(emp.getEmployeeName());
                employees.add(employeeName);
            });
            Details employeesDetails = new Details("Employees", employees);

            detailsContainer.add(activitiesDetails, employeesDetails);

            content.add(label, detailsContainer, totTime);

            statsContainer.add(content);


        } else if(param.equals(Activity.class)){
            // For activity
            Activity activity = (Activity) thing;
            VerticalLayout content = new VerticalLayout();
            HorizontalLayout detailsContainer = new HorizontalLayout();
            VerticalLayout belongsToContainer = new VerticalLayout();
            VerticalLayout employees = new VerticalLayout();
            Label label = new Label("Activity: " + activity.getActivityName());
            Span totTime = new Span("Time spent on project: " + qfew.totalTimeForActivity(activity));

            Customer customer = qfew.getCustomerForActivity(activity);
            Project project = qfew.getProjectForActivity(activity);

            // Add projname and custname
            belongsToContainer.add(new Span("Customer: " + customer.getCustomerName()), new Span("Project: " + project.getProjectName()));

            qfew.getEmployeesForActivity(activity).forEach(emp -> {
                Span employeeName = new Span(emp.getEmployeeName());
                employees.add(employeeName);
            });
            // handle no assigned employees?
            Details employeesDetails = new Details("Employees", employees);

            detailsContainer.add(belongsToContainer, employeesDetails);

            content.add(label, detailsContainer, totTime);

            statsContainer.add(content);

        } else if(param.equals(Employee.class)){

            // For Employee as ADMIN
            Employee employee = (Employee) thing;
            // Timereports for given employee.
            List<Timereport> reportForEmployee =
                    timereportService.findAll()
                            .stream()
                            .filter(tr -> tr.getEmployeeID() == employee.getEmployeeID())
                            .collect(Collectors.toList());


            // TABS for CUSTOMERS, PROJECT, ACTIVITIES

        }

        return statsContainer;
    }
}
