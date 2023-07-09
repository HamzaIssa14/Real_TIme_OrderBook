import order.validators.OrderValidator;
import orderbook.BuyOrderBook;
import orderbook.handlers.OrderBookHandler;
import orderbook.SellOrderBook;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    // Responsible for listening for incoming connections
    private ServerSocket serverSocket;
    private OrderValidator orderValidator;
    private BuyOrderBook buyOrderBook;
    private SellOrderBook sellOrderBook;

    public Server(ServerSocket serverSocket,
                  OrderValidator orderValidator,
                  BuyOrderBook buyOrderBook,
                  SellOrderBook sellOrderBook){

        this.serverSocket = serverSocket;
        this.orderValidator = orderValidator;
        this.buyOrderBook = buyOrderBook;
        this.sellOrderBook = sellOrderBook;
    }

    public void startServer(){
        try {
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept(); // Blocking method. Program will halt here. Waiting.
                System.out.println("A new client has connected");
                OrderBookHandler orderBookHandler = new OrderBookHandler(socket, orderValidator, buyOrderBook, sellOrderBook);
                Thread thread = new Thread(orderBookHandler);
                thread.start();
            }
        } catch (IOException e){
            System.out.println("There is an issue with the server connection");
            closeServerSocket();
        }
    }
    public void closeServerSocket(){
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e){
            System.out.println("Failure closing server socket");
            e.printStackTrace();
        }
    }

}
