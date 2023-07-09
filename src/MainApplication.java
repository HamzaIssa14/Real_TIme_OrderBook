import order.validators.DefaultOrderValidator;
import order.validators.OrderValidator;
import orderbook.BuyOrderBook;
import orderbook.SellOrderBook;

import java.io.IOException;
import java.net.ServerSocket;

public class MainApplication {
    public static void main(String[] args) throws IOException {
        OrderValidator orderValidator= new DefaultOrderValidator();
        BuyOrderBook buyOrderBook = new BuyOrderBook();
        SellOrderBook sellOrderBook = new SellOrderBook();

        System.out.println("Server on port 8080 is BOOTING up...");

        ServerSocket serverSocket = new ServerSocket(8080);
        Server server = new Server(serverSocket, orderValidator, buyOrderBook, sellOrderBook);
        System.out.println("Server on port 8080 has STARTED");
        server.startServer();
    }
}
