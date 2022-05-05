package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import org.springframework.beans.factory.annotation.Autowired;
import se.pulsen.lia_timereportproject.Entities.Employee;
import se.pulsen.lia_timereportproject.Services.EmployeeService;

import javax.annotation.security.RolesAllowed;

@Route(value = "/admin/manage_employees", layout = MainView.class)
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {
    Grid<Employee> employeeGrid = new Grid<>(Employee.class);

    public AdminView(@Autowired EmployeeService employeeService){

        /*
        Admin ska främst ha möjlighet till att se, ta bort och lägga till anställda.
        Man bör även ha tillgång till samtliga rapporter och möjlighet till att modifiera dem.

        Att ha någon typ av historik för aktivitet i systemet (borttagna/uppdaterade användare/rappporter bör finnas).
        - EMP ACTIONS
        - ADMIN ACTIONS

        Admins bör kunna skapa rapporter i användares namn (Framtida utveckling kan vara att anvä-
        ndaren får en notis när detta händer).
         */
        employeeGrid.setItems(employeeService.findAll());
        employeeGrid.addComponentColumn(employee -> {
            Button deleteButton = new Button(new Icon(VaadinIcon.CLOSE), evt -> {
                employeeService.deleteEmployee(employee);
                updateGrid(employeeService);
            });
            deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return deleteButton;
        });

        Button newEmployee = new Button("Add New Employee", evt -> addEmployee(employeeService));

        add(employeeGrid, newEmployee);
    }

    private void addEmployee(EmployeeService employeeService) {
        Dialog dialog = new Dialog();

        FormLayout empform = new FormLayout();
        TextField empName = new TextField("Employee Name:");
        NumberField empPhone = new NumberField("Phone number:");
        TextField empUsername = new TextField("Username");
        PasswordField empPassword = new PasswordField("Password");

        Button createEmployee = new Button("Register", evt -> {
            saveNewEmployee(empName, empPhone, empUsername, empPassword, employeeService);
            dialog.close();
            updateGrid(employeeService);
        });

        empform.add(empName, empPhone, empUsername, empPassword, createEmployee);
        dialog.add(empform);
        dialog.open();

    }

    private void updateGrid(EmployeeService employeeService){
        employeeGrid.setItems(employeeService.findAll());
    }

    private Employee saveNewEmployee(TextField empName, NumberField empPhone, TextField empUsername, PasswordField empPassword, EmployeeService employeeService) {
        String name = empName.getValue();
        String phone = empPhone.getValue().toString();
        String username = empUsername.getValue();
        String password = empPassword.getValue();
        return employeeService.saveEmployee(new Employee(name,phone,username,password));
    }

}
