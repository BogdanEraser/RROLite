package AppPackage.Controllers;

import AppPackage.Entities.GoodsInCheck;
import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import AppPackage.Utils.CheckInternetConnnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ResourceBundle;


public class OrderFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(OrderFormController.class);
    private Scene scene;
    private static MainApp mainApp;
    public static ObservableList<GoodsInCheck> goodsInCheckObservableList = FXCollections.observableArrayList();

    @FXML
    private static BorderPane OrderForm;
    @FXML
    private GridPane buttonsGridPane;
    @FXML
    private Label lblRROSumCash;
    @FXML
    private Label lblRROSumCredit;
    @FXML
    private ImageView ConnectedIcon;
    @FXML
    private ImageView NotConnectedIcon;
    @FXML
    private TableView<GoodsInCheck> checkTableView;
    @FXML
    private TableColumn<GoodsInCheck, String> goodsNameColumn;
    @FXML
    private TableColumn<GoodsInCheck, BigDecimal> goodsPriceColumn;
    @FXML
    private TableColumn<GoodsInCheck, BigDecimal> goodsQtyColumn;
    @FXML
    private TableColumn<GoodsInCheck, BigDecimal> goodsSummColumn;
    @FXML
    private Label globalSumOnCheck;

    private ResourceBundle bundle;

    public OrderFormController() {
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        OrderFormController.mainApp = mainApp;
    }

    public static MainApp getMainApp() {
        return mainApp;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public static BorderPane getRootPane() {
        return OrderForm;
    }

    public static void setRootPane(BorderPane orderForm) {
        OrderForm = orderForm;
    }

    public static ObservableList<GoodsInCheck> getGoodsInCheckObservableList() {
        return goodsInCheckObservableList;
    }

    public static void setGoodsInCheckObservableList(ObservableList<GoodsInCheck> goodsInCheckObservableList) {
        OrderFormController.goodsInCheckObservableList = goodsInCheckObservableList;
    }

    @FXML
    public void initialize() {
        log.debug("Initialising MainForm");
        lblRROSumCash.setText("Сумма оплат наличными: " + CurrentRRO.getInstance((byte) 1, "7", "115200").getCashInRRO());
        lblRROSumCredit.setText("Сумма оплат кредитной картой: " + CurrentRRO.getInstance((byte) 1, "7", "115200").getCreditInRRO());
        if (CheckInternetConnnection.getInstance().isConnected()) {
            ConnectedIcon.setVisible(true);
            NotConnectedIcon.setVisible(false);
        } else {
            ConnectedIcon.setVisible(false);
            NotConnectedIcon.setVisible(true);
        }

        Timeline everySecond = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (LocalTime.now().getSecond() % 10 == 0) { //каждые 10 секунд проверяем наличие интернет-соединения
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
        for (int i = 0; i < mainApp.allGoodsGroupsArrayList.size(); i++) {
            log.debug("Создаю кнопку группы для группы: " + mainApp.allGoodsGroupsArrayList.get(i).getName());
            Button btn = new Button();
            btn.setText(mainApp.allGoodsGroupsArrayList.get(i).getName());
            btn.setId(btn.hashCode() + mainApp.allGoodsGroupsArrayList.get(i).getName());
            final int finalI = i;
            btn.setOnAction(innerEvent -> {
                log.debug("нажали кнопку " + mainApp.allGoodsGroupsArrayList.get(finalI).getName());

                GoodsFormController.setSelectedGroup(mainApp.allGoodsGroupsArrayList.get(finalI).getCode());

                try {
                    String fxmlFormPath = "/fxml/GoodsForm/GoodsForm.fxml";
                    log.debug("Loading GroupForm for selecting goods into RootLayout");
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
                    log.debug("Setting location from FXML - GoodsForm");
                    BorderPane goodsPane = fxmlLoader.load();
                    log.debug("Отображаем форму выбора товаров");
                    // Set GoodsForm into the center of root layout.
                    mainApp.rootLayout.setCenter(goodsPane);
                    // Give the controller access to the main app.
                    GoodsFormController goodsFormController = fxmlLoader.getController();
                    goodsFormController.setRootPane(goodsPane);
                    goodsFormController.setMainApp(mainApp);
                } catch (IOException e) {
                    log.debug("Ошибка загрузки формы выбора товаров " + e.toString());
                    e.printStackTrace();
                }

            });
            btn.setPrefWidth(280);
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

            globalSumOnCheck.setText(getGlobalSumOnCheck().toString());
        }

        // Add observable list data to the table
        checkTableView.setItems(goodsInCheckObservableList);

        /*WITHOUT LAMBDA
        goodsNameColumn = new TableColumn<GoodsInCheck,String>();
        goodsNameColumn.setCellValueFactory(new PropertyValueFactory<GoodsInCheck,String>("name"));
        goodsPriceColumn = new TableColumn<GoodsInCheck,BigDecimal>();
        goodsPriceColumn.setCellValueFactory(new PropertyValueFactory<GoodsInCheck,BigDecimal>("price"));
        */
        goodsNameColumn.setCellValueFactory(cellData -> cellData.getValue().getGoods().nameProperty());
        goodsNameColumn.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 22");
        goodsPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getGoods().priceProperty());
        goodsPriceColumn.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-size: 22");
        goodsQtyColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        goodsQtyColumn.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-size: 22");
        goodsSummColumn.setCellValueFactory(cellData -> cellData.getValue().summaryOnGoodsProperty());
        goodsSummColumn.setStyle("-fx-alignment: CENTER-RIGHT; -fx-font-size: 22");

        goodsInCheckObservableList.addListener((ListChangeListener) change -> globalSumOnCheck.setText(getGlobalSumOnCheck().toString()));


        // Listen for selection changes and show the tovar details when changed.
        //checkTableView.getSelectionModel().selectedItemProperty().addListener(
        //        (observable, oldValue, newValue) -> showTovarDetails(newValue));


    }

    public void setBackButton() {
        mainApp.rootLayout.setCenter(MainFormController.getRootPane());
    }

    public static BigDecimal getGlobalSumOnCheck(){
        BigDecimal sumOnCheck = new BigDecimal(0);
        for (GoodsInCheck aGoodsInCheckObservableList : goodsInCheckObservableList) {
            sumOnCheck = sumOnCheck.add(new BigDecimal(aGoodsInCheckObservableList.getSummaryOnGoods().toString()));
        }
        return sumOnCheck;
    }

}
