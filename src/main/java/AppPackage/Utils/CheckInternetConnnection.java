package AppPackage.Utils;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Eraser on 02.03.2016.
 */
public class CheckInternetConnnection {
    private static final Logger log = Logger.getLogger(CheckInternetConnnection.class);
    private static CheckInternetConnnection instance;

    private CheckInternetConnnection() {
    }

    public static CheckInternetConnnection getInstance() {
        if (instance == null) {
            instance = new CheckInternetConnnection();
        }
        return instance;
    }

    public boolean isConnected() {
        Socket socket = null;
        boolean isConnected = false;
        try {
            socket = new Socket("google.com.ua", 80);
            isConnected = true;
        } catch (UnknownHostException e) {
            log.debug("Unknown host while checking internet connection: " + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            log.debug("IO Exception while checking internet connection: " + e.toString());
            e.printStackTrace();
        } finally {
            if (socket != null) try {
                socket.close();
            } catch (IOException e) {
                log.debug("Unable to close socket while checking internet connection");
            }
        }
        return isConnected;
    }
}
