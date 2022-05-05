package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.beans.factory.annotation.Autowired;
import se.pulsen.lia_timereportproject.Entities.Activity;
import se.pulsen.lia_timereportproject.Entities.Customer;
import se.pulsen.lia_timereportproject.Entities.Project;
import se.pulsen.lia_timereportproject.Entities.Timereport;
import se.pulsen.lia_timereportproject.Services.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import java.util.List;

@Route(value = "/test", layout = MainView.class)
@AnonymousAllowed
public class StatisticsView extends HorizontalLayout {

    //Will contain a clickbased interface for retrieving filtered data.

    RadioButtonGroup<Customer> customer = new RadioButtonGroup<>();
    RadioButtonGroup<Project> project = new RadioButtonGroup<>();
    RadioButtonGroup<Activity> activity = new RadioButtonGroup<>();

    Grid selectionStatistics = new Grid();

    public StatisticsView(@Autowired CustomerService customerService, @Autowired ProjectService projectService, @Autowired ActivityService activityService, @Autowired TimereportService timereportService, @Autowired EmployeeService employeeService){

        project.setVisible(false);
        activity.setVisible(false);

        List<RadioButtonGroup> style = List.of(customer, project, activity);
        style.forEach(c -> c.addThemeVariants(RadioGroupVariant.LUMO_VERTICAL));


        customer.setLabel("Customer");
        project.setLabel("Project");
        activity.setLabel("Activity");

        customer.setItems(customerService.findAll());

        customer.addValueChangeListener(evt -> {
            project.setItems(projectService.projectsForCustomer(evt.getValue().getCustomerID()));
            project.setVisible(true);
            renderStatistics(customer, timereportService, employeeService);
        });

        project.addValueChangeListener(evt -> {
            activity.setItems(activityService.findActivitiesForProject(evt.getValue()));
            activity.setVisible(true);
        });

        HorizontalLayout selectionContainer = new HorizontalLayout(customer, project, activity);

        add(selectionContainer, selectionStatistics);

    }

    public<T> void renderStatistics(RadioButtonGroup<T> selection, TimereportService timereportService, EmployeeService employeeService){
        if(selection.isEmpty())
            return;

        Class type = selection.getValue().getClass();

        System.out.println(type.toString());

        switch(type.toString()){
            case "class se.pulsen.lia_timereportproject.Entities.Customer":
                Customer customer = (Customer) selection.getValue();
                selectionStatistics.setItems(statsForCustomer(customer, timereportService, employeeService));
                break;
            default:
                System.out.println("No");
                break;

        }
    }

    private List<Timereport> statsForCustomer(Customer customer, TimereportService timereportService, EmployeeService employeeService) {
        List<Timereport> doMathOnThis = timereportService.getTimeSpentOnCustomer(customer.getCustomerID(), employeeService.findEmployeeByUsername(PrincipalUtils.getUsername()).getEmployeeID().toString());
        return doMathOnThis;
    }
}
