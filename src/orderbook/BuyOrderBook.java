package orderbook;

import order.Order;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

// This  should be a singleton.
// Make sure this object is thread safe
public class BuyOrderBook implements Serializable {

    PriorityBlockingQueue<Order> orderBook;


    public BuyOrderBook(){
        orderBook = new PriorityBlockingQueue<>(11, Collections.reverseOrder());
    }

    public void addOrder(Order order){
        orderBook.add(order);
    }


    /**
     * listOrder() is not atomic.
     * This means that the state of the queue might change during their execution
     * if other threads are adding or removing elements
     * @return
     */
    public List<Order> listOrders() {
        List<Order> orders = new ArrayList<>();
        PriorityQueue<Order> copyOrderBook = new PriorityQueue<>(Collections.reverseOrder());
        copyOrderBook.addAll(orderBook);
        while(!copyOrderBook.isEmpty()){
            Order order = copyOrderBook.poll();
            orders.add(order);
        }
        return orders;
    }

    /**
     * listOrder(int price) is not atomic.
     * This means that the state of the queue might change during their execution
     * if other threads are adding or removing elements
     * @return
     */
    public List<Order> listOrders(int price) {
        List<Order> orders = new ArrayList<>();
        Iterator<Order> orderIterator = orderBook.iterator();
        while (orderIterator.hasNext()){
            orders.add(orderIterator.next());
        }
        orders.sort(null);

        int l = 0;
        int r = orders.size() - 1;
        int mid = 0;

        while (l <= r){
            mid = (l + r)/2;
            if (orders.get(mid).getPrice() == price){
                // Found exact match, find the first one
                while(mid > 0 && orders.get(mid - 1).getPrice() == price) {
                    mid--;
                }
                break;
            }
            if (orders.get(mid).getPrice() < price){
                l = mid + 1;
            }
            else if (orders.get(mid).getPrice() > price){
                r = mid - 1;
            }
        }

        // If not found, the left pointer will be at the position to insert price
        // Hence all prices from l onward are greater than price
        return orders.subList(l, orders.size());
    }

}
