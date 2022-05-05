package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Entities.Timereport;
import se.pulsen.lia_timereportproject.Services.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import javax.annotation.security.PermitAll;

@Route(value = "/user/myreports", layout = MainView.class)
@PermitAll
public class ReportsView extends VerticalLayout {

    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;

    EmployeeService employeeService; //Varför funkar inte autowired här? (även om konstruktorn ändras)

    TimereportService timereportService;

    QueryFromEverywhere queryFromEverywhere;

    Employee loggedInEmployee = null;
    Grid<Timereport> grid = new Grid<>(Timereport.class, false);

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

        GridListDataView<Timereport> dataView = grid.setItems(timereportService.getReportsAdmin());

        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder("Search");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> dataView.refreshAll());

        this.add(searchField);

        dataView.addFilter(timereport -> {
            String searchTerm = searchField.getValue().trim();

            if (searchTerm.isEmpty())
                return true;

            boolean matchesFullName = matches(employeeService.getEmployeeNameFromID(timereport.getEmployeeID()),
                    searchTerm);
            boolean matchesCustomerName = matches(customerService.getCustomerFromID(projectService.getProjectFromID(activityService.getActivityFromID(timereport.getActivityID()).getProject().getProjectID()).getCustomerID()).getCustomerName(), searchTerm);

            return matchesFullName || matchesCustomerName;
        });

        grid.addColumn(tr -> employeeService.getEmployeeNameFromID(tr.getEmployeeID())).setHeader("Employee:");
        grid.addColumn(tr -> tr.getAmountHours()).setHeader("Amount of Hours");
        grid.addColumn(tr -> tr.getReportDate()).setHeader("Report Date:");
        grid.addColumn(tr -> tr.getSubmitDate()).setHeader("Submitted:");

        grid.getColumns().forEach(column -> column.setSortable(true));
        grid.setAllRowsVisible(true);

        grid.asSingleSelect().addValueChangeListener(evt -> editReport(evt.getValue()));
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
            grid.setItems(timereportService.getReportsAdmin());
        } else {
            grid.setItems(timereportService.getReportsForEmployee(loggedInEmployee));
        }
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty() || value
                .toLowerCase().contains(searchTerm.toLowerCase());
    }
}
