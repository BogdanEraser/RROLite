package AppPackage.Controllers;

import AppPackage.CurrentUser;
import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import AppPackage.Utils.CheckInternetConnnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(MainFormController.class);
    public static LocalTime currentTime;
    private Scene scene;
    private MainApp mainApp;
    private Timeline everySecond;
    @FXML
    private BorderPane MainForm;
    @FXML
    private Label lblTime;
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblRROSumCash;
    @FXML
    private Label lblRROSumCredit;
    @FXML
    private Button btnExit;
    @FXML
    private ImageView ConnectedIcon;
    @FXML
    private ImageView NotConnectedIcon;
    private ResourceBundle bundle;

    public MainFormController() {
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void setLblMessage(String caption) {
        this.lblMessage.setText(caption);
    }

    @FXML
    public void initialize() {
        log.debug("Initialising MainForm");
        lblRROSumCash.setText("Сумма оплат наличными: " + CurrentRRO.getInstance((byte) 1, "7", "115200").getCashInRRO());
        lblRROSumCredit.setText("Сумма оплат кредитной картой: " + CurrentRRO.getInstance((byte) 1, "7", "115200").getCreditInRRO());
        currentTime = LocalTime.now();
        lblMessage.setText("Здравствуйте, " + CurrentUser.getInstance().getName());
        lblTime.setText("Сейчас: " + currentTime.truncatedTo(ChronoUnit.SECONDS).toString());

        everySecond = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            currentTime = LocalTime.now();
            lblTime.setText("Сейчас: " + currentTime.truncatedTo(ChronoUnit.SECONDS).toString());
            if (currentTime.getSecond() % 10 == 0) { //каждые 10 секунд проверяем наличие интернет-соединения
                if (CheckInternetConnnection.getInstance().isConnected()) {
                    ConnectedIcon.setVisible(true);
                    NotConnectedIcon.setVisible(false);
                } else {
                    ConnectedIcon.setVisible(false);
                    NotConnectedIcon.setVisible(true);
                }
            } else {
                lblMessage.setText("Здравствуйте, " + CurrentUser.getInstance().getName());
            }
        }));
        everySecond.setCycleCount(Timeline.INDEFINITE);
        everySecond.play();
    }

    public void ExitButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("Уточнение");
        alertQuestion.setHeaderText("Вы действительно хотите выйти?");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

}
