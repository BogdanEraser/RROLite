package AppPackage.Entities;

import java.math.BigDecimal;

/**
 * Created by Eraser on 03.03.2016.
 */
public class Goods {
    private int code;
    private String name;
    private String sellType;
    private int sellTypeRRO;
    private BigDecimal price;
    private int goodsGroup;
    private int taxGroup;
    private int discoutGroup;
    private String barcode;

    public Goods() {
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

    public String getSellType() {
        return sellType;
    }

    public void setSellType(String sellType) {
        this.sellType = sellType;
    }

    public int getSellTypeRRO() {
        return sellTypeRRO;
    }

    public void setSellTypeRRO(int sellTypeRRO) {
        this.sellTypeRRO = sellTypeRRO;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getGoodsGroup() {
        return goodsGroup;
    }

    public void setGoodsGroup(int goodsGroup) {
        this.goodsGroup = goodsGroup;
    }

    public int getTaxGroup() {
        return taxGroup;
    }

    public void setTaxGroup(int taxGroup) {
        this.taxGroup = taxGroup;
    }

    public int getDiscoutGroup() {
        return discoutGroup;
    }

    public void setDiscoutGroup(int discoutGroup) {
        this.discoutGroup = discoutGroup;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Goods goods = (Goods) o;

        return getCode() == goods.getCode();

    }

    @Override
    public int hashCode() {
        return getCode();
    }
}
