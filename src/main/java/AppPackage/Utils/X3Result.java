package AppPackage.Utils;

import java.math.BigDecimal;

/**
 * Created by Eraser on 24.02.2016.
 * 
 * Class for decoded line for X3 reports from MiniFP-54
 *
 * code - код товара
 * price - цена (0.00 если открытаая)
 * name - наименование
 * barcode - штирх-код
 * qty - количество (остаток)
 * qtyIn - количество (продажа)
 * MRKPIn - наценка (продажа)
 * RDCTIn - скидка (продажа)
 * TRNOVRIn - оборот (продажа)
 * qtyOut - количество (возврат)
 * MRKPOut - наценка (возврат)
 * RDCTOut - скидка (возврат)
 * TRNOVROut - оборот (возврат)
 */
public class X3Result {
    private int code;
    private BigDecimal price;
    private String name;
    private String barcode;
    private BigDecimal qty;
    private BigDecimal qtyIn;
    private BigDecimal MRKPIn;
    private BigDecimal RDCTIn;
    private BigDecimal TRNOVRIn;
    private BigDecimal qtyOut;
    private BigDecimal MRKPOut;
    private BigDecimal RDCTOut;
    private BigDecimal TRNOVROut;

    X3Result() {
    }

    @Override
    public String toString() {
        return "X3Result{" +
                " code=" + code +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", barcode=" + barcode +
                ", qty=" + qty +
                ", qtyIn=" + qtyIn +
                ", MRKPIn=" + MRKPIn +
                ", RDCTIn=" + RDCTIn +
                ", TRNOVRIn=" + TRNOVRIn +
                ", qtyOut=" + qtyOut +
                ", MRKPOut=" + MRKPOut +
                ", RDCTOut=" + RDCTOut +
                ", TRNOVROut=" + TRNOVROut +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getQtyIn() {
        return qtyIn;
    }

    public void setQtyIn(BigDecimal qtyIn) {
        this.qtyIn = qtyIn;
    }

    public BigDecimal getMRKPIn() {
        return MRKPIn;
    }

    public void setMRKPIn(BigDecimal MRKPIn) {
        this.MRKPIn = MRKPIn;
    }

    public BigDecimal getRDCTIn() {
        return RDCTIn;
    }

    public void setRDCTIn(BigDecimal RDCTIn) {
        this.RDCTIn = RDCTIn;
    }

    public BigDecimal getTRNOVRIn() {
        return TRNOVRIn;
    }

    public void setTRNOVRIn(BigDecimal TRNOVRIn) {
        this.TRNOVRIn = TRNOVRIn;
    }

    public BigDecimal getQtyOut() {
        return qtyOut;
    }

    public void setQtyOut(BigDecimal qtyOut) {
        this.qtyOut = qtyOut;
    }

    public BigDecimal getMRKPOut() {
        return MRKPOut;
    }

    public void setMRKPOut(BigDecimal MRKPOut) {
        this.MRKPOut = MRKPOut;
    }

    public BigDecimal getRDCTOut() {
        return RDCTOut;
    }

    public void setRDCTOut(BigDecimal RDCTOut) {
        this.RDCTOut = RDCTOut;
    }

    public BigDecimal getTRNOVROut() {
        return TRNOVROut;
    }

    public void setTRNOVROut(BigDecimal TRNOVROut) {
        this.TRNOVROut = TRNOVROut;
    }
}
