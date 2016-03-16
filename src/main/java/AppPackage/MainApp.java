package AppPackage;

import AppPackage.Controllers.LoginFormController;
import AppPackage.Entities.Goods;
import AppPackage.Entities.GoodsGroup;
import AppPackage.Entities.GoodsInCheck;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleObjectProperty;
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
    public ArrayList<Goods> allGoodsArrayList;
    public ArrayList<GoodsGroup> allGoodsGroupsArrayList;
    public static ObservableList<GoodsInCheck> goodsInCheckObservableList = FXCollections.observableArrayList(goodsInCheckQTY -> new Observable[]{goodsInCheckQTY.quantityProperty()});
    private static SimpleObjectProperty<BigDecimal> checkSummary;
    private Stage MainStage;
    private static String pathToDataFile;
    private static int printerType;
    private static int printerPort;
    private static int prinerPortSpeed;

    /**
     * MAIN ROUTINE
     */
    public static void main(String[] args) {

        /* ДЛЯ поддержки виртуальной клавиатуры
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

        //получение параметров настройки программы из файла настроек
        try {
            String setupFilePath = "rro.ini";
            log.debug("getting setup data from rro.ini");
            //если файл существует и размером не более 1024 байта (что бы не переполнить буфер)
            if (Files.exists(Paths.get(setupFilePath)) & (Files.size(Paths.get(setupFilePath)) <= 1024)) {
                List<String> lines = Files.readAllLines(Paths.get(setupFilePath), Charset.defaultCharset());
                String[] splittedLines = lines.get(0).split(";");
                pathToDataFile = splittedLines[0];
                printerType = Integer.parseInt(splittedLines[1]);
                printerPort = Integer.parseInt(splittedLines[2]);
                prinerPortSpeed = Integer.parseInt(splittedLines[3]);
                log.debug("pathToDataFile: " + pathToDataFile);
                log.debug("printerType: " + printerType);
                log.debug("printerPort: " + printerPort);
                log.debug("printerPortSpeed: " + prinerPortSpeed);
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

    public static int getPrinerPortSpeed() {
        return prinerPortSpeed;
    }

    public static int getPrinterPort() {
        return printerPort;
    }

    public static int getPrinterType() {
        return printerType;
    }

    @Override
    public void start(Stage stage) {
        log.info("\n-= Starting РРО Софт - Автоматизация работы с фискальными регистраторами =-");
        this.MainStage = stage;
        stage.setTitle("РРО Софт - Автоматизация работы с фискальными регистраторами");
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
            log.debug("Ошибка загрузки корневого лэйаута");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * показываем лэйаут с формой логина внутри корневого лэйаута
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
            loginFormController.setMainApp(this);

        } catch (IOException e) {
            log.debug("Ошибка загрузки формы логина" + e.toString());
            String headerText;
            String contentText;
            if (MainApp.getPathToDataFile() == null) {
                headerText = "Нет информации о параметрах программы";
                contentText = "Возможно, не найден или пуст файл 'rro.ini', \nили его размер более 1Кб";
            } else if (!Files.exists(Paths.get(MainApp.getPathToDataFile()))) {
                headerText = "Недоступен файл с данными программы";
                contentText = "Путь к файлу (согласно параметрам из rro.ini): " + MainApp.getPathToDataFile();
            } else {
                headerText = "Неизвестная ошибка загрузки формы логина";
                contentText = e.toString();
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                log.debug("нормальный выход");
                Platform.exit();
                System.exit(0);
            }
        }
    }
}
