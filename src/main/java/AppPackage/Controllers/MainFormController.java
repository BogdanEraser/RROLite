package AppPackage.Controllers;

import AppPackage.Entities.CurrentUser;
import AppPackage.Entities.Goods;
import AppPackage.Entities.GoodsGroup;
import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import AppPackage.Utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(MainFormController.class);
    private LocalTime currentTime;
    private Scene scene;
    private Stage stage;
    private Parent root;
    private BigDecimal cashToBeInputed;

    private static MainApp mainApp;
    @FXML
    private static BorderPane mainForm;
    @FXML
    private Label lblTime;
    @FXML
    private Label lblMessage;
    @FXML
    private Label lblRROSumCash;
    @FXML
    private Label lblRROSumCredit;
    @FXML
    private Button btnExit;
    @FXML
    private Button btnSetupRRO;
    @FXML
    private Button btnStart;
    @FXML
    private ImageView ConnectedIcon;
    @FXML
    private ImageView NotConnectedIcon;
    @FXML
    private Label lblProgress;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Button btnCashInOut;
    @FXML
    private Button btnZReport;
    @FXML
    private Button btnXReport;
    @FXML
    private Button btnEmptyReceipt;



    private ResourceBundle bundle;

    public MainFormController() {
    }


    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        MainFormController.mainApp = mainApp;
    }

    public static MainApp getMainApp() {
        return mainApp;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }


    public static BorderPane getRootPane() {
        return mainForm;
    }

    public static void setRootPane(BorderPane mainForm) {
        MainFormController.mainForm = mainForm;
    }


    @FXML
    public void initialize() {

        log.debug("Initialising mainForm");
        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
            MainApp.setCashSumInRRO("Наличными: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
            MainApp.setCCSumInRRO("Кредитной картой: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO().toString());
        } else {
            MainApp.setCashSumInRRO("Наличными: Н/Д");
            MainApp.setCCSumInRRO("Кредитной картой: Н/Д");
        }
        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();

        lblMessage.setText("Пользователь: " + CurrentUser.getInstance().getName());

        lblRROSumCash.textProperty().bind(MainApp.cashSumInRROProperty());
        lblRROSumCredit.textProperty().bind(MainApp.CCSumInRROProperty());

        if (CurrentUser.getInstance().getAccessLevel() < 2) {
            btnSetupRRO.setVisible(true);
            btnEmptyReceipt.setVisible(true);
            //btnCashInOut.setVisible(true);
        } else {
            btnSetupRRO.setVisible(false);
            btnEmptyReceipt.setVisible(false);
            //btnCashInOut.setVisible(false);
        }

        currentTime = LocalTime.now();
        lblTime.setText("Сейчас: " + currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
        if (CheckInternetConnnection.getInstance().isConnected()) {
            ConnectedIcon.setVisible(true);
            NotConnectedIcon.setVisible(false);
        } else {
            ConnectedIcon.setVisible(false);
            NotConnectedIcon.setVisible(true);
        }
        Timeline everySecond = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            currentTime = LocalTime.now();
            lblTime.setText("Сейчас: " + currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
            if (currentTime.getSecond() % 10 == 0) { //каждые 10 секунд проверяем наличие интернет-соединения
                if (CheckInternetConnnection.getInstance().isConnected()) {
                    ConnectedIcon.setVisible(true);
                    NotConnectedIcon.setVisible(false);
                } else {
                    ConnectedIcon.setVisible(false);
                    NotConnectedIcon.setVisible(true);
                }
            }
        }));
        everySecond.setCycleCount(Timeline.INDEFINITE);
        everySecond.play();
    }

    public void setStartButton() {
        boolean isShiftOpened = false;
        //узнаем в каком состоянии РРО
        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
            //сначала проверим, открыта ли смена в РРО
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftOpened()) {
                //смена открыта
                isShiftOpened = true;
                Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                alertQuestion.setTitle("Подтверждение");
                alertQuestion.setHeaderText("В РРО обнаружена открытая смена.\nПродолжаем?");
                Optional<ButtonType> result = alertQuestion.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                    log.debug("shift is open - user selected setStartButton interrupt");
                    return; //отменяем дальнейшее выполнение метода для кнопки "Старт"
                }

                //проверим продолжительность смены в РРО
            /*  LocalDateTime shiftStartDateTimeFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO();
                LocalDateTime localDateTime = LocalDateTime.now();
                if (java.time.Duration.between(localDateTime, shiftStartDateTimeFromRRO).abs().toHours() >= 24) { //продолжительность смены более 24 часов
                    log.debug("shift duration is more than 24 hours");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Предупреждение");
                    alert.setHeaderText("Длительность смены превышает 24 часа.\nПредлагается закрыть смену.");
                    alert.setContentText("Начало смены: " + shiftStartDateTimeFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                }
                */

                //проверим, что продолжительность смены в РРО не более 23 часов
                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftMoreThan23Hours()) {
                    //смена более 23 часов
                    //проверим, что продолжительность смены в РРО не более 24 часов
                    if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftMoreThan24Hours()) {
                        //смена более 24 часов
                        log.debug("shift duration is more than 24 hours");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Предупреждение");
                        alert.setHeaderText("Длительность смены превышает 24 часа\nРабота с РРО будет недоступна\nПредлагается закрыть смену");
                        alert.setContentText("Начало смены: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                        alert.showAndWait();
                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                        return; //отменяем дальнейшее выполнение метода для кнопки "Старт"
                    } else {
                        log.debug("shift duration is more than 23 hours");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Предупреждение");
                        alert.setHeaderText("Длительность смены превышает 23 часа.\nПредлагается закрыть смену.");
                        alert.setContentText("Начало смены: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                        alert.showAndWait();
                    }
                }

                //проверим, что разница между датой и временем последней передачи данных и текущей датой и временем составляет не более 24 часов
                LocalDateTime pointOfNotSentFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getPointOfNotSentFromRRO();
                LocalDateTime localDateTime = LocalDateTime.now();
                if (pointOfNotSentFromRRO != null) {
                    if (java.time.Duration.between(localDateTime, pointOfNotSentFromRRO).abs().toHours() >= 24) { //продолжительность неотправки отчетов более 24 часов
                        log.debug("reports was not sent more than 24 hours ago");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Предупреждение");
                        alert.setHeaderText("Разница между датой и временем последней передачи данных и текущей датой и временем составляет более 24 часов.");
                        alert.setContentText("Точка отсчет до блокировки РРО: " + pointOfNotSentFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) +
                                "\nРРО заблокируется:  " + pointOfNotSentFromRRO.plusHours(72).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                        alert.showAndWait();
                    }
                }
            }


            //затем проверим время в РРО
            String dateTimeFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getDateTimeInRRO();
            if (dateTimeFromRRO.length() == 0) {
                //строка даты из РРО пустая
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Невозможно получить дату из РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                alert.showAndWait();
            } else { //сравним время из РРО с текущими, разрешая расхождение не более 5 минут
                LocalDateTime localDateTime = LocalDateTime.now();
                LocalDateTime localDateTimeFromRRO = LocalDateTime.parse(dateTimeFromRRO, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                if (java.time.Duration.between(localDateTime, localDateTimeFromRRO).abs().toMinutes() >= 5) { //расхождение во времение более 5 минут
                    Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                    alertQuestion.setTitle("Подтверждение");
                    alertQuestion.setHeaderText("Время в РРО и программе различаются более чем на 5 минут\nЗадать текущее время в РРО согласно времени в программе?");
                    alertQuestion.setContentText("В РРО: " + localDateTimeFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + "\nВ программе: " + localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                    Optional<ButtonType> result = alertQuestion.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).setDateTimeInRRO()) {
                            //задать дату и время в РРО не удалось
                            log.debug("unable to set datetime to RRO");
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Невозможно задать время в РРО\nРекомендуется перезагрузить РРО и задать время вручную\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("При открытой смене нельзя задавать время в РРО\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                            //return; //отменяем дальнейшее выполнение метода для кнопки "Старт"
                        } else {
                            log.debug("successfully have set time to RRO");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Сообщение");
                            alert.setHeaderText("Время в РРО успешно обновлено");
                            alert.setContentText("Рекомендуется перезагрузить РРО и затем продолжить");
                            alert.showAndWait();
                            //return; //отменяем дальнейшее выполнение метода для кнопки "Старт"
                        }
                    } else {
                        if (java.time.Duration.between(localDateTime, localDateTimeFromRRO).abs().toMinutes() > 90) { //расхождение во времение более 90 минут
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                            log.debug("datetime difference > 90 minutes - setStartButton interrupt ");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Время в РРО и программе различаются более чем на 1,5 часа\nК сожалению, дальнейшая работа невозможна");
                            alert.setContentText("Рекомендуется обратиться к администратору\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                            return; //отменяем дальнейшее выполнение метода для кнопки "Старт"
                        }
                    }
                }
            }

        }
        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();

        // получаем данные о товарах из файла экселя
        Workbook workbook = ExcelUtils.getWorkbookFromExcelFile(MainApp.getPathToDataFile());  //получаем книгу экселя

        //получим все ИЗБРАННЫЕ ДЛЯ ПРОДАЖИ товары в список
        String sheetName = "product_selected";
        Sheet sheet = workbook.getSheet(sheetName);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        if (sheet == null) {
            log.debug("File " + MainApp.getPathToDataFile() + " does not have sheet " + sheetName);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не могу открыть лист " + sheetName + " в файле " + MainApp.getPathToDataFile());
            alert.showAndWait();
        } else {
            int rowStart = 4; // Decide which rows to process (с 5-го по 38-й) - т.е. максимум 34 товара в группе
            int rowEnd = 37;
            int idx = 0;
            boolean isGroupExists;
            mainApp.allSelectedGoodsArrayList = new ArrayList<>();
            mainApp.allGoodsGroupsArrayList = new ArrayList<>();
            for (int i = 1; i < 21; i++) { //цикл по группам товаров

                Row r = sheet.getRow(rowStart - 1); //проверка наличия группы
                Cell cell = r.getCell(1, Row.RETURN_BLANK_AS_NULL);
                isGroupExists = false;
                switch (evaluator.evaluateInCell(cell).getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                        if (cell.getNumericCellValue() != 0) {
                            isGroupExists = true;
                        }
                        break;
                    case Cell.CELL_TYPE_STRING:
                        if (cell.getStringCellValue().length() > 0) {
                            if (cell.getStringCellValue() != "0") {
                                try {
                                    mainApp.allGoodsGroupsArrayList.add(new GoodsGroup((int) r.getCell(0, Row.RETURN_BLANK_AS_NULL).getNumericCellValue(), cell.getStringCellValue()));
                                } catch (IllegalStateException e) {
                                    log.debug("Wrong cell type while parsing GoodsGroup " + e);
                                }
                                isGroupExists = true;
                            }
                        }
                        break;
                    case Cell.CELL_TYPE_BLANK:
                        isGroupExists = false;
                        break;
                    default:
                        isGroupExists = false;
                        break;
                }
                if (!isGroupExists) {
                    break;
                } //при пропусках группы выходим из цикла импорта полностью

                for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                    r = sheet.getRow(rowNum);
                    if (r.getCell(0, Row.RETURN_BLANK_AS_NULL) == null) {
                        // This whole row is empty
                        //continue;
                        break; //при пропусках строки (код товара пустой) выходим из импорта
                    }
                    mainApp.allSelectedGoodsArrayList.add(new Goods()); //не пустая строка - добавляем новый элемент в список для импорта
                    //до 9 столбцов
                    for (int cn = 0; cn < 9; cn++) {
                        cell = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
                        if (cell != null) {
                            switch (cn) {
                                case 0: //goods code
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setCode((int) cell.getNumericCellValue());
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setCode(Integer.parseInt(cell.getStringCellValue()));
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setCode(0);
                                            break;
                                    }
                                    break;
                                case 1: //goods name
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setName(String.valueOf(cell.getNumericCellValue()));
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setName(cell.getStringCellValue());
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setName("Н/Д");
                                            break;
                                    }
                                    break;
                                case 2: //goods sellType
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setSellType(String.valueOf(cell.getNumericCellValue()));
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setSellType(cell.getStringCellValue());
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setSellType("");
                                            break;
                                    }
                                    break;
                                case 3: //goods sellTypeRRO
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setSellTypeRRO((int) cell.getNumericCellValue());
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setSellTypeRRO(Integer.parseInt(cell.getStringCellValue()));
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setSellTypeRRO(0);
                                            break;
                                    }
                                    break;
                                case 4: //goods price
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setPrice(BigDecimal.valueOf(cell.getNumericCellValue()));
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setPrice(BigDecimal.valueOf(Double.parseDouble(cell.getStringCellValue())));
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setPrice(BigDecimal.ZERO);
                                            break;
                                    }
                                    break;
                                case 5: //goods group
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setGoodsGroup((int) cell.getNumericCellValue());
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setGoodsGroup(Integer.parseInt(cell.getStringCellValue()));
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setGoodsGroup(20);
                                            break;
                                    }
                                    break;
                                case 6: //goods tax group
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup((int) cell.getNumericCellValue());
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            switch (cell.getStringCellValue().toUpperCase()) {
                                                case "А":
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(1);
                                                    break;
                                                case "Б":
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(2);
                                                    break;
                                                case "В":
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(3);
                                                    break;
                                                case "Г":
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(4);
                                                    break;
                                                case "Д":
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(5);
                                                    break;
                                                default:
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(1);
                                                    break;
                                            }
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(1);
                                            break;
                                    }
                                    break;
                                case 7: //goods discount group
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setDiscoutGroup((int) cell.getNumericCellValue());
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setDiscoutGroup(Integer.parseInt(cell.getStringCellValue()));
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setDiscoutGroup(0);
                                            break;
                                    }
                                    break;
                                case 8: //goods barcode
                                    switch (evaluator.evaluateInCell(cell).getCellType()) {
                                        case Cell.CELL_TYPE_NUMERIC:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setBarcode(String.valueOf(cell.getNumericCellValue()));
                                            break;
                                        case Cell.CELL_TYPE_STRING:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setBarcode(cell.getStringCellValue());
                                            break;
                                        case Cell.CELL_TYPE_BLANK:
                                            mainApp.allSelectedGoodsArrayList.get(idx).setBarcode("");
                                            break;
                                    }
                                    break;
                            }
                        }
                    }
                    idx++;
                }
                rowStart = rowStart + 35;
                rowEnd = rowEnd + 35;
            }
        }

        //получим ВСЕ ТОВАРЫ в список
        sheetName = "product_all";
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            log.debug("File " + MainApp.getPathToDataFile() + " does not have sheet " + sheetName);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не могу открыть лист " + sheetName + " в файле " + MainApp.getPathToDataFile());
            alert.showAndWait();
        } else {
            int rowStart = 3; // Decide which rows to process (с 4-го по 16004-й)
            int rowEnd = 16004;
            int idx = 0;
            mainApp.allGoodsArrayList = new ArrayList<>();

            for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                Row r = sheet.getRow(rowNum);

                if (r.getCell(0, Row.RETURN_BLANK_AS_NULL) == null) {
                    // This whole row is empty
                    //continue;
                    break; //при пропусках строки (код товара пустой) выходим из импорта
                }
                mainApp.allGoodsArrayList.add(new Goods()); //не пустая строка - добавляем новый элемент в список для импорта
                //до 9 столбцов
                for (int cn = 0; cn < 9; cn++) {
                    Cell cell = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
                    if (cell != null) {
                        switch (cn) {
                            case 0: //goods code
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        mainApp.allGoodsArrayList.get(idx).setCode((int) cell.getNumericCellValue());
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        mainApp.allGoodsArrayList.get(idx).setCode(Integer.parseInt(cell.getStringCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        mainApp.allGoodsArrayList.get(idx).setCode(0);
                                        break;
                                }
                                break;
                            case 1: //goods name
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        mainApp.allGoodsArrayList.get(idx).setName(String.valueOf(cell.getNumericCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        mainApp.allGoodsArrayList.get(idx).setName(cell.getStringCellValue());
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        mainApp.allGoodsArrayList.get(idx).setName("Н/Д");
                                        break;
                                }
                                break;
                            case 2: //goods sellType
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        mainApp.allGoodsArrayList.get(idx).setSellType(String.valueOf(cell.getNumericCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        mainApp.allGoodsArrayList.get(idx).setSellType(cell.getStringCellValue());
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        mainApp.allGoodsArrayList.get(idx).setSellType("");
                                        break;
                                }
                                break;
                            case 3: //goods sellTypeRRO
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        mainApp.allGoodsArrayList.get(idx).setSellTypeRRO((int) cell.getNumericCellValue());
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        mainApp.allGoodsArrayList.get(idx).setSellTypeRRO(Integer.parseInt(cell.getStringCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        mainApp.allGoodsArrayList.get(idx).setSellTypeRRO(0);
                                        break;
                                }
                                break;
                            case 4: //goods price
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        mainApp.allGoodsArrayList.get(idx).setPrice(BigDecimal.valueOf(cell.getNumericCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        mainApp.allGoodsArrayList.get(idx).setPrice(BigDecimal.valueOf(Double.parseDouble(cell.getStringCellValue())));
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        mainApp.allGoodsArrayList.get(idx).setPrice(BigDecimal.ZERO);
                                        break;
                                }
                                break;
                            case 5: //goods group
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        mainApp.allGoodsArrayList.get(idx).setGoodsGroup((int) cell.getNumericCellValue());
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        mainApp.allGoodsArrayList.get(idx).setGoodsGroup(Integer.parseInt(cell.getStringCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        mainApp.allGoodsArrayList.get(idx).setGoodsGroup(20);
                                        break;
                                }
                                break;
                            case 6: //goods tax group
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        mainApp.allGoodsArrayList.get(idx).setTaxGroup((int) cell.getNumericCellValue());
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        switch (cell.getStringCellValue().toUpperCase()) {
                                            case "А":
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(1);
                                                break;
                                            case "Б":
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(2);
                                                break;
                                            case "В":
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(3);
                                                break;
                                            case "Г":
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(4);
                                                break;
                                            case "Д":
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(5);
                                                break;
                                            default:
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(1);
                                                break;
                                        }
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        mainApp.allGoodsArrayList.get(idx).setTaxGroup(1);
                                        break;
                                }
                                break;
                            case 7: //goods discount group
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        mainApp.allGoodsArrayList.get(idx).setDiscoutGroup((int) cell.getNumericCellValue());
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        mainApp.allGoodsArrayList.get(idx).setDiscoutGroup(Integer.parseInt(cell.getStringCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        mainApp.allGoodsArrayList.get(idx).setDiscoutGroup(0);
                                        break;
                                }
                                break;
                            case 8: //goods barcode
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        mainApp.allGoodsArrayList.get(idx).setBarcode(String.valueOf(cell.getNumericCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        mainApp.allGoodsArrayList.get(idx).setBarcode(cell.getStringCellValue());
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        mainApp.allGoodsArrayList.get(idx).setBarcode("");
                                        break;
                                }
                                break;
                        }
                    }
                }
                idx++;
            }
        }
        if (!isShiftOpened) {
            //обновляем товары в РРО только если смена закрыта
            Thread addpluThread = null;
            if (mainApp.allGoodsArrayList.size() > 0) {
                //добавляем в РРО недостающие товары
                Task task = new Task<Void>() {
                    @Override
                    public Void call() {
                        int code;
                        int taxGroup;
                        int sellTypeRRO;
                        String name;
                        int maxProgress = mainApp.allGoodsArrayList.size();
                        int currentProgress = 0;
                        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                            for (Goods goods : mainApp.allGoodsArrayList) {
                                code = goods.getCode();
                                taxGroup = goods.getTaxGroup();
                                sellTypeRRO = goods.getSellTypeRRO();
                                name = goods.getName();

                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).addGoodsToRRO(code, taxGroup, sellTypeRRO, name)) {
                                    //добавление товар в РРО неуспешно
                                    log.debug("unable to add goods to RRO");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("Ошибка");
                                    alert.setHeaderText("Невозможно добавить(обновить) товар в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    //TODO добавить расшифровку описания ошибки при невозможности обновить товар
                                    alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult()
                                            + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastEvent());
                                    alert.showAndWait();
                                }
                                currentProgress++;
                                updateProgress(currentProgress, maxProgress);
                                try {
                                    Thread.sleep(30);
                                    Thread.yield();
                                    Thread.sleep(30);
                                } catch (InterruptedException e) {
                                    log.debug("thread 'add_plu' interrupted " + e.toString());
                                }
                            }
                        }
                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                        return null;
                    }
                };

                progressIndicator.progressProperty().bind(task.progressProperty());
                progressIndicator.setVisible(true);
                lblProgress.setVisible(true);
                addpluThread = new Thread(task);
                log.debug("thread 'add_plu' started");
                addpluThread.start();

            }

            if (addpluThread != null) {
                while (addpluThread.isAlive()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Сообщение");
                    alert.setHeaderText("Обновление товаров в РРО. Подождите...");
                    alert.showAndWait();
                }
                log.debug("thread 'add_plu' finished");
            }

            //а так же, проверим если файл с суммами существует, то предложим внести необходимую сумму в РРО
            cashToBeInputed = new BigDecimal(0);
            try {
                String cashFileName = "rrocash.lck";
                log.debug("getting current summ (cash & cc) from rrocash.lck");
                if (Files.exists(Paths.get(cashFileName)) & Files.isReadable(Paths.get(cashFileName))){
                    List<String> lines = Files.readAllLines(Paths.get(cashFileName), Charset.defaultCharset());
                    if (lines.size()>0) {
                        cashToBeInputed = new BigDecimal(lines.get(0));
                    }
                }

                if (cashToBeInputed.compareTo(BigDecimal.ZERO)==1) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Сообщение");
                    alert.setHeaderText("Необходимо произвести служебный внос в размере " + cashToBeInputed.toString());
                    alert.setContentText("После нажатия [ОК] будет открыта форма для служебного вноса с необходимой суммой");
                    alert.showAndWait();
                    //если сумма для внесения больше 0, то покажем форму для внесения
                    setCashInOutButton(cashToBeInputed);

                }
            } catch (IOException e) {
                log.debug("error getting cash and cc from rrocash.lck " + e.toString());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Не удалось считать сумму для служебного вноса");
                alert.setContentText("Возможно программа была аварийно закрыта или сумма записана вручную");
                alert.showAndWait();
            }

        }

        //сохраняем в файл выбранные товари и группы (сериализация)
       /* Thread saveSelectedGoodsAndGroupsToFileThread = new Thread(() -> {
            String goodsGroupsFilePath = "gdsgrps.ser";
            String selectedGoodsFilePath = "selgds.ser";
            try {
                log.debug("serializing goods groups to file");
                ObjectOutputStream out= new ObjectOutputStream(Files.newOutputStream(Paths.get(goodsGroupsFilePath)));
                out.writeObject(mainApp.allGoodsGroupsArrayList);
                out.flush();
                out.close();

                log.debug("serializing selected goods to file");
                out= new ObjectOutputStream(Files.newOutputStream(Paths.get(selectedGoodsFilePath)));
                out.writeObject(mainApp.allSelectedGoodsArrayList);
                out.flush();
                out.close();
            } catch (IOException e) {
                log.debug("error while serializing selected goods and goods groups to file " + e.toString());
            }
        });
        saveSelectedGoodsAndGroupsToFileThread.start();    //Запуск потока
        */

        try {
            String fxmlFormPath = "/fxml/OrderForm/OrderForm.fxml";
            log.debug("Loading OrderForm for making orders into RootLayout");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
            log.debug("Setting location from FXML - OrderForm");
            BorderPane orderPane = fxmlLoader.load();
            log.debug("Отображаем форму заказов");
            // Set OrderForm into the center of root layout.
            MainApp.rootLayout.setCenter(orderPane);
            // Give the controller access to the main app.
            OrderFormController orderFormController = fxmlLoader.getController();
            mainApp.orderFormController = orderFormController;
            OrderFormController.setRootPane(orderPane);
            orderFormController.setMainApp(mainApp);
            orderFormController.setScene(mainApp.getMainStage().getScene());
            progressIndicator.setVisible(false);
            lblProgress.setVisible(false);
        } catch (IOException e) {
            log.debug("Ошибка загрузки формы заказов " + e.toString());
        }
    }


    /**
     * Обработка кнопки выхода
     */
    public void setExitButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        //alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("Подтверждение");
        alertQuestion.setHeaderText("Вы действительно хотите выйти?");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            log.debug("trying to exit program normally");

            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {

                //проверим время в РРО
                String dateTimeFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getDateTimeInRRO();
                if (dateTimeFromRRO.length() == 0) {
                    //строка даты из РРО пустая
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно получить дату из РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alert.showAndWait();
                } else { //сравним время из РРО с текущими, разрешая расхождение не более 5 минут
                    LocalDateTime localDateTime = LocalDateTime.now();
                    LocalDateTime localDateTimeFromRRO = LocalDateTime.parse(dateTimeFromRRO, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                    if (java.time.Duration.between(localDateTime, localDateTimeFromRRO).abs().toMinutes() >= 5) { //расхождение во времение более 5 минут
                        Alert alertTimeQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                        alertTimeQuestion.setTitle("Подтверждение");
                        alertTimeQuestion.setHeaderText("Время в РРО и программе различаются более чем на 5 минут\nЗадать текущее время в РРО согласно времени в программе?");
                        alertTimeQuestion.setContentText("В РРО: " + localDateTimeFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + "\nВ программе: " + localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                        Optional<ButtonType> resultTime = alertTimeQuestion.showAndWait();
                        if (resultTime.isPresent() && resultTime.get() == ButtonType.OK) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).setDateTimeInRRO()) {
                                //задать дату и время в РРО не удалось
                                log.debug("unable to set datetime to RRO");
                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Ошибка");
                                alert.setHeaderText("Невозможно задать время в РРО\nРекомендуется перезагрузить РРО и задать время вручную\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("При открытой смене нельзя задавать время в РРО\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                            } else {
                                log.debug("successfully have set time to RRO");
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Сообщение");
                                alert.setHeaderText("Время в РРО успешно обновлено");
                                alert.setContentText("Рекомендуется перезагрузить РРО и затем продолжить");
                                alert.showAndWait();
                            }
                        }
                    }
                }

                //далее, проверим, открыта ли смена в РРО
                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftOpened()) {
                    //смена открыта
                    log.debug("shift is opened");
                    Alert alertShiftQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                    alertShiftQuestion.setTitle("Подтверждение");
                    alertShiftQuestion.setHeaderText("В РРО обнаружена открытая смена.\nПродолжаем выход?");
                    Optional<ButtonType> resultShift = alertShiftQuestion.showAndWait();
                    if (resultShift.isPresent() && resultShift.get() == ButtonType.OK) { //смена открыта и мы продолжаем выход

                        //проверим, что продолжительность смены в РРО не более 23 часов
                        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftMoreThan23Hours()) {
                            //смена более 23 часов
                            //проверим, что продолжительность смены в РРО не более 24 часов
                            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftMoreThan24Hours()) {
                                //смена более 24 часов
                                log.debug("shift duration is more than 24 hours");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Предупреждение");
                                alert.setHeaderText("Длительность смены превышает 24 часа\nРабота с РРО будет недоступна\nПредлагается закрыть смену");
                                alert.setContentText("Начало смены: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                                alert.showAndWait();
//                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
//                                return; //отменяем дальнейшее выполнение метода для кнопки "Выход"
                            } else {
                                log.debug("shift duration is more than 23 hours");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Предупреждение");
                                alert.setHeaderText("Длительность смены превышает 23 часа.\nПредлагается закрыть смену.");
                                alert.setContentText("Начало смены: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                                alert.showAndWait();
                            }
                        }

                        //проверим, что разница между датой и временем последней передачи данных и текущей датой и временем составляет не более 24 часов
                        LocalDateTime pointOfNotSentFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getPointOfNotSentFromRRO();
                        LocalDateTime localDateTime = LocalDateTime.now();
                        if (pointOfNotSentFromRRO != null) {
                            if (java.time.Duration.between(localDateTime, pointOfNotSentFromRRO).abs().toHours() >= 24) { //продолжительность неотправки отчетов более 24 часов
                                log.debug("reports was not sent more than 24 hours ago");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Предупреждение");
                                alert.setHeaderText("Разница между датой и временем последней передачи данных и текущей датой и временем составляет более 24 часов.");
                                alert.setContentText("Точка отсчет до блокировки РРО: " + pointOfNotSentFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) +
                                        "\nРРО заблокируется:  " + pointOfNotSentFromRRO.plusHours(72).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                                alert.showAndWait();
                            }
                        }

                        //теперь проверим состояние чека
                        switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                            case 0: {//чек закрыт, НО смена открыта. Сделаем Х-отчеты и сохраним в файл значения сумм в кассе
                                log.debug("receipt is closed");
                                //распечатка Х-отчета
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Информация");
                                alert.setHeaderText("Распечатка общего дневного Х-отчета на РРО");
                                alert.showAndWait();
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).execXReport()) {
                                    //ошибка при попытке распечатки Х-отчета
                                    log.debug("error while getting X-report");
                                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                                    alertError.setTitle("Ошибка");
                                    alertError.setHeaderText("Ошибка при печати общего дневного Х-отчета\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alertError.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alertError.showAndWait();
                                }

                                //сохранение Х-отчетов по товарам в файл

                                String folderName = MainApp.getPathToExchageFolder() + "\\" + MainApp.getSellingPointName() + "\\";
                                String excelFileName = MainApp.getSellingPointName() + " " + localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)).replace(".", "_").replace(":", "_") + ".xlsx";
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Сообщение");
                                alert.setHeaderText("Сохранение Х-отчетов в файл " + folderName + excelFileName + "\nПосле завершения нажмите [ОК]");
                                // alert.showAndWait();


                                Task task = saveXreports(folderName, excelFileName);
                                Thread saveXreportThread = null;
                                saveXreportThread = new Thread(task);
                                progressIndicator.progressProperty().bind(task.progressProperty());
                                lblProgress.setText("Сохранение Х-отчетов в файл");
                                progressIndicator.setVisible(true);
                                lblProgress.setVisible(true);

                                log.debug("thread 'saveXreportThread' started");
                                saveXreportThread.setDaemon(true);
                                saveXreportThread.start();


                                while (saveXreportThread.isAlive()) {
                                    alert.showAndWait();
                                    Thread.yield();
                                }
                                log.debug("thread 'saveXreportThread' finished");


                                //сохранение сумм из РРО в файл
                                BigDecimal cash = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO();
                                BigDecimal cc = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO();
                                if (!saveCashFromRROToFile(cash, cc)) {
                                    //сохранение сумм прошло неуспешно
                                    alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка");
                                    alert.setHeaderText("Ошибка сохранения суммы из РРО в файл\nРекомендуется записать вручную для последующего вноса в РРО");
                                    alert.setContentText("Сумма наличными: "+cash.toString());
                                    alert.showAndWait();
                                }



                                //печать Z-отчета и закрытие программы
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Информация");
                                alert.setHeaderText("После нажатия кнопки [ОК] будет сделан Z-отчет на РРО и выход из программы\nПосле окончания принтер будет перезагружен");
                                alert.showAndWait();
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).execZReport()) {
                                    //ошибка при попытке распечатки Z-отчета
                                    log.debug("error while getting Z-report");
                                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                                    alertError.setTitle("Ошибка");
                                    alertError.setHeaderText("Ошибка при печати общего дневного Z-отчета\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alertError.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alertError.showAndWait();
                                }

                                break;
                            }
                            case 1: {//чек открыт для продажи
                                Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                                alertRecQuestion.setTitle("Подтверждение");
                                alertRecQuestion.setHeaderText("Чек открыт для продажи\nОтменить чек или отменить выход?");
                                alertRecQuestion.setContentText("'ОК' - отменить чек и продолжить\t'Отмена' - отказ от выхода из программы");
                                Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                                if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                        //отмена чека неуспешно
                                        log.debug("unable to cancel receipt - setExitButtonButton interrupt");
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("Ошибка");
                                        alert.setHeaderText("Ошибка при отмене чека в РРО. Прерываем выход\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                        alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                        alert.showAndWait();
                                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                        return; //отменяем дальнейшее выполнение метода для кнопки "Выход"
                                    }
                                } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                    log.debug("receipt opened for sale - user selected setExitButton interrupt");
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                    return; //отменяем дальнейшее выполнение метода для кнопки "Выход"
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
                                alertRecQuestion.setHeaderText("Чек открыт для возврата\nОтменить чек или отменить выход?");
                                alertRecQuestion.setContentText("'ОК' - отменить чек и продолжить\t'Отмена' - отказ от выхода из программы");
                                Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                                if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                        //отмена чека неуспешно
                                        log.debug("unable to cancel receipt - setExitButtonButton interrupt");
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("Ошибка");
                                        alert.setHeaderText("Ошибка при отмене чека в РРО. Прерываем выход\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                        alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                        alert.showAndWait();
                                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                        return; //отменяем дальнейшее выполнение метода для кнопки "Выход"
                                    }
                                } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                    log.debug("receipt opened for return - user selected setExitButton interrupt");
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                    return; //отменяем дальнейшее выполнение метода для кнопки "Выход"
                                }
                                break;
                            }
                            case 99: {//состояние чека неопределено
                                log.debug("receipt status not defined - setExitButton interrupt ");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Ошибка");
                                alert.setHeaderText("Состояние чека не определено\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("Рекомендуется обратиться к администратору\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                                return; //отменяем дальнейшее выполнение метода для кнопки "Выход"
                            }
                        }

                    } else if (resultShift.isPresent() && resultShift.get() == ButtonType.CANCEL) {
                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                        log.debug("shift is open - user selected setExitButton interrupt");
                        return; //отменяем дальнейшее выполнение метода для кнопки "Выход"
                    }

                }

            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();

            Platform.exit();
            System.exit(0);

        }
    }


    public void setSetupRROButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("Подтверждение");
        alertQuestion.setHeaderText("Открыть настроки РРО?");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                log.debug("открываем внешнее приложение настроек РРО");
                Runtime.getRuntime().exec("C:\\UNI-PROGress\\Uniprog.exe");
            } catch (IOException e) {
                log.debug("Ошибка открытия внешнего приложения настроек РРО. " + e.toString());
            }
        }
    }


    public void setXReportButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("Подтверждение");
        alertQuestion.setHeaderText("Сделать X-отчет?");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                //пробуем сделать X-отчет
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).execXReport()) {
                    //ошибка при выполнении X-отчета
                    log.debug("unable to execute X-report");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Ошибка при выполнениие X-отчета\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alert.setContentText("Рекомендуется обратиться к администратору\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alert.showAndWait();
                }
                MainApp.rootLayout.setCenter(LoginFormController.getRootPane());
                //сохранение сумм из РРО в файл
                BigDecimal cash = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO();
                BigDecimal cc = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO();
                if (!saveCashFromRROToFile(cash, cc)) {
                    //сохранение сумм прошло неуспешно
                    Alert alertSave = new Alert(Alert.AlertType.ERROR);
                    alertSave.setTitle("Ошибка");
                    alertSave.setHeaderText("Ошибка сохранения суммы из РРО в файл\nРекомендуется записать вручную для последующего вноса в РРО");
                    alertSave.setContentText("Сумма наличными: "+cash.toString());
                    alertSave.showAndWait();
                }

            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
        }
    }


    public void setZReportButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("Подтверждение");
        alertQuestion.setHeaderText("Сделать Z-отчет c обнулением?\nПотребуется перерегистрация кассира");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                //пробуем сделать Z-отчет
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).execZReport()) {
                    //ошибка при выполнении Z-отчета
                    log.debug("unable to execute Z-report");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Ошибка при выполнениие дневного Z-отчета c обнулением\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alert.setContentText("Рекомендуется обратиться к администратору\nСлужебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alert.showAndWait();
                }
                MainApp.rootLayout.setCenter(LoginFormController.getRootPane());
            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
        }
    }


    public void setCashInOutButton() {
        //покажем форму вноса/изъятия
        try {
            stage = new Stage();
            String fxmlFormPath = "/fxml/IncassoForm/IncassoForm.fxml";
            log.debug("Loading IncassoForm for making cash in or out, into new scene");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
            log.debug("Setting location from FXML - IncassoForm");
            root = fxmlLoader.load();
            log.debug("Отображаем форму вноса/изъятия");
            stage.setScene(new Scene(root, 800, 500));
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Служебный внос / изъятие");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initOwner(btnCashInOut.getScene().getWindow());
            stage.initStyle(StageStyle.UTILITY);
            stage.setOnCloseRequest(windowEvent -> {
                windowEvent.consume();
            });
            // Give the controller access to the main app.
            IncassoFormController incassoFormController = fxmlLoader.getController();
            incassoFormController.setMainApp(mainApp);
            stage.showAndWait();

        } catch (IOException e) {
            log.debug("Ошибка загрузки формы вноса/изъятия " + e.toString());
        }
    }


    public void setCashInOutButton(BigDecimal cashToRRO) {
        //покажем форму вноса/изъятия
        try {
            stage = new Stage();
            String fxmlFormPath = "/fxml/IncassoForm/IncassoForm.fxml";
            log.debug("Loading IncassoForm for making cash in or out, into new scene");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
            log.debug("Setting location from FXML - IncassoForm");
            root = fxmlLoader.load();
            log.debug("Отображаем форму вноса/изъятия");
            stage.setScene(new Scene(root, 800, 500));
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Служебный внос / изъятие");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.initOwner(btnCashInOut.getScene().getWindow());
            stage.initStyle(StageStyle.UTILITY);
            stage.setOnCloseRequest(windowEvent -> {
                windowEvent.consume();
            });
            // Give the controller access to the main app.
            IncassoFormController incassoFormController = fxmlLoader.getController();
            incassoFormController.setMainApp(mainApp);
            incassoFormController.setTxtValue(cashToRRO.toString());
            stage.showAndWait();

        } catch (IOException e) {
            log.debug("Ошибка загрузки формы вноса/изъятия " + e.toString());
        }
    }


    public void setEmptyReceiptButton() {
        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
            //проверим, в каком состоянии чек
            switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                case 0: {//чек закрыт, печатаем нулевой чек
                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).printEmptyReceipt()) {
                        //печать нулевого чека неуспешна
                        log.debug("unable to print empty receipt - setEmptyReceiptButton interrupt ");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Невозможно распечатать нулевой чек\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                        alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                        alert.showAndWait();
                    }
                    break;
                }
                case 1: {//чек открыт для продажи
                    Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                    alertRecQuestion.setTitle("Подтверждение");
                    alertRecQuestion.setHeaderText("Чек открыт для продажи\nОтменить чек или отменить процесс печати нулевого чека?");
                    alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от печати нулевого чека");
                    Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                    if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                            //отмена чека неуспешно
                            log.debug("unable to cancel receipt - setEmptyReceiptButton interrupt");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                        }
                    } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                        log.debug("receipt opened for sale - user selected setEmptyReceiptButton interrupt");
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
                    alertRecQuestion.setHeaderText("Чек открыт для возврата\nОтменить чек или отменить процесс печати нулевого чека?");
                    alertRecQuestion.setContentText("'ОК' - отменить чек\t'Отмена' - отказ от печати нулевого чека");
                    Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                    if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                            //отмена чека неуспешно
                            log.debug("unable to cancel receipt - setEmptyReceiptButton interrupt");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Ошибка при отмене чека в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                        }
                    } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                        log.debug("receipt opened for return - user selected setEmptyReceiptButton interrupt");
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
        }
        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();

    }


    /**
     * сохранение в файл информации о суммах (наличкой и по кредитным картам) в РРО
     * @param cash наличка в РРО
     * @param cc деньги по кредитным картам в РРО
     * @return success or not
     */

    private boolean saveCashFromRROToFile(BigDecimal cash, BigDecimal cc) {
        try {
            String cashFileName = "rrocash.lck";
            log.debug("putting current summ (cash & cc) from RRO into rrocash.lck");
            if (Files.exists(Paths.get(cashFileName))) {
                Files.delete(Paths.get(cashFileName));
            }
            Files.createFile(Paths.get(cashFileName));
            if (Files.isWritable(Paths.get(cashFileName))) {
                String str = cash.toString()+"\n"+cc.toString();
                Files.write(Paths.get(cashFileName), str.getBytes());
            }
            return true;
        } catch (IOException e) {
            log.debug("error putting cash and cc from RRO into rrocash.lck " + e.toString());
            return false;
        }
    }


    /**
     * Формирование задачи сохранения данных по отчетам из принтера в файл
     * @param folderName         имя папки сохранения
     * @param excelFileName      имя файла
     * @return                   возвращает задачу для выполнения в отдельном потоке
     */
    private Task<Void> saveXreports(String folderName, String excelFileName) {
        //задача сохранения Х отчетов
        return new Task<Void>() {
            @Override
            public Void call() {


                X1FullResult x1Res = new X1FullResult();
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getXReport(0, 1, 1)) {
                    //ошибка при попытке получения файла Х1-отчета
                    log.debug("error while getting X1-report from RRO");
                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                    alertError.setTitle("Ошибка");
                    alertError.setHeaderText("Ошибка при получении полного дневного отчета (Х1-отчета) из РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alertError.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alertError.showAndWait();
                } else {
                    updateProgress(5, 100);

                    //==== разбираем файл отчетов x1.bin
                    log.debug("decoding x1.bin");

                    String x1filename = "x1.bin";
                    if ((Files.exists(Paths.get(x1filename))) & (!Files.isDirectory(Paths.get(x1filename))) & (Files.isReadable(Paths.get(x1filename)))) {
                        int filesize = 0;
                        byte[] bFile = new byte[1];
                        try {
                            filesize = (int) Files.size(Paths.get(x1filename));
                            bFile = new byte[filesize];
                            bFile = Files.readAllBytes(Paths.get(x1filename));   //читаем все байты в массив байт
                        } catch (IOException e) {
                            log.debug("unable to get file size for '" + x1filename + "' : " + e.toString());
                        }

                        x1Res = XRepUtil.decodeX1Full(bFile);

                    } else {
                        log.debug("unable to open x1.bin");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Файл полного дневного Х-отчета не найден или не может быть открыт");
                        alert.showAndWait();
                    }
                }
                updateProgress(8, 100);

                X5Result x5Res = new X5Result();
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getXReport(4, 1, 1)) {
                    //ошибка при попытке получения файла Х5-отчета
                    log.debug("error while getting X5-report from RRO");
                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                    alertError.setTitle("Ошибка");
                    alertError.setHeaderText("Ошибка при получении отчета по кассирам (Х5-отчета) из РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alertError.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alertError.showAndWait();
                } else {
                    updateProgress(64, 100);

                    //==== разбираем файл отчетов x5.bin
                    log.debug("decoding x5.bin");

                    String x5filename = "x5.bin";
                    if ((Files.exists(Paths.get(x5filename))) & (!Files.isDirectory(Paths.get(x5filename))) & (Files.isReadable(Paths.get(x5filename)))) {
                        int filesize = 0;
                        byte[] bFile = new byte[1];
                        try {
                            filesize = (int) Files.size(Paths.get(x5filename));
                            bFile = new byte[filesize];
                            bFile = Files.readAllBytes(Paths.get(x5filename));   //читаем все байты в массив байт
                        } catch (IOException e) {
                            log.debug("unable to get file size for '" + x5filename + "' : " + e.toString());
                        }

                        x5Res = XRepUtil.decodeX5(bFile);

                    } else {
                        log.debug("unable to open x1.bin");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Файл полного дневного Х-отчета не найден или не может быть открыт");
                        alert.showAndWait();
                    }
                }

                int maxGoods = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getStatusGoodsOccupiedInRRO();
                if (maxGoods == 0) {
                    maxGoods = 1; //что бы не было ошибки в команде получения Х3 отчета
                }
                updateProgress(69, 100);

                ArrayList<X3Result> x3Res = new ArrayList<X3Result>();
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getXReport(3, 1, maxGoods)) {
                    //ошибка при попытке получения файла Х3-отчета
                    log.debug("error while getting X3-report from RRO");
                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                    alertError.setTitle("Ошибка");
                    alertError.setHeaderText("Ошибка при получении файла отчета по товарам (Х3-отчета) из РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alertError.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alertError.showAndWait();
                } else {
                    updateProgress(81, 100);

                    //==== разбираем файл отчетов x3.bin
                    log.debug("decoding x3.bin");

                    String x3filename = "x3.bin";
                    if ((Files.exists(Paths.get(x3filename))) & (!Files.isDirectory(Paths.get(x3filename))) & (Files.isReadable(Paths.get(x3filename)))) {
                        int filesize = 0;
                        byte[] bFile = new byte[1];
                        try {
                            filesize = (int) Files.size(Paths.get(x3filename));
                            bFile = new byte[filesize];
                            bFile = Files.readAllBytes(Paths.get(x3filename));   //читаем все байты в массив байт
                        } catch (IOException e) {
                            log.debug("unable to get file size for '" + x3filename + "' : " + e.toString());
                        }

                        x3Res = XRepUtil.decodeX3ToArrayList(bFile);

                    } else {
                        log.debug("unable to open x3.bin");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Файл Х-отчета по товарам не найден или не может быть открыт");
                        alert.showAndWait();
                    }
                }
                updateProgress(85, 100);

                if (!XRepUtil.writeToXlsx(x1Res, x5Res, x3Res, folderName, excelFileName, "Х-отчет дневной полный", "Х-отчет по кассирам", "Х-отчет по товарам")) {
                    //ошибка при попытке записи Х-отчетов в файл xlsx
                    log.debug("error while writing X-reports to xlsx file");
                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                    alertError.setTitle("Ошибка");
                    alertError.setHeaderText("Ошибка при создании xlsx-файла с отчетами");
                    alertError.setContentText("Возможные причины: \n- xlsx-файл открыт для редактирования в другой программе \n- xlsx-файл недоступен для записи (диск переполнен)");
                    alertError.showAndWait();
                }
                updateProgress(100, 100);

                return null;
            }

        };
    }

}

