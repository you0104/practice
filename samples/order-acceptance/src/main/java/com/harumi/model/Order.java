package com.harumi.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 1人のお客様の注文を表すオブジェクト。
 * 注文受付画面で「コーヒーを押下」するたびに addItem() で加算され、
 * セッション属性 "order" として保持される（DB登録は支払い確定時）。
 */
public class Order {

    /** itemNo をキーに明細を保持（同じ商品は数量を合算） */
    private final Map<Integer, OrderLine> lines = new LinkedHashMap<>();

    /** 適用クーポンの値引額（円）。未適用は0。※課税前に小計から引く */
    private int discount = 0;

    /** 商品を1個追加する（既にある商品なら数量+1） */
    public void addItem(Item item) {
        OrderLine line = lines.computeIfAbsent(item.getItemNo(), k -> new OrderLine(item));
        line.addQuantity(1);
    }

    public List<OrderLine> getLines() {
        return new ArrayList<>(lines.values());
    }

    public boolean isEmpty() { return lines.isEmpty(); }

    public void clear() { lines.clear(); discount = 0; }

    public void setDiscount(int discount) { this.discount = discount; }
    public int getDiscount() { return discount; }

    /** 小計（税抜・値引き前グロス） */
    public int getSubTotal() {
        return lines.values().stream().mapToInt(OrderLine::getLineAmount).sum();
    }

    /** 注文全体のコーヒー使用量(ml) */
    public int getTotalCoffee() {
        return lines.values().stream().mapToInt(OrderLine::getLineCoffee).sum();
    }

    /** 課税対象額 = 小計 − 値引 */
    public int getTaxableAmount() {
        return getSubTotal() - discount;
    }

    /** 消費税8%・切り捨て（課税対象額に対して計算） */
    public int getTax() {
        return (int) Math.floor(getTaxableAmount() * 0.08);
    }

    /** 総計 = 課税対象額 + 消費税 */
    public int getTotal() {
        return getTaxableAmount() + getTax();
    }
}
