package AppPackage.Controllers;

import AppPackage.Entities.CurrentUser;
import AppPackage.Entities.Goods;
import AppPackage.Entities.GoodsGroup;
import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import AppPackage.Utils.CheckInternetConnnection;
import AppPackage.Utils.ExcelUtils;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(MainFormController.class);
    private LocalTime currentTime;
    private Scene scene;
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
        lblRROSumCash.setText("Сумма оплат наличными: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getCashInRRO());
        lblRROSumCredit.setText("Сумма оплат кредитной картой: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getCreditInRRO());
        lblMessage.setText("Пользователь: " + CurrentUser.getInstance().getName());

        if (CurrentUser.getInstance().getAccessLevel()<2) {btnSetupRRO.setVisible(true);}
        else {btnSetupRRO.setVisible(false);}

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
            mainApp.allGoodsArrayList = new ArrayList<>();
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
                    if (r == null) {
                        // This whole row is empty
                        //continue;
                        break; //при пропусках строки в группе выходим из импорта данной группы
                    }
                    mainApp.allGoodsArrayList.add(new Goods()); //не пустая строка - добавляем новый элемент в список для импорта
                    //до 9 столбцов
                    for (int cn = 0; cn < 9; cn++) {
                        cell = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
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
                rowStart = rowStart + 35;
                rowEnd = rowEnd + 35;
            }
        }


        try {
            String fxmlFormPath = "/fxml/OrderForm/OrderForm.fxml";
            log.debug("Loading OrderForm for making orders into RootLayout");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
            log.debug("Setting location from FXML - OrderForm");
            BorderPane orderPane = fxmlLoader.load();
            log.debug("Отображаем форму заказов");
            // Set OrderForm into the center of root layout.
            mainApp.rootLayout.setCenter(orderPane);
            // Give the controller access to the main app.
            OrderFormController orderFormController = fxmlLoader.getController();
            OrderFormController.setRootPane(orderPane);
            orderFormController.setMainApp(mainApp);
            orderFormController.setScene(mainApp.getMainStage().getScene());
        } catch (IOException e) {
            log.debug("Ошибка загрузки формы заказов " + e.toString());
        }
    }

    public void setExitButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("Уточнение");
        alertQuestion.setHeaderText("Вы действительно хотите выйти?");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            log.debug("нормальный выход");
            Platform.exit();
            System.exit(0);
        }
    }

    public void setSetupRROButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("Уточнение");
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
}
