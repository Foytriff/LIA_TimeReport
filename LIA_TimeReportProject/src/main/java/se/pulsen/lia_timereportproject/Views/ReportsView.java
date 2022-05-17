package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import se.pulsen.lia_timereportproject.Entities.*;
import se.pulsen.lia_timereportproject.Services.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import javax.annotation.security.PermitAll;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Route(value = "/user/myreports", layout = MainView.class)
@PermitAll
public class ReportsView extends VerticalLayout {

    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;

    EmployeeService employeeService; //Varför funkar inte autowired här? (även om konstruktorn ändras)

    TimereportService timereportService;

    QueryFromEverywhere queryFromEverywhere;

    List<Timereport> reportsToRender = new ArrayList<>();

    Employee loggedInEmployee = null;
    Grid<Timereport> grid = new Grid<>(Timereport.class, false);

    List<Timereport> employeeFilteredReportList = new ArrayList<>();
    List<Timereport> noEmployeeFilteredReportList = new ArrayList<>();
    List<Employee> selectedEmployees = new ArrayList<>();

    public ReportsView(EmployeeService employeeService, TimereportService timereportService, QueryFromEverywhere queryFromEverywhere, CustomerService customerService, ProjectService projectService, ActivityService activityService){
        this.employeeService = employeeService;
        this.timereportService = timereportService;
        this.queryFromEverywhere = queryFromEverywhere;
        this.customerService = customerService;
        this.projectService = projectService;
        this.activityService = activityService;

        if(PrincipalUtils.isAuthenticated())
            loggedInEmployee = employeeService.findEmployeeByUsername(PrincipalUtils.getUsername());

        if (loggedInEmployee == null)
            return;

        String loggedInRole = PrincipalUtils.getRole();

        switch (loggedInRole){
            case "[ROLE_ADMIN]":
                renderAdminView();
                break;
            case "[ROLE_USER]":
                renderEmployeeView();
                break;
            default:
                renderEmployeeView();
                break;
        }

        add(grid);


    }

