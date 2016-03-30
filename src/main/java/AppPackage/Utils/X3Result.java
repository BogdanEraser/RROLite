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

     int getCode() {
        return code;
    }

     void setCode(int code) {
        this.code = code;
    }

     BigDecimal getPrice() {
        return price;
    }

     void setPrice(BigDecimal price) {
        this.price = price;
    }

     String getName() {
        return name;
    }

     void setName(String name) {
        this.name = name;
    }

     String getBarcode() {
        return barcode;
    }

     void setBarcode(String barcode) {
        this.barcode = barcode;
    }

     BigDecimal getQty() {
        return qty;
    }

     void setQty(BigDecimal qty) {
        this.qty = qty;
    }

     BigDecimal getQtyIn() {
        return qtyIn;
    }

     void setQtyIn(BigDecimal qtyIn) {
        this.qtyIn = qtyIn;
    }

     BigDecimal getMRKPIn() {
        return MRKPIn;
    }

     void setMRKPIn(BigDecimal MRKPIn) {
        this.MRKPIn = MRKPIn;
    }

     BigDecimal getRDCTIn() {
        return RDCTIn;
    }

     void setRDCTIn(BigDecimal RDCTIn) {
        this.RDCTIn = RDCTIn;
    }

     BigDecimal getTRNOVRIn() {
        return TRNOVRIn;
    }

     void setTRNOVRIn(BigDecimal TRNOVRIn) {
        this.TRNOVRIn = TRNOVRIn;
    }

     BigDecimal getQtyOut() {
        return qtyOut;
    }

     void setQtyOut(BigDecimal qtyOut) {
        this.qtyOut = qtyOut;
    }

     BigDecimal getMRKPOut() {
        return MRKPOut;
    }

     void setMRKPOut(BigDecimal MRKPOut) {
        this.MRKPOut = MRKPOut;
    }

     BigDecimal getRDCTOut() {
        return RDCTOut;
    }

     void setRDCTOut(BigDecimal RDCTOut) {
        this.RDCTOut = RDCTOut;
    }

     BigDecimal getTRNOVROut() {
        return TRNOVROut;
    }

     void setTRNOVROut(BigDecimal TRNOVROut) {
        this.TRNOVROut = TRNOVROut;
    }
}
