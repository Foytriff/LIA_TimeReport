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

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Route(value = "/statistics", layout = MainView.class)
@RolesAllowed({"ADMIN", "USER"})
public class StatisticsView extends HorizontalLayout {

    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;
    EmployeeService employeeService;
    TimereportService timereportService;
    QueryFromEverywhere queryFromEverywhere;
    Div statCardsContainer = new Div();

    public StatisticsView(CustomerService customerService, ProjectService projectService, ActivityService activityService, EmployeeService employeeService, QueryFromEverywhere queryFromEverywhere, TimereportService timereportService){
        this.customerService = customerService;
        this.timereportService = timereportService;
        this.projectService = projectService;
        this.activityService = activityService;
        this.employeeService = employeeService;
        this.queryFromEverywhere = queryFromEverywhere;

        statCardsContainer.getElement().getStyle().set("flex-wrap", "wrap");

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

        projectsel.getElement().getStyle().set("margin-left", "2rem");
        activitysel.getElement().getStyle().set("margin-left", "4rem");

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


        // Adding components to a container and adding the container to the UI
        Div content = new Div();
        content.add(customersel, projectsel, activitysel);

        // Employee Selection for ADMINs
        if (PrincipalUtils.getRole().equals("[ROLE_ADMIN]")){
            employees.setItems(employeeService.findAll());
            employees.addThemeVariants(CheckboxGroupVariant.LUMO_VERTICAL);
            employees.addValueChangeListener( c -> {
                employees.setEnabled(false);
                renderBtnReg.get().remove();
                renderBtnReg.set(renderStatsBtn.addClickListener(evt -> renderStats(c.getValue().iterator().next())));
            });
            employeesel.add("Employees", employees);
            employeesel.close();
            content.add(employeesel);
        }

        content.add(renderStatsBtn);

        add(content);

        add(statCardsContainer);
    }

    private<E> void renderStats(E selection) {
        StatCard customerCard = new StatCard(this, queryFromEverywhere, timereportService, selection);
        // Add style for StatCard (it's a Div)

        // Adding the statistics to the UI
        statCardsContainer.add(customerCard);
    }
}
