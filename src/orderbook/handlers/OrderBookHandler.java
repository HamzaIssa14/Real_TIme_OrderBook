package orderbook.handlers;

import order.Order;
import order.enums.OrderType;
import order.validators.OrderValidator;
import orderbook.BuyOrderBook;
import orderbook.OrderBookContainer;
import orderbook.SellOrderBook;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class OrderBookHandler implements Runnable{
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private OrderValidator orderValidator;
    private BuyOrderBook buyOrderBook;
    private SellOrderBook sellOrderBook;
    public static List<OrderBookHandler> orderBookHandlers = new ArrayList<>();

    public OrderBookHandler(Socket socket,
                            OrderValidator orderValidator,
                            BuyOrderBook buyOrderBook,
                            SellOrderBook sellOrderBook){
        System.out.println("Starting the orderbookhandler");

        this.socket = socket;
        this.orderValidator = orderValidator;
        this.buyOrderBook = buyOrderBook;
        this.sellOrderBook = sellOrderBook;

        try {
            this.objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            objectOutputStream.flush(); // This is important to ensure the header is written immediately
            this.objectInputStream = new ObjectInputStream( new BufferedInputStream( socket.getInputStream()));

            System.out.println("Successfully created the orderbookhandler");
        }catch (IOException e){
            System.out.println("Failure in creting orderbook handler");
            throw new RuntimeException("Failed to create streams for socket", e);
        }
        orderBookHandlers.add(this);
    }

    /**
     * Listen for any incoming new orders from the client.
     */
    @Override
    public void run() {
        // Listen to particular clients socket
        Order incomingOrder;
        while(socket.isConnected()){
            System.out.println("OrderHandler listening for new orders from client");
            try {
                System.out.println("Waiting for new order");
                incomingOrder = (Order) objectInputStream.readObject();
                System.out.println("Order collected");
                if (orderValidator.validate(incomingOrder)){
                    sendOrder(incomingOrder);
                }
            } catch (EOFException e){
                System.out.println("Connection closed by the client");
                closeEverything(socket, objectOutputStream, objectInputStream);
                break;
            } catch (ClassNotFoundException e){
                System.out.println("order.Order class object could not be found in object output stream");
                closeEverything(socket, objectOutputStream, objectInputStream);
                break;
            } catch (IOException e){
                System.out.println("Failure in receiving incoming stream from socket");
                closeEverything(socket, objectOutputStream, objectInputStream);
                break;
            }
        }
    }


    public void sendOrder(Order order){
        for (OrderBookHandler orderBookHandler : orderBookHandlers){

            try {
                if(order.getType().equals(OrderType.BUY)){
                    buyOrderBook.addOrder(order);
                    List<Order> buyOrderBookList = buyOrderBook.listOrders();
                    List<Order> sellOrderBookList = sellOrderBook.listOrders();
                    OrderBookContainer orderBookContainer = new OrderBookContainer(buyOrderBookList, sellOrderBookList);
                    orderBookHandler.objectOutputStream.writeObject(orderBookContainer);
                    orderBookHandler.objectOutputStream.flush();
                }
                else if(order.getType().equals(OrderType.SELL)){
                    sellOrderBook.addOrder(order);
                    List<Order> buyOrderBookList = buyOrderBook.listOrders();
                    List<Order> sellOrderBookList = sellOrderBook.listOrders();
                    OrderBookContainer orderBookContainer = new OrderBookContainer(buyOrderBookList, sellOrderBookList);
                    orderBookHandler.objectOutputStream.writeObject(orderBookContainer);
                    orderBookHandler.objectOutputStream.flush();
                }
            }catch (IOException e){
                System.out.println("Error in sending new orderbook to the client via socket");
                closeEverything(socket, orderBookHandler.objectOutputStream, orderBookHandler.objectInputStream);
            }

        }

    }

    private void closeEverything(Socket socket, ObjectOutputStream objectOutputStream, ObjectInputStream objectInputStream){
        try {
            if(socket != null){
                socket.close();
            }
            if(objectOutputStream != null){
                objectOutputStream.close();
            }
            if(objectInputStream != null){
                objectInputStream.close();
            }
        }catch (IOException e){
            System.out.println("Error in closing the OrderHandler streams and socket");
            e.printStackTrace();
        }

    }
}
