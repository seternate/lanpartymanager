package controller;

import entities.game.Game;
import entities.game.GameList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class MainController {
    private static Logger log = Logger.getLogger(MainController.class);


    public volatile Game focusedGame;
    @FXML
    private ScrollPane spMain;
    @FXML
    private Label lblStatus, lblFileStatus;
    @FXML
    private ImageView ivUsers, ivSettings, ivOrder, ivServerbrowser;


    @FXML
    private void initialize(){
        log.info("Initializing.");
        //Set the labels for the client to update
        ApplicationManager.setServerStatusLabel(lblStatus);
        ApplicationManager.setFileStatusLabel(lblFileStatus);
        //Create the tooltips for the buttons
        Tooltip.install(ivUsers, new Tooltip("Open userlist"));
        Tooltip.install(ivSettings, new Tooltip("Open settings"));
        Tooltip.install(ivOrder, new Tooltip("Open food-ordering"));
        Tooltip.install(ivServerbrowser, new Tooltip("Open serverbrowser"));
        //Add all handlers to the buttons
        addButtonHandler();
        //Settings for the Scrollpane
        spMain.setFitToWidth(true);
        //Create the gamepane
        updateGamePane();
        //Mouseover effect for buttons
        ivUsers.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivSettings.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivOrder.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivServerbrowser.addEventHandler(MouseEvent.MOUSE_ENTERED, this::mouseEntered);
        ivUsers.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivSettings.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivOrder.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
        ivServerbrowser.addEventHandler(MouseEvent.MOUSE_EXITED, this::mouseExited);
    }

    private void addButtonHandler(){
        //Show UserStage with all connected users
        ivUsers.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.showUsers();
        });
        //Show LoginStage in SettingStage mode
        ivSettings.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.showSettings();
        });
        //Show the food order table
        ivOrder.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.showOrder();
        });
        ivServerbrowser.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton() == MouseButton.PRIMARY)
                ApplicationManager.showServerBrowser();
        });
        log.info("Added all button listener.");
    }

    public void updateGamePane(){
        //Get all games
        GameList games = ApplicationManager.getGames();
        //Pane for gametiles
        GridPane tilePane = new GridPane();
        //Setting the gaps between the gametiles
        tilePane.setHgap(20);
        tilePane.setVgap(30);
        //Create for every game a gametile
        for(int i = 0; i < games.size(); i++){
            Node gameTile = gameTile(games.get(i));
            /*
                Order the gametiles in rows of 3
                Add some settings for the gametiles on the tilepane
             */
            tilePane.addRow(i/3, gameTile);
            GridPane.setHgrow(gameTile, Priority.ALWAYS);
            GridPane.setHalignment(gameTile, HPos.CENTER);
        }
        //Add the gametilepane with all gametile to the scrollpane of the MainStage
        spMain.setContent(tilePane);
        log.info("Updated the gametilepane.");
    }

    private Node gameTile(Game game){
        //Create background imageview of the gametile
        ImageView gameTileImage = new ImageView(getGameCover(game));
        gameTileImage.setPreserveRatio(false);
        //Imageview resizing
        gameTileImage.fitWidthProperty().bind(spMain.widthProperty().divide(3.5));
        gameTileImage.fitHeightProperty().bind(spMain.widthProperty().multiply(0.4));
        //Creates the gametileoverlay with the imageview as background of the game
        VBox gameTileOverlay = gameTileOverlay(gameTileImage, game);
        gameTileOverlay.prefHeightProperty().bind(gameTileImage.fitHeightProperty());
        gameTileOverlay.prefWidthProperty().bind(gameTileImage.fitWidthProperty());
        //Hide the gametile overlay until mouse over event occurs
        gameTileOverlay.setVisible(false);
        //Create the Stackpane holding the gametileimage and the overlay
        StackPane gameTile = new StackPane(gameTileImage, gameTileOverlay);
        //Add the eventhandlers for showing and updating the gametile information
        gameTile.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            focusedGame = game;
            gameTile.getChildren().get(1).setVisible(true);
            event.consume();
        });
        gameTile.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            gameTile.getChildren().get(1).setVisible(false);
            event.consume();
        });
        log.info("Created the gametile of '" + game + "'");
        return gameTile;
    }

    private Image getGameCover(Game game) {
        File coverpath = new File(ApplicationManager.getGamepath() + "/images");
        if(coverpath.listFiles() != null){
            for(File cover : coverpath.listFiles()){
                int index = cover.getName().lastIndexOf(".");
                if(cover.getName().substring(0, index).equals(game.getName())) {
                    log.info("Local cover of '" + game + "' found.");
                    return new Image("file:" + cover.getAbsolutePath(), true);
                }
            }
        }
        if(!game.getCoverUrl().isEmpty()) {
            log.info("Web cover used, because there is no local cover for '" + game + "'.");
            return new Image(game.getCoverUrl(), true);
        }
        log.error("No local or web cover found for '" + game + "'. Using dummy cover.");
        return new Image(ClassLoader.getSystemResource("dummycover.jpg").toString(),true);
    }

    private VBox gameTileOverlay(ImageView gameTileImage, Game game){
        //Loading FXML
        FXMLLoader loader = new FXMLLoader(ClassLoader.getSystemResource("gameoverlay.fxml"));
        //Setting the controller of the gametileoverlay
        loader.setController(new GameOverlayController(gameTileImage, game));
        try {
            return loader.load();
        } catch (IOException e) {
            log.fatal("Problem loading gameoverlay.fxml.", e);
        }
        return null;
    }

    private void mouseEntered(MouseEvent event){
        ImageView imageView = (ImageView)event.getTarget();
        if(imageView.equals(ivOrder))
            imageView.setImage(new Image(ClassLoader.getSystemResource("food_mo.png").toString(), true));
        else if(imageView.equals(ivServerbrowser))
            imageView.setImage(new Image(ClassLoader.getSystemResource("serverbrowser_mo.png").toString(), true));
        else if(imageView.equals(ivSettings))
            imageView.setImage(new Image(ClassLoader.getSystemResource("config_mo.png").toString(), true));
        else if(imageView.equals(ivUsers))
            imageView.setImage(new Image(ClassLoader.getSystemResource("user_mo.png").toString(), true));
    }

    private void mouseExited(MouseEvent event){
        ImageView imageView = (ImageView)event.getTarget();
        if(imageView.equals(ivOrder))
            imageView.setImage(new Image(ClassLoader.getSystemResource("food.png").toString(), true));
        else if(imageView.equals(ivServerbrowser))
            imageView.setImage(new Image(ClassLoader.getSystemResource("serverbrowser.png").toString(), true));
        else if(imageView.equals(ivSettings))
            imageView.setImage(new Image(ClassLoader.getSystemResource("config.png").toString(), true));
        else if(imageView.equals(ivUsers))
            imageView.setImage(new Image(ClassLoader.getSystemResource("user.png").toString(), true));
    }

}
