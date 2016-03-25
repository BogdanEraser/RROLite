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
     * ��������� ������� �������� ������
     *
     * @param digit ����� ��� ������
     */
    public void pushTheButton(String digit) {
        if (txtValue.getText().length() < 8) {
            if (!txtValue.getText().equals("0")) {
                txtValue.setText(txtValue.getText() + digit);
            } else {
                txtValue.setText(digit);
            }
        }
        //��������� ����� ����������
        setShowCharge(new BigDecimal(txtValue.getText().replace(",", ".")).subtract(new BigDecimal(MainApp.checkSummaryProperty().getValue().toString())));
    }


    /**
     * ������� ������ "1"
     */
    public void setBtn1() {
        pushTheButton("1");
    }

    /**
     * ������� ������ "2"
     */
    public void setBtn2() {
        pushTheButton("2");
    }

    /**
     * ������� ������ "3"
     */
    public void setBtn3() {
        pushTheButton("3");
    }

    /**
     * ������� ������ "4"
     */
    public void setBtn4() {
        pushTheButton("4");
    }

    /**
     * ������� ������ "5"
     */
    public void setBtn5() {
        pushTheButton("5");
    }

    /**
     * ������� ������ "6"
     */
    public void setBtn6() {
        pushTheButton("6");
    }

    /**
     * ������� ������ "7"
     */
    public void setBtn7() {
        pushTheButton("7");
    }

    /**
     * ������� ������ "8"
     */
    public void setBtn8() {
        pushTheButton("8");
    }

    /**
     * ������� ������ "9"
     */
    public void setBtn9() {
        pushTheButton("9");
    }

    /**
     * ������� ������ "0"
     */
    public void setBtn0() {
        pushTheButton("0");
    }

    /**
     * ������� ������ "."
     */
    public void setBtnComa() {
        if (txtValue.getText().length() < 8) {
            if (txtValue.getText().length() == 0) {
                txtValue.setText("0,");
            } else if (!txtValue.getText().contains(".")) {
                txtValue.setText(txtValue.getText() + ".");
            }
        }
        //��������� ����� ����������
        setShowCharge(new BigDecimal(txtValue.getText().replace(",", ".")).subtract(new BigDecimal(MainApp.checkSummaryProperty().getValue().toString())));
    }

    /**
     * ������� ������ "�������"
     */
    public void setBtnBackSpace() {
        if (txtValue.getText().length() <= 1) {
            txtValue.setText("0");
        } else txtValue.setText(txtValue.getText(0, txtValue.getText().length() - 1));
        //��������� ����� ����������
        setShowCharge(new BigDecimal(txtValue.getText().replace(",", ".")).subtract(new BigDecimal(MainApp.checkSummaryProperty().getValue().toString())));
    }

    /**
     * ������� ������ "������"
     */
    public void setBtnCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    /**
     * ������� ������ "��������"
     */
    public void setPayCash() {
        if ((txtValue.getText().length() != 0) & (new BigDecimal(txtValue.getText().replace(",", ".")).compareTo(new BigDecimal(txtToPay.getText().replace(",", "."))) != -1)) {

            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                //��������, ���������� �� ����� ��� ����� � ���
                if (new BigDecimal(txtCharge.getText().replace(",", ".")).compareTo(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO()) < 1) {
                    //��������, � ����� ��������� ���
                    switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                        case 0: {//��� ������, ��������� ���
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openReceipt(0)) {
                                //�������� ���� ���������
                                log.debug("unable to open receipt - payCashButton interrupt ");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("���������� ������� ��� ��� �������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                return; //�������� ���������� ���������� ������ ��� ������ "��������"
                            }
                            for (GoodsInCheck gic : MainApp.getGoodsInCheckObservableList()) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).saleGoodsToRRO(0, gic.getQuantity(), gic.getGoods().getCode(), gic.getGoods().getPrice())) {
                                    //���������� ������ � ��� ��� ���������
                                    log.debug("unable to make 'sale_plu' to RRO - payCashButton interrupt ");
                                    Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                                    alertQuestion.setTitle("������");
                                    alertQuestion.setHeaderText("������ ��� ���������� ������ � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError())
                                            + "\n�������� ������ ����?");
                                    alertQuestion.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult()
                                            + " {" + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastEvent() + "}");
                                    Optional<ButtonType> result = alertQuestion.showAndWait();
                                    if (result.isPresent() && result.get() == ButtonType.OK) {
                                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt();
                                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                        return; //�������� ���������� ���������� ������ ��� ������ "��������"
                                    }
                                }
                            }
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).payGoodsToRRO(0, new BigDecimal(txtValue.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_EVEN))) {
                                //������ ���� ���������
                                log.debug("unable to pay receipt - payCashButton interrupt ");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("���������� ������� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                            } else {
                                MainApp.getGoodsInCheckObservableList().clear();  //������ �������, ������� ������ �� ����
                                mainApp.setCheckSummary(BigDecimal.ZERO);  //� �������� �� ����
                                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                                    MainApp.setCashSumInRRO("���������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
                                    MainApp.setCCSumInRRO("��������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO().toString());
                                } else {
                                    MainApp.setCashSumInRRO("���������: �/�");
                                    MainApp.setCCSumInRRO("��������� ������: �/�");
                                }

                            }
                            break;
                        }
                        case 1: {//��� ������ ��� �������
                            Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                            alertRecQuestion.setTitle("�������������");
                            alertRecQuestion.setHeaderText("��� ������ ��� �������\n�������� ��� ��� �������� ������� ������ ���������?");
                            alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ������");
                            Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                            if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                    //������ ���� ���������
                                    log.debug("unable to cancel receipt - setExitButtonButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("������");
                                    alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                }
                            } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                log.debug("receipt opened for sale - user selected setPayCashButton interrupt");
                            }
                            break;
                        }
                        case 2: {//��� ������ ������ ��� ������
                            log.debug("receipt opened only for payment");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("�������������");
                            alert.setHeaderText("��� ������ ������ ��� ������\n��� �������� �������� ������ �������.\n��������� � �������� ��� � �������� ������ ����������� �����");
                            alert.showAndWait();
                            break;
                        }
                        case 3: {//��� ������ ��� ��������
                            Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                            alertRecQuestion.setTitle("�������������");
                            alertRecQuestion.setHeaderText("��� ������ ��� ��������\n�������� ��� ��� �������� ������� ������ ���������?");
                            alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ������");
                            Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                            if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                    //������ ���� ���������
                                    log.debug("unable to cancel receipt - setExitButtonButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("������");
                                    alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                }
                            } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                log.debug("receipt opened for return - user selected setPayCashButton interrupt");
                            }
                            break;
                        }
                        case 99: {//��������� ���� ������������
                            log.debug("receipt status not defined");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("��������� ���� �� ����������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("������������� ���������� � ��������������\n��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                            break;
                        }
                    }
                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                    Stage stage = (Stage) btnPayCash.getScene().getWindow();
                    stage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("������");
                    alert.setHeaderText("����� ����� ��������� ���������� �������� � �����");
                    alert.showAndWait();
                }
            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("������");
            alert.setHeaderText("������� ������� ����� �������� �� ����������");
            alert.showAndWait();
        }
    }

    /**
     * ������� ������ "�����"
     */
    public void setPayCC() {
        Alert alertCCQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertCCQuestion.setTitle("�������������");
        alertCCQuestion.setHeaderText("������� �� ������ ������ ��������� ������?");
        ButtonType buttonSuccess = new ButtonType("�������");
        ButtonType buttonUnsuccess = new ButtonType("������ �� ������");
        ButtonType buttonCancel = new ButtonType("������", ButtonBar.ButtonData.CANCEL_CLOSE);
        alertCCQuestion.getButtonTypes().setAll(buttonSuccess, buttonUnsuccess, buttonCancel);
        Optional<ButtonType> resultCC = alertCCQuestion.showAndWait();
        if (resultCC.isPresent() && resultCC.get() == buttonSuccess) {
            //������ ������ �������
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                //������� ��������, � ����� ��������� ���
                switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                    case 0: {//��� ������, ��������� ���
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openReceipt(0)) {
                            //�������� ���� ���������
                            log.debug("unable to open receipt - payCashButton interrupt ");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("���������� ������� ��� ��� �������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                            return; //�������� ���������� ���������� ������ ��� ������ "��������"
                        }
                        for (GoodsInCheck gic : MainApp.getGoodsInCheckObservableList()) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).saleGoodsToRRO(0, gic.getQuantity(), gic.getGoods().getCode(), gic.getGoods().getPrice())) {
                                //���������� ������ � ��� ��� ���������
                                log.debug("unable to make 'sale_plu' to RRO - payCashButton interrupt ");
                                Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                                alertQuestion.setTitle("������");
                                alertQuestion.setHeaderText("������ ��� ���������� ������ � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError())
                                        + "�������� ������ ����?");
                                alertQuestion.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult()
                                        + " {" + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastEvent() + "}");
                                Optional<ButtonType> result = alertQuestion.showAndWait();
                                if (result.isPresent() && result.get() == ButtonType.OK) {
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt();
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                    return; //�������� ���������� ���������� ������ ��� ������ "�����"
                                }
                            }
                        }
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).payGoodsToRRO(2, new BigDecimal(txtValue.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_EVEN))) {
                            //������ ���� ���������
                            log.debug("unable to pay receipt - payCashButton interrupt");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("���������� ������� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                        } else {
                            MainApp.getGoodsInCheckObservableList().clear();  //������ �������, ������� ������ �� ����
                            mainApp.setCheckSummary(BigDecimal.ZERO);  //� �������� �� ����
                            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                                MainApp.setCashSumInRRO("���������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
                                MainApp.setCCSumInRRO("��������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO().toString());
                            } else {
                                MainApp.setCashSumInRRO("���������: �/�");
                                MainApp.setCCSumInRRO("��������� ������: �/�");
                            }
                        }

                        break;
                    }
                    case 1: {//��� ������ ��� �������
                        Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                        alertRecQuestion.setTitle("�������������");
                        alertRecQuestion.setHeaderText("��� ������ ��� �������\n�������� ��� ��� �������� ������� ������ ������?");
                        alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ������");
                        Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                        if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                //������ ���� ���������
                                log.debug("unable to cancel receipt - setPayCCButton interrupt");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                            }
                        } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                            log.debug("receipt opened for sale - user selected setPay��Button interrupt");
                        }
                        break;
                    }
                    case 2: {//��� ������ ������ ��� ������
                        log.debug("receipt opened only for payment");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("�������������");
                        alert.setHeaderText("��� ������ ������ ��� ������\n��� �������� �������� ������ �������.\n��������� � �������� ��� � �������� ������ ����������� �����");
                        alert.showAndWait();
                        break;
                    }
                    case 3: {//��� ������ ��� ��������
                        Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                        alertRecQuestion.setTitle("�������������");
                        alertRecQuestion.setHeaderText("��� ������ ��� ��������\n�������� ��� ��� �������� ������� ������ ������?");
                        alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ������");
                        Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                        if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                //������ ���� ���������
                                log.debug("unable to cancel receipt - setPayCCButton interrupt");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                            }
                        } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                            log.debug("receipt opened for return - user selected setPayCCButton interrupt");
                        }
                        break;
                    }
                    case 99: {//��������� ���� ������������
                        log.debug("receipt status not defined");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("������");
                        alert.setHeaderText("��������� ���� �� ����������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                        alert.setContentText("������������� ���������� � ��������������\n��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                        alert.showAndWait();
                        break;
                    }
                }
            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
            Stage stage = (Stage) btnPayCC.getScene().getWindow();
            stage.close();
        } else if (resultCC.isPresent() && resultCC.get() == buttonUnsuccess) {
            //������ �� ������
        } else if (resultCC.isPresent() && resultCC.get() == buttonCancel) {
            //������ �� ������
            Stage stage = (Stage) btnPayCC.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * ������� ������ "������� ������"
     */
    public void setGoodsReturn() {
        Alert alertRetQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertRetQuestion.setTitle("�������������");
        alertRetQuestion.setHeaderText("�������� ������� ���������� ������?");
        Optional<ButtonType> resultRet = alertRetQuestion.showAndWait();
        if (resultRet.isPresent() && resultRet.get() == ButtonType.OK) {
            //��, ������ ������ �����������
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                //��������, ���������� �� ����� ��� �������� � ���
                if (new BigDecimal(txtToPay.getText().replace(",", ".")).compareTo(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO()) < 1) {
                    //��������, � ����� ��������� ���
                    switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                        case 0: {//��� ������, ��������� ��� ��� ��������
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openReceipt(1)) {
                                //�������� ���� ���������
                                log.debug("unable to open receipt - goodsReturnButton interrupt ");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("���������� ������� ��� ��� ������� ������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                return; //�������� ���������� ���������� ������ ��� ������ "������� ������"
                            }
                            for (GoodsInCheck gic : MainApp.getGoodsInCheckObservableList()) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).saleGoodsToRRO(0, gic.getQuantity(), gic.getGoods().getCode(), gic.getGoods().getPrice())) {
                                    //���������� ������ � ��� ��� ���������
                                    log.debug("unable to make 'sale_plu' to RRO - goodsReturnButton interrupt ");
                                    Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                                    alertQuestion.setTitle("������");
                                    alertQuestion.setHeaderText("������ ��� ���������� ������ � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError())
                                            + "\n�������� ������ ����?");
                                    alertQuestion.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult()
                                            + " {" + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastEvent() + "}");
                                    Optional<ButtonType> result = alertQuestion.showAndWait();
                                    if (result.isPresent() && result.get() == ButtonType.OK) {
                                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt();
                                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                        return; //�������� ���������� ���������� ������ ��� ������ "������� ������"
                                    }
                                }
                            }
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).payGoodsToRRO(0, new BigDecimal(txtToPay.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_EVEN))) {
                                //������ ���� ���������
                                log.debug("unable to pay receipt - payCashButton interrupt ");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("���������� ������� ������ ���� �������� ������� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                            } else {
                                MainApp.getGoodsInCheckObservableList().clear();  //������ �������, ������� ������ �� ����
                                mainApp.setCheckSummary(BigDecimal.ZERO);  //� �������� �� ����
                                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                                    MainApp.setCashSumInRRO("���������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
                                    MainApp.setCCSumInRRO("��������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO().toString());
                                } else {
                                    MainApp.setCashSumInRRO("���������: �/�");
                                    MainApp.setCCSumInRRO("��������� ������: �/�");
                                }

                            }
                            break;
                        }
                        case 1: {//��� ������ ��� �������
                            Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                            alertRecQuestion.setTitle("�������������");
                            alertRecQuestion.setHeaderText("��� ������ ��� �������\n�������� ��� ��� �������� ������� �������� ������?");
                            alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ��������");
                            Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                            if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                    //������ ���� ���������
                                    log.debug("unable to cancel receipt - goodsReturnButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("������");
                                    alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                }
                            } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                log.debug("receipt opened for sale - user selected goodsReturnButton interrupt");
                            }
                            break;
                        }
                        case 2: {//��� ������ ������ ��� ������
                            log.debug("receipt opened only for payment");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("�������������");
                            alert.setHeaderText("��� ������ ������ ��� ������\n��� �������� �������� ������ �������.\n��������� � �������� ��� � �������� ������ ����������� �����");
                            alert.showAndWait();
                            break;
                        }
                        case 3: {//��� ������ ��� ��������
                            Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                            alertRecQuestion.setTitle("�������������");
                            alertRecQuestion.setHeaderText("��� ������ ��� ��������\n�������� ��� ��� �������� ������� �������� ������?");
                            alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ��������");
                            Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                            if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                    //������ ���� ���������
                                    log.debug("unable to cancel receipt - goodsReturnButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("������");
                                    alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                }
                            } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                log.debug("receipt opened for return - user selected goodsReturnButton interrupt");
                            }
                            break;
                        }
                        case 99: {//��������� ���� ������������
                            log.debug("receipt status not defined");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("��������� ���� �� ����������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("������������� ���������� � ��������������\n��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                            break;
                        }
                    }
                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                    Stage stage = (Stage) btnGoodsReturn.getScene().getWindow();
                    stage.close();
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("������");
                    alert.setHeaderText("����� �������� ��������� ���������� �������� � �����");
                    alert.showAndWait();
                }
            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
        } else if (resultRet.isPresent() && resultRet.get() == ButtonType.CANCEL) {
            //������ �� ��������
            Stage stage = (Stage) btnGoodsReturn.getScene().getWindow();
            stage.close();
        }
    }


    public ArrayList<Goods> deserializeGoods() {
        ArrayList<Goods> goods = null;
        String selectedGoodsFilePath = "selgds.ser";
        try {
            log.debug("deserializing selected goods from file");
            ObjectInputStream in = new ObjectInputStream(Files.newInputStream(Paths.get(selectedGoodsFilePath)));
            goods = (ArrayList<Goods>) in.readObject();
            in.close();
        } catch (ClassNotFoundException | IOException e) {
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
        } catch (ClassNotFoundException | IOException e) {
            log.debug("error while deserializing goods group from file " + e.toString());
        }
        return goodsGroups;
    }
}
