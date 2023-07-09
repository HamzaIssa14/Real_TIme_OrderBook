package order;

import order.enums.OrderType;

import java.io.Serializable;
import java.util.Random;

public class Order implements Comparable<Order>, Serializable {
    private final long id;
    private final String ticker;
    private final double price;
    private final int quantity;
    private final OrderType type;
    private final long timestamp;

    public Order(String ticker, double price, int quantity, OrderType type, long timestamp){
        Random random = new Random();
        this.id = random.nextLong();
        this.ticker = ticker;
        this.price = price;
        this.quantity = quantity;
        this.type = type;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }
    public String getTicker(){
        return ticker;
    }
    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public OrderType getType() {
        return type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(Order other) {
        // Here we implement the price-time priority matching algorithm.
        if(this.price != other.price){
            return Double.compare(this.price, other.price);
        } else{
            return Long.compare(this.timestamp, other.timestamp);
        }
    }
}
