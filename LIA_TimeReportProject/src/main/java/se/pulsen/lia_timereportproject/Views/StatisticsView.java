package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

    CheckboxGroup<Customer> customer = new CheckboxGroup<>();
    CheckboxGroup<Project> project = new CheckboxGroup<>();
    CheckboxGroup<Activity> activity = new CheckboxGroup<>();

    public StatisticsView(@Autowired CustomerService customerService, @Autowired ProjectService projectService, @Autowired ActivityService activityService, @Autowired TimereportService timereportService, @Autowired EmployeeService employeeService){

        project.setVisible(false);
        activity.setVisible(false);

        List<CheckboxGroup> style = List.of(customer, project, activity);
        style.forEach(c -> c.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL));

        VerticalLayout selectionStats = new VerticalLayout();

        customer.setLabel("Customer");
        project.setLabel("Project");
        activity.setLabel("Activity");

        customer.setItems(customerService.findAll());

        customer.addValueChangeListener(evt -> {
            project.setItems(projectService.projectsForCustomer(evt.getValue().iterator().next().getCustomerID()));
            project.setVisible(true);
            testRender(customerService, selectionStats);
        });

        project.addValueChangeListener(evt -> {
            activity.setItems(activityService.findActivitiesForProject(evt.getValue().iterator().next()));
            activity.setVisible(true);
        });

        HorizontalLayout selectionContainer = new HorizontalLayout(customer, project, activity);


        add(selectionContainer, selectionStats);

    }

    private void testRender(CustomerService customerService, VerticalLayout stats) {
        Grid<Customer> grid = new Grid<>(Customer.class);
        grid.setItems(customerService.findAll());
        stats.add(grid);
        //selectionStatistics.setItems(customerService.findAll());
    }

    private List<Timereport> statsForCustomer(Customer customer, TimereportService timereportService, EmployeeService employeeService) {
        List<Timereport> doMathOnThis = timereportService.getTimeSpentOnCustomer(customer.getCustomerID(), employeeService.findEmployeeByUsername(PrincipalUtils.getUsername()).getEmployeeID().toString());
        return doMathOnThis;
    }
}
