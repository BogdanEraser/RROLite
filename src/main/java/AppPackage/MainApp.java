package AppPackage;

import AppPackage.Controllers.LoginFormController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MainApp extends Application {
    private static final Logger log = Logger.getLogger(MainApp.class);
    private static boolean isRROLogEnabled;
    public BorderPane rootLayout;
    private Stage MainStage;

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
        launch(args);
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
            log.debug("Ошибка загрузки формы логина");
            e.printStackTrace();
        }
    }
}
