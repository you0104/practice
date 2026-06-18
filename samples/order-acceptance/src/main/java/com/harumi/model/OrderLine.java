package com.harumi.model;

/**
 * 注文明細1行（商品＋数量）。注文受付画面の「注文リスト」1行に対応。
 */
public class OrderLine {
    private final Item item;
    private int quantity;

    public OrderLine(Item item) {
        this.item = item;
        this.quantity = 0;
    }

    public void addQuantity(int q) { this.quantity += q; }

    public Item getItem()     { return item; }
    public int getQuantity()  { return quantity; }

    /** この明細の金額（税抜） = 単価 × 個数 */
    public int getLineAmount() { return item.getPrice() * quantity; }

    /** この明細のコーヒー使用量(ml) = 使用量 × 個数 */
    public int getLineCoffee() { return item.getCoffeeAmount() * quantity; }
}
