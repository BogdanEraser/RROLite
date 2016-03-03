package AppPackage.Entities;

/**
 * Created by Eraser on 03.03.2016.
 */
public class GoodsGroup {
    private int code;
    private String name;

    public GoodsGroup(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodsGroup that = (GoodsGroup) o;

        return getCode() == that.getCode();

    }

    @Override
    public int hashCode() {
        return getCode();
    }
}
