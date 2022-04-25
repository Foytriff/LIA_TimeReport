package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import se.pulsen.lia_timereportproject.Entities.Customer;
import se.pulsen.lia_timereportproject.Services.ActivityService;
import se.pulsen.lia_timereportproject.Services.CustomerService;
import se.pulsen.lia_timereportproject.Services.ProjectService;

@Route(value = "", layout = MainView.class)
public class EmployeeView extends VerticalLayout {
    H1 test = new H1("Welcome");


    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;

    public EmployeeView(CustomerService customerService, ProjectService projectService, ActivityService activityService){
        this.customerService = customerService;
        this.projectService = projectService;
        this.activityService = activityService;

        ReportForm reportForm = new ReportForm(customerService, projectService, activityService);
        Div reportContainer = new Div(reportForm);

        reportContainer.setClassName("form_container");

        this.setAlignItems(Alignment.CENTER);

        Button save = new Button("Save");

        add(test, reportContainer, save);
    }
}
