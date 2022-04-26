package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Entities.Timereport;
import se.pulsen.lia_timereportproject.Services.EmployeeService;
import se.pulsen.lia_timereportproject.Services.TimereportService;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import javax.annotation.security.PermitAll;

@Route(value = "/user/myreports", layout = MainView.class)
@PermitAll
public class ReportsView extends VerticalLayout {

    EmployeeService employeeService; //Varför funkar inte autowired här? (även om konstruktorn ändras)

    TimereportService timereportService;

    Employee loggedInEmployee = null;
    Grid<Timereport> grid = new Grid<>(Timereport.class);

    public ReportsView(EmployeeService employeeService, TimereportService timereportService){
        this.employeeService = employeeService;
        this.timereportService = timereportService;
        if(PrincipalUtils.isAuthenticated())
            loggedInEmployee = employeeService.findEmployeeByUsername(PrincipalUtils.getName());

        if (loggedInEmployee == null)
            return;

        grid.setItems(timereportService.getReportsForEmployee(loggedInEmployee));

        add(grid);


    }
}
