package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.context.annotation.Role;
import org.springframework.security.access.annotation.Secured;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Entities.Timereport;
import se.pulsen.lia_timereportproject.Services.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import java.sql.Time;

@Route(value = "/user/myreports", layout = MainView.class)
@RolesAllowed("ADMIN")
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
            loggedInEmployee = employeeService.findEmployeeByUsername(PrincipalUtils.getName());

        if (loggedInEmployee == null)
            return;

        grid.setItems(timereportService.getReportsForEmployee(loggedInEmployee));

        grid.addColumn(tr -> queryFromEverywhere.getCustomerNameFromActivityID(tr.getActivityID())).setHeader("Customer:");
        grid.addColumn(tr -> queryFromEverywhere.getProjectNameFromActivity(tr.getActivityID())).setHeader("Project:");
        grid.addColumn(tr -> queryFromEverywhere.getActivityName(tr.getActivityID())).setHeader("Activity:");
        grid.addColumn(Timereport::getAmountHours).setHeader("Reported Hours:");
        grid.addColumn(Timereport::getReportDate).setHeader("Date of Work:");

        grid.asSingleSelect().addValueChangeListener(evt -> editReport(evt.getValue()));


        add(grid);


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
        grid.setItems(timereportService.getReportsForEmployee(loggedInEmployee));
    }
}
