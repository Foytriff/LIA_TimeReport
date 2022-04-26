package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import se.pulsen.lia_timereportproject.Entities.Customer;
import se.pulsen.lia_timereportproject.Entities.Timereport;
import se.pulsen.lia_timereportproject.Services.*;

@Route(value = "", layout = MainView.class)
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

        reportContainer.setClassName("form_container");

        this.setAlignItems(Alignment.CENTER);

        add(test, reportContainer);
    }
}
