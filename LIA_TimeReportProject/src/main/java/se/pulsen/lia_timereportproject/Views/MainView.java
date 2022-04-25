package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class MainView extends AppLayout {

    public MainView() {
        // Adding navbar
        HorizontalLayout navbar = new HorizontalLayout();
        H1 navbarTitle = new H1("My page");
        Button loginButton = new Button("Login");

        navbar.add(new DrawerToggle(), navbarTitle, loginButton);

        navbar.setWidthFull();
        navbar.setMargin(true);
        navbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        navbar.setAlignItems(FlexComponent.Alignment.CENTER);



        addToNavbar(navbar);
    }
}
