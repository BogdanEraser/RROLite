package AppPackage;

import AppPackage.Controllers.LoginFormController;
import AppPackage.Controllers.OrderFormController;
import AppPackage.Entities.Goods;
import AppPackage.Entities.GoodsGroup;
import AppPackage.Entities.GoodsInCheck;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MainApp extends Application {
    private static final Logger log = Logger.getLogger(MainApp.class);
    private static boolean isRROLogEnabled;
    public static BorderPane rootLayout;
    public ArrayList<Goods> allGoodsArrayList; //������ ��� ��������� ������
    public ArrayList<Goods> allSelectedGoodsArrayList; //��� ������, ��������� ��� �������
    public ArrayList<GoodsGroup> allGoodsGroupsArrayList; //��� ������ �������
    private static ObservableList<GoodsInCheck> goodsInCheckObservableList = FXCollections.observableArrayList(goodsInCheck -> new Observable[]{goodsInCheck.quantityProperty()});
    /*public static ObservableList<GoodsInCheck> goodsInCheckObservableList = FXCollections.observableArrayList(new Callback<GoodsInCheck, Observable[]>() {
        @Override
        public Observable[] call(GoodsInCheck goodsInCheck) {
            return new Observable[]{(Observable) goodsInCheck.getQuantity()};
        }
    });*/
    private static SimpleObjectProperty<BigDecimal> checkSummary;
    private static SimpleStringProperty CashSumInRRO;
    private static SimpleStringProperty CCSumInRRO;
    private Stage MainStage;
    private static String sellingPointName;
    private static String pathToDataFile;
    private static int printerType;
    private static int printerPort;
    private static int printerPortSpeed;
    private static String pathToExchageFolder;
    public OrderFormController orderFormController;

    /**
     * MAIN ROUTINE
     */
    public static void main(String[] args) {

        /* ��� ��������� ����������� ����������
        System.setProperty("com.sun.javafx.isEmbedded", "true");
        System.setProperty("com.sun.javafx.touch", "true");
        System.setProperty("com.sun.javafx.virtualKeyboard", "javafx");*/
        setRROLogEnabled(false);
        if (args.length > 0) {
            if (args[0].equals("-log")) {
                setRROLogEnabled(true);
            }
        }

        checkSummary = new SimpleObjectProperty<BigDecimal>(new BigDecimal(0));
        CashSumInRRO = new SimpleStringProperty("");
        CCSumInRRO = new SimpleStringProperty("");

        //��������� ���������� ��������� ��������� �� ����� ��������
        try {
            String setupFilePath = "rro.ini";
            log.debug("getting setup data from rro.ini");
            //���� ���� ���������� � �������� �� ����� 1024 ����� (��� �� �� ����������� �����)
            if (Files.exists(Paths.get(setupFilePath)) & (Files.size(Paths.get(setupFilePath)) <= 1024)) {
                List<String> lines = Files.readAllLines(Paths.get(setupFilePath), Charset.defaultCharset());
                String[] splittedLines = lines.get(0).split(";");
                sellingPointName = splittedLines[0];
                pathToDataFile = splittedLines[1];
                printerType = Integer.parseInt(splittedLines[2]);
                printerPort = Integer.parseInt(splittedLines[3]);
                printerPortSpeed = Integer.parseInt(splittedLines[4]);
                pathToExchageFolder = splittedLines[5];
                log.debug("sellingPointName: " + sellingPointName);
                log.debug("pathToDataFile: " + pathToDataFile);
                log.debug("printerType: " + printerType);
                log.debug("printerPort: " + printerPort);
                log.debug("printerPortSpeed: " + printerPortSpeed);
                log.debug("pathToExchangeFolder: " + pathToExchageFolder);
            }
        } catch (IOException e) {
            log.debug("error getting setup data from rro.ini" + e.toString());
        }


        launch(args);
    }

    public static BorderPane getRootLayout() {
        return rootLayout;
    }

    public static ObservableList<GoodsInCheck> getGoodsInCheckObservableList() {
        return goodsInCheckObservableList;
    }

    public static void setGoodsInCheckObservableList(ObservableList<GoodsInCheck> goodsInCheckObservableList) {
        MainApp.goodsInCheckObservableList = goodsInCheckObservableList;
    }

    public static String getCCSumInRRO() {
        return CCSumInRRO.get();
    }

    public static SimpleStringProperty CCSumInRROProperty() {
        return CCSumInRRO;
    }

    public static void setCCSumInRRO(String CCSumInRRO) {
        MainApp.CCSumInRRO.set(CCSumInRRO);
    }

    public static String getCashSumInRRO() {
        return CashSumInRRO.get();
    }

    public static SimpleStringProperty cashSumInRROProperty() {
        return CashSumInRRO;
    }

    public static void setCashSumInRRO(String cashSumInRRO) {
        MainApp.CashSumInRRO.set(cashSumInRRO);
    }

    public static BigDecimal getCheckSummary() {
        return checkSummary.get();
    }

    public static SimpleObjectProperty<BigDecimal> checkSummaryProperty() {
        return checkSummary;
    }

    public void setCheckSummary(BigDecimal checkSummary) {
        MainApp.checkSummary.set(checkSummary);
    }

    public static boolean isRROLogEnabled() {
        return isRROLogEnabled;
    }

    private static void setRROLogEnabled(boolean RROLogEnabled) {
        isRROLogEnabled = RROLogEnabled;
    }

    public Stage getMainStage() {
        return MainStage;
    }

    public static String getPathToDataFile() {
        return pathToDataFile;
    }

    public static int getPrinterPortSpeed() {
        return printerPortSpeed;
    }

    public static int getPrinterPort() {
        return printerPort;
    }

    public static int getPrinterType() {
        return printerType;
    }

    public static String getPathToExchageFolder() {
        return pathToExchageFolder;
    }

    public static void setPathToExchageFolder(String pathToExchageFolder) {
        MainApp.pathToExchageFolder = pathToExchageFolder;
    }

    public static String getSellingPointName() {
        return sellingPointName;
    }

    public static void setSellingPointName(String sellingPointName) {
        MainApp.sellingPointName = sellingPointName;
    }

    @Override
    public void start(Stage stage) {
        log.info("\n-= Starting ��� ���� - ������������� ������ � ����������� �������������� =-");
        this.MainStage = stage;
        stage.setTitle("��� ���� - ������������� ������ � ����������� ��������������");
        stage.setMaximized(true);
        stage.setResizable(false);
        // Set the application icon.
        this.MainStage.getIcons().add(new Image(MainApp.class.getResourceAsStream("/images/Cash_register.png")));

        initRootLayout();
        showLoginForm();
        stage.show();
    }

    /**
     * Initializes the root layout.
     */
    public boolean initRootLayout() {
        try {
            // Load root layout from fxml file.
            String fxmlFormPath = "/fxml/RootLayout.fxml";
            log.debug("Loading RootLayout for main view");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
            rootLayout = fxmlLoader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            MainStage.setScene(scene);
            MainStage.show();
            return true;
        } catch (IOException e) {
            log.debug("������ �������� ��������� �������");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ���������� ������ � ������ ������ ������ ��������� �������
     */
    public void showLoginForm() {
        try {
            String fxmlFormPath = "/fxml/LoginForm/LoginForm.fxml";
            log.debug("Loading LoginForm for main view into RootLayout");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(MainApp.class.getResource(fxmlFormPath));
            BorderPane loginPane = fxmlLoader.load();

            // Set LoginForm into the center of root layout.
            rootLayout.setCenter(loginPane);

            // Give the controller access to the main app.
            LoginFormController loginFormController = fxmlLoader.getController();
            LoginFormController.setRootPane(loginPane);
            loginFormController.setMainApp(this);

        } catch (IOException e) {
            log.debug("������ �������� ����� ������" + e.toString());
            String headerText;
            String contentText;
            if (MainApp.getPathToDataFile() == null) {
                headerText = "��� ���������� � ���������� ���������";
                contentText = "��������, �� ������ ��� ���� ���� 'rro.ini', \n��� ��� ������ ����� 1��";
            } else if (!Files.exists(Paths.get(MainApp.getPathToDataFile()))) {
                headerText = "���������� ���� � ������� ���������";
                contentText = "���� � ����� (�������� ���������� �� rro.ini): " + MainApp.getPathToDataFile();
            } else {
                headerText = "����������� ������ �������� ����� ������";
                contentText = e.toString();
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("������");
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                log.debug("���������� �����");
                Platform.exit();
                System.exit(0);
            }
        }
    }

}
