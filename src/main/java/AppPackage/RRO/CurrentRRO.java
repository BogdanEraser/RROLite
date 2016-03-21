package AppPackage.RRO;

import AppPackage.Controllers.MainFormController;
import AppPackage.MainApp;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import javafx.scene.control.Alert;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
     * то в ответном сообщении вернется код ошибки
     * (если ошибок несколько, то отобразится наименьшая в десятичном формате),
     * а через точку с запятой результат команды get_sys_monitor.
     */

    //TODO где то проверять файл с серийным номером
    public boolean openPortMiniFP() {
        if (MainApp.isRROLogEnabled()) {
            //получим путь расположения jar и зададим его для хранения лога от дравйвера принтера
            String pathToJar = MainApp.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            try {
                pathToJar = URLDecoder.decode(pathToJar, "utf-8");
            } catch (UnsupportedEncodingException e) {
                log.debug("unable to decode path to jar" + e.toString());
            }
            pathToJar = pathToJar.substring(1, pathToJar.lastIndexOf("/"));
            Dispatch.call((Dispatch) rro_object, getDllName(), "set_dir;" + pathToJar + ";");
            Dispatch.call((Dispatch) rro_object, getDllName(), "set_error_log;1;");
        } else {
            Dispatch.call((Dispatch) rro_object, getDllName(), "set_error_log;0;");  //отключаем лог ошибок драйвера принтера}
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
            setLastEvent(Dispatch.call((Dispatch) rro_object, "get_last_event").toString());
            log.debug("Printer port not opened: " + getLastResult());
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Невозможно открыть порт регистратора\nОписание ошибки: " + errorCodesHashMap.get(getLastError()));
            alert.setContentText("Пожалуйста, проверьте настройки порта или подключение принтера\n" + "Служебная информация: " + getLastResult() + " {" + getLastEvent() + "}");
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
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_status;1;").toString())) {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    goodsOccupiedInRRO = Integer.parseInt(Arrays.asList(getLastResult().split(";")).get(17)); //par17 - Количество использованных записей в базе товаров
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                }
                break;
            }
            case 2: {
            }
        }
        return goodsOccupiedInRRO;
    }


    /**
     * Метод получения текущей даты и времени в РРО
     *
     * @return String
     */
    public String getDateTimeInRRO() {
        StringBuilder dateTimeInRRO = new StringBuilder();
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_date_time;").toString())) {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    dateTimeInRRO.append(Arrays.asList(getLastResult().split(";")).get(1)); //par1 - Дата: (dd.mm.yyyy)
                    dateTimeInRRO.append(" ").append(Arrays.asList(getLastResult().split(";")).get(2)); //par2 - Время: (hh:mm:ss)
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                }
                break;
            }
            case 2: {
            }
        }
        return dateTimeInRRO.toString();
    }


    /**
     * Метод определения открыта ли смена в РРО
     *
     * @return String
     */
    public boolean isShiftOpened() {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_status;1;").toString())) {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    if (Integer.parseInt(Arrays.asList(getLastResult().split(";")).get(2)) == 1) {
                        return true;
                    } //par2 - Смена закрыта/открыта (0/1)
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


    /**
     * Метод задания текущего времени в РРО
     *
     * @return boolean
     */
    public boolean setDateTimeInRRO() {
        LocalDateTime localDateTime = LocalDateTime.now();
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "set_time;" + localDateTime.getHour() + ";" + localDateTime.getMinute() + ";" + localDateTime.getSecond() + ";").toString())) {
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


    /**
     * Метод получения начала смены в РРО
     *
     * @return LocalDateTime
     */
    public LocalDateTime getShiftStartDateTimeFromRRO() {
        LocalDateTime shiftStartDateTime = null;
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_status;1;").toString())) {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    //par11 - Дата начала смены дд.мм.гггг  par12 - Время начала смены чч:мм:сс
                    shiftStartDateTime = LocalDateTime.parse(((Arrays.asList(getLastResult().split(";")).get(11)) + (Arrays.asList(getLastResult().split(";")).get(11))), DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                    return shiftStartDateTime;
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                }
                break;
            }
            case 2: {
            }
        }
        return shiftStartDateTime;
    }


    /**
     * Метод получения точки отсчета 72 часов до блокировки по причине непередачи отчетов
     *
     * @return LocalDateTime
     */
    public LocalDateTime getPointOfNotSentFromRRO() {
        LocalDateTime pointOfNotSentDateTime = null;
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_status;1;").toString())) {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    //par23 - Точка отсчета 72 часов до блокировки по причине непередачи отчетов , дата   дд.мм.гггг
                    //  Если все данные переданы (дата/время блокировки не установлено), параметр будет 00.00.0000
                    //par24 - Точка отсчета 72 часов до блокировки по причине непередачи отчетов , время   чч:мм:сс
                    //  Если все данные переданы (дата/время блокировки не установлено), параметр будет 00:00:00
                    if (!(Arrays.asList(getLastResult().split(";")).get(23).equals("00.00.0000")) & !(Arrays.asList(getLastResult().split(";")).get(24).equals("00:00:00"))) {
                        pointOfNotSentDateTime = LocalDateTime.parse(((Arrays.asList(getLastResult().split(";")).get(23)) + " " + (Arrays.asList(getLastResult().split(";")).get(24))), DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                        return pointOfNotSentDateTime;
                    }
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                }
                break;
            }
            case 2: {
            }
        }
        return pointOfNotSentDateTime;
    }


    /**
     * Метод определения, что длительность текущей смены не превышает 23 часа/превышает 23 часа
     *
     * @return boolean
     */
    public boolean isShiftMoreThan23Hours() {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_status;1;").toString())) {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    //par9 - Длительность текущей смены не превышает 23 часа/превышает 23 часа (0/1) Целое число
                    if (Integer.parseInt(Arrays.asList(getLastResult().split(";")).get(9)) == 1) {
                        return true;
                    }
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


    /**
     * Метод определения, что длительность текущей смены не превышает 24 часа/превышает 24 часа
     *
     * @return boolean
     */
    public boolean isShiftMoreThan24Hours() {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_status;1;").toString())) {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    //par10 - Длительность текущей смены не превышает 24 часа/превышает 24 часа (0/1) Целое число
                    if (Integer.parseInt(Arrays.asList(getLastResult().split(";")).get(10)) == 1) {
                        return true;
                    }
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


    /**
     * Метод определения статуса чека в РРО
     *
     * @return boolean
     */
    public int getReceiptStatusFromRRO() {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                /*par3 Состояние чека (Целое число):
                 *   0 – Чек закрыт
                 *   1 – Чек открыт для продажи
                 *   2 – Чек открыт только для оплаты
                 *   3 – Чек открыт для возврата
                 */
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "get_status;1;").toString())) {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    return Integer.parseInt(Arrays.asList(getLastResult().split(";")).get(3));
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                }
                break;
            }
            case 2: {
            }
        }
        return 99;
    }


    /**
     * Метод регистрации кассира и задание его имени в таблице внутренней памяти РРО
     *
     * @return boolean
     */
    public boolean cashierRegister(String name) {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                /*
                 *  par1  Номер кассира Целое число 1-8/15 (номер кассира) 0 – отмена регистрации кассира
                 *  par2  Пароль кассира Целое число 0-999999999 Пустой пароль – 0. При отмене регистрации ввести 0 (ноль)
                 *
                 *  write_table;par1;par2;par3; [par4]; … ; [parX]; Запись в таблицу настроек
                 *         3 Имена кассиров 24
                 *  par1  Номер таблицы Целое число
                 *  par2  Номер ряда таблицы Целое число
                 *  par3-parX Данные для записи в таблицу Строка
                 */
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "cashier_registration;1;0;").toString())) {
                    if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "write_table;3;1;" + name+";").toString())) {
                        return true;
                    } else {
                        setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                        setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                        setLastEvent(Dispatch.call((Dispatch) rro_object, "get_last_event").toString());
                    }
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                    setLastEvent(Dispatch.call((Dispatch) rro_object, "get_last_event").toString());
                }
                break;
            }
            case 2: {
            }
        }
        return false;
    }


    /**
     * Метод открытия чека в РРО
     *
     * @return boolean
     */
    public boolean openReceipt(int addDel) {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                /*  open_receipt Открыть чек
                 *  par1  Открыть чек продажи/возврата (0/1) Целое число
                 */
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "open_receipt;" + addDel + ";").toString())) {
                    return true;
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                    setLastEvent(Dispatch.call((Dispatch) rro_object, "get_last_event").toString());
                }
                break;
            }
            case 2: {
            }
        }
        return false;
    }


    /**
     * Метод отмены чека в РРО
     *
     * @return boolean
     */
    public boolean cancelReceipt() {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "cancel_receipt;").toString())) {
                    return true;
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                    setLastEvent(Dispatch.call((Dispatch) rro_object, "get_last_event").toString());
                }
                break;
            }
            case 2: {
            }
        }
        return false;
    }


    /**
     * Метод добавления товаров в базу товаров в РРО
     *
     * @return boolean
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
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "add_plu;" +
                        code + ";" + taxGroup + ";" + sellTypeRRO + ";0;1;1;1;0.00;0;" + name + ";0;").toString())) {
                    return true;
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                    setLastEvent(Dispatch.call((Dispatch) rro_object, "get_last_event").toString());
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно добавить(обновить) товар в РРО\nОписание ошибки: " + getLastError());
                    //TODO добавить расшифровку описания ошибки при невозможности обновить товар
                    alert.setContentText("Служебная информация: " + getLastResult() + " { " + getLastEvent() + " }");
                    alert.showAndWait();
                }
                break;

            }
            case 2: {
            }
        }
        return false;
    }


    /**
     * Метод добавления товаров в чек в РРО
     *
     * @return boolean
     */
    public boolean saleGoodsToRRO(int addDel, BigDecimal qty, int code, BigDecimal price) {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                /*  sale_plu Продажа товара
                 *  par1  Добавить/отменить позицию в чеке (0/1) Целое число
                 *  par2  Продажа по коду/штрих-коду (0/1) Целое число Продажа по штрих-коду не доступна для фискальных регистраторов
                 *  par3  Продажа по запрограммированной цене/открытой цене (0/1) Целое число
                 *  par4  Количество (в штуках или килограммах) Число с запятой (3 знака) 0.001 – 9999 Разделитель целой и дробной части – точка.
                 *  par5  Код или штрих-код товара (в зависимости от опции в par2) Целое число Код: 1-999999 Штрих-код: 1-(1019-1)
                 *  par6  Цена товара (в грн.) Число с запятой (2 знака)
                 *        При отсутствии параметра продажа осуществляется по запрограммированной цене. Разделитель целой и дробной части – точка.
                 */
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "sale_plu;" + addDel + ";0;1;" +
                        qty.toString() + ";" + code + ";" + price.toString() + ";").toString())) {
                    return true;
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                    setLastEvent(Dispatch.call((Dispatch) rro_object, "get_last_event").toString());
                   /* Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно добавить(отменить) товар чеке в РРО\nОписание ошибки: " + getLastError());
                    //TODO добавить расшифровку описания ошибки при невозможности продать товар
                    alert.setContentText("Служебная информация: " + getLastResult() + " { " + getLastEvent() + " }");
                    alert.showAndWait();
                    */
                }
                break;

            }
            case 2: {
            }
        }
        return false;
    }


    /**
     * Метод продажи товаров добавленных в чек в РРО
     *
     * @return boolean
     */
    public boolean payGoodsToRRO(int payType, BigDecimal summ) {
        switch (getPrinterType()) {
            case 0: {
            }
            case 1: { //мини-фп
                /*  pay   Продажа товара
                 *  par1  Тип оплаты (0-8)
                 *          0 – наличные
                 *          1 – чек
                 *          2 – магнитная карта
                 *          3-7 – пользовательские типы оплаты Целое число
                 *  par2  Сумма оплаты (в грн.) Число с запятой (2 знака) Разделитель целой и дробной части – точка.
                 *          Если выставить сумму оплаты равной 0, то при выполнении сумма будет равна сумме по чеку
                 */
                if (Boolean.valueOf(Dispatch.call((Dispatch) rro_object, getDllName(), "pay;" + payType + ";" + summ.toString() + ";").toString())) {
                    return true;
                } else {
                    setLastResult(Dispatch.call((Dispatch) rro_object, "get_last_result").toString());
                    setLastError(Long.parseLong(Dispatch.call((Dispatch) rro_object, "get_last_error").toString()));
                    setLastEvent(Dispatch.call((Dispatch) rro_object, "get_last_event").toString());
                    /*Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно добавить(отменить) товар чеке в РРО\nОписание ошибки: " + getLastError());
                    //TODO добавить расшифровку описания ошибки при невозможности продать товар
                    alert.setContentText("Служебная информация: " + getLastResult() + " { " + getLastEvent() + " }");
                    alert.showAndWait();
                    */
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
