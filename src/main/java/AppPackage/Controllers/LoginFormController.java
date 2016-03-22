package AppPackage.Controllers;

import AppPackage.Entities.CurrentUser;
import AppPackage.Entities.User;
import AppPackage.MainApp;
import AppPackage.RRO.CurrentRRO;
import AppPackage.Utils.ExcelUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class LoginFormController //implements Initializable
{
    private static final Logger log = Logger.getLogger(LoginFormController.class);
    public int i = 10;
    public ArrayList<String> btnArrayList = new ArrayList();
    private Scene scene;
    private MainApp mainApp;
    private ArrayList<User> userArrayList;
    @FXML
    private AnchorPane loginForm;
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
    private Button btnOKLogin;
    @FXML
    private Button btnCancelLogin;
    @FXML
    private Button btnBackSpace;
    @FXML
    private TextField txtPassword;
    private ResourceBundle bundle;

    public LoginFormController() {
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

    @FXML
    public void initialize() {
        //textField.getProperties().put("vkType", "numeric");
        log.debug("Initialising loginForm");
        //получение данных о логинах и паролях из файла экселя
        Workbook workbook = ExcelUtils.getWorkbookFromExcelFile(MainApp.getPathToDataFile());  //получаем книгу экселя
        String sheetName = "access";
        Sheet sheet = workbook.getSheet(sheetName);
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        if (sheet == null) {
            log.debug("File " + MainApp.getPathToDataFile() + " does not have sheet " + sheetName);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Не могу открыть лист " + sheetName + " в файле " + MainApp.getPathToDataFile());
            alert.showAndWait();
        } else {
            // Decide which rows to process (с 4-го по 103) - т.е. максимум 100 аккаунтов
            int rowStart = 3;
            int rowEnd = 103;
            int idx = 0;
            userArrayList = new ArrayList<>();
            for (int rowNum = rowStart; rowNum < rowEnd; rowNum++) {
                Row r = sheet.getRow(rowNum);
                if (r == null) {
                    // This whole row is empty
                    //continue;
                    break; //при пропусках строки выходим из импорта
                }
                userArrayList.add(new User("", 0, 999)); //не пустая строка - добавляем новый элемент в список для импорта
                //до 3х столбцов
                for (int cn = 0; cn < 3; cn++) {
                    Cell cell = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
                    if (cell != null) {
                        switch (cn) {
                            case 0: //user name
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        userArrayList.get(idx).setName(String.valueOf(cell.getNumericCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        userArrayList.get(idx).setName(cell.getStringCellValue());
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        userArrayList.get(idx).setName("Н/Д");
                                        break;
                                }
                                break;
                            case 1: //user pswd
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        userArrayList.get(idx).setPswd((int) cell.getNumericCellValue());
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        userArrayList.get(idx).setPswd(Integer.parseInt(cell.getStringCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        userArrayList.get(idx).setPswd(0);
                                        break;
                                }
                                break;
                            case 2: //user acccess level
                                switch (evaluator.evaluateInCell(cell).getCellType()) {
                                    case Cell.CELL_TYPE_NUMERIC:
                                        userArrayList.get(idx).setAccessLevel((int) cell.getNumericCellValue());
                                        break;
                                    case Cell.CELL_TYPE_STRING:
                                        userArrayList.get(idx).setAccessLevel(Integer.parseInt(cell.getStringCellValue()));
                                        break;
                                    case Cell.CELL_TYPE_BLANK:
                                        userArrayList.get(idx).setAccessLevel(10);
                                        break;
                                }
                                break;
                        }
                    }
                }
                idx++;
            }
        }

        //      bundle = resources;
        //      messagelabel.setText(bundle.getString("Label.text"));
    }


    /**
     * нажатие кнопки "1"
     */
    public void setBtn1() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("1");
            } else txtPassword.setText(txtPassword.getText() + "1");
        }
    }

    /**
     * нажатие кнопки "2"
     */
    public void setBtn2() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("2");
            } else txtPassword.setText(txtPassword.getText() + "2");
        }
    }

    /**
     * нажатие кнопки "3"
     */
    public void setBtn3() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("3");
            } else txtPassword.setText(txtPassword.getText() + "3");
        }
    }

    /**
     * нажатие кнопки "4"
     */
    public void setBtn4() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("4");
            } else txtPassword.setText(txtPassword.getText() + "4");
        }
    }

    /**
     * нажатие кнопки "5"
     */
    public void setBtn5() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("5");
            } else txtPassword.setText(txtPassword.getText() + "5");
        }
    }

    /**
     * нажатие кнопки "6"
     */
    public void setBtn6() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("6");
            } else txtPassword.setText(txtPassword.getText() + "6");
        }
    }

    /**
     * нажатие кнопки "7"
     */
    public void setBtn7() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("7");
            } else txtPassword.setText(txtPassword.getText() + "7");
        }
    }

    /**
     * нажатие кнопки "8"
     */
    public void setBtn8() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("8");
            } else txtPassword.setText(txtPassword.getText() + "8");
        }
    }

    /**
     * нажатие кнопки "9"
     */
    public void setBtn9() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("9");
            } else txtPassword.setText(txtPassword.getText() + "9");
        }
    }

    /**
     * нажатие кнопки "0"
     */
    public void setBtn0() {
        if (txtPassword.getText().length() < 8) {
            if (txtPassword.getText().length() == 0) {
                txtPassword.setText("0");
            } else txtPassword.setText(txtPassword.getText() + "0");
        }
    }

    /**
     * нажатие кнопки "удалить"
     */
    public void setBtnBackSpace() {
        if (txtPassword.getText().length() <= 1) {
            txtPassword.setText("");
        } else txtPassword.setText(txtPassword.getText(0, txtPassword.getText().length() - 1));
    }

    /**
     * нажатие кнопки "Выход"
     */
    public void setBtnCancelLogin() {
        Alert alertQuestion = new Alert(Alert.AlertType.CONFIRMATION);
        alertQuestion.initOwner(mainApp.getMainStage());
        alertQuestion.setTitle("Уточнение");
        alertQuestion.setHeaderText("Вы действительно хотите выйти?");
        Optional<ButtonType> result = alertQuestion.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    /**
     * нажатие кнопки "ОК"
     */
    public void setBtnOKLogin() {
        if (txtPassword.getText().length() != 0) {
            CurrentUser.setInstance();//сбрасываем текущего юзера
            for (User user : userArrayList) {
                if (Integer.parseInt(txtPassword.getText()) == user.getPswd()) {
                    try {
                        CurrentUser.getInstance(user.getName(), user.getPswd(), user.getAccessLevel());
                        if (CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).openPortMiniFP()) {
                            if (!CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).cashierRegister(user.getName())) {
                                //регистрация кассира не удалась
                                log.debug("unable register cashier in RRO - setBtnLogin interrupt");
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Ошибка");
                                alert.setHeaderText("Не удалось зарегистрировать кассира в РРО\nОписание ошибки: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).errorCodesHashMap.get(CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastError()));
                                alert.setContentText("Служебная информация: " + CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).getLastResult());
                                alert.showAndWait();
                                return; //отменяем дальнейшее выполнение метода для кнопки "ОК"
                            }
                        }
                        CurrentRRO.getInstance(MainApp.getPrinterType(), String.valueOf(MainApp.getPrinterPort()), String.valueOf(MainApp.getPrinerPortSpeed())).closePortMiniFP();
                        String fxmlFormPath = "/fxml/MainForm/MainForm.fxml";
                        log.debug("Loading MainForm for main view into RootLayout");
                        FXMLLoader fxmlLoader = new FXMLLoader();
                        fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
                        log.debug("Setting location from FXML - MainForm");
                        BorderPane mainPane = fxmlLoader.load();
                        log.debug("Отображаем главную форму");
                        // Set MainForm into the center of root layout.
                        MainApp.rootLayout.setCenter(mainPane);
                        // Give the controller access to the main app.
                        MainFormController mainFormController = fxmlLoader.getController();
                        MainFormController.setRootPane(mainPane);
                        mainFormController.setMainApp(this.mainApp);
                    } catch (IOException e) {
                        log.debug("Ошибка загрузки главной формы" + e.toString());
                        String headerText;
                        String contentText;
                        if (MainApp.getPrinterType() == 0 | MainApp.getPrinterPort() == 0 | MainApp.getPrinerPortSpeed() == 0) {
                            headerText = "Нет информации о подключении фискального принтера в файле настроек 'rro.ini'";
                            contentText = "Возможно, эти параметры заданы неверно";
                        } else {
                            headerText = "Неизвестная ошибка загрузки главной формы";
                            contentText = e.toString();
                        }
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText(headerText);
                        alert.setContentText(contentText);
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            log.debug("exit on error");
                            Platform.exit();
                            System.exit(0);
                        }
                    }
                }
            }
            if (CurrentUser.getInstance().getAccessLevel() == 999) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.initOwner(mainApp.getMainStage());
                alert.setTitle("Ошибка");
                alert.setHeaderText("Неверный пароль");
                alert.setContentText("Пожалуйста, набирайте внимательнее");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getMainStage());
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ничего не введено");
            alert.setContentText("Пожалуйста, набирайте внимательнее");
            alert.showAndWait();
        }
    }
