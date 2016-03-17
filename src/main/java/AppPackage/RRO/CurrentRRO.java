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
     * конструктор для заданного типа принтера
     *
     * @param printerType - цифровой код принтера
     * @param port        СОМ-порт
     * @param speed       скорость СОМ-порта
     */
    private CurrentRRO(int printerType, String port, String speed) {
        switch (printerType) {
            case 0: {
            }
            case 1: { //мини-фп
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

        //получение расшифровки кодов ошибок из текстового файла
        try {
            String errorCodesFilePath = "rro_error_codes.txt";
            log.debug("getting error codes comments data from rro_error_codes.txt");
            //если файл существует и размером не более 4096 байта (что бы не переполнить буфер)
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
     * Метод открытия порта (только для подключенный по СОМ-порту принтеров, напр. Мини-ФП54)
     *
     * @return в случае успеха - возвращается модель принтера.
     * иначе -если устройство находится в состоянии ошибки,
     * то в ответ вернется код ошибки
     * (если ошибок несколько, то отобразится наименьшая в десятичном формате),
     * а через точку с запятой результат команды get_sys_monitor.
     */

    //TODO где то проверять файл с серийным номером
    public boolean openPortMiniFP() {
        if (!MainApp.isRROLogEnabled()) {
            Dispatch.call((Dispatch) rro_object, getDllName(), "set_error_log;0;"); //отключаем лог ошибок драйвера принтера}
        } else {
            Dispatch.call((Dispatch) rro_object, getDllName(), "set_error_log;1;");
        }
        Dispatch.call((Dispatch) rro_object, getDllName(), "open_port;" + port + ";" + speed);  //выполняем команду открытия порта
        setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));

        if (getLastError() == 0) { //порт открыт успешно

            Dispatch.call((Dispatch) rro_object, getDllName(), "get_serial_num;");  //выполняем команду проверки серийного номера
            setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
            setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
            if (getLastError() == 0) { //серийный номер считан успешно
                if (getSerialNum().length() == 0) { //серийный номер пока не заполнен (первый вызов)
                    setSerialNum(Arrays.asList(getLastResult().split(";")).get(3)); //par3 - серийный номер
                    setSerialNumberLockFile(); //запишем так же в файл
                    return true;
                } else if (getSerialNum().equals(Arrays.asList(getLastResult().split(";")).get(3))) { //серийный номер совпадает с предидущим
                    return true;
                } else { //сериый номер не совпадает с тем, что был распечатан в прошлый раз
                    log.debug("RRO serial number not equal. Was: " + getSerialNum() + "  Now: " + Arrays.asList(getLastResult().split(";")).get(3));
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Серийный номер РРО не совпадает с тем, что был при прошлой операции\n" +
                            "Для продолжения работы нужен вход администратора");
                    alert.setContentText("Был: " + getSerialNum() + "  Текущий: " + Arrays.asList(getLastResult().split(";")).get(3));
                    alert.showAndWait();
                    MainApp.getRootLayout().setCenter(MainFormController.getRootPane());
                    return false;
                }
            } else {
                log.debug("serial number not read: " + getLastResult());
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Невозможно считать серийный номер регистратора\nОписание ошибки: " + errorCodesHashMap.get(getLastError()));
                alert.setContentText("Пожалуйста, проверьте настройки порта или подключение принтера\n" + "Служебная информация: " + getLastResult());
                alert.showAndWait();
                return false;
            }
        } else {
            setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
            log.debug("Printer port not opened: " + getLastResult());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Невозможно открыть порт регистратора\nОписание ошибки: " + errorCodesHashMap.get(getLastError()));
            alert.setContentText("Пожалуйста, проверьте настройки порта или подключение принтера\n" + "Служебная информация: " + getLastResult());
            alert.showAndWait();
            return false;
        }

    }


    /**
     * Метод получения суммы оплат наличными в РРО
     *
     * @return BigDecimal сумма наличных в РРО
     */
    public BigDecimal getCashInRRO() {
        BigDecimal cash = new BigDecimal(0);
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                //if (openPortMiniFP()) {
                    if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_cashbox_sum;").toString())) {
                        setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                        cash = new BigDecimal(Arrays.asList(getLastResult().split(";")).get(1)); //par1 - наличные
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
     * Метод получения суммы оплат по кредитным картам в РРО
     *
     * @return BigDecimal сумма оплат по кредитным картам в РРО
     */
    public BigDecimal getCreditInRRO() {
        BigDecimal cc = new BigDecimal(0);
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                //if (openPortMiniFP()) {
                    if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_cashbox_sum;").toString())) {
                        setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                        cc = new BigDecimal(Arrays.asList(getLastResult().split(";")).get(3)); //par3 - кредитная карта
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
     * Метод получения количества использованных записей в базе товаров в РРО
     *
     * @return int
     */
    public int getStatusGoodsOccupiedInRRO() {
        int goodsOccupiedInRRO = 0;
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                //if (openPortMiniFP()) {
                    if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_status;1;").toString())) {
                        setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                        goodsOccupiedInRRO = Integer.parseInt(Arrays.asList(getLastResult().split(";")).get(17)); //par17 - Количество использованных записей в базе товаров
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
     * Метод добавления товаров в базу товаров в РРО
     *
     * @return int
     */
    public boolean addGoodsToRRO(int code, int taxGroup, int sellTypeRRO, String name) {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                /*  add_plu Добавить/обновить товар
                 *  par1  Код товара Целое число 1-999999
                 *  par2  Налоговая ставка: 0–Ставка Е Без НДС и Без доп. сбора 1-5 Ставки А-Д Целое число
                 *  par3  Штучный/весовой товар (0/1) Целое число
                 *  par4  Разрешить/запретить продажу (0/1) Целое число
                 *  par5  Разрешить/запретить подсчет количества (0/1) Целое число
                 *  par6  Одиночная продажа запрещена/разрешена (0/1) Целое число
                 *  par7  номер отдела Целое число 1-64
                 *  par8  Цена (в грн.)  Число с запятой (2 знака) 0-999999.99 Разделитель целой и дробной частей – точка.
                 *        Для программирования товара с открытой ценой, следует в параметрах товара указать цену: 0.00
                 *  par9  Штрих-код Целое число 0-(1019-1) Игнорируется для фискальных регистраторов
                 *  par10 Наименование товара Строка До 48 символов. Кодовая таблица WIN-1251. Разрешены символы: 20h-FFh. Символ «;» записывается как «\;»
                 *  par11 Количество (в штуках или килограммах) Число с запятой (3 знака) 0-2147483.647 Количество указывается в штуках или килограммах. Разделитель целой и дробной части – точка
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
