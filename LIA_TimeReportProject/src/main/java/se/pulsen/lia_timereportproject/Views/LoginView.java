package se.pulsen.lia_timereportproject.Views;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.Route;

@Route("/login")
public class LoginView extends Div implements BeforeEnterListener {

    LoginOverlay loginOverlay = new LoginOverlay();

    public LoginView(){
        loginOverlay.setTitle("Logga in");
        loginOverlay.setDescription("gör det ja wooo");
        loginOverlay.setOpened(true);
        loginOverlay.setAction("login");

        add(loginOverlay);

    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if(beforeEnterEvent.getLocation().getQueryParameters().getParameters().containsKey("error")){
            loginOverlay.setError(true);
            add(loginOverlay);
        }
    }
}
