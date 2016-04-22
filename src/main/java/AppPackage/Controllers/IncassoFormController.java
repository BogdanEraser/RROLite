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
            lblRROSumCash.setText("���������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
        } else {
            lblRROSumCash.setText("���������: �/�");
        }
        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
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
     * ������� ������ "���� � ���"
     */
    public void setToRRO() {
        if ((txtValue.getText().length() != 0) & (new BigDecimal(txtValue.getText().replace(",", ".")).compareTo(new BigDecimal("0.01")) != -1)) {
            Alert alertInQuestion = new Alert(Alert.AlertType.CONFIRMATION);
            alertInQuestion.setTitle("�������������");
            alertInQuestion.setHeaderText("������ � ��� " + txtValue.getText() + " ?");
            Optional<ButtonType> resultRet = alertInQuestion.showAndWait();
            if (resultRet.isPresent() && resultRet.get() == ButtonType.OK) {
                //��, ���� ����� �����������
                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                    //��������, � ����� ��������� ���
                    switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                        case 0: {//��� ������, ��������� ���
                            BigDecimal summ = new BigDecimal(txtValue.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cashInOut(0, summ)) {
                                //���� ����� ����������
                                log.debug("unable to make cash in - toRROButton interrupt");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("���������� ���������� ���� ����� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                return; //�������� ���������� ���������� ������ ��� ������ "����"
                            }
                            MainApp.setCashSumInRRO("���������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
                            break;
                        }
                        case 1: {//��� ������ ��� �������
                            Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                            alertRecQuestion.setTitle("�������������");
                            alertRecQuestion.setHeaderText("��� ������ ��� �������\n�������� ��� ��� �������� ������� �������� �����?");
                            alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ��������");
                            Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                            if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                    //������ ���� ���������
                                    log.debug("unable to cancel receipt - toRROButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("������");
                                    alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                }
                            } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                log.debug("receipt opened for sale - user selected toRROButton interrupt");
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
                            alertRecQuestion.setHeaderText("��� ������ ��� ��������\n�������� ��� ��� �������� ������� �������� �����?");
                            alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ��������");
                            Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                            if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                    //������ ���� ���������
                                    log.debug("unable to cancel receipt - toRROButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("������");
                                    alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                }
                            } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                log.debug("receipt opened for return - user selected toRROButton interrupt");
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
                    Stage stage = (Stage) btnToRRO.getScene().getWindow();
                    stage.close();
                }
                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
            } else if (resultRet.isPresent() && resultRet.get() == ButtonType.CANCEL) {
                //������ �� ��������
                Stage stage = (Stage) btnToRRO.getScene().getWindow();
                stage.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("������");
            alert.setHeaderText("������������ ����");
            alert.setContentText("���������� ���������� ����� ����� - 0,01 ��� (1 �������)");
            alert.showAndWait();
        }
    }

    /**
     * ������� ������ "������� �� ���"
     */

    public void setFromRRO() {
        if ((txtValue.getText().length() != 0) & (new BigDecimal(txtValue.getText().replace(",", ".")).compareTo(new BigDecimal("0.01")) != -1)) {
            Alert alertInQuestion = new Alert(Alert.AlertType.CONFIRMATION);
            alertInQuestion.setTitle("�������������");
            alertInQuestion.setHeaderText("������ �� ��� " + txtValue.getText() + " ?");
            Optional<ButtonType> resultRet = alertInQuestion.showAndWait();
            if (resultRet.isPresent() && resultRet.get() == ButtonType.OK) {
                //��, ���� ����� �����������
                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                    //��������, ���������� �� ����� ��� ������� �� ���
                    if (new BigDecimal(txtValue.getText().replace(",", ".")).compareTo(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO()) < 1) {
                        //��, ����� ����������
                        //��������, � ����� ��������� ���
                        switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                            case 0: {//��� ������, ��������� ���
                                BigDecimal summ = new BigDecimal(txtValue.getText().replace(",", ".")).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cashInOut(1, summ)) {
                                    //���� ����� ����������
                                    log.debug("unable to make cash in - toRROButton interrupt");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("������");
                                    alert.setHeaderText("���������� ���������� ���� ����� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alert.showAndWait();
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                    return; //�������� ���������� ���������� ������ ��� ������ "����"
                                }
                                MainApp.setCashSumInRRO("���������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
                                break;
                            }
                            case 1: {//��� ������ ��� �������
                                Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                                alertRecQuestion.setTitle("�������������");
                                alertRecQuestion.setHeaderText("��� ������ ��� �������\n�������� ��� ��� �������� ������� �������� �����?");
                                alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ��������");
                                Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                                if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                        //������ ���� ���������
                                        log.debug("unable to cancel receipt - toRROButton interrupt");
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("������");
                                        alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                        alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                        alert.showAndWait();
                                    }
                                } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                    log.debug("receipt opened for sale - user selected toRROButton interrupt");
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
                                alertRecQuestion.setHeaderText("��� ������ ��� ��������\n�������� ��� ��� �������� ������� �������� �����?");
                                alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ��������");
                                Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                                if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                        //������ ���� ���������
                                        log.debug("unable to cancel receipt - toRROButton interrupt");
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("������");
                                        alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                        alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                        alert.showAndWait();
                                    }
                                } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                    log.debug("receipt opened for return - user selected toRROButton interrupt");
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
                        Stage stage = (Stage) btnToRRO.getScene().getWindow();
                        stage.close();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("������");
                        alert.setHeaderText("����� ������� ��������� ���������� �������� � �����");
                        alert.showAndWait();
                    }
                }
                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
            } else if (resultRet.isPresent() && resultRet.get() == ButtonType.CANCEL) {
                //����� �� ��������
                Stage stage = (Stage) btnToRRO.getScene().getWindow();
                stage.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("������");
            alert.setHeaderText("������������ ����");
            alert.setContentText("���������� ���������� ����� ������� - 0,01 ��� (1 �������)");
            alert.showAndWait();
        }
    }
}