    private void renderAdminView(){

        HorizontalLayout filterCheckboxes = new HorizontalLayout();
        CheckboxGroup<Customer> filterCustomer = new CheckboxGroup();
        CheckboxGroup<Project> filterProject = new CheckboxGroup();
        CheckboxGroup<Activity> filterActivity = new CheckboxGroup();
        CheckboxGroup<Employee> filterEmployee = new CheckboxGroup();

        Accordion customersel = new Accordion();
        Accordion projectsel = new Accordion();
        Accordion activitysel = new Accordion();
        Accordion employeesel = new Accordion();
        Button renderTrBtn = new Button("Render Reports");
        Button newFilter = new Button("New Filter Search");

        newFilter.setVisible(false);
        newFilter.getElement().getStyle().set("padding-left", "1rem");
        newFilter.addClickListener(evt -> {
            reportsToRender = new ArrayList<>();
            filterCustomer.clear();
            filterProject.clear();
            filterActivity.clear();
            filterEmployee.clear();
            reportsToRender.clear();
            filterCustomer.setReadOnly(false);
            filterProject.setReadOnly(false);
            filterActivity.setReadOnly(false);
            filterEmployee.setReadOnly(false);
            employeeFilteredReportList = new ArrayList<>();
            noEmployeeFilteredReportList = new ArrayList<>();
            selectedEmployees = new ArrayList<>();
            newFilter.setVisible(false);
        });

        // cust -> emp1 -> emp2 == reports for emp1 only (fix so both emps trs shows)

        AtomicReference<Registration> currentClick = new AtomicReference<>(renderTrBtn.addClickListener(evt -> Notification.show("No selection")));

        customersel.add("Customers", filterCustomer);
        projectsel.add("Projects", filterProject);
        activitysel.add("Activities", filterActivity);
        employeesel.add("Employees", filterEmployee);

        filterCheckboxes.add(customersel, projectsel, activitysel, employeesel);


        employeesel.close();
        filterEmployee.setItems(employeeService.findAll());
        // EMPLOYEE FILTER
        filterEmployee.addValueChangeListener(evt -> {
                currentClick.get().remove();
                // sets renderbutton when emp is selected
                currentClick.set(renderTrBtn.addClickListener( e -> {
                    grid.setItems(reportsToRender);
                    filterCustomer.setReadOnly(true);
                    filterProject.setReadOnly(true);
                    filterActivity.setReadOnly(true);
                    filterEmployee.setReadOnly(true);
                    newFilter.setVisible(true);
                }));

                reportsToRender = new ArrayList<>();

                employeeFilteredReportList = noEmployeeFilteredReportList;
                selectedEmployees = evt.getValue().stream().toList();

                // Sets reports if no customer is selected
                if (filterCustomer.getValue().stream().allMatch(sel -> sel == null)){
                    //if (!(reportsToRender == null || reportsToRender.isEmpty()))        // this does nothing... I think
                    //    reportsToRender.removeAll(reportsToRender);

                    selectedEmployees.forEach(selection -> {
                    reportsToRender.addAll(timereportService.getReportsForEmployee(selection));
                    });
                } else {
                    // Adding employee filter to already filtered list
                        reportsToRender = employeeFilteredReportList //fix multiple employees on order: custoemr select -> one emp select, two emp select EDIT: added a new list w/o empFilter
                                .stream()
                                .filter(tr -> {
                                    boolean match = selectedEmployees.stream().anyMatch(emp -> tr.getEmployeeID().equals(emp.getEmployeeID()));
                                    return match;
                                })
                                .toList();
                }
        });


        // CUSTOMER FILTER
        filterCustomer.setRequired(true);
        filterCustomer.addValueChangeListener(evt -> {
            // If customer is unselected
            if (evt.getValue().isEmpty() || evt.getValue() == null){
                grid.setItems();
            }

            List<Project> projectsForCustomers = new ArrayList<>();
            evt.getValue().forEach(selection -> {

                // Code for employee selection prior to customer sel
                if(selectedEmployees.size() > 0){
                    List<Timereport> temp = new ArrayList<>();
                    temp.addAll(queryFromEverywhere.getTimereportsForCustomer(selection.getCustomerID()));
                    temp = temp
                            .stream()
                            .filter(tr -> {

                                for(Employee emp : selectedEmployees){
                                    if(tr.getEmployeeID().equals(emp.getEmployeeID())){
                                        return true;
                                    }
                                }
                                return false;
                            })
                            .distinct()
                            .collect(Collectors.toList());
                    reportsToRender.removeAll(reportsToRender);
                    reportsToRender.addAll(temp);
                } else {
                    // if no emp is selected, when selecting customer
                    reportsToRender.addAll(queryFromEverywhere.getTimereportsForCustomer(selection.getCustomerID()));
                    noEmployeeFilteredReportList.addAll(queryFromEverywhere.getTimereportsForCustomer(selection.getCustomerID())); // bonus list
                }
                projectService.projectsForCustomer(selection.getCustomerID()).forEach( project -> {
                    projectsForCustomers.add(project);
                });
            });
            currentClick.get().remove();
            currentClick.set(renderTrBtn.addClickListener( e -> {
                grid.setItems(reportsToRender);
                filterCustomer.setReadOnly(true);
                filterProject.setReadOnly(true);
                filterActivity.setReadOnly(true);
                filterEmployee.setReadOnly(true);
                newFilter.setVisible(true);
            }));
            filterProject.setItems(projectsForCustomers);
        });


        // PROJECT FILTER
        filterProject.setRequired(true);
        filterProject.addValueChangeListener(evt -> {

            List<Activity> activitiesForProjects = new ArrayList<>();
            reportsToRender = new ArrayList<>();
            // old: reportsToRender.removeAll(reportsToRender);
            evt.getValue().forEach(selection -> {

                if(selectedEmployees.size() > 0){
                    List<Timereport> temp = new ArrayList<>();
                    temp.addAll(queryFromEverywhere.getTimereportsForProject(selection));
                    temp = temp
                            .stream()
                            .filter(tr -> {

                                for(Employee emp : selectedEmployees){
                                    if(tr.getEmployeeID().equals(emp.getEmployeeID())){
                                        return true;
                                    }
                                }
                                return false;
                            })
                            .distinct()
                            .collect(Collectors.toList());
                    reportsToRender.removeAll(reportsToRender);
                    reportsToRender.addAll(temp);
                } else {
                    reportsToRender.addAll(queryFromEverywhere.getTimereportsForProject(selection));
                }

                activityService.findActivitiesForProject(selection).forEach( activity -> {
                    activitiesForProjects.add(activity);
                });
            });
            filterActivity.setItems(activitiesForProjects);
        });

        // ACTIVITY FILTER
        filterActivity.addValueChangeListener(evt -> {
            reportsToRender = new ArrayList<>();
           evt.getValue().forEach(selection -> {

               if(selectedEmployees.size() > 0){
                   List<Timereport> temp = new ArrayList<>();
                   temp.addAll(queryFromEverywhere.getTimereportsForActivity(selection));
                   temp = temp
                           .stream()
                           .filter(tr -> {

                               for(Employee emp : selectedEmployees){
                                   if(tr.getEmployeeID().equals(emp.getEmployeeID())){
                                       return true;
                                   }
                               }
                               return false;
                           })
                           .distinct()
                           .collect(Collectors.toList());
                   reportsToRender.removeAll(reportsToRender);
                   reportsToRender.addAll(temp);
               } else {
                   reportsToRender.addAll(queryFromEverywhere.getTimereportsForActivity(selection));
               }
               noEmployeeFilteredReportList.addAll(queryFromEverywhere.getTimereportsForActivity(selection));
           });
        });




        filterCustomer.setItems(customerService.findAll());


        HorizontalLayout buttons = new HorizontalLayout(renderTrBtn, newFilter);
        this.add(filterCheckboxes, buttons);

        grid.addColumn(tr -> employeeService.getEmployeeNameFromID(tr.getEmployeeID())).setHeader("Employee:");
        grid.addColumn(tr -> queryFromEverywhere.getCustomerNameFromActivityID(tr.getActivityID())).setHeader("Customer:");
        grid.addColumn(tr -> queryFromEverywhere.getProjectNameFromActivity(tr.getActivityID())).setHeader("Project:");
        grid.addColumn(tr -> queryFromEverywhere.getActivityName(tr.getActivityID())).setHeader("Activity:");
        grid.addColumn(tr -> tr.getAmountHours()).setHeader("Amount of Hours");
        grid.addColumn(tr -> tr.getReportDate()).setHeader("Report Date:");
        grid.addColumn(tr -> tr.getSubmitDate()).setHeader("Submitted:");

        grid.addComponentColumn(tr -> {
            Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE), evt -> {
                timereportService.deleteTimereport(tr);
                reportsToRender.remove(tr);
                updateGrid(timereportService);
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return deleteButton;
        });

        grid.getColumns().forEach(column -> column.setSortable(true));
        grid.setAllRowsVisible(true);

        grid.asSingleSelect().addValueChangeListener(evt -> editReport(evt.getValue()));
    }

