package AppPackage.Entities;

import javafx.beans.property.SimpleObjectProperty;

import java.math.BigDecimal;

/**
 * Created by Eraser on 07.03.2016.
 */
public class GoodsInCheck {
    private Goods goods;
    private SimpleObjectProperty<BigDecimal> quantity;
    private SimpleObjectProperty<BigDecimal> summaryOnGoods;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public BigDecimal getQuantity() {
        return quantity.get();
    }

    public SimpleObjectProperty<BigDecimal> quantityProperty() {
        return new SimpleObjectProperty<BigDecimal>(new BigDecimal(quantity.getValue().toString()).setScale(3,BigDecimal.ROUND_CEILING));
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity.set(quantity);
    }

    public BigDecimal getSummaryOnGoods() {
        return summaryOnGoods.get();
    }

    public SimpleObjectProperty<BigDecimal> summaryOnGoodsProperty() {
        return new SimpleObjectProperty<BigDecimal>(new BigDecimal(summaryOnGoods.getValue().toString()).setScale(2,BigDecimal.ROUND_HALF_EVEN));
    }

    public void setSummaryOnGoods(BigDecimal summaryOnGoods) {
        this.summaryOnGoods.set(summaryOnGoods);
    }

    public GoodsInCheck(Goods goods, BigDecimal qty, BigDecimal summary) {
        this.goods = goods;
        this.quantity = new SimpleObjectProperty<BigDecimal>(qty);
        this.summaryOnGoods = new SimpleObjectProperty<BigDecimal>(summary);
    }
}
