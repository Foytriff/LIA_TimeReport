package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.checkbox.CheckboxGroupVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.radiobutton.RadioGroupVariant;
import com.vaadin.flow.dom.DomEventListener;
import com.vaadin.flow.dom.DomListenerRegistration;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;
import org.springframework.beans.factory.annotation.Autowired;
import se.pulsen.lia_timereportproject.Entities.*;
import se.pulsen.lia_timereportproject.Services.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "/test", layout = MainView.class)
@AnonymousAllowed
public class StatisticsView extends HorizontalLayout {

    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;
    EmployeeService employeeService;
    QueryFromEverywhere queryFromEverywhere;

    public StatisticsView(CustomerService customerService, ProjectService projectService, ActivityService activityService, EmployeeService employeeService, QueryFromEverywhere queryFromEverywhere){
        this.customerService = customerService;
        this.projectService = projectService;
        this.activityService = activityService;
        this.employeeService = employeeService;
        this.queryFromEverywhere = queryFromEverywhere;


        // Creating UI components
        CheckboxGroup<Customer> customers = new CheckboxGroup<>();
        CheckboxGroup<Project> projects = new CheckboxGroup<>();
        CheckboxGroup<Activity> activities = new CheckboxGroup<>();
        CheckboxGroup<Employee> employees = new CheckboxGroup<>();
        Accordion customersel = new Accordion();
        Accordion projectsel = new Accordion();
        Accordion activitysel = new Accordion();
        Accordion employeesel = new Accordion();
        Button renderStatsBtn = new Button("Render Statistics");
        AtomicReference<Registration> renderBtnReg = new AtomicReference<>();
        renderBtnReg.set(renderStatsBtn.addClickListener(evt -> Notification.show("No selection")));



        //Setting up first selection (Customer)
        customers.setItems(customerService.findAll());
        customers.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        customersel.add("Customer", customers);

        // Customer select behavior
        customers.addValueChangeListener(c -> { // Break out into a method(Accordion acc, CheckboxGroup<E> chbxs)
            // Only one selection for customer
            customers.setEnabled(false);

            // Setting project selection
            projects.setItems(projectService.projectsForCustomer(c.getValue().iterator().next().getCustomerID()));
            projects.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
            projectsel.add("Projects", projects);

            // Removing initial render button behavior and adding new.
            renderBtnReg.get().remove();
            // Sending the selected customer to render function
            renderBtnReg.set(renderStatsBtn.addClickListener(evt -> renderStats(c.getValue().iterator().next())));
        }); // Change so that the checked box can be unchecked, and all boxes enabled after unselecting.


        // Project select behavior
        if (projectsel.isVisible()){
            projects.addValueChangeListener( c -> {
                projects.setEnabled(false);

                activities.setItems(activityService.findActivitiesForProject(c.getValue().iterator().next()));
                activities.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
                activitysel.add("Activites", activities);
                renderBtnReg.get().remove();
                renderBtnReg.set(renderStatsBtn.addClickListener(evt -> renderStats(c.getValue().iterator().next())));
            });
        }

        if(activitysel.isVisible()){
            activities.addValueChangeListener( c -> {
               activities.setEnabled(false);

               renderBtnReg.get().remove();
               renderBtnReg.set(renderStatsBtn.addClickListener(evt -> renderStats(c.getValue().iterator().next())));
            });
        }





        // Employee Selection for ADMINs
        employees.setItems(employeeService.findAll());
        employees.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
        employeesel.add("Employees", employees);



        // Adding components to a container and adding the container to the UI
        Div content = new Div();
        content.add(customersel, projectsel, activitysel, renderStatsBtn);
        add(content);
    }

    private<E> void renderStats(E selection) {
        StatCard customerCard = new StatCard(this, queryFromEverywhere, selection);
        // Add style for StatCard (it's a Div)

        // Adding the statistics to the UI
        add(customerCard);
    }
}
