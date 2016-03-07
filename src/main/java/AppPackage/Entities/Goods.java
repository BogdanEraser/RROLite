package AppPackage.Entities;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.math.BigDecimal;

/**
 * Created by Eraser on 03.03.2016.
 */
public class Goods {
    private SimpleIntegerProperty code;
    private SimpleStringProperty name;
    private SimpleStringProperty sellType;
    private SimpleIntegerProperty sellTypeRRO;
    private SimpleObjectProperty price;
    private SimpleIntegerProperty goodsGroup;
    private SimpleIntegerProperty taxGroup;
    private SimpleIntegerProperty discoutGroup;
    private SimpleStringProperty barcode;

    public Goods() {
        this.code = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.sellType = new SimpleStringProperty();
        this.sellTypeRRO = new SimpleIntegerProperty();
        this.price = new SimpleObjectProperty<BigDecimal>(new BigDecimal(0));
        this.goodsGroup =  new SimpleIntegerProperty();
        this.taxGroup =  new SimpleIntegerProperty();
        this.discoutGroup =  new SimpleIntegerProperty();
        this.barcode =  new SimpleStringProperty();
    }


    public int getCode() {
        return code.get();
    }

    public SimpleIntegerProperty codeProperty() {
        return code;
    }

    public void setCode(int code) {
        this.code.set(code);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getSellType() {
        return sellType.get();
    }

    public SimpleStringProperty sellTypeProperty() {
        return sellType;
    }

    public void setSellType(String sellType) {
        this.sellType.set(sellType);
    }

    public int getSellTypeRRO() {
        return sellTypeRRO.get();
    }

    public SimpleIntegerProperty sellTypeRROProperty() {
        return sellTypeRRO;
    }

    public void setSellTypeRRO(int sellTypeRRO) {
        this.sellTypeRRO.set(sellTypeRRO);
    }

    public SimpleObjectProperty<BigDecimal> getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price.set(price);
    }

    public SimpleObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public int getGoodsGroup() {
        return goodsGroup.get();
    }

    public SimpleIntegerProperty goodsGroupProperty() {
        return goodsGroup;
    }

    public void setGoodsGroup(int goodsGroup) {
        this.goodsGroup.set(goodsGroup);
    }

    public int getTaxGroup() {
        return taxGroup.get();
    }

    public SimpleIntegerProperty taxGroupProperty() {
        return taxGroup;
    }

    public void setTaxGroup(int taxGroup) {
        this.taxGroup.set(taxGroup);
    }

    public int getDiscoutGroup() {
        return discoutGroup.get();
    }

    public SimpleIntegerProperty discoutGroupProperty() {
        return discoutGroup;
    }

    public void setDiscoutGroup(int discoutGroup) {
        this.discoutGroup.set(discoutGroup);
    }

    public String getBarcode() {
        return barcode.get();
    }

    public SimpleStringProperty barcodeProperty() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode.set(barcode);
    }

}
