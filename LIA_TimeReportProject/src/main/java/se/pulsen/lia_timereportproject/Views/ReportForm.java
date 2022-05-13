package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.datepicker.DatePicker;
import se.pulsen.lia_timereportproject.Entities.*;
import se.pulsen.lia_timereportproject.Services.*;
import se.pulsen.lia_timereportproject.security.PrincipalUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class ReportForm extends FormLayout {

    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;
    TimereportService timereportService;
    EmployeeService employeeService;

    ComboBox name = new ComboBox("Employee Name:");
    ComboBox selectionsForActivity = new ComboBox<>("");
    NumberField amountHours = new NumberField("Amount of Hours");
    DatePicker reportDate = new DatePicker("Date for Work Preformed");
    TextArea comment = new TextArea("Comment:");
    Button saveButton = new Button("Save");
    Button undo = new Button("Reset");
    Employee loggedInEmployee = null;
    Timereport editedReport;
    ReportsView reportsView;

    public ReportForm (CustomerService customerService, ProjectService projectService, ActivityService activityService, TimereportService timereportService, EmployeeService employeeService, ReportsView reportsView){
        this(customerService, projectService, activityService, timereportService, employeeService);
        this.reportsView = reportsView;
    }

    public ReportForm (CustomerService customerService, ProjectService projectService, ActivityService activityService, TimereportService timereportService, EmployeeService employeeService){
        this.customerService = customerService;
        this.projectService = projectService;
        this.activityService = activityService;
        this.timereportService = timereportService;
        this.employeeService = employeeService;
        this.editedReport = null;

        if (PrincipalUtils.isAuthenticated()){
            loggedInEmployee = employeeService.findEmployeeByUsername(PrincipalUtils.getUsername());
            name.setItems(employeeService.findAll());
            name.setValue(loggedInEmployee);
            if(!PrincipalUtils.isAdmin()){
                name.setReadOnly(true);
            } else {
                name.setValue("");
            }
        }

        name.setRequired(true);
        selectionsForActivity.setRequired(true);
        selectionsForActivity.setErrorMessage("Please provide: Customer, Project and Activity");
        amountHours.setRequiredIndicatorVisible(true);
        reportDate.setRequired(true);
        amountHours.setMax(16);
        amountHours.setMin(0);

        // Provide selected class to determine behaviour
        selectionsForActivity.addValueChangeListener(e -> {
            updateLabel(e, selectionsForActivity);
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> onSave());
        undo.getElement().setAttribute("size", "small");
        undo.addClickListener(e -> resetActivitySelection());
        HorizontalLayout activity = new HorizontalLayout(selectionsForActivity, undo);

        selectionsForActivity.setItems(customerService.findAll());

        activity.expand(selectionsForActivity);
        activity.setVerticalComponentAlignment(FlexComponent.Alignment.END, undo);
        add(name, activity, amountHours, reportDate, comment, saveButton);
    }

    private void onSave() {

        if (name.getValue() == null || selectionsForActivity.getValue() == null || amountHours.getValue() == null || reportDate.getValue() == null){
            Notification.show("Please make sure to fill every required field");
            return;
        }


        double amountHours = this.amountHours.getValue();
        String reportDate = this.reportDate.getValue().toString();
        Activity activity = (Activity) this.selectionsForActivity.getValue(); //OBS!
        String activityID = activity.getActivityID();
        String comment = this.comment.getValue();
        Employee employee = (Employee) name.getValue();

        Timereport newReport = new Timereport(employee, amountHours, reportDate, comment, activityID);

        if(loggedInEmployee == null)
            return;

        if(editedReport == null){
            timereportService.save(newReport);
            Notification notification = Notification.show("Report submitted!");
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            clearFields();

        } else {
            editedReport.setAmountHours(this.amountHours.getValue());
            editedReport.setReportDate(this.reportDate.getValue().toString());
            editedReport.setActivityID(activity.getActivityID());
            editedReport.setComment(this.comment.getValue());
            editedReport.setEmployee((Employee) name.getValue());
            timereportService.save(editedReport);
            Notification.show("Report Updated!");

            this.getParent().ifPresent(component -> {
                            if(component instanceof Dialog){
                                ((Dialog) component).close();
                            }
                        });
            if(reportsView != null)
                reportsView.renderReports();
            editedReport = null;
        }

    }

    private void clearFields() {
        if (PrincipalUtils.isAdmin())
        name.clear();

        resetActivitySelection();
        reportDate.clear();
        amountHours.clear();
        comment.clear();
    }

    private void resetActivitySelection() {
        selectionsForActivity.setLabel("");
        selectionsForActivity.setItems(customerService.findAll());
    }

    private<T> void updateLabel(HasValue.ValueChangeEvent e, ComboBox<T> field) {

        // OBS: ändra så att etiketten uppdateras korrekt oavsett selektionsnivå (man ska inte behöva välja KUND - PROJ - AKT, i den ordningen)
        if(field.isEmpty())
            return;

        Class selection = field.getValue().getClass();

        if (selection.equals(Customer.class)){
            Customer customer = (Customer) field.getValue();
            Notification.show(customer.getCustomerID());
            selectionsForActivity.setItems(projectService.projectsForCustomer(customer.getCustomerID()));

            field.setLabel(customer.getCustomerName().toUpperCase());
        } else if (selection.equals(Project.class)){
            Project project = (Project) field.getValue();
            selectionsForActivity.setItems(activityService.findActivitiesForProject(project));




            //-----
            field.setLabel(field.getLabel() + ": " + project.getProjectName().toUpperCase());




        } else if (selection.equals(Activity.class)){
            Activity activity = (Activity) field.getValue();

            String[] amntSelections = field.getLabel().split(":");
            if(amntSelections.length == 3){
                int lastSeparatorIndex = field.getLabel().lastIndexOf(':');
                String subString = field.getLabel().substring(lastSeparatorIndex);
                field.setLabel(field.getLabel().replace(subString, ": " + activity.getActivityName().toUpperCase()));
            } else {
                field.setLabel(field.getLabel() + ": " + activity.getActivityName().toUpperCase());
            }
        }


    }

    public void setValues(Timereport timereport) {
        this.editedReport = timereport;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(timereport.getReportDate(), dtf);

        reportDate.setReadOnly(true);
        amountHours.setReadOnly(true);
        comment.setReadOnly(true);
        name.setReadOnly(true);
        selectionsForActivity.setReadOnly(true);
        undo.setEnabled(false);
        reportDate.setValue(localDate);
        amountHours.setValue(timereport.getAmountHours());
        comment.setValue(timereport.getComment());

        Activity a = activityService.getActivityFromID(timereport.getActivityID());
        Project p = projectService.getProjectFromID(a.getProject().getProjectID());
        Customer c = customerService.getCustomerFromID(p.getCustomerID());

        selectionsForActivity.setLabel(c.getCustomerName() + ": " + p.getProjectName() + ": " + a.getActivityName());
        selectionsForActivity.setItems(activityService.findActivitiesForProject(p));
        selectionsForActivity.setValue(activityService.getActivityFromID(timereport.getActivityID()));
        name.setValue(employeeService.getEmployeeFromID(timereport.getEmployeeID())); //kanske att denna krånglar för att boxen innehåller en sträng ist för en employee. EDIT: verkar funka ändå! EDIT: den funkade inte om man redigerade och tryckte save
        remove(saveButton);
        Button editButton = new Button("Edit");
        editButton.addClickListener(evt -> editReport(editButton));
        add(editButton);
    }

    private void editReport(Button editButton) {
        reportDate.setReadOnly(false);
        amountHours.setReadOnly(false);
        comment.setReadOnly(false);
        selectionsForActivity.setReadOnly(false);
        undo.setEnabled(true);

        if(PrincipalUtils.isAdmin()){
            name.setReadOnly(false);
        }

        remove(editButton);
        add(saveButton);
    }
}
