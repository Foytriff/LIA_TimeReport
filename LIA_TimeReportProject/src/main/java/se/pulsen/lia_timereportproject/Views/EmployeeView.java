package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.security.core.context.SecurityContextHolder;
import se.pulsen.lia_timereportproject.Services.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import javax.annotation.security.PermitAll;


@Route(value = "/user", layout = MainView.class)
@PermitAll
public class EmployeeView extends VerticalLayout {
    H1 test = new H1("Welcome");


    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;
    TimereportService timereportService;
    EmployeeService employeeService;

    public EmployeeView(CustomerService customerService, ProjectService projectService, ActivityService activityService, TimereportService timereportService, EmployeeService employeeService){
        this.customerService = customerService;
        this.projectService = projectService;
        this.activityService = activityService;
        this.timereportService = timereportService;
        this.employeeService = employeeService;

        ReportForm reportForm = new ReportForm(customerService, projectService, activityService, timereportService, employeeService);
        Div reportContainer = new Div(reportForm);

        System.out.println("ROLE: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString());

        reportContainer.setClassName("form_container");

        this.setAlignItems(Alignment.CENTER);

        add(test, reportContainer);
    }
}