    private void updateGrid(TimereportService timerportService) {
        GridListDataView hej = reportsToRender.isEmpty() ? grid.setItems(timerportService.getReportsAdmin()) : grid.setItems(reportsToRender);
    }

    private void renderEmployeeView(){
        grid.setItems(timereportService.getReportsForEmployee(loggedInEmployee));

        grid.addColumn(tr -> queryFromEverywhere.getCustomerNameFromActivityID(tr.getActivityID())).setHeader("Customer:");
        grid.addColumn(tr -> queryFromEverywhere.getProjectNameFromActivity(tr.getActivityID())).setHeader("Project:");
        grid.addColumn(tr -> queryFromEverywhere.getActivityName(tr.getActivityID())).setHeader("Activity:");
        grid.addColumn(Timereport::getAmountHours).setHeader("Reported Hours:");
        grid.addColumn(Timereport::getReportDate).setHeader("Date of Work:");

        grid.getColumns().forEach(column -> column.setSortable(true));
        grid.setAllRowsVisible(true);

        grid.asSingleSelect().addValueChangeListener(evt -> editReport(evt.getValue()));
    }

    private void editReport(Timereport timereport) {

        if(timereport == null)
            return;

        Dialog dialog = new Dialog();

        ReportForm reportForm = new ReportForm(customerService, projectService, activityService, timereportService, employeeService, this);

        reportForm.setValues(timereport);

        dialog.add(reportForm);
        dialog.open();
    }

    public void renderReports(){
        if(PrincipalUtils.isAdmin()){
            if(reportsToRender.isEmpty()){
                grid.setItems(timereportService.getReportsAdmin());
            } else {
                grid.setItems(reportsToRender);
            }
        } else {
            grid.setItems(timereportService.getReportsForEmployee(loggedInEmployee));
        }
    }
}