/*
    public void setAddbutton(Event event) {
        String logintextboxText = logintextbox.getText();
        StringBuilder stringBuilder = new StringBuilder();
        textField.getProperties().put("vkType", "numeric");

        if (!StringUtils.isEmpty(logintextboxText)) {
            stringBuilder.append(logintextboxText);
        }

        if (stringBuilder.length() > 0) {
            log.debug("Создаю кнопку " + stringBuilder.toString());
            Button btn = new Button();
            btn.setText(stringBuilder.toString());
            btn.setId(btn.hashCode() + stringBuilder.toString());
            if (btnArrayList.size() < 3) {
                btn.setOnAction(innerEvent -> log.debug("нажали кнопку " + stringBuilder.toString()));
            } else {
                btn.setText("Кнопка очищения");
                btn.setOnAction(innerEvent -> {
                    log.debug("очищаем кнопки ");
                    int chldQty = loginForm.getChildren().size();
                    for (String aBtnArrayList : btnArrayList) {
                        for (int j = 0; j <= chldQty; j++) {
                            if (aBtnArrayList.equals(loginForm.getChildren().get(j).getId())) {
                                loginForm.getChildren().remove(j);
                                break;
                            }
                        }
                    }
                    btnArrayList.clear();
                });
            }
            btn.setLayoutX(20);
            btn.setLayoutY(20 + i);
            loginForm.getChildren().add(btn);
            btnArrayList.add(btn.getId());
            i = i + 30;
            messagelabel.setText("Создаю кнопку " + stringBuilder.toString());
        } else {
            log.debug("пусто название для кнопки недопустимо");
            messagelabel.setText("пусто название для кнопки недопустимо");
        }
    }
*/
}
