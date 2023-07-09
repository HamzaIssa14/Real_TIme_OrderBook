package orderbook;

import order.Order;

import java.io.Serializable;
import java.util.List;

public class OrderBookContainer implements Serializable {
    private List<Order> buyOrderBook;
    private List<Order> sellOrderBook;

    public OrderBookContainer(List<Order> buyOrderBook, List<Order> sellOrderBook){
        this.buyOrderBook = buyOrderBook;
        this.sellOrderBook = sellOrderBook;
    }

    public List<Order> getBuyOrderBook(){
        return buyOrderBook;
    }

    public List<Order> getSellOrderBook(){
        return sellOrderBook;
    }

    public void displayOrderBook() {
        // Determine the maximum number of rows to display
        int maxRows = Math.max(buyOrderBook.size(), sellOrderBook.size());

        // Create the header row for the tables
        String header = String.format("%-15s %-15s || %-15s %-15s", "BUY PRICE", "Quantity", "SELL PRICE", "QUANTITY");

        // Print the header row
        System.out.println(header);
        System.out.println(new String(new char[header.length()]).replace("\0", "-"));

        // Print each row in the table
        for (int i = 0; i < maxRows; i++) {
            String buyOrderQuantity = i < buyOrderBook.size() ? String.valueOf(buyOrderBook.get(i).getQuantity()) : "";
            String buyPrice = i < buyOrderBook.size() ? String.valueOf(buyOrderBook.get(i).getPrice()) : "";
            String sellOrderQuantity = i < sellOrderBook.size() ? String.valueOf(sellOrderBook.get(i).getQuantity()) : "";
            String sellPrice = i < sellOrderBook.size() ? String.valueOf(sellOrderBook.get(i).getPrice()) : "";

            System.out.println(String.format("%-15s %-15s || %-15s %-15s", buyOrderQuantity, buyPrice, sellOrderQuantity, sellPrice));
        }
        System.out.println(new String(new char[header.length()]).replace("\0", "="));
        System.lineSeparator();

    }

}
