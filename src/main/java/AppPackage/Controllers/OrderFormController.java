package AppPackage.Controllers;

import AppPackage.Entities.GoodsInCheck;
import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import AppPackage.Utils.CheckInternetConnnection;
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ListIterator;
import java.util.Optional;
import java.util.ResourceBundle;


public class OrderFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(OrderFormController.class);
    private static MainApp mainApp;
    private static Scene scene;
    private Stage stage;
    private Parent root;

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
    public TableView<GoodsInCheck> checkTableView;
    @FXML
    private TableColumn<GoodsInCheck, String> goodsNameColumn;
    @FXML
    private TableColumn<GoodsInCheck, BigDecimal> goodsPriceColumn;
    @FXML
    private TableColumn<GoodsInCheck, String> goodsQtyColumn;
    @FXML
    private TableColumn<GoodsInCheck, String> goodsSummColumn;
    @FXML
    private Label globalSumOnCheck;
    @FXML
    private Button btnCheckout;

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
        OrderFormController.scene = scene;
    }

    public static BorderPane getRootPane() {
        return OrderForm;
    }

    public static void setRootPane(BorderPane orderForm) {
        OrderForm = orderForm;
    }

    public static Scene getScene() {
        return scene;
    }

    public Label getLblRROSumCash() {
        return lblRROSumCash;
    }

    public Label getLblRROSumCredit() {
        return lblRROSumCredit;
    }

    public TableView<GoodsInCheck> getCheckTableView() {
        return checkTableView;
    }

    /**
     * Adds autoscroll to JavaFX tableview and selects last added row.
     *
     * @param view
     */
    public static <GoodsInCheck> void addAutoScroll(final TableView<GoodsInCheck> view) {
        try {
            view.getItems().addListener((ListChangeListener<GoodsInCheck>) (change -> {
                change.next();
                final int size = view.getItems().size();
                if (size > 0) {
                    view.scrollTo(size - 1);
                    view.getSelectionModel().selectLast();
                }
            }));
        } catch (NullPointerException ignored) {
        }
    }


    @FXML
    public void initialize() {
        log.debug("Initialising MainForm");
        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
            MainApp.setCashSumInRRO("Наличными: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
            MainApp.setCCSumInRRO("Кредитной картой: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO().toString());
        } else {
            MainApp.setCashSumInRRO("Наличными: Н/Д");
            MainApp.setCCSumInRRO("Кредитной картой: Н/Д");
        }
        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();

        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
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
            //log.debug("Создаю кнопку группы для группы: " + mainApp.allGoodsGroupsArrayList.get(i).getName());
            Button btn = new Button();
            btn.setText(mainApp.allGoodsGroupsArrayList.get(i).getName());
            btn.setId(btn.hashCode() + mainApp.allGoodsGroupsArrayList.get(i).getName());
            final int finalI = i;
            btn.setOnAction(innerEvent -> {
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
                    MainApp.rootLayout.setCenter(goodsPane);
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

            //globalSumOnCheck.setText(getGlobalSumOnCheck().toString());
        }

        globalSumOnCheck.textProperty().bind(MainApp.checkSummaryProperty().asString());
        lblRROSumCash.textProperty().bind(MainApp.cashSumInRROProperty());
        lblRROSumCredit.textProperty().bind(MainApp.CCSumInRROProperty());

        // Add observable list data to the table
        checkTableView.setItems(MainApp.getGoodsInCheckObservableList());
        //добавление автоскрола на последнюю строку и ее выделение
        addAutoScroll(checkTableView);

        //запрет перемещения столбцов таблицы (получаем строку заголовка и отслеживаем попытки ее изменения)
        checkTableView.widthProperty().addListener((source, oldWidth, newWidth) -> {
            TableHeaderRow header = (TableHeaderRow) checkTableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((observable, oldValue, newValue) -> {
                header.setReordering(false);
            });
        });

        /*WITHOUT LAMBDA
        goodsNameColumn = new TableColumn<GoodsInCheck,String>();
        goodsNameColumn.setCellValueFactory(new PropertyValueFactory<GoodsInCheck,String>("name"));
        goodsPriceColumn = new TableColumn<GoodsInCheck,BigDecimal>();
        goodsPriceColumn.setCellValueFactory(new PropertyValueFactory<GoodsInCheck,BigDecimal>("price"));
        goodsQtyColumn = new TableColumn<GoodsInCheck,String>();
        goodsQtyColumn.setCellValueFactory(new PropertyValueFactory<GoodsInCheck,String>("quantity"));
        goodsSummColumn = new TableColumn<GoodsInCheck,String>();
        goodsSummColumn.setCellValueFactory(new PropertyValueFactory<GoodsInCheck,String>("summaryOnGoods"));
        */

        /*With LAMBDA*/
        goodsNameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getGoods().getName()));
        goodsPriceColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<BigDecimal>(cellData.getValue().getGoods().getPrice()));
        goodsQtyColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty());
        goodsSummColumn.setCellValueFactory(cellData -> cellData.getValue().summaryOnGoodsProperty());


        checkTableView.getColumns().get(0).setVisible(false);
        checkTableView.getColumns().get(0).setVisible(true);
        /*goodsQtyColumn.setCellValueFactory(cellData -> new ObjectBinding<BigDecimal>() {
            @Override
            protected BigDecimal computeValue() {
                return cellData.getValue().getQuantity();
            }
        });
        goodsSummColumn.setCellValueFactory(cellData -> new ObjectBinding<BigDecimal>() {
            @Override
            protected BigDecimal computeValue() {
                return cellData.getValue().getSummaryOnGoods();
            }
        });
*/

        goodsNameColumn.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-size: 21");
        String tblColStyle = "-fx-alignment: CENTER-RIGHT; -fx-font-size: 21";
        goodsPriceColumn.setStyle(tblColStyle);
        goodsQtyColumn.setStyle(tblColStyle);
        goodsSummColumn.setStyle(tblColStyle);


        //обработка двойного клика на строке таблицы для удаления товара
        checkTableView.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Вопрос");
                alert.setHeaderText("Удалить товар из заказа?");
                alert.setContentText(checkTableView.getSelectionModel().getSelectedItem().getGoods().getName());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    deleteGoodsFromOrder(checkTableView.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    public void setBackButton() {
        MainApp.rootLayout.setCenter(MainFormController.getRootPane());
    }

    public void setCheckout() {
        //покажем форму выбора оплаты
        if (MainApp.getCheckSummary().compareTo(BigDecimal.ZERO)==0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Пустой заказ");
            alert.setContentText("Добавьте в заказ товары и попробуйте еще раз");
            alert.showAndWait();
        } else {
            try {
                stage = new Stage();
                String fxmlFormPath = "/fxml/PayForm/PayForm.fxml";
                log.debug("Loading PayTypeForm for making payment into new scene");
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
                log.debug("Setting location from FXML - PayForm");
                root = fxmlLoader.load();
                log.debug("Отображаем форму оплаты");
                stage.setScene(new Scene(root, 800, 600));
                //stage.initStyle(StageStyle.UNDECORATED);
                stage.setTitle("Выбор типа оплаты");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.initOwner(btnCheckout.getScene().getWindow());
                stage.initStyle(StageStyle.UTILITY);
                stage.setOnCloseRequest(windowEvent -> {
                    windowEvent.consume();
                });
                // Give the controller access to the main app.
                PayFormController payFormController = fxmlLoader.getController();
                payFormController.setMainApp(mainApp);
                stage.showAndWait();

            } catch (IOException e) {
                log.debug("Ошибка загрузки формы выбора типа оплаты " + e.toString());
            }
        }
    }

    public static BigDecimal getGlobalSumOnCheck() {
        BigDecimal sumOnCheck = new BigDecimal(0);
        for (GoodsInCheck entry : MainApp.getGoodsInCheckObservableList()) {
            sumOnCheck = sumOnCheck.add(new BigDecimal(entry.getSummaryOnGoods().toString()));
        }
        return sumOnCheck.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public static void deleteGoodsFromOrder(GoodsInCheck goodsInCheck) {
        ListIterator<GoodsInCheck> goodsInCheckListIterator = MainApp.getGoodsInCheckObservableList().listIterator();
        while (goodsInCheckListIterator.hasNext()) {
            if (goodsInCheckListIterator.next().getGoods().getCode() == goodsInCheck.getGoods().getCode()) {
                goodsInCheckListIterator.remove();
                MainApp.checkSummaryProperty().setValue(new BigDecimal(OrderFormController.getGlobalSumOnCheck().toString()));
                break;
            }
        }
    }

    public static void TableRefresh() {
        mainApp.orderFormController.checkTableView.getColumns().get(0).setVisible(false);
        mainApp.orderFormController.checkTableView.getColumns().get(0).setVisible(true);
    }

}
