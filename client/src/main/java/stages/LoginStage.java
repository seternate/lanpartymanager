package stages;

import org.apache.log4j.Logger;

public class LoginStage extends Stage {

    public LoginStage(){
        setTitle("Lanpartymanager - Login");
        setResizable(false);
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(LoginStage.class);
    }

    @Override
    public String getFXML() {
        return "login.fxml";
    }

    @Override
    public void hide() {
        super.hide();
        getController().shutdown();
    }

}
