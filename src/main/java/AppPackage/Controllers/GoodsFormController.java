package AppPackage.Controllers;

import AppPackage.Entities.Goods;
import AppPackage.Entities.GoodsInCheck;
import AppPackage.MainApp;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class GoodsFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(OrderFormController.class);
    private Scene scene;
    private MainApp mainApp;
    private static int selectedGroup;
    private ArrayList<Goods> goodsInSelectedGroup;

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

        setMainApp(OrderFormController.getMainApp());
        goodsInSelectedGroup = new ArrayList<>();
        for (int j = 0; j < mainApp.allGoodsArrayList.size(); j++) {
            if (mainApp.allGoodsArrayList.get(j).getGoodsGroup() == GoodsFormController.getSelectedGroup()) {
                goodsInSelectedGroup.add(mainApp.allGoodsArrayList.get(j));
            }
        }
        int col = 0;
        int row = 0;
        //расставим кнопки согласно списку групп
        for (int i = 0; i < goodsInSelectedGroup.size(); i++) {
            log.debug("Создаю кнопку товара '" + goodsInSelectedGroup.get(i).getName());
            Button btn = new Button();
            btn.setText(goodsInSelectedGroup.get(i).getName());
            btn.setId(btn.hashCode() + goodsInSelectedGroup.get(i).getName());
            final int finalI = i;
            btn.setOnAction(innerEvent -> {
                log.debug("нажали кнопку " + goodsInSelectedGroup.get(finalI).getName());

                GoodsInCheck goodsInCheck = new GoodsInCheck(goodsInSelectedGroup.get(finalI),new BigDecimal(2),new BigDecimal(2).multiply(new BigDecimal(3)));
                OrderFormController.getGoodsInCheckObservableList().add(goodsInCheck);

                mainApp.rootLayout.setCenter(OrderFormController.getRootPane());

            });
            btn.setPrefWidth(250);
            btn.setPrefHeight(100);
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
        log.debug("Создаю кнопку возврата");
        Button btn = new Button();
        btn.setText("Отмена");
        btn.setId(btn.hashCode() + btn.getText());
        btn.setOnAction(innerEvent -> {
            mainApp.rootLayout.setCenter(OrderFormController.getRootPane());
        });
        btn.setPrefWidth(250);
        btn.setPrefHeight(100);
        btn.setFont(Font.font("Verdana", 22));
        btn.setWrapText(true);
        btn.setTextOverrun(OverrunStyle.CLIP);
        btn.setTextAlignment(TextAlignment.CENTER);
        buttonsGridPane.add(btn, 4, 6);
            /* WITHOUT LAMBDA
        nameColumn = new TableColumn<Tovar,String>("Наименование");
        nameColumn.setCellValueFactory(new PropertyValueFactory<Tovar,String>("name"));
        priceColumn = new TableColumn<Tovar,BigDecimal>("Цена");
        priceColumn.setCellValueFactory(new PropertyValueFactory<Tovar,BigDecimal>("price"));
        */

        // goodsNameColumn.setCellValueFactory(new Cell<Goods,String>(goodsObservableList.get(i).getName()));
        //goodsNameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        //goodsPriceColumn.setCellValueFactory(cellData -> cellData.getValue().getPrice());

        // Listen for selection changes and show the tovar details when changed.
        //checkTableView.getSelectionModel().selectedItemProperty().addListener(
        //        (observable, oldValue, newValue) -> showTovarDetails(newValue));


    }

}
