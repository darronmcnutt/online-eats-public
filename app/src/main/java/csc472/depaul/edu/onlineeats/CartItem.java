package csc472.depaul.edu.onlineeats;

public class CartItem {
    private String name;
    private int qty;
    private float price;

    public CartItem() {
        // Default constructor required for calls to DataSnapshot.getValue(CartItem.class)
    }

    public CartItem(String name, float price, int qty) {
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public int getQty() {
        return qty;
    }

    public float getPrice() {
        return price;
    }
}
