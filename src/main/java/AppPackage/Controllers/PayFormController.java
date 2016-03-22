package AppPackage.Controllers;

import AppPackage.Entities.Goods;
import AppPackage.Entities.GoodsGroup;
import AppPackage.Entities.GoodsInCheck;
import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class PayFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(PayFormController.class);
    private Scene scene;
    private MainApp mainApp;
    private BigDecimal chkCharge;
    private SimpleObjectProperty showCharge;
    @FXML
    private AnchorPane PayForm;
    @FXML
    private Button btnPayCash;
    @FXML
    private Button btnPayCC;
    @FXML
    private Button btnGoodsReturn;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btn1;
    @FXML
    private Button btn2;
    @FXML
    private Button btn3;
    @FXML
    private Button btn4;
    @FXML
    private Button btn5;
    @FXML
    private Button btn6;
    @FXML
    private Button btn7;
    @FXML
    private Button btn8;
    @FXML
    private Button btn9;
    @FXML
    private Button btn0;
    @FXML
    private Button btnComa;
    @FXML
    private Button btnBackSpace;
    @FXML
    private TextField txtValue;
    @FXML
    private TextField txtToPay;
    @FXML
    private TextField txtCharge;

    private ResourceBundle bundle;

    public PayFormController() {
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

    public Object getShowCharge() {
        return showCharge.get();
    }

    public SimpleObjectProperty showChargeProperty() {
        return showCharge;
    }

    public void setShowCharge(Object showCharge) {
        this.showCharge.set(showCharge);
    }

    @FXML
    public void initialize() {
        //textField.getProperties().put("vkType", "numeric");
        log.debug("Initialising PayForm");

        txtValue.setText("0");
        txtToPay.textProperty().bind(MainApp.checkSummaryProperty().asString());

        chkCharge = new BigDecimal(String.valueOf(BigDecimal.ZERO.subtract(new BigDecimal(MainApp.checkSummaryProperty().getValue().toString()))));
        showCharge = new SimpleObjectProperty<BigDecimal>(chkCharge);
        txtCharge.textProperty().bind(showChargeProperty().asString());

        txtValue.requestFocus();

        //      bundle = resources;
        //      messagelabel.setText(bundle.getString("Label.text"));
    }


    /**
     * обработка нажатия цифровой кнопки
     *
     * @param digit цифра для печати
     */
    public void pushTheButton(String digit) {
        if (txtValue.getText().length() < 8) {
            if (!txtValue.getText().equals("0")) {
                txtValue.setText(txtValue.getText() + digit);
            } else {
                txtValue.setText(digit);
            }
        }
        //обновляем сдачу покупателю
        setShowCharge(new BigDecimal(txtValue.getText().replace(",", ".")).subtract(new BigDecimal(MainApp.checkSummaryProperty().getValue().toString())));
    }


    /**
     * нажатие кнопки "1"
     */
    public void setBtn1() {
        pushTheButton("1");
    }

    /**
     * нажатие кнопки "2"
     */
    public void setBtn2() {
        pushTheButton("2");
    }

    /**
     * нажатие кнопки "3"
     */
    public void setBtn3() {
        pushTheButton("3");
    }

    /**
     * нажатие кнопки "4"
     */
    public void setBtn4() {
        pushTheButton("4");
    }

    /**
     * нажатие кнопки "5"
     */
    public void setBtn5() {
        pushTheButton("5");
    }

    /**
     * нажатие кнопки "6"
     */
    public void setBtn6() {
        pushTheButton("6");
    }

    /**
     * нажатие кнопки "7"
     */
    public void setBtn7() {
        pushTheButton("7");
    }

    /**
     * нажатие кнопки "8"
     */
    public void setBtn8() {
        pushTheButton("8");
    }

    /**
     * нажатие кнопки "9"
     */
    public void setBtn9() {
        pushTheButton("9");
    }

    /**
     * нажатие кнопки "0"
     */
    public void setBtn0() {
        pushTheButton("0");
    }

    /**
     * нажатие кнопки "."
     */
    public void setBtnComa() {
        if (txtValue.getText().length() < 8) {
            if (txtValue.getText().length() == 0) {
                txtValue.setText("0,");
            } else if (!txtValue.getText().contains(".")) {
                txtValue.setText(txtValue.getText() + ".");
            }
        }
        //обновляем сдачу покупателю
        setShowCharge(new BigDecimal(txtValue.getText().replace(",", ".")).subtract(new BigDecimal(MainApp.checkSummaryProperty().getValue().toString())));
    }

    /**
     * нажатие кнопки "удалить"
     */
    public void setBtnBackSpace() {
        if (txtValue.getText().length() <= 1) {
            txtValue.setText("0");
        } else txtValue.setText(txtValue.getText(0, txtValue.getText().length() - 1));
        //обновляем сдачу покупателю
        setShowCharge(new BigDecimal(txtValue.getText().replace(",", ".")).subtract(new BigDecimal(MainApp.checkSummaryProperty().getValue().toString())));
    }

    /**
     * нажатие кнопки "Отмена"
     */
    public void setBtnCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * нажатие кнопки "наличные"
     */
    public void setPayCash() {
        if ((txtValue.getText().length() != 0) & (new BigDecimal(txtValue.getText().replace(",", ".")).compareTo(new BigDecimal(txtToPay.getText().replace(",", "."))) != -1)) {
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).openPortMiniFP()) {
                //сначала проверим, в каком состоянии чек
                switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getReceiptStatusFromRRO()) {
                    case 0: {//чек закрыт, открываем его
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).openReceipt(0)) {
                            //открытие чека неуспешно
                            log.debug("unable to open receipt - payCashButton interrupt ");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Невозможно открыть чек для продажи\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                            alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                            alert.showAndWait();
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                            return; //отменяем дальнейшее выполнение метода для кнопки "Наличные"
                        }
                        for (GoodsInCheck gic : MainApp.getGoodsInCheckObservableList()) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).saleGoodsToRRO(0, gic.getQuantity(), gic.getGoods().getCode(), gic.getGoods().getPrice())) {
                                //добавление товара в чек РРО неуспешно
                                log.debug("unable to make 'sale_plu' to RRO - payCashButton interrupt ");
                                Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                                alertQuestion.setTitle("Ошибка");
                                alertQuestion.setHeaderText("Ошибка при добавлении товара в чек\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError())
                                        + "Отменить печать чека?");
                                alertQuestion.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult()
                                        + " {" + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastEvent() + "}");
                                Optional<ButtonType> result = alertQuestion.showAndWait();
                                if (result.isPresent() && result.get() == ButtonType.OK) {
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).cancelReceipt();
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                                    return; //отменяем дальнейшее выполнение метода для кнопки "Наличные"
                                }
                            }
                        }
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).payGoodsToRRO(0, new BigDecimal(txtValue.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_EVEN))) {
                            //оплата чека неуспешно
                            log.debug("unable to pay receipt - payCashButton interrupt ");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Невозможно сделать оплату чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                            alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                            alert.showAndWait();
                        } else {
                            MainApp.getGoodsInCheckObservableList().clear();  //оплата успешна, очищаем товары из чека
                            mainApp.setCheckSummary(BigDecimal.ZERO);  //и суммарно по чеку
                            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).openPortMiniFP()) {
                                MainApp.setCashSumInRRO("Наличными: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getCashInRRO().toString());
                                MainApp.setCCSumInRRO("Кредитной картой: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getCreditInRRO().toString());
                            } else {
                                MainApp.setCashSumInRRO("Наличными: Н/Д");
                                MainApp.setCCSumInRRO("Кредитной картой: Н/Д");
                            }

                        }
                        break;
                    }
                    case 1: {//чек открыт для продажи
                        Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                        alertRecQuestion.setTitle("Подтверждение");
                        alertRecQuestion.setHeaderText("Чек открыт для продажи\nОтменить чек или отменить процесс оплаты наличными?");
                        alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от оплаты");
                        Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                        if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).cancelReceipt()) {
                                //отмена чека неуспешно
                                log.debug("unable to cancel receipt - setExitButtonButton interrupt");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Ошибка");
                                alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                                alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                                alert.showAndWait();
                            }
                        } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                            log.debug("receipt opened for sale - user selected setPayCashButton interrupt");
                        }
                        break;
                    }
                    case 2: {//чек открыт только для оплаты
                        log.debug("receipt opened only for payment");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Подтверждение");
                        alert.setHeaderText("Чек открыт только для оплаты\nЧек возможно отменить только вручную.\nВыключите и включите РРО с нажатием кнопки продвижения ленты");
                        alert.showAndWait();
                        break;
                    }
                    case 3: {//чек открыт для возврата
                        Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                        alertRecQuestion.setTitle("Подтверждение");
                        alertRecQuestion.setHeaderText("Чек открыт для возврата\nОтменить чек или отменить процесс оплаты наличными?");
                        alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от оплаты");
                        Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                        if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).cancelReceipt()) {
                                //отмена чека неуспешно
                                log.debug("unable to cancel receipt - setExitButtonButton interrupt");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Ошибка");
                                alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                                alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                                alert.showAndWait();
                            }
                        } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                            log.debug("receipt opened for return - user selected setPayCashButton interrupt");
                        }
                        break;
                    }
                    case 99: {//состояние чека неопределено
                        log.debug("receipt status not defined");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Состояние чека не определено\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                        alert.setContentText("Рекомендуется обратиться к администратору\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                        alert.showAndWait();
                        break;
                    }
                }
            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
            Stage stage = (Stage) btnPayCash.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Неверно указана сумма наличных от покупателя");
            alert.showAndWait();
        }
    }

    /**
     * нажатие кнопки "карта"
     */
    public void setPayCC() {
        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).openPortMiniFP()) {
            //сначала проверим, в каком состоянии чек
            switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getReceiptStatusFromRRO()) {
                case 0: {//чек закрыт, открываем его
                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).openReceipt(0)) {
                        //открытие чека неуспешно
                        log.debug("unable to open receipt - payCashButton interrupt ");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Невозможно открыть чек для продажи\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                        alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                        alert.showAndWait();
                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                        return; //отменяем дальнейшее выполнение метода для кнопки "Наличные"
                    }
                    for (GoodsInCheck gic : MainApp.getGoodsInCheckObservableList()) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).saleGoodsToRRO(0, gic.getQuantity(), gic.getGoods().getCode(), gic.getGoods().getPrice())) {
                            //добавление товара в чек РРО неуспешно
                            log.debug("unable to make 'sale_plu' to RRO - payCashButton interrupt ");
                            Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                            alertQuestion.setTitle("Ошибка");
                            alertQuestion.setHeaderText("Ошибка при добавлении товара в чек\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError())
                                    + "Отменить печать чека?");
                            alertQuestion.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult()
                                    + " {" + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastEvent() + "}");
                            Optional<ButtonType> result = alertQuestion.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).cancelReceipt();
                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                                return; //отменяем дальнейшее выполнение метода для кнопки "Карта"
                            }
                        }
                    }
                    Alert alertCCQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                    alertCCQuestion.setTitle("Подтверждение");
                    alertCCQuestion.setHeaderText("Успешно ли прошла оплата кредитной картой?");
                    //alertCCQuestion.setContentText("'ОК' - успешно\t'Отмена' - оплата не прошла");
                    ButtonType buttonSuccess = new ButtonType("Успешно");
                    ButtonType buttonUnsuccess = new ButtonType("Оплата не прошла");
                    ButtonType buttonCancel = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
                    alertCCQuestion.getButtonTypes().setAll(buttonSuccess, buttonUnsuccess, buttonCancel);
                    Optional<ButtonType> resultCC = alertCCQuestion.showAndWait();
                    if (resultCC.isPresent() && resultCC.get() == buttonSuccess) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).payGoodsToRRO(2, new BigDecimal(txtValue.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_EVEN))) {
                            //оплата чека неуспешно
                            log.debug("unable to pay receipt - payCashButton interrupt");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Невозможно сделать оплату чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                            alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                            alert.showAndWait();
                        } else {
                            MainApp.getGoodsInCheckObservableList().clear();  //оплата успешна, очищаем товары из чека
                            mainApp.setCheckSummary(BigDecimal.ZERO);  //и суммарно по чеку
                            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).openPortMiniFP()) {
                                MainApp.setCashSumInRRO("Наличными: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getCashInRRO().toString());
                                MainApp.setCCSumInRRO("Кредитной картой: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getCreditInRRO().toString());
                            } else {
                                MainApp.setCashSumInRRO("Наличными: Н/Д");
                                MainApp.setCCSumInRRO("Кредитной картой: Н/Д");
                            }
                        }
                    }
                    break;
                }
                case 1: {//чек открыт для продажи
                    Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                    alertRecQuestion.setTitle("Подтверждение");
                    alertRecQuestion.setHeaderText("Чек открыт для продажи\nОтменить чек или отменить процесс оплаты картой?");
                    alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от оплаты");
                    Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                    if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).cancelReceipt()) {
                            //отмена чека неуспешно
                            log.debug("unable to cancel receipt - setPayCCButton interrupt");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                            alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                            alert.showAndWait();
                        }
                    } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                        log.debug("receipt opened for sale - user selected setPayССButton interrupt");
                    }
                    break;
                }
                case 2: {//чек открыт только для оплаты
                    log.debug("receipt opened only for payment");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Подтверждение");
                    alert.setHeaderText("Чек открыт только для оплаты\nЧек возможно отменить только вручную.\nВыключите и включите РРО с нажатием кнопки продвижения ленты");
                    alert.showAndWait();
                    break;
                }
                case 3: {//чек открыт для возврата
                    Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                    alertRecQuestion.setTitle("Подтверждение");
                    alertRecQuestion.setHeaderText("Чек открыт для возврата\nОтменить чек или отменить процесс оплаты картой?");
                    alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от оплаты");
                    Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                    if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).cancelReceipt()) {
                            //отмена чека неуспешно
                            log.debug("unable to cancel receipt - setPayCCButton interrupt");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                            alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                            alert.showAndWait();
                        }
                    } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                        log.debug("receipt opened for return - user selected setPayCCButton interrupt");
                    }
                    break;
                }
                case 99: {//состояние чека неопределено
                    log.debug("receipt status not defined");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Состояние чека не определено\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                    alert.setContentText("Рекомендуется обратиться к администратору\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                    alert.showAndWait();
                    break;
                }
            }
        }
        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
        Stage stage = (Stage) btnPayCC.getScene().getWindow();
        stage.close();
    }

    /**
     * нажатие кнопки "возврат товара"
     */
    public void setGoodsReturn() {

        //получаем из файла выбранные товари и группы (десериализация)

     /*   for (GoodsGroup tmp : deserializeGoodsGroup()) {
            System.out.println(tmp.getCode() + " " + tmp.getName());
        }
        for (Goods tmp : deserializeGoods()) {
            System.out.println(tmp.getCode() + " " + tmp.getName() + " " + tmp.getPrice());
        }
    */
        Stage stage = (Stage) btnGoodsReturn.getScene().getWindow();
        stage.close();
    }


    public ArrayList<Goods> deserializeGoods() {
        ArrayList<Goods> goods = null;
        String selectedGoodsFilePath = "selgds.ser";
        try {
            log.debug("deserializing selected goods from file");
            ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Paths.get(selectedGoodsFilePath)));
            goods = (ArrayList<Goods>) in.readObject();
            in.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            log.debug("error while deserializing selected goods from file " + e.toString());
        }
        return goods;
    }

    public ArrayList<GoodsGroup> deserializeGoodsGroup() {
        ArrayList<GoodsGroup> goodsGroups = null;
        String goodsGroupsFilePath = "gdsgrps.ser";
        try {
            log.debug("deserializing goods groups from file");
            ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Paths.get(goodsGroupsFilePath)));
            goodsGroups = (ArrayList<GoodsGroup>) in.readObject();
            in.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            log.debug("error while deserializing goods group from file " + e.toString());
        }
        return goodsGroups;
    }
}
