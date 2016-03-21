package AppPackage.Controllers;

import AppPackage.Entities.Goods;
import AppPackage.Entities.GoodsGroup;
import AppPackage.Entities.GoodsInCheck;
import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
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
     * ������� ������ ","
     */
    public void setBtnComa() {
        if (txtValue.getText().length() < 8) {
            if (txtValue.getText().length() == 0) {
                txtValue.setText("0,");
            } else if (!txtValue.getText().contains(",")) {
                txtValue.setText(txtValue.getText() + ",");
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
        if ((txtValue.getText().length() != 0) & (new BigDecimal(txtValue.getText()).compareTo(new BigDecimal(0.01)) == 1)) {
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).openPortMiniFP()) {
                //������� ��������, � ����� ��������� ���
                switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getReceiptStatusFromRRO()) {
                    case 0: {//��� ������, ��������� ���
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).openReceipt(0)) {
                            //�������� ���� ���������
                            log.debug("unable to open receipt - payCashButton interrupt ");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("���������� ������� ��� ��� �������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                            alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                            alert.showAndWait();
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                            return; //�������� ���������� ���������� ������ ��� ������ "��������"
                        }
                        for (GoodsInCheck gic : MainApp.getGoodsInCheckObservableList()) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).saleGoodsToRRO(0, gic.getQuantity(), gic.getGoods().getCode(), gic.getGoods().getPrice())) {
                                //���������� ������ � ��� ��� ���������
                                log.debug("unable to make 'sale_plu' to RRO - payCashButton interrupt ");
                                Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                                alertQuestion.setTitle("������");
                                alertQuestion.setHeaderText("������ ��� ���������� ������ � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError())
                                        + "�������� ������ ����?");
                                alertQuestion.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult()
                                        + " {" + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastEvent() + "}");
                                Optional<ButtonType> result = alertQuestion.showAndWait();
                                if (result.isPresent() && result.get() == ButtonType.OK) {
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).cancelReceipt();
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                                    return; //�������� ���������� ���������� ������ ��� ������ "��������"
                                }
                            }
                        }
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).payGoodsToRRO(0, new BigDecimal(txtValue.getText()).setScale(2, BigDecimal.ROUND_HALF_EVEN))) {
                            //������ ���� ���������
                            log.debug("unable to pay receipt - payCashButton interrupt ");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("���������� ������� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                            alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                            alert.showAndWait();
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                            return; //�������� ���������� ���������� ������ ��� ������ "��������"
                        } else {
                            MainApp.getGoodsInCheckObservableList().clear();  //������ �������, ������� ������ �� ����
                        }
                        break;
                    }
                    case 2: {//��� ������ ������ ��� ������
                        log.debug("receipt opened only for payment");
                        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                        alertQuestion.setTitle("�������������");
                        alertQuestion.setHeaderText("��� ������ ������ ��� ������\n�������� ��� ��� �������� ������ ����?");
                        Optional<ButtonType> result = alertQuestion.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).payGoodsToRRO(0, new BigDecimal(txtValue.getText()).setScale(2, BigDecimal.ROUND_HALF_EVEN))) {
                                //������ ���� ���������
                                log.debug("unable to pay receipt - payCashButton interrupt ");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("���������� ������� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                                alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                                alert.showAndWait();
                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                                return; //�������� ���������� ���������� ������ ��� ������ "��������"
                            } else {
                                MainApp.getGoodsInCheckObservableList().clear();  //������ �������, ������� ������ �� ����
                            }
                        } else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).cancelReceipt();
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                            return; //�������� ���������� ���������� ������ ��� ������ "��������"
                        }
                        break;
                    }


                }
            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
            Stage stage = (Stage) btnPayCash.getScene().getWindow();
            stage.close();
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
        Stage stage = (Stage) btnPayCash.getScene().getWindow();
        stage.close();
    }

    /**
     * ������� ������ "������� ������"
     */
    public void setGoodsReturn() {

        //�������� �� ����� ��������� ������ � ������ (��������������)

     /*   for (GoodsGroup tmp : deserializeGoodsGroup()) {
            System.out.println(tmp.getCode() + " " + tmp.getName());
        }
        for (Goods tmp : deserializeGoods()) {
            System.out.println(tmp.getCode() + " " + tmp.getName() + " " + tmp.getPrice());
        }
    */
        Stage stage = (Stage) btnPayCash.getScene().getWindow();
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
