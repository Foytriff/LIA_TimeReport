package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
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
import se.pulsen.lia_timereportproject.Entities.*;
import se.pulsen.lia_timereportproject.Services.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import java.util.List;

@Route(value = "/test", layout = MainView.class)
@AnonymousAllowed
public class StatisticsView extends HorizontalLayout {

    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;
    EmployeeService employeeService;

    public StatisticsView(CustomerService customerService, ProjectService projectService, ActivityService activityService, EmployeeService employeeService){
        this.customerService = customerService;
        this.projectService = projectService;
        this.activityService = activityService;
        this.employeeService = employeeService;

        CheckboxGroup<Customer> customers = new CheckboxGroup<>();
        CheckboxGroup<Project> projects = new CheckboxGroup<>();
        CheckboxGroup<Activity> activities = new CheckboxGroup<>();
        CheckboxGroup<Employee> employees = new CheckboxGroup<>();

        Accordion customersel = new Accordion();
        Accordion projectsel = new Accordion();
        Accordion activitysel = new Accordion();
        Accordion employeesel = new Accordion();



        Button renderStatsBtn = new Button("Render Statistics");
        renderStatsBtn.addClickListener(evt -> renderStats());

        //Choosing customer, project... (activity ? print(good!) : add)
        customers.setItems(customerService.findAll());
        customers.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        customers.addValueChangeListener(c -> { // Break out into a method(Accordion acc, CheckboxGroup<E> chbxs)
            customers.setEnabled(false);
            customersel.close();
            projects.setItems(projectService.projectsForCustomer(c.getValue().iterator().next().getCustomerID()));
            projectsel.add("Projects", projects);
        }); // Change so that the checked box can be unchecked, and all boxes enabled after unselecting.

        if (projectsel.isVisible()){
            projects.addValueChangeListener( c -> {
                projects.setEnabled(false);
                projectsel.close();
                activities.setItems(activityService.findActivitiesForProject(c.getValue().iterator().next()));
                activitysel.add("Activites", activities);
            });
        }


        // Employee Selection for ADMINs
        employees.setItems(employeeService.findAll());
        employees.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        employeesel.add("Employees", employees);


        customersel.add("Customer", customers);

        Div content = new Div();
        content.add(customersel, projectsel, activitysel);
        add(content);
    }

    private void renderStats() {



    }
}
