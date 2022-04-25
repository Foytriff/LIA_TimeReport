package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.data.binder.Binder;
import jdk.jfr.Event;
import org.springframework.beans.factory.annotation.Autowired;
import se.pulsen.lia_timereportproject.Services.ActivityService;
import se.pulsen.lia_timereportproject.Services.CustomerService;
import se.pulsen.lia_timereportproject.Services.ProjectService;

import java.util.ArrayList;
import java.util.List;


public class ReportForm extends FormLayout {

    CustomerService customerService;
    ProjectService projectService;
    ActivityService activityService;

    TextField name = new TextField("Employee Name:");
    ComboBox selectionsForActivity = new ComboBox<>("");
    NumberField amtHrs = new NumberField("Amount of Hours");
    DatePicker reportDate = new DatePicker("Date for Work Preformed");

    String selectionLabel = "";
    int value = 0;

    public ReportForm (CustomerService customerService, ProjectService projectService, ActivityService activityService){
        this.customerService = customerService;
        this.projectService = projectService;
        this.activityService = activityService;

        // Provide selected class to determine behaviour
        selectionsForActivity.addValueChangeListener(e -> {
            updateLabel(e, selectionsForActivity);
        });

        Button undo = new Button("back");
        undo.getElement().setAttribute("size", "small");
        HorizontalLayout activity = new HorizontalLayout(selectionsForActivity, undo);


        selectionsForActivity.setItems(customerService.findAll());

        activity.expand(selectionsForActivity);
        add(name, activity, amtHrs, reportDate);
    }

    private void updateSelections(){

    }

    private void updateLabel(HasValue.ValueChangeEvent e, ComboBox field) {
        if(field.getLabel() == null || field.isEmpty())
            return;

        String[] test = field.getLabel().split(",");

        if(test.length == 1){
            field.setItems(projectService.findAll());
        } else if (test.length == 2){
            field.setItems(activityService.findAll());
        }

        if (field.getLabel().equals("")){
            field.setLabel(e.getValue().toString().toUpperCase());
        } else {
            field.setLabel(field.getLabel() + ", " + e.getValue().toString().toUpperCase());
        }


    }

}
