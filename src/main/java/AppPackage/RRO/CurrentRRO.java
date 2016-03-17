package AppPackage.RRO;

import AppPackage.Controllers.MainFormController;
import AppPackage.MainApp;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


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
    private String serialNum = "";
    private int printerType;
    public HashMap<Long, String> errorCodesHashMap;

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

        //��������� ����������� ����� ������ �� ���������� �����
        try {
            String errorCodesFilePath = "rro_error_codes.txt";
            log.debug("getting error codes comments data from rro_error_codes.txt");
            //���� ���� ���������� � �������� �� ����� 4096 ����� (��� �� �� ����������� �����)
            if (Files.exists(Paths.get(errorCodesFilePath)) & (Files.size(Paths.get(errorCodesFilePath)) <= 4096)) {
                List<String> lines = Files.readAllLines(Paths.get(errorCodesFilePath), Charset.defaultCharset());
                errorCodesHashMap = new HashMap<Long, String>(100);
                for (String string : lines) {
                    String[] splittedLines = string.split(";");
                    errorCodesHashMap.putIfAbsent(Long.parseLong(splittedLines[0]), splittedLines[1]);
                }
            }

        } catch (IOException e) {
            log.debug("error getting error codes comments data from rro_error_codes.txt " + e.toString());
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

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }


    private void setSerialNumberLockFile() {
        try {
            String serialNumberLockFilePath = "rronum.lck";
            log.debug("putting current RRO serial number into rronum.lck");
            if (Files.exists(Paths.get(serialNumberLockFilePath))) {
                Files.delete(Paths.get(serialNumberLockFilePath));
            }
            Files.createFile(Paths.get(serialNumberLockFilePath));
            if (Files.isWritable(Paths.get(serialNumberLockFilePath))) {
                Files.write(Paths.get(serialNumberLockFilePath), getSerialNum().getBytes());
            }
        } catch (IOException e) {
            log.debug("error putting current RRO serial number into rronum.lck " + e.toString());
        }
    }


    /**
     * ����� �������� ����� (������ ��� ������������ �� ���-����� ���������, ����. ����-��54)
     *
     * @return � ������ ������ - ������������ ������ ��������.
     * ����� -���� ���������� ��������� � ��������� ������,
     * �� � ����� �������� ��� ������
     * (���� ������ ���������, �� ����������� ���������� � ���������� �������),
     * � ����� ����� � ������� ��������� ������� get_sys_monitor.
     */

    //TODO ��� �� ��������� ���� � �������� �������
    public boolean openPortMiniFP() {
        if (!MainApp.isRROLogEnabled()) {
            Dispatch.call((Dispatch) rro_object, getDllName(), "set_error_log;0;"); //��������� ��� ������ �������� ��������}
        } else {
            Dispatch.call((Dispatch) rro_object, getDllName(), "set_error_log;1;");
        }
        Dispatch.call((Dispatch) rro_object, getDllName(), "open_port;" + port + ";" + speed);  //��������� ������� �������� �����
        setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));

        if (getLastError() == 0) { //���� ������ �������

            Dispatch.call((Dispatch) rro_object, getDllName(), "get_serial_num;");  //��������� ������� �������� ��������� ������
            setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
            setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
            if (getLastError() == 0) { //�������� ����� ������ �������
                if (getSerialNum().length() == 0) { //�������� ����� ���� �� �������� (������ �����)
                    setSerialNum(Arrays.asList(getLastResult().split(";")).get(3)); //par3 - �������� �����
                    setSerialNumberLockFile(); //������� ��� �� � ����
                    return true;
                } else if (getSerialNum().equals(Arrays.asList(getLastResult().split(";")).get(3))) { //�������� ����� ��������� � ����������
                    return true;
                } else { //������ ����� �� ��������� � ���, ��� ��� ���������� � ������� ���
                    log.debug("RRO serial number not equal. Was: " + getSerialNum() + "  Now: " + Arrays.asList(getLastResult().split(";")).get(3));
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("������");
                    alert.setHeaderText("�������� ����� ��� �� ��������� � ���, ��� ��� ��� ������� ��������\n" +
                            "��� ����������� ������ ����� ���� ��������������");
                    alert.setContentText("���: " + getSerialNum() + "  �������: " + Arrays.asList(getLastResult().split(";")).get(3));
                    alert.showAndWait();
                    MainApp.getRootLayout().setCenter(MainFormController.getRootPane());
                    return false;
                }
            } else {
                log.debug("serial number not read: " + getLastResult());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("������");
                alert.setHeaderText("���������� ������� �������� ����� ������������\n�������� ������: " + errorCodesHashMap.get(getLastError()));
                alert.setContentText("����������, ��������� ��������� ����� ��� ����������� ��������\n" + "��������� ����������: " + getLastResult());
                alert.showAndWait();
                return false;
            }
        } else {
            setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
            log.debug("Printer port not opened: " + getLastResult());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("������");
            alert.setHeaderText("���������� ������� ���� ������������\n�������� ������: " + errorCodesHashMap.get(getLastError()));
            alert.setContentText("����������, ��������� ��������� ����� ��� ����������� ��������\n" + "��������� ����������: " + getLastResult());
            alert.showAndWait();
            return false;
        }

    }


    /**
     * ����� ��������� ����� ����� ��������� � ���
     *
     * @return BigDecimal ����� �������� � ���
     */
    public BigDecimal getCashInRRO() {
        BigDecimal cash = new BigDecimal(0);
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //����-��
                //if (openPortMiniFP()) {
                    if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_cashbox_sum;").toString())) {
                        setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                        cash = new BigDecimal(Arrays.asList(getLastResult().split(";")).get(1)); //par1 - ��������
                //    }
                //    closePortMiniFP();
                }
                break;
            }
            case 2: {
            }
        }
        return cash;
    }

    /**
     * ����� ��������� ����� ����� �� ��������� ������ � ���
     *
     * @return BigDecimal ����� ����� �� ��������� ������ � ���
     */
    public BigDecimal getCreditInRRO() {
        BigDecimal cc = new BigDecimal(0);
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //����-��
                //if (openPortMiniFP()) {
                    if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_cashbox_sum;").toString())) {
                        setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                        cc = new BigDecimal(Arrays.asList(getLastResult().split(";")).get(3)); //par3 - ��������� �����
                //    }
                //    closePortMiniFP();
                }
                break;
            }
            case 2: {
            }
        }
        return cc;
    }


    /**
     * ����� ��������� ���������� �������������� ������� � ���� ������� � ���
     *
     * @return int
     */
    public int getStatusGoodsOccupiedInRRO() {
        int goodsOccupiedInRRO = 0;
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //����-��
                //if (openPortMiniFP()) {
                    if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_status;1;").toString())) {
                        setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                        goodsOccupiedInRRO = Integer.parseInt(Arrays.asList(getLastResult().split(";")).get(17)); //par17 - ���������� �������������� ������� � ���� �������
                  //  }
                  //  closePortMiniFP();
                }
                break;
            }
            case 2: {
            }
        }
        return goodsOccupiedInRRO;
    }

    /**
     * ����� ���������� ������� � ���� ������� � ���
     *
     * @return int
     */
    public boolean addGoodsToRRO(int code, int taxGroup, int sellTypeRRO, String name) {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //����-��
                /*  add_plu ��������/�������� �����
                 *  par1  ��� ������ ����� ����� 1-999999
                 *  par2  ��������� ������: 0������� � ��� ��� � ��� ���. ����� 1-5 ������ �-� ����� �����
                 *  par3  �������/������� ����� (0/1) ����� �����
                 *  par4  ���������/��������� ������� (0/1) ����� �����
                 *  par5  ���������/��������� ������� ���������� (0/1) ����� �����
                 *  par6  ��������� ������� ���������/��������� (0/1) ����� �����
                 *  par7  ����� ������ ����� ����� 1-64
                 *  par8  ���� (� ���.)  ����� � ������� (2 �����) 0-999999.99 ����������� ����� � ������� ������ � �����.
                 *        ��� ���������������� ������ � �������� �����, ������� � ���������� ������ ������� ����: 0.00
                 *  par9  �����-��� ����� ����� 0-(1019-1) ������������ ��� ���������� �������������
                 *  par10 ������������ ������ ������ �� 48 ��������. ������� ������� WIN-1251. ��������� �������: 20h-FFh. ������ �;� ������������ ��� �\;�
                 *  par11 ���������� (� ������ ��� �����������) ����� � ������� (3 �����) 0-2147483.647 ���������� ����������� � ������ ��� �����������. ����������� ����� � ������� ����� � �����
                 */
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "add_plu;"+
                        code+";"+taxGroup+";"+sellTypeRRO+";0;1;1;1;0.00;0;"+name+";0;").toString())) {
                    return true;
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                }
                break;

            }
            case 2: {
            }
        }
        return false;
    }
    
    public void closePortMiniFP() {
        Dispatch.call((Dispatch) rro_object, getDllName(), "close_port");
    }


}
