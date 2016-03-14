package AppPackage.Controllers;

import AppPackage.MainApp;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
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
     * нажатие кнопки ","
     */
    public void setBtnComa() {
        if (txtValue.getText().length() < 8) {
            if (txtValue.getText().length() == 0) {
                txtValue.setText("0,");
            } else if (!txtValue.getText().contains(",")) {
                txtValue.setText(txtValue.getText() + ",");
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
        Stage stage = (Stage) btnPayCash.getScene().getWindow();
        stage.close();
    }

    /**
     * нажатие кнопки "карта"
     */
    public void setPayCC() {
        Stage stage = (Stage) btnPayCash.getScene().getWindow();
        stage.close();
    }

    /**
     * нажатие кнопки "возврат товара"
     */
    public void setGoodsReturn() {
        Stage stage = (Stage) btnPayCash.getScene().getWindow();
        stage.close();
    }

}
