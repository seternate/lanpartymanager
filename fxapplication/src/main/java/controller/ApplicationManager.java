package controller;

import clientInterface.Client;
import clientInterface.FXDataClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ApplicationManager {
    public static ApplicationManager manager = null;

    private Stage primaryStage;
    private Client client;


    public ApplicationManager(Stage primaryStage){
        client = new Client();
        manager = this;
        this.primaryStage = primaryStage;
        URL iconURL = ClassLoader.getSystemResource("icon.png");
        primaryStage.getIcons().add(new Image(iconURL.toExternalForm()));
    }

    public Client getClient(){
        return client;
    }

    public void loadLogin(){
        primaryStage.hide();
        setTitle("Login");
        loadFXML("login.fxml");
        primaryStage.show();
    }

    private void setTitle(String title){
        primaryStage.setTitle("Lanpartymanager - " + title);
    }

    private void loadFXML(String fxmlSource){
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource(fxmlSource));
        Parent rootNode = null;
        try {
            rootNode = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(new Scene(rootNode));
    }


}
