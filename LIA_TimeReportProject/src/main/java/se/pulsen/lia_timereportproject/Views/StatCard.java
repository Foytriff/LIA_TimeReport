package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.springframework.beans.factory.annotation.Autowired;
import se.pulsen.lia_timereportproject.Entities.*;
import se.pulsen.lia_timereportproject.Services.*;

import java.util.List;
import java.util.stream.Collectors;

public class StatCard {

    StatisticsView statisticsView;
    @Autowired
    EmployeeService employeeService;
    @Autowired
    TimereportService timereportService;
    @Autowired
    QueryFromEverywhere qfew;

    public<E> StatCard(StatisticsView statisticsView, E thing){
        this.statisticsView = statisticsView;
        Div test = new Div();
        stats(thing);
        test.setClassName("StatCard");

    }

    public<E> Details stats(E thing){

        Details stats = new Details();
        // Parameter control
        Class param = thing.getClass();

        if(param.equals(Customer.class)){
            Customer customer = (Customer) thing;
            // For customer
            // Projects
            VerticalLayout projectsForCustomer = new VerticalLayout();
            statisticsView.projectService.projectsForCustomer(customer.getCustomerID()).forEach(c ->{
                Span project = new Span(c.getProjectName());
                projectsForCustomer.add(project);
            });
            stats = new Details("Projects", projectsForCustomer);
            //Employees
            VerticalLayout employeeForCustomer = new VerticalLayout();
            // IMPL: get employees for customer
            // NOT WORKING: in QueryFromEverywhere
            // TotTime also in qfew


        } else if(param.equals(Project.class)){
            // For project




        } else if(param.equals(Activity.class)){
            // For activity

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

        return stats;
    }
}
