package chess.controller;

import chess.model.ChessFacade;
import chess.model.Piece;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * MenuController handles the menu
 */
public class MenuController implements Initializable {
    private ChessFacade model = ChessFacade.getInstance();
    private Parent chessParent;
    private Scene scene;

    private String media_URL_1 = "/background_videos/background_video_1.mp4";
    private String media_URL_2 = "/background_videos/background_video_2.mp4";
    private String media_URL_3 = "/background_videos/background_video_3.mp4";
    private String media_URL_4 = "/background_videos/background_video_4.mp4";
    List<String> media_list = Arrays.asList(media_URL_1, media_URL_2, media_URL_3, media_URL_4);

    private MediaPlayer mediaPlayer;

    private ChessController chessController;

    public void setChessController(ChessController chessController) {
        this.chessController = chessController;
    }

    private HashMap<String, Integer> timerMap = new LinkedHashMap<>();

    @FXML private MediaView media;
    @FXML private AnchorPane rootAnchor;
    @FXML private ImageView btnStart;
    @FXML private ImageView btnExit;
    @FXML private TextField player1NameField;
    @FXML private TextField player2NameField;
    @FXML private Label timeLabel;
    @FXML private ComboBox btnTimerDrop;

    /**
     * Gets the inputs from the start page and switches to the board scene, and brings the inputs with it
     *
     * Happens when you click the start button
     *
     * @param event Clicked the button
     */
    @FXML
    void goToBoard (MouseEvent event) {
        //Does not create a new boardmap
        model.createNewGame();
        model.getCurrentGame().initGame();

        chessController.updateImageHandler();

        model.getPlayer1().setName(player1NameField.getText());
        model.getPlayer2().setName(player2NameField.getText());
        model.getPlayer1().getTimer().setTime(timerMap.get(btnTimerDrop.getValue()));
        model.getPlayer2().getTimer().setTime(timerMap.get(btnTimerDrop.getValue()));

        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();

        chessController.init();
        chessController.drawPieces();
    }

    public void createChessScene(Parent chessParent){
        this.chessParent = chessParent;
        this.scene = new Scene(chessParent);
    }

    /**
     * Exits the application when called
     *
     * @param event Pressed the button
     */
    @FXML
    void Exit (MouseEvent event) {
        System.exit(0);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Fetches a random video from /background_videos/ and sets it as the background in our root
        Random ran = new Random();
        int videoIndex = ran.nextInt(4);
        mediaPlayer = new MediaPlayer(new Media(getClass().getResource(media_list.get(videoIndex)).toExternalForm()));
        mediaPlayer.setAutoPlay(true);
        media.setMediaPlayer(mediaPlayer);

        initTimer();
    }

    /**
     * creates the timermap and gives the dropdown menu its options
     *
     * Where the possible times is decided
     */
    private void initTimer(){
        timerMap.put("03:00 min", 180);
        timerMap.put("05:00 min", 300);
        timerMap.put("10:00 min", 600);
        timerMap.put("30:00 min", 1800);
        timerMap.put("60:00 min", 3600);

        btnTimerDrop.getSelectionModel().selectFirst();

        timerMap.forEach((key,value) -> btnTimerDrop.getItems().add(key));
    }

    //Game

}
