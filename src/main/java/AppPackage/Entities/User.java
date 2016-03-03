package AppPackage.Entities;

/**
 * Created by Eraser on 29.02.2016.
 */
public class User {
    private String name;
    private int pswd;
    private int accessLevel;

    public User() {
    }

    public User(String name, int pswd, int accessLevel) {
        this.name = name;
        this.pswd = pswd;
        this.accessLevel = accessLevel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPswd() {
        return pswd;
    }

    public void setPswd(int pswd) {
        this.pswd = pswd;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }
}
