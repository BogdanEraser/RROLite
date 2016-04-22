package AppPackage.Controllers;

import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.ResourceBundle;


public class IncassoFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(IncassoFormController.class);
    private Scene scene;
    private MainApp mainApp;
    private BigDecimal chkCharge;
    private SimpleObjectProperty showCharge;
    @FXML
    private AnchorPane PayForm;
    @FXML
    private Button btnToRRO;
    @FXML
    private Button btnFromRRO;
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
    private Label lblRROSumCash;
    private ResourceBundle bundle;

    public IncassoFormController() {
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

    public void setTxtValue(String value) {
        this.txtValue.setText(value);
    }

    @FXML
    public void initialize() {
        //textField.getProperties().put("vkType", "numeric");
        log.debug("Initialising IncassoForm");
        txtValue.setText("0");
        txtValue.requestFocus();
        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
            lblRROSumCash.setText("Наличными: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
        } else {
            lblRROSumCash.setText("Наличными: Н/Д");
        }
        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
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
     * нажатие кнопки "внос в РРО"
     */
    public void setToRRO() {
        if ((txtValue.getText().length() != 0) & (new BigDecimal(txtValue.getText().replace(",", ".")).compareTo(new BigDecimal("0.01")) != -1)) {
            Alert alertInQuestion = new Alert(Alert.AlertType.CONFIRMATION);
            alertInQuestion.setTitle("Подтверждение");
            alertInQuestion.setHeaderText("Внести в РРО " + txtValue.getText() + " ?");
            Optional<ButtonType> resultRet = alertInQuestion.showAndWait();
            if (resultRet.isPresent() && resultRet.get() == ButtonType.OK) {
                //да, внос денег подтвержден
                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                    //проверим, в каком состоянии чек
                    switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                        case 0: {//чек закрыт, открываем его
                            BigDecimal summ = new BigDecimal(txtValue.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cashInOut(0, summ)) {
                                //внос денег неуспешный
                                log.debug("unable to make cash in - toRROButton interrupt");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Ошибка");
                                alert.setHeaderText("Невозможно произвести внос денег в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                return; //отменяем дальнейшее выполнение метода для кнопки "внос"
                            }
                            MainApp.setCashSumInRRO("Наличными: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
                            break;
                        }
                        case 1: {//чек открыт для продажи
                            Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                            alertRecQuestion.setTitle("Подтверждение");
                            alertRecQuestion.setHeaderText("Чек открыт для продажи\nОтменить чек или отменить процесс внесения денег?");
                            alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от внесения");
                            Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                            if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                    //отмена чека неуспешно
                                    log.debug("unable to cancel receipt - toRROButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("Ошибка");
                                    alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                }
                            } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                log.debug("receipt opened for sale - user selected toRROButton interrupt");
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
                            alertRecQuestion.setHeaderText("Чек открыт для возврата\nОтменить чек или отменить процесс внесения денег?");
                            alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от внесения");
                            Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                            if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                    //отмена чека неуспешно
                                    log.debug("unable to cancel receipt - toRROButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("Ошибка");
                                    alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                }
                            } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                log.debug("receipt opened for return - user selected toRROButton interrupt");
                            }
                            break;
                        }
                        case 99: {//состояние чека неопределено
                            log.debug("receipt status not defined");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Состояние чека не определено\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("Рекомендуется обратиться к администратору\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                            break;
                        }
                    }
                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                    Stage stage = (Stage) btnToRRO.getScene().getWindow();
                    stage.close();
                }
                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
            } else if (resultRet.isPresent() && resultRet.get() == ButtonType.CANCEL) {
                //отаказ от внесения
                Stage stage = (Stage) btnToRRO.getScene().getWindow();
                stage.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Недопустимый ввод");
            alert.setContentText("Минимально допустимая сумма вноса - 0,01 грн (1 копейка)");
            alert.showAndWait();
        }
    }

    /**
     * нажатие кнопки "изъятие из РРО"
     */

    public void setFromRRO() {
        if ((txtValue.getText().length() != 0) & (new BigDecimal(txtValue.getText().replace(",", ".")).compareTo(new BigDecimal("0.01")) != -1)) {
            Alert alertInQuestion = new Alert(Alert.AlertType.CONFIRMATION);
            alertInQuestion.setTitle("Подтверждение");
            alertInQuestion.setHeaderText("Изъять из РРО " + txtValue.getText() + " ?");
            Optional<ButtonType> resultRet = alertInQuestion.showAndWait();
            if (resultRet.isPresent() && resultRet.get() == ButtonType.OK) {
                //да, внос денег подтвержден
                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                    //проверим, достаточно ли денег для изъятия из РРО
                    if (new BigDecimal(txtValue.getText().replace(",", ".")).compareTo(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO()) < 1) {
                        //да, денег достаточно
                        //проверим, в каком состоянии чек
                        switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                            case 0: {//чек закрыт, открываем его
                                BigDecimal summ = new BigDecimal(txtValue.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cashInOut(1, summ)) {
                                    //внос денег неуспешный
                                    log.debug("unable to make cash in - toRROButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("Ошибка");
                                    alert.setHeaderText("Невозможно произвести внос денег в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                    return; //отменяем дальнейшее выполнение метода для кнопки "внос"
                                }
                                MainApp.setCashSumInRRO("Наличными: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
                                break;
                            }
                            case 1: {//чек открыт для продажи
                                Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                                alertRecQuestion.setTitle("Подтверждение");
                                alertRecQuestion.setHeaderText("Чек открыт для продажи\nОтменить чек или отменить процесс внесения денег?");
                                alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от внесения");
                                Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                                if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                        //отмена чека неуспешно
                                        log.debug("unable to cancel receipt - toRROButton interrupt");
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("Ошибка");
                                        alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                        alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                        alert.showAndWait();
                                    }
                                } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                    log.debug("receipt opened for sale - user selected toRROButton interrupt");
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
                                alertRecQuestion.setHeaderText("Чек открыт для возврата\nОтменить чек или отменить процесс внесения денег?");
                                alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от внесения");
                                Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                                if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                        //отмена чека неуспешно
                                        log.debug("unable to cancel receipt - toRROButton interrupt");
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("Ошибка");
                                        alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                        alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                        alert.showAndWait();
                                    }
                                } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                    log.debug("receipt opened for return - user selected toRROButton interrupt");
                                }
                                break;
                            }
                            case 99: {//состояние чека неопределено
                                log.debug("receipt status not defined");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Ошибка");
                                alert.setHeaderText("Состояние чека не определено\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("Рекомендуется обратиться к администратору\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                                break;
                            }
                        }
                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                        Stage stage = (Stage) btnToRRO.getScene().getWindow();
                        stage.close();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Сумма изъятия превышает количество наличных в кассе");
                        alert.showAndWait();
                    }
                }
                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
            } else if (resultRet.isPresent() && resultRet.get() == ButtonType.CANCEL) {
                //отказ от внесения
                Stage stage = (Stage) btnToRRO.getScene().getWindow();
                stage.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Недопустимый ввод");
            alert.setContentText("Минимально допустимая сумма изъятия - 0,01 грн (1 копейка)");
            alert.showAndWait();
        }
    }
}
