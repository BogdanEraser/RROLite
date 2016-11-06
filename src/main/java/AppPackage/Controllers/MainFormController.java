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
            MainApp.setCashSumInRRO("���������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO().toString());
            MainApp.setCCSumInRRO("��������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO().toString());
        } else {
            MainApp.setCashSumInRRO("���������: �/�");
            MainApp.setCCSumInRRO("��������� ������: �/�");
        }
        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();

        lblMessage.setText("������������: " + CurrentUser.getInstance().getName());

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
        lblTime.setText("������: " + currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
        if (CheckInternetConnnection.getInstance().isConnected()) {
            ConnectedIcon.setVisible(true);
            NotConnectedIcon.setVisible(false);
        } else {
            ConnectedIcon.setVisible(false);
            NotConnectedIcon.setVisible(true);
        }
        Timeline everySecond = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            currentTime = LocalTime.now();
            lblTime.setText("������: " + currentTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)));
            if (currentTime.getSecond() % 10 == 0) { //������ 10 ������ ��������� ������� ��������-����������
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
        //������ � ����� ��������� ���
        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
            //������� ��������, ������� �� ����� � ���
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftOpened()) {
                //����� �������
                isShiftOpened = true;
                Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                alertQuestion.setTitle("�������������");
                alertQuestion.setHeaderText("� ��� ���������� �������� �����.\n����������?");
                Optional<ButtonType> result = alertQuestion.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                    log.debug("shift is open - user selected setStartButton interrupt");
                    return; //�������� ���������� ���������� ������ ��� ������ "�����"
                }

                //�������� ����������������� ����� � ���
            /*  LocalDateTime shiftStartDateTimeFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO();
                LocalDateTime localDateTime = LocalDateTime.now();
                if (java.time.Duration.between(localDateTime, shiftStartDateTimeFromRRO).abs().toHours() >= 24) { //����������������� ����� ����� 24 �����
                    log.debug("shift duration is more than 24 hours");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("��������������");
                    alert.setHeaderText("������������ ����� ��������� 24 ����.\n������������ ������� �����.");
                    alert.setContentText("������ �����: " + shiftStartDateTimeFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                }
                */

                //��������, ��� ����������������� ����� � ��� �� ����� 23 �����
                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftMoreThan23Hours()) {
                    //����� ����� 23 �����
                    //��������, ��� ����������������� ����� � ��� �� ����� 24 �����
                    if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftMoreThan24Hours()) {
                        //����� ����� 24 �����
                        log.debug("shift duration is more than 24 hours");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("��������������");
                        alert.setHeaderText("������������ ����� ��������� 24 ����\n������ � ��� ����� ����������\n������������ ������� �����");
                        alert.setContentText("������ �����: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                        alert.showAndWait();
                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                        return; //�������� ���������� ���������� ������ ��� ������ "�����"
                    } else {
                        log.debug("shift duration is more than 23 hours");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("��������������");
                        alert.setHeaderText("������������ ����� ��������� 23 ����.\n������������ ������� �����.");
                        alert.setContentText("������ �����: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                        alert.showAndWait();
                    }
                }

                //��������, ��� ������� ����� ����� � �������� ��������� �������� ������ � ������� ����� � �������� ���������� �� ����� 24 �����
                LocalDateTime pointOfNotSentFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getPointOfNotSentFromRRO();
                LocalDateTime localDateTime = LocalDateTime.now();
                if (pointOfNotSentFromRRO != null) {
                    if (java.time.Duration.between(localDateTime, pointOfNotSentFromRRO).abs().toHours() >= 24) { //����������������� ���������� ������� ����� 24 �����
                        log.debug("reports was not sent more than 24 hours ago");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("��������������");
                        alert.setHeaderText("������� ����� ����� � �������� ��������� �������� ������ � ������� ����� � �������� ���������� ����� 24 �����.");
                        alert.setContentText("����� ������ �� ���������� ���: " + pointOfNotSentFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) +
                                "\n��� �������������:  " + pointOfNotSentFromRRO.plusHours(72).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                        alert.showAndWait();
                    }
                }
            }


            //����� �������� ����� � ���
            String dateTimeFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getDateTimeInRRO();
            if (dateTimeFromRRO.length() == 0) {
                //������ ���� �� ��� ������
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("������");
                alert.setHeaderText("���������� �������� ���� �� ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                alert.showAndWait();
            } else { //������� ����� �� ��� � ��������, �������� ����������� �� ����� 5 �����
                LocalDateTime localDateTime = LocalDateTime.now();
                LocalDateTime localDateTimeFromRRO = LocalDateTime.parse(dateTimeFromRRO, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                if (java.time.Duration.between(localDateTime, localDateTimeFromRRO).abs().toMinutes() >= 5) { //����������� �� �������� ����� 5 �����
                    Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                    alertQuestion.setTitle("�������������");
                    alertQuestion.setHeaderText("����� � ��� � ��������� ����������� ����� ��� �� 5 �����\n������ ������� ����� � ��� �������� ������� � ���������?");
                    alertQuestion.setContentText("� ���: " + localDateTimeFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + "\n� ���������: " + localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                    Optional<ButtonType> result = alertQuestion.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).setDateTimeInRRO()) {
                            //������ ���� � ����� � ��� �� �������
                            log.debug("unable to set datetime to RRO");
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("���������� ������ ����� � ���\n������������� ������������� ��� � ������ ����� �������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("��� �������� ����� ������ �������� ����� � ���\n��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                            //return; //�������� ���������� ���������� ������ ��� ������ "�����"
                        } else {
                            log.debug("successfully have set time to RRO");
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("���������");
                            alert.setHeaderText("����� � ��� ������� ���������");
                            alert.setContentText("������������� ������������� ��� � ����� ����������");
                            alert.showAndWait();
                            //return; //�������� ���������� ���������� ������ ��� ������ "�����"
                        }
                    } else {
                        if (java.time.Duration.between(localDateTime, localDateTimeFromRRO).abs().toMinutes() > 90) { //����������� �� �������� ����� 90 �����
                            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                            log.debug("datetime difference > 90 minutes - setStartButton interrupt ");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("����� � ��� � ��������� ����������� ����� ��� �� 1,5 ����\n� ���������, ���������� ������ ����������");
                            alert.setContentText("������������� ���������� � ��������������\n��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                            return; //�������� ���������� ���������� ������ ��� ������ "�����"
                        }
                    }
                }
            }

        }
        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();

        // �������� ������ � ������� �� ����� ������
        Workbook workbook = ExcelUtils.getWorkbookFromExcelFile(MainApp.getPathToDataFile());  //�������� ����� ������

        //������� ��� ��������� ��� ������� ������ � ������
        String sheetName = "product_selected";
        Sheet sheet = workbook.getSheet(sheetName);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        if (sheet == null) {
            log.debug("File " + MainApp.getPathToDataFile() + " does not have sheet " + sheetName);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("������");
            alert.setHeaderText("�� ���� ������� ���� " + sheetName + " � ����� " + MainApp.getPathToDataFile());
            alert.showAndWait();
        } else {
            int rowStart = 4; // Decide which rows to process (� 5-�� �� 38-�) - �.�. �������� 34 ������ � ������
            int rowEnd = 37;
            int idx = 0;
            boolean isGroupExists;
            mainApp.allSelectedGoodsArrayList = new ArrayList<>();
            mainApp.allGoodsGroupsArrayList = new ArrayList<>();
            for (int i = 1; i < 21; i++) { //���� �� ������� �������

                Row r = sheet.getRow(rowStart - 1); //�������� ������� ������
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
                } //��� ��������� ������ ������� �� ����� ������� ���������

                for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                    r = sheet.getRow(rowNum);
                    if (r.getCell(0, Row.RETURN_BLANK_AS_NULL) == null) {
                        // This whole row is empty
                        //continue;
                        break; //��� ��������� ������ (��� ������ ������) ������� �� �������
                    }
                    mainApp.allSelectedGoodsArrayList.add(new Goods()); //�� ������ ������ - ��������� ����� ������� � ������ ��� �������
                    //�� 9 ��������
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
                                            mainApp.allSelectedGoodsArrayList.get(idx).setName("�/�");
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
                                                case "�":
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(1);
                                                    break;
                                                case "�":
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(2);
                                                    break;
                                                case "�":
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(3);
                                                    break;
                                                case "�":
                                                    mainApp.allSelectedGoodsArrayList.get(idx).setTaxGroup(4);
                                                    break;
                                                case "�":
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

        //������� ��� ������ � ������
        sheetName = "product_all";
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            log.debug("File " + MainApp.getPathToDataFile() + " does not have sheet " + sheetName);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("������");
            alert.setHeaderText("�� ���� ������� ���� " + sheetName + " � ����� " + MainApp.getPathToDataFile());
            alert.showAndWait();
        } else {
            int rowStart = 3; // Decide which rows to process (� 4-�� �� 16004-�)
            int rowEnd = 16004;
            int idx = 0;
            mainApp.allGoodsArrayList = new ArrayList<>();

            for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                Row r = sheet.getRow(rowNum);

                if (r.getCell(0, Row.RETURN_BLANK_AS_NULL) == null) {
                    // This whole row is empty
                    //continue;
                    break; //��� ��������� ������ (��� ������ ������) ������� �� �������
                }
                mainApp.allGoodsArrayList.add(new Goods()); //�� ������ ������ - ��������� ����� ������� � ������ ��� �������
                //�� 9 ��������
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
                                        mainApp.allGoodsArrayList.get(idx).setName("�/�");
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
                                            case "�":
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(1);
                                                break;
                                            case "�":
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(2);
                                                break;
                                            case "�":
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(3);
                                                break;
                                            case "�":
                                                mainApp.allGoodsArrayList.get(idx).setTaxGroup(4);
                                                break;
                                            case "�":
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
            //��������� ������ � ��� ������ ���� ����� �������
            Thread addpluThread = null;
            if (mainApp.allGoodsArrayList.size() > 0) {
                //��������� � ��� ����������� ������
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
                                    //���������� ����� � ��� ���������
                                    log.debug("unable to add goods to RRO");
                                    Alert alert = new Alert(Alert.AlertType.WARNING);
                                    alert.setTitle("������");
                                    alert.setHeaderText("���������� ��������(��������) ����� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    //TODO �������� ����������� �������� ������ ��� ������������� �������� �����
                                    alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult()
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
                    alert.setTitle("���������");
                    alert.setHeaderText("���������� ������� � ���. ���������...");
                    alert.showAndWait();
                }
                log.debug("thread 'add_plu' finished");
            }

            //� ��� ��, �������� ���� ���� � ������� ����������, �� ��������� ������ ����������� ����� � ���
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
                    alert.setTitle("���������");
                    alert.setHeaderText("���������� ���������� ��������� ���� � ������� " + cashToBeInputed.toString());
                    alert.setContentText("����� ������� [��] ����� ������� ����� ��� ���������� ����� � ����������� ������");
                    alert.showAndWait();
                    //���� ����� ��� �������� ������ 0, �� ������� ����� ��� ��������
                    setCashInOutButton(cashToBeInputed);

                }
            } catch (IOException e) {
                log.debug("error getting cash and cc from rrocash.lck " + e.toString());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("������");
                alert.setHeaderText("�� ������� ������� ����� ��� ���������� �����");
                alert.setContentText("�������� ��������� ���� �������� ������� ��� ����� �������� �������");
                alert.showAndWait();
            }

        }

        //��������� � ���� ��������� ������ � ������ (������������)
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
        saveSelectedGoodsAndGroupsToFileThread.start();    //������ ������
        */

        try {
            String fxmlFormPath = "/fxml/OrderForm/OrderForm.fxml";
            log.debug("Loading OrderForm for making orders into RootLayout");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
            log.debug("Setting location from FXML - OrderForm");
            BorderPane orderPane = fxmlLoader.load();
            log.debug("���������� ����� �������");
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
            log.debug("������ �������� ����� ������� " + e.toString());
        }
    }


    /**
     * ��������� ������ ������
     */
    public void setExitButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        //alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("�������������");
        alertQuestion.setHeaderText("�� ������������� ������ �����?");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            log.debug("trying to exit program normally");

            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {

                //�������� ����� � ���
                String dateTimeFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getDateTimeInRRO();
                if (dateTimeFromRRO.length() == 0) {
                    //������ ���� �� ��� ������
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("������");
                    alert.setHeaderText("���������� �������� ���� �� ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alert.showAndWait();
                } else { //������� ����� �� ��� � ��������, �������� ����������� �� ����� 5 �����
                    LocalDateTime localDateTime = LocalDateTime.now();
                    LocalDateTime localDateTimeFromRRO = LocalDateTime.parse(dateTimeFromRRO, DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
                    if (java.time.Duration.between(localDateTime, localDateTimeFromRRO).abs().toMinutes() >= 5) { //����������� �� �������� ����� 5 �����
                        Alert alertTimeQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                        alertTimeQuestion.setTitle("�������������");
                        alertTimeQuestion.setHeaderText("����� � ��� � ��������� ����������� ����� ��� �� 5 �����\n������ ������� ����� � ��� �������� ������� � ���������?");
                        alertTimeQuestion.setContentText("� ���: " + localDateTimeFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + "\n� ���������: " + localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                        Optional<ButtonType> resultTime = alertTimeQuestion.showAndWait();
                        if (resultTime.isPresent() && resultTime.get() == ButtonType.OK) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).setDateTimeInRRO()) {
                                //������ ���� � ����� � ��� �� �������
                                log.debug("unable to set datetime to RRO");
                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("���������� ������ ����� � ���\n������������� ������������� ��� � ������ ����� �������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("��� �������� ����� ������ �������� ����� � ���\n��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                            } else {
                                log.debug("successfully have set time to RRO");
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("���������");
                                alert.setHeaderText("����� � ��� ������� ���������");
                                alert.setContentText("������������� ������������� ��� � ����� ����������");
                                alert.showAndWait();
                            }
                        }
                    }
                }

                //�����, ��������, ������� �� ����� � ���
                if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftOpened()) {
                    //����� �������
                    log.debug("shift is opened");
                    Alert alertShiftQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                    alertShiftQuestion.setTitle("�������������");
                    alertShiftQuestion.setHeaderText("� ��� ���������� �������� �����.\n���������� �����?");
                    Optional<ButtonType> resultShift = alertShiftQuestion.showAndWait();
                    if (resultShift.isPresent() && resultShift.get() == ButtonType.OK) { //����� ������� � �� ���������� �����

                        //��������, ��� ����������������� ����� � ��� �� ����� 23 �����
                        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftMoreThan23Hours()) {
                            //����� ����� 23 �����
                            //��������, ��� ����������������� ����� � ��� �� ����� 24 �����
                            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).isShiftMoreThan24Hours()) {
                                //����� ����� 24 �����
                                log.debug("shift duration is more than 24 hours");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("��������������");
                                alert.setHeaderText("������������ ����� ��������� 24 ����\n������ � ��� ����� ����������\n������������ ������� �����");
                                alert.setContentText("������ �����: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                                alert.showAndWait();
//                                CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
//                                return; //�������� ���������� ���������� ������ ��� ������ "�����"
                            } else {
                                log.debug("shift duration is more than 23 hours");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("��������������");
                                alert.setHeaderText("������������ ����� ��������� 23 ����.\n������������ ������� �����.");
                                alert.setContentText("������ �����: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getShiftStartDateTimeFromRRO().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                                alert.showAndWait();
                            }
                        }

                        //��������, ��� ������� ����� ����� � �������� ��������� �������� ������ � ������� ����� � �������� ���������� �� ����� 24 �����
                        LocalDateTime pointOfNotSentFromRRO = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getPointOfNotSentFromRRO();
                        LocalDateTime localDateTime = LocalDateTime.now();
                        if (pointOfNotSentFromRRO != null) {
                            if (java.time.Duration.between(localDateTime, pointOfNotSentFromRRO).abs().toHours() >= 24) { //����������������� ���������� ������� ����� 24 �����
                                log.debug("reports was not sent more than 24 hours ago");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("��������������");
                                alert.setHeaderText("������� ����� ����� � �������� ��������� �������� ������ � ������� ����� � �������� ���������� ����� 24 �����.");
                                alert.setContentText("����� ������ �� ���������� ���: " + pointOfNotSentFromRRO.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) +
                                        "\n��� �������������:  " + pointOfNotSentFromRRO.plusHours(72).format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)));
                                alert.showAndWait();
                            }
                        }

                        //������ �������� ��������� ����
                        switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                            case 0: {//��� ������, �� ����� �������. ������� �-������ � �������� � ���� �������� ���� � �����
                                log.debug("receipt is closed");
                                //���������� �-������
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("����������");
                                alert.setHeaderText("���������� ������ �������� �-������ �� ���");
                                alert.showAndWait();
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).execXReport()) {
                                    //������ ��� ������� ���������� �-������
                                    log.debug("error while getting X-report");
                                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                                    alertError.setTitle("������");
                                    alertError.setHeaderText("������ ��� ������ ������ �������� �-������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alertError.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alertError.showAndWait();
                                }

                                //���������� �-������� �� ������� � ����

                                String folderName = MainApp.getPathToExchageFolder() + "\\" + MainApp.getSellingPointName() + "\\";
                                String excelFileName = MainApp.getSellingPointName() + " " + localDateTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)).replace(".", "_").replace(":", "_") + ".xlsx";
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("���������");
                                alert.setHeaderText("���������� �-������� � ���� " + folderName + excelFileName + "\n����� ���������� ������� [��]");
                                // alert.showAndWait();


                                Task task = saveXreports(folderName, excelFileName);
                                Thread saveXreportThread = null;
                                saveXreportThread = new Thread(task);
                                progressIndicator.progressProperty().bind(task.progressProperty());
                                lblProgress.setText("���������� �-������� � ����");
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


                                //���������� ���� �� ��� � ����
                                BigDecimal cash = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO();
                                BigDecimal cc = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO();
                                if (!saveCashFromRROToFile(cash, cc)) {
                                    //���������� ���� ������ ���������
                                    alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("������");
                                    alert.setHeaderText("������ ���������� ����� �� ��� � ����\n������������� �������� ������� ��� ������������ ����� � ���");
                                    alert.setContentText("����� ���������: "+cash.toString());
                                    alert.showAndWait();
                                }



                                //������ Z-������ � �������� ���������
                                alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("����������");
                                alert.setHeaderText("����� ������� ������ [��] ����� ������ Z-����� �� ��� � ����� �� ���������\n����� ��������� ������� ����� ������������");
                                alert.showAndWait();
                                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).execZReport()) {
                                    //������ ��� ������� ���������� Z-������
                                    log.debug("error while getting Z-report");
                                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                                    alertError.setTitle("������");
                                    alertError.setHeaderText("������ ��� ������ ������ �������� Z-������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                    alertError.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                    alertError.showAndWait();
                                }

                                break;
                            }
                            case 1: {//��� ������ ��� �������
                                Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                                alertRecQuestion.setTitle("�������������");
                                alertRecQuestion.setHeaderText("��� ������ ��� �������\n�������� ��� ��� �������� �����?");
                                alertRecQuestion.setContentText("'��' - �������� ��� � ����������\t'������' - ����� �� ������ �� ���������");
                                Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                                if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                        //������ ���� ���������
                                        log.debug("unable to cancel receipt - setExitButtonButton interrupt");
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("������");
                                        alert.setHeaderText("������ ��� ������ ���� � ���. ��������� �����\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                        alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                        alert.showAndWait();
                                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                        return; //�������� ���������� ���������� ������ ��� ������ "�����"
                                    }
                                } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                    log.debug("receipt opened for sale - user selected setExitButton interrupt");
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                    return; //�������� ���������� ���������� ������ ��� ������ "�����"
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
                                alertRecQuestion.setHeaderText("��� ������ ��� ��������\n�������� ��� ��� �������� �����?");
                                alertRecQuestion.setContentText("'��' - �������� ��� � ����������\t'������' - ����� �� ������ �� ���������");
                                Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                                if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                                        //������ ���� ���������
                                        log.debug("unable to cancel receipt - setExitButtonButton interrupt");
                                        Alert alert = new Alert(Alert.AlertType.WARNING);
                                        alert.setTitle("������");
                                        alert.setHeaderText("������ ��� ������ ���� � ���. ��������� �����\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                        alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                        alert.showAndWait();
                                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                        return; //�������� ���������� ���������� ������ ��� ������ "�����"
                                    }
                                } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                                    log.debug("receipt opened for return - user selected setExitButton interrupt");
                                    CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                                    return; //�������� ���������� ���������� ������ ��� ������ "�����"
                                }
                                break;
                            }
                            case 99: {//��������� ���� ������������
                                log.debug("receipt status not defined - setExitButton interrupt ");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("������");
                                alert.setHeaderText("��������� ���� �� ����������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                                alert.setContentText("������������� ���������� � ��������������\n��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                                alert.showAndWait();
                                return; //�������� ���������� ���������� ������ ��� ������ "�����"
                            }
                        }

                    } else if (resultShift.isPresent() && resultShift.get() == ButtonType.CANCEL) {
                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
                        log.debug("shift is open - user selected setExitButton interrupt");
                        return; //�������� ���������� ���������� ������ ��� ������ "�����"
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
        alertQuestion.setTitle("�������������");
        alertQuestion.setHeaderText("������� �������� ���?");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                log.debug("��������� ������� ���������� �������� ���");
                Runtime.getRuntime().exec("C:\\UNI-PROGress\\Uniprog.exe");
            } catch (IOException e) {
                log.debug("������ �������� �������� ���������� �������� ���. " + e.toString());
            }
        }
    }


    public void setXReportButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("�������������");
        alertQuestion.setHeaderText("������� X-�����?");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                //������� ������� X-�����
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).execXReport()) {
                    //������ ��� ���������� X-������
                    log.debug("unable to execute X-report");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("������");
                    alert.setHeaderText("������ ��� ����������� X-������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alert.setContentText("������������� ���������� � ��������������\n��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alert.showAndWait();
                }
                MainApp.rootLayout.setCenter(LoginFormController.getRootPane());
                //���������� ���� �� ��� � ����
                BigDecimal cash = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCashInRRO();
                BigDecimal cc = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getCreditInRRO();
                if (!saveCashFromRROToFile(cash, cc)) {
                    //���������� ���� ������ ���������
                    Alert alertSave = new Alert(Alert.AlertType.ERROR);
                    alertSave.setTitle("������");
                    alertSave.setHeaderText("������ ���������� ����� �� ��� � ����\n������������� �������� ������� ��� ������������ ����� � ���");
                    alertSave.setContentText("����� ���������: "+cash.toString());
                    alertSave.showAndWait();
                }

            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
        }
    }


    public void setZReportButton() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("�������������");
        alertQuestion.setHeaderText("������� Z-����� c ����������?\n����������� ��������������� �������");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
                //������� ������� Z-�����
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).execZReport()) {
                    //������ ��� ���������� Z-������
                    log.debug("unable to execute Z-report");
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("������");
                    alert.setHeaderText("������ ��� ����������� �������� Z-������ c ����������\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alert.setContentText("������������� ���������� � ��������������\n��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alert.showAndWait();
                }
                MainApp.rootLayout.setCenter(LoginFormController.getRootPane());
            }
            CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).closePortMiniFP();
        }
    }


    public void setCashInOutButton() {
        //������� ����� �����/�������
        try {
            stage = new Stage();
            String fxmlFormPath = "/fxml/IncassoForm/IncassoForm.fxml";
            log.debug("Loading IncassoForm for making cash in or out, into new scene");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
            log.debug("Setting location from FXML - IncassoForm");
            root = fxmlLoader.load();
            log.debug("���������� ����� �����/�������");
            stage.setScene(new Scene(root, 800, 500));
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("��������� ���� / �������");
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
            log.debug("������ �������� ����� �����/������� " + e.toString());
        }
    }


    public void setCashInOutButton(BigDecimal cashToRRO) {
        //������� ����� �����/�������
        try {
            stage = new Stage();
            String fxmlFormPath = "/fxml/IncassoForm/IncassoForm.fxml";
            log.debug("Loading IncassoForm for making cash in or out, into new scene");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
            log.debug("Setting location from FXML - IncassoForm");
            root = fxmlLoader.load();
            log.debug("���������� ����� �����/�������");
            stage.setScene(new Scene(root, 800, 500));
            //stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("��������� ���� / �������");
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
            log.debug("������ �������� ����� �����/������� " + e.toString());
        }
    }


    public void setEmptyReceiptButton() {
        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).openPortMiniFP()) {
            //��������, � ����� ��������� ���
            switch (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getReceiptStatusFromRRO()) {
                case 0: {//��� ������, �������� ������� ���
                    if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).printEmptyReceipt()) {
                        //������ �������� ���� ���������
                        log.debug("unable to print empty receipt - setEmptyReceiptButton interrupt ");
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("������");
                        alert.setHeaderText("���������� ����������� ������� ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                        alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                        alert.showAndWait();
                    }
                    break;
                }
                case 1: {//��� ������ ��� �������
                    Alert alertRecQuestion = new Alert(Alert.AlertType.CONFIRMATION);
                    alertRecQuestion.setTitle("�������������");
                    alertRecQuestion.setHeaderText("��� ������ ��� �������\n�������� ��� ��� �������� ������� ������ �������� ����?");
                    alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ������ �������� ����");
                    Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                    if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                            //������ ���� ���������
                            log.debug("unable to cancel receipt - setEmptyReceiptButton interrupt");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                        }
                    } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                        log.debug("receipt opened for sale - user selected setEmptyReceiptButton interrupt");
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
                    alertRecQuestion.setHeaderText("��� ������ ��� ��������\n�������� ��� ��� �������� ������� ������ �������� ����?");
                    alertRecQuestion.setContentText("'��' - �������� ���\t'������' - ����� �� ������ �������� ����");
                    Optional<ButtonType> resultRec = alertRecQuestion.showAndWait();
                    if (resultRec.isPresent() && resultRec.get() == ButtonType.OK) {
                        if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).cancelReceipt()) {
                            //������ ���� ���������
                            log.debug("unable to cancel receipt - setEmptyReceiptButton interrupt");
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("������");
                            alert.setHeaderText("������ ��� ������ ���� � ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                            alert.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                            alert.showAndWait();
                        }
                    } else if (resultRec.isPresent() && resultRec.get() == ButtonType.CANCEL) {
                        log.debug("receipt opened for return - user selected setEmptyReceiptButton interrupt");
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

    }


    /**
     * ���������� � ���� ���������� � ������ (�������� � �� ��������� ������) � ���
     * @param cash ������� � ���
     * @param cc ������ �� ��������� ������ � ���
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
     * ������������ ������ ���������� ������ �� ������� �� �������� � ����
     * @param folderName         ��� ����� ����������
     * @param excelFileName      ��� �����
     * @return                   ���������� ������ ��� ���������� � ��������� ������
     */
    private Task<Void> saveXreports(String folderName, String excelFileName) {
        //������ ���������� � �������
        return new Task<Void>() {
            @Override
            public Void call() {


                X1FullResult x1Res = new X1FullResult();
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getXReport(0, 1, 1)) {
                    //������ ��� ������� ��������� ����� �1-������
                    log.debug("error while getting X1-report from RRO");
                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                    alertError.setTitle("������");
                    alertError.setHeaderText("������ ��� ��������� ������� �������� ������ (�1-������) �� ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alertError.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alertError.showAndWait();
                } else {
                    updateProgress(5, 100);

                    //==== ��������� ���� ������� x1.bin
                    log.debug("decoding x1.bin");

                    String x1filename = "x1.bin";
                    if ((Files.exists(Paths.get(x1filename))) & (!Files.isDirectory(Paths.get(x1filename))) & (Files.isReadable(Paths.get(x1filename)))) {
                        int filesize = 0;
                        byte[] bFile = new byte[1];
                        try {
                            filesize = (int) Files.size(Paths.get(x1filename));
                            bFile = new byte[filesize];
                            bFile = Files.readAllBytes(Paths.get(x1filename));   //������ ��� ����� � ������ ����
                        } catch (IOException e) {
                            log.debug("unable to get file size for '" + x1filename + "' : " + e.toString());
                        }

                        x1Res = XRepUtil.decodeX1Full(bFile);

                    } else {
                        log.debug("unable to open x1.bin");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("������");
                        alert.setHeaderText("���� ������� �������� �-������ �� ������ ��� �� ����� ���� ������");
                        alert.showAndWait();
                    }
                }
                updateProgress(8, 100);

                X5Result x5Res = new X5Result();
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getXReport(4, 1, 1)) {
                    //������ ��� ������� ��������� ����� �5-������
                    log.debug("error while getting X5-report from RRO");
                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                    alertError.setTitle("������");
                    alertError.setHeaderText("������ ��� ��������� ������ �� �������� (�5-������) �� ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alertError.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alertError.showAndWait();
                } else {
                    updateProgress(64, 100);

                    //==== ��������� ���� ������� x5.bin
                    log.debug("decoding x5.bin");

                    String x5filename = "x5.bin";
                    if ((Files.exists(Paths.get(x5filename))) & (!Files.isDirectory(Paths.get(x5filename))) & (Files.isReadable(Paths.get(x5filename)))) {
                        int filesize = 0;
                        byte[] bFile = new byte[1];
                        try {
                            filesize = (int) Files.size(Paths.get(x5filename));
                            bFile = new byte[filesize];
                            bFile = Files.readAllBytes(Paths.get(x5filename));   //������ ��� ����� � ������ ����
                        } catch (IOException e) {
                            log.debug("unable to get file size for '" + x5filename + "' : " + e.toString());
                        }

                        x5Res = XRepUtil.decodeX5(bFile);

                    } else {
                        log.debug("unable to open x1.bin");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("������");
                        alert.setHeaderText("���� ������� �������� �-������ �� ������ ��� �� ����� ���� ������");
                        alert.showAndWait();
                    }
                }

                int maxGoods = CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getStatusGoodsOccupiedInRRO();
                if (maxGoods == 0) {
                    maxGoods = 1; //��� �� �� ���� ������ � ������� ��������� �3 ������
                }
                updateProgress(69, 100);

                ArrayList<X3Result> x3Res = new ArrayList<X3Result>();
                if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getXReport(3, 1, maxGoods)) {
                    //������ ��� ������� ��������� ����� �3-������
                    log.debug("error while getting X3-report from RRO");
                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                    alertError.setTitle("������");
                    alertError.setHeaderText("������ ��� ��������� ����� ������ �� ������� (�3-������) �� ���\n�������� ������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastError()));
                    alertError.setContentText("��������� ����������: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinterPortSpeed())).getLastResult());
                    alertError.showAndWait();
                } else {
                    updateProgress(81, 100);

                    //==== ��������� ���� ������� x3.bin
                    log.debug("decoding x3.bin");

                    String x3filename = "x3.bin";
                    if ((Files.exists(Paths.get(x3filename))) & (!Files.isDirectory(Paths.get(x3filename))) & (Files.isReadable(Paths.get(x3filename)))) {
                        int filesize = 0;
                        byte[] bFile = new byte[1];
                        try {
                            filesize = (int) Files.size(Paths.get(x3filename));
                            bFile = new byte[filesize];
                            bFile = Files.readAllBytes(Paths.get(x3filename));   //������ ��� ����� � ������ ����
                        } catch (IOException e) {
                            log.debug("unable to get file size for '" + x3filename + "' : " + e.toString());
                        }

                        x3Res = XRepUtil.decodeX3ToArrayList(bFile);

                    } else {
                        log.debug("unable to open x3.bin");
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("������");
                        alert.setHeaderText("���� �-������ �� ������� �� ������ ��� �� ����� ���� ������");
                        alert.showAndWait();
                    }
                }
                updateProgress(85, 100);

                if (!XRepUtil.writeToXlsx(x1Res, x5Res, x3Res, folderName, excelFileName, "�-����� ������� ������", "�-����� �� ��������", "�-����� �� �������")) {
                    //������ ��� ������� ������ �-������� � ���� xlsx
                    log.debug("error while writing X-reports to xlsx file");
                    Alert alertError = new Alert(Alert.AlertType.WARNING);
                    alertError.setTitle("������");
                    alertError.setHeaderText("������ ��� �������� xlsx-����� � ��������");
                    alertError.setContentText("��������� �������: \n- xlsx-���� ������ ��� �������������� � ������ ��������� \n- xlsx-���� ���������� ��� ������ (���� ����������)");
                    alertError.showAndWait();
                }
                updateProgress(100, 100);

                return null;
            }

        };
    }

}

