package AppPackage;

/**
 * Seems to be Singleton for current user
 * Created by Eraser on 29.02.2016.
 */
public class CurrentUser extends User {

    private static CurrentUser instance;

    private CurrentUser() {
        super("", 143143143, 999);
    }

    private CurrentUser(String name, int pswd, int accessLevel) {
        super(name, pswd, accessLevel);
    }

    public static CurrentUser getInstance() {
        if (instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public static CurrentUser getInstance(String name, int pswd, int accessLevel) {
        if (instance == null) {
            instance = new CurrentUser(name, pswd, accessLevel);
        }
        return instance;
    }

    public static void setInstance() {
        instance = null;
    }
}
