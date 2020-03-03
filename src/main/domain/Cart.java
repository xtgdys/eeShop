package main.domain;

import java.util.HashMap;
import java.util.Map;

public class Cart {
    private Map<String, CartItem> cartItems = new HashMap<>();
    private double total;

    public Cart() {
    }

    public Cart(Map<String, CartItem> cartItems, double total) {
        this.cartItems = cartItems;
        this.total = total;
    }

    public Map<String, CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(Map<String, CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
