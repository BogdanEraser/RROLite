package AppPackage.Entities;

import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;

/**
 * Created by Eraser on 07.03.2016.
 */
public class GoodsInCheck {
    private Goods goods;
    private SimpleObjectProperty quantity;
    private SimpleObjectProperty summaryOnGoods;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public Object getQuantity() {
        return quantity.get();
    }

    public SimpleObjectProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(Object quantity) {
        this.quantity.set(quantity);
    }

    public Object getSummaryOnGoods() {
        return summaryOnGoods.get();
    }

    public SimpleObjectProperty summaryOnGoodsProperty() {
        return summaryOnGoods;
    }

    public void setSummaryOnGoods(Object summaryOnGoods) {
        this.summaryOnGoods.set(summaryOnGoods);
    }

    public GoodsInCheck(Goods goods, BigDecimal qty, BigDecimal summary) {
        this.goods = goods;
        this.quantity = new SimpleObjectProperty<BigDecimal>(qty);
        this.summaryOnGoods = new SimpleObjectProperty<BigDecimal>(summary);
    }
}
