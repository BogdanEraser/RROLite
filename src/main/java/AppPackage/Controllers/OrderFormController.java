package AppPackage.Controllers;

import AppPackage.Entities.CurrentUser;
import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import AppPackage.Utils.CheckInternetConnnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.ResourceBundle;


public class OrderFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(OrderFormController.class);
    private LocalTime currentTime;
    private Scene scene;
    private MainApp mainApp;
    private Timeline everySecond;
    @FXML
    private BorderPane orderForm;
    @FXML
    private GridPane buttonsGridPane;
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

    public OrderFormController() {
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


    @FXML
    public void initialize() {
        log.debug("Initialising MainForm");
        lblRROSumCash.setText("Сумма оплат наличными: " + CurrentRRO.getInstance((byte) 1, "7", "115200").getCashInRRO());
        lblRROSumCredit.setText("Сумма оплат кредитной картой: " + CurrentRRO.getInstance((byte) 1, "7", "115200").getCreditInRRO());
        lblMessage.setText("Пользователь: " + CurrentUser.getInstance().getName());
        currentTime = LocalTime.now();
        lblTime.setText("Сейчас: " + currentTime.truncatedTo(ChronoUnit.SECONDS).toString());
        if (CheckInternetConnnection.getInstance().isConnected()) {
            ConnectedIcon.setVisible(true);
            NotConnectedIcon.setVisible(false);
        } else {
            ConnectedIcon.setVisible(false);
            NotConnectedIcon.setVisible(true);
        }

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
            }
        }));
        everySecond.setCycleCount(Timeline.INDEFINITE);
        everySecond.play();

        setMainApp(MainFormController.getMainApp());
        //расставим кнопки согласно списку групп
        for (int i = 0; i < mainApp.goodsGroupArrayList.size(); i++) {
            log.debug("Создаю кнопку группы для группы: " + mainApp.goodsGroupArrayList.get(i).getName());
            Button btn = new Button();
            btn.setText(mainApp.goodsGroupArrayList.get(i).getName());
            btn.setId(btn.hashCode() + mainApp.goodsGroupArrayList.get(i).getName());
            final int finalI = i;
            btn.setOnAction(innerEvent -> {
                log.debug("нажали кнопку " + mainApp.goodsGroupArrayList.get(finalI).getName());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(mainApp.getMainStage());
                alert.setTitle("COOL");
                alert.setHeaderText("нажали кнопку " + mainApp.goodsGroupArrayList.get(finalI).getName());
                alert.showAndWait();
            });
            btn.setPrefWidth(300);
            btn.setPrefHeight(80);
            btn.setFont(Font.font("Verdana", 20));
            btn.setWrapText(true);
            btn.setTextOverrun(OverrunStyle.CLIP);
            btn.setTextAlignment(TextAlignment.CENTER);
            if (i <= 9) {
                buttonsGridPane.add(btn, 0, i);
            } else {
                buttonsGridPane.add(btn, 1, i - 10);
            }
        }
    }

    public void setExitButton() {
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
