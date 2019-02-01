package stages;

import controller.ApplicationManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.InputStream;

public class LoginStage extends Stage {

    public LoginStage(){
        super();
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("login.fxml"));
        try {
            Parent rootNode = loader.load();
            setScene(new Scene(rootNode));
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream icon = ClassLoader.getSystemResourceAsStream("icon.png");
        if (icon != null) {
            getIcons().add(new Image(icon));
        }
        setTitle("Lanpartymanager - Login");
        setResizable(false);
        //controller = loader.getController();
    }

    public void show(PreloaderStage preloaderStage){
        super.show();
        preloaderStage.hide();
    }

    @Override
    public void hide(){
        super.hide();
        ApplicationManager.close();
    }

    /*
    private FXDataService client;
    private Stage stage;
    private ServerStatus status;
    private InterfaceController interfaceController;
    @FXML
    private TextField txtfieldUsername, txtfieldGamepath;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnFinish;


    public LoginStage() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:8080/fx/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        client = retrofit.create(FXDataService.class);
        interfaceController = new InterfaceController();
    }

    @Override
    public void init(){
        lookForClient();
        updateStatus();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        User login = getLogin();
        this.stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("login.fxml"));
        loader.setController(this);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("Interface.css")).toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Lanpartymanager - Start");
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("icon.png")).toExternalForm()));
        txtfieldUsername.setText(login.getUsername());
        txtfieldGamepath.setText(login.getGamepath());
        primaryStage.show();
    }

    @Override
    public void stop(){
        System.exit(0);
    }

    @FXML
    public void openInterface() throws IOException {
        if(!status.serverConnection) {
            System.err.println("No connection to a server.");
            return;
        }
        if(txtfieldUsername.getText().trim().equals("") || txtfieldGamepath.getText().trim().equals("")) return;
        User user = new User(txtfieldUsername.getText().trim(), txtfieldGamepath.getText().trim());
        Call<Boolean> postLogin = client.login(user);
        postLogin.execute();
        loadInterface();
    }

    @FXML
    public void enter(KeyEvent event) throws IOException {
        if(event.getCode() == KeyCode.ENTER){
            openInterface();
        }
    }

    private void loadInterface() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Interface.fxml"));
        loader.setController(interfaceController);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getClassLoader().getResource("Interface.css")).toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Lanpartymanager");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResource("icon.png")).toExternalForm()));
        stage.setResizable(false);
        interfaceController.load();
        this.stage.hide();
        stage.show();
    }

    private void lookForClient(){
        try {
            client.getStatus().execute();
        } catch (ConnectException e) {
            System.err.println("No client running.");
            System.exit(-15);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-16);
        }
    }

    private void updateStatus(){
        Call<ServerStatus> callStatus = client.getStatus();
        callStatus.enqueue(new Callback<ServerStatus>() {
            @Override
            public void onResponse(Call<ServerStatus> call, Response<ServerStatus> response) {
                status = response.body();
                interfaceController.status = status;
                if(Objects.requireNonNull(status).serverConnection)
                    Platform.runLater(() -> {
                        if(stage.isShowing())
                            lblStatus.setText("Connected to server: " + status.serverIP);
                        else
                            interfaceController.lblStatus.setText("Connected to server: " + status.serverIP);
                    });
                else
                    Platform.runLater(() -> {
                        if(stage.isShowing())
                            lblStatus.setText("No server found.");
                        else
                            interfaceController.lblStatus.setText("No server found.");
                    });
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateStatus();
            }
            @Override
            public void onFailure(Call<ServerStatus> call, Throwable t) {
                System.err.println("No client running.");
                System.exit(-19);
            }
        });
    }

    private User getLogin(){
        User login = null;
        try {
            login = client.getLogin().execute().body();
        } catch (ConnectException e) {
            e.printStackTrace();
            System.err.println("No client running.");
            System.exit(-18);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No logindata received.");
            System.exit(-17);
        }
        return login;
    }
    */
}
