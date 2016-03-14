package AppPackage.RRO;

import AppPackage.MainApp;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.Arrays;


/**
 * Seems to be Singleton for RRO printer
 * Created by Eraser on 29.02.2016.
 */
public class CurrentRRO {

    private static final Logger log = Logger.getLogger(CurrentRRO.class);
    private static CurrentRRO instance;
    private long lastError;
    private String lastEvent;
    private String lastResult;
    private Object rro_object;
    private String port;
    private String speed;
    private String dllName;
    private int printerType;

    /**
     * ����������� ��� ��������� ���� ��������
     *
     * @param printerType - �������� ��� ��������
     * @param port        ���-����
     * @param speed       �������� ���-�����
     */
    private CurrentRRO(int printerType, String port, String speed) {
        switch (printerType) {
            case 0: {
            }
            case 1: { //����-��
                ActiveXComponent comp = new ActiveXComponent("ecrmini.T400");
                rro_object = comp.getObject();
                log.debug("The Library been loaded, and an activeX component been created");
                setPort(port);
                setSpeed(speed);
                setDllName("t400me");
                setPrinterType(printerType);
                break;
            }
            case 2: {
            }
        }
    }

    public static CurrentRRO getInstance(int printerType, String port, String speed) {
        if (instance == null) {
            instance = new CurrentRRO(printerType, port, speed);
        }
        return instance;
    }


    public int getPrinterType() {
        return printerType;
    }

    public void setPrinterType(int printerType) {
        this.printerType = printerType;
    }

    public long getLastError() {
        return lastError;
    }

    private void setLastError(long lastError) {
        this.lastError = lastError;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    private void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public String getLastResult() {
        return lastResult;
    }

    private void setLastResult(String lastResult) {
        this.lastResult = lastResult;
    }

    public String getPort() {
        return port;
    }

    private void setPort(String port) {
        this.port = port;
    }

    public String getSpeed() {
        return speed;
    }

    private void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getDllName() {
        return dllName;
    }

    private void setDllName(String dllName) {
        this.dllName = dllName;
    }

    /**
     * ������� �������� ����� (������ ��� ������������ �� ���-����� ���������, ����. ����-��54)
     *
     * @return � ������ ������ - ������������ ������ ��������.
     * ����� -���� ���������� ��������� � ��������� ������,
     * �� � ����� �������� ��� ������
     * (���� ������ ���������, �� ����������� ���������� � ���������� �������),
     * � ����� ����� � ������� ��������� ������� get_sys_monitor.
     */
    private boolean openPortMiniFP() {
        if (!MainApp.isRROLogEnabled()) {
            Dispatch.call((Dispatch) rro_object, getDllName(), "set_error_log;0;"); //��������� ��� ������ �������� ��������}
        } else {
            Dispatch.call((Dispatch) rro_object, getDllName(), "set_error_log;1;");
        }
        Dispatch.call((Dispatch) rro_object, getDllName(), "open_port;" + port + ";" + speed);  //��������� �������
        setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString())); // �������� ���������
        if (getLastError() == 0) { //���� ������ �������
            return true;
        } else {
            setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
            log.debug("Printer port not opened: " + getLastResult());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("������");
            alert.setHeaderText("���������� ������� ���� ������������");
            alert.setContentText("����������, ��������� ��������� �����\n" + "��������� ����������:" + getLastResult());
            alert.showAndWait();
            return false;
        }
    }


    /**
     * ������� ��������� ����� ����� ��������� � ���
     *
     * @return BigDecimal ����� �������� � ���
     */
    public BigDecimal getCashInRRO() {
        BigDecimal cash = new BigDecimal(0);
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //����-��
                if (openPortMiniFP()) {
                    if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_cashbox_sum;").toString())) {
                        setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                        cash = new BigDecimal(Arrays.asList(getLastResult().split(";")).get(1)); //par1 - ��������
                    }
                    closePortMiniFP();
                }
                break;
            }
            case 2: {
            }
        }
        return cash;
    }

    /**
     * ������� ��������� ����� ����� �� ��������� ������ � ���
     *
     * @return BigDecimal ����� ����� �� ��������� ������ � ���
     */
    public BigDecimal getCreditInRRO() {
        BigDecimal cash = new BigDecimal(0);
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //����-��
                if (openPortMiniFP()) {
                    if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_cashbox_sum;").toString())) {
                        setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                        cash = new BigDecimal(Arrays.asList(getLastResult().split(";")).get(3)); //par3 - ��������� �����
                    }
                    closePortMiniFP();
                }
                break;
            }
            case 2: {
            }
        }
        return cash;
    }


    private void closePortMiniFP() {
        Dispatch.call((Dispatch) rro_object, getDllName(), "close_port");
    }


}
