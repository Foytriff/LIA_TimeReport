package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.DataProvider;
import se.pulsen.lia_timereportproject.Entities.*;
import se.pulsen.lia_timereportproject.Services.*;

import java.util.List;


public class ReportForm extends FormLayout {

    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;
    TimereportService timereportService;
    EmployeeService employeeService;

    TextField name = new TextField("Employee Name:");
    ComboBox selectionsForActivity = new ComboBox<>("");
    NumberField amountHours = new NumberField("Amount of Hours");
    DatePicker reportDate = new DatePicker("Date for Work Preformed");
    TextArea comment = new TextArea("Comment:");
    Button saveButton = new Button("Save");
    int selectionLevel = 0;

    public ReportForm (CustomerService customerService, ProjectService projectService, ActivityService activityService, TimereportService timereportService, EmployeeService employeeService){
        this.customerService = customerService;
        this.projectService = projectService;
        this.activityService = activityService;
        this.timereportService = timereportService;
        this.employeeService = employeeService;

        // Provide selected class to determine behaviour
        selectionsForActivity.addValueChangeListener(e -> {
            updateLabel(e, selectionsForActivity);
        });

        saveButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> onSave());
        Button undo = new Button("Reset");
        undo.getElement().setAttribute("size", "small");
        HorizontalLayout activity = new HorizontalLayout(selectionsForActivity, undo);

        selectionsForActivity.setItems(customerService.findAll());

        activity.expand(selectionsForActivity);
        activity.setVerticalComponentAlignment(FlexComponent.Alignment.END, undo);
        add(name, activity, amountHours, reportDate, comment, saveButton);
    }

    private void onSave() {

        Employee loggedInnEmployee = employeeService.findEmployeeByName(name.getValue());
        double amountHours = this.amountHours.getValue();
        String reportDate = this.reportDate.getValue().toString();
        String activityID = activityService.activityIDFromName(this.selectionsForActivity.getValue().toString());
        String comment = this.comment.getValue();

        Timereport timereport = new Timereport(loggedInnEmployee, amountHours, reportDate, comment, activityID);
        timereportService.save(timereport);
        Notification notification = Notification.show("Report submitted!");
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        clearFields();
    }

    private void clearFields() {
        name.clear();
        resetActivitySelection();
        reportDate.clear();
        amountHours.clear();
        comment.clear();
    }

    private void resetActivitySelection() {
        selectionsForActivity.setLabel("");
        selectionsForActivity.setItems(customerService.findAll());
        selectionLevel = 0;
    }











    private<T> void updateLabel(HasValue.ValueChangeEvent e, ComboBox<T> field) {

        if(field.isEmpty())
            return;

        System.out.println(field.getValue().getClass().getName());

        Class selection = field.getValue().getClass();

        if (selection.equals(Customer.class)){
            Customer customer = (Customer) field.getValue();
            selectionsForActivity.setItems(projectService.projectsForCustomer(customer.getCustomerID()));

            field.setLabel(customer.getCustomerName());
        } else if (selection.equals(Project.class)){
            Project project = (Project) field.getValue();
            selectionsForActivity.setItems(activityService.findActivitiesForProject(project.getProjectID()));

            field.setLabel(field.getLabel() + ": " + project.getProjectName());
        } else if (selection.equals(Activity.class)){
            Activity activity = (Activity) field.getValue();

            String[] amntSelections = field.getLabel().split(":");
            if(amntSelections.length == 3){
                int lastSeparatorIndex = field.getLabel().lastIndexOf(':');
                String subString = field.getLabel().substring(lastSeparatorIndex);
                field.setLabel(field.getLabel().replace(subString, ": " + activity.getActivityName()));
            } else {
                field.setLabel(field.getLabel() + ": " + activity.getActivityName());
            }
        }


    }

}
