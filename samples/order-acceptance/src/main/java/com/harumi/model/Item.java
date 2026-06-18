package com.harumi.model;

/**
 * 商品（商品テーブル Item の1行に対応）。
 */
public class Item {
    private int itemNo;        // 商品番号
    private String name;       // 商品名
    private int price;         // 販売単価（税抜）
    private int coffeeAmount;  // 1個あたりコーヒー使用量(ml)

    public Item(int itemNo, String name, int price, int coffeeAmount) {
        this.itemNo = itemNo;
        this.name = name;
        this.price = price;
        this.coffeeAmount = coffeeAmount;
    }

    public int getItemNo()       { return itemNo; }
    public String getName()      { return name; }
    public int getPrice()        { return price; }
    public int getCoffeeAmount() { return coffeeAmount; }
}
