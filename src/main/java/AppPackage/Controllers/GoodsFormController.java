package AppPackage.Controllers;

import AppPackage.Entities.Goods;
import AppPackage.Entities.GoodsInCheck;
import AppPackage.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class GoodsFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(OrderFormController.class);
    private Scene scene;
    private Stage stage;
    private Parent root;
    private static MainApp mainApp;
    private static int selectedGroup;
    private ArrayList<Goods> goodsInSelectedGroup;
    private static BigDecimal quantity = new BigDecimal(1);

    @FXML
    private BorderPane GoodsForm;
    @FXML
    private GridPane buttonsGridPane;

    private ResourceBundle bundle;

    public GoodsFormController() {
    }

    public static int getSelectedGroup() {
        return selectedGroup;
    }

    public static void setSelectedGroup(int selectedGroup) {
        GoodsFormController.selectedGroup = selectedGroup;
    }

    public BorderPane getRootPane() {
        return GoodsForm;
    }

    public void setRootPane(BorderPane goodsForm) {
        GoodsForm = goodsForm;
    }

    public void setMainApp(MainApp mainApp) {
        GoodsFormController.mainApp = mainApp;
    }

    public static MainApp getMainApp() {
        return mainApp;
    }

    public static BigDecimal getQuantity() {
        return quantity;
    }

    public static void setQuantity(BigDecimal quantity) {
        GoodsFormController.quantity = quantity;
    }

    @FXML
    public void initialize() {
        log.debug("Initialising MainForm");

        setMainApp(OrderFormController.getMainApp());
        goodsInSelectedGroup = new ArrayList<>();
        for (int j = 0; j < mainApp.allSelectedGoodsArrayList.size(); j++) {
            if (mainApp.allSelectedGoodsArrayList.get(j).getGoodsGroup() == GoodsFormController.getSelectedGroup()) {
                goodsInSelectedGroup.add(mainApp.allSelectedGoodsArrayList.get(j));
            }
        }
        int col = 0;
        int row = 0;
        //расставим кнопки согласно списку групп
        for (int i = 0; i < goodsInSelectedGroup.size(); i++) {
            //log.debug("—оздаю кнопку товара '" + goodsInSelectedGroup.get(i).getName());
            Button btn = new Button();
            btn.setText(goodsInSelectedGroup.get(i).getName());
            btn.setId(btn.hashCode() + goodsInSelectedGroup.get(i).getName());
            final int finalI = i;
            btn.setOnAction(innerEvent -> {

                //покажем форму ввода количества товара
                try {
                    stage = new Stage();
                    String fxmlFormPath = "/fxml/QtyInputForm/QtyInputForm.fxml";
                    log.debug("Loading QtyInputForm for input quantity of goods into new scene");
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
                    log.debug("Setting location from FXML - QtyInputForm");
                    //BorderPane qtyInputPane = fxmlLoader.load();
                    root = fxmlLoader.load();
                    log.debug("ќтображаем форму выбора товаров");
                    stage.setScene(new Scene(root, 640, 500));
                    stage.setTitle(goodsInSelectedGroup.get(finalI).getName() + ",   цена: " + goodsInSelectedGroup.get(finalI).getPrice().toString());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    //stage.initStyle(StageStyle.UNDECORATED);
                    stage.initOwner(btn.getScene().getWindow());
                    stage.initStyle(StageStyle.UTILITY);
                    stage.setOnCloseRequest(windowEvent -> {
                        windowEvent.consume();
                    });
                    // Give the controller access to the main app.
                    QtyInputFormController qtyInputFormController = fxmlLoader.getController();
                    qtyInputFormController.setMainApp(mainApp);

                    if (goodsInSelectedGroup.get(finalI).getSellTypeRRO() == 0) { //если товар штучный
                        qtyInputFormController.getBtnComa().setVisible(false); //то пр€чем кнопку ','
                    } else { //если товар весовой
                        qtyInputFormController.getBtnComa().setVisible(true); //то показываем кнопку ','
                    }

                    stage.showAndWait();

                } catch (IOException e) {
                    log.debug("ќшибка загрузки формы ввода количества товара " + e.toString());
                    e.printStackTrace();
                }
                if (quantity.compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal summary = new BigDecimal(goodsInSelectedGroup.get(finalI).getPrice().toString()).multiply(quantity);

                    if (MainApp.getGoodsInCheckObservableList().size() == 0) { //заказ пуст, добавл€ем первый товар в чек
                        MainApp.getGoodsInCheckObservableList().add(new GoodsInCheck(goodsInSelectedGroup.get(finalI), quantity, summary));
                    } else {
                        boolean isGoodsFound = false;  //заказ не пуст, ищем такой же товар
                        for (int j = 0; j < MainApp.getGoodsInCheckObservableList().size(); j++) {
                            if (MainApp.getGoodsInCheckObservableList().get(j).getGoods().getCode() == goodsInSelectedGroup.get(finalI).getCode()) { //товар найден, измен€ем количество
                                BigDecimal localQty = new BigDecimal(MainApp.getGoodsInCheckObservableList().get(j).getQuantity().toString()).add(quantity);
                                BigDecimal localSummary = new BigDecimal(goodsInSelectedGroup.get(finalI).getPrice().toString()).multiply(localQty);
                                MainApp.getGoodsInCheckObservableList().get(j).setQuantity(localQty);
                                MainApp.getGoodsInCheckObservableList().get(j).setSummaryOnGoods(localSummary);
                                isGoodsFound = true;
                                break;
                            }
                        }
                        if (!isGoodsFound) { //товар не найден, добавл€ем новый товар
                            MainApp.getGoodsInCheckObservableList().add(new GoodsInCheck(goodsInSelectedGroup.get(finalI), quantity, summary));
                        }
                    }
                }
                //подсчитаем общий итог по чеку
                MainApp.checkSummaryProperty().setValue(new BigDecimal(OrderFormController.getGlobalSumOnCheck().toString()));
                //small trick to refresh tableview
                OrderFormController.TableRefresh();
                MainApp.rootLayout.setCenter(OrderFormController.getRootPane());

            });
            btn.setPrefWidth(250);
            btn.setPrefHeight(105);
            btn.setFont(Font.font("Verdana", 20));
            btn.setWrapText(true);
            btn.setTextOverrun(OverrunStyle.CLIP);
            btn.setTextAlignment(TextAlignment.CENTER);
            buttonsGridPane.add(btn, col, row);

            if ((row != 0) && (row % 6 == 0)) {
                col = col + 1;
                row = 0;
            } else {
                row++;
            }
        }
        log.debug("—оздаю кнопку возврата");
        Button btn = new Button();
        btn.setText("ќтмена");
        btn.setId(btn.hashCode() + btn.getText());
        btn.setOnAction(innerEvent -> {
            MainApp.rootLayout.setCenter(OrderFormController.getRootPane());
        });
        btn.setPrefWidth(250);
        btn.setPrefHeight(105);
        btn.setFont(Font.font("Verdana", 22));
        btn.setWrapText(true);
        btn.setTextOverrun(OverrunStyle.CLIP);
        btn.setTextAlignment(TextAlignment.CENTER);
        buttonsGridPane.add(btn, 4, 6);

    }

}
