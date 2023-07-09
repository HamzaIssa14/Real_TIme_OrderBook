package order.client;

import order.Order;
import order.enums.OrderType;
import order.enums.Ticker;
import orderbook.OrderBookContainer;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public Client(Socket socket){
        try {
            System.out.println("In client constructor, attempting to intstantiate");
            this.socket = socket;
            this.objectOutputStream = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            objectOutputStream.flush(); // This is important to ensure the header is written immediately
            this.objectInputStream = new ObjectInputStream( new BufferedInputStream( socket.getInputStream()));

            System.out.println("Client created");
        } catch (IOException e){
            System.out.println("Failure setting up the socket and streamd for the client");
            e.printStackTrace();
            closeEverything(socket, objectOutputStream, objectInputStream);
        } catch (Exception e){
            System.out.println("An exception occured");
            e.printStackTrace();
        }
    }
    public void listenForUpdates(){
        try {
            while(true){
                OrderBookContainer orderBook = (OrderBookContainer) objectInputStream.readObject();
                clearConsole();
                orderBook.displayOrderBook();
            }
        }catch (IOException | ClassNotFoundException e){
            System.out.println("Error listening for orderbook updates");
            closeEverything(socket, objectOutputStream, objectInputStream);

        }

    }
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void createOrder(){
        String ticker;
        double price = 0;
        int quantity = 0;
        OrderType type;
        long timestamp;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Would you like to buy a stock (Y/N)? ");
        String res = scanner.nextLine();
        if (res.equalsIgnoreCase("yes") || res.equalsIgnoreCase("y")){
            System.out.println("Please input the ticker of the stock you are interested in: ");
            res = scanner.nextLine();
            while(!validTicker(res)){
                System.out.println("Sorry that ticker isn't available. Please input a different ticker: ");
                res = scanner.nextLine();
            }
            ticker = res;


            System.out.println("Is this a BUY or SELL order (BUY/SELL)? ");
            res = scanner.nextLine();

            while(!validOrderType(res)){
                System.out.println("Please input BUY for a buy order or SELL for sell order: ");
                res = scanner.nextLine();
            }
            type = OrderType.valueOf(res.toUpperCase());


            boolean validInput = false;
            while (!validInput) {
                System.out.println("How many shares of " + ticker + " would you like?");
                res = scanner.nextLine();

                try {
                    int number = Integer.parseInt(res);
                    validInput = true;
                    quantity = number;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: not a valid integer. Please try again.");
                }
            }

            validInput = false;
            while (!validInput) {
                System.out.println("At what price would you like to " + type + " " + ticker +" stock? ");
                res = scanner.nextLine();

                try {
                    double number = Double.parseDouble(res);
                    validInput = true;
                    price = number;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input: not a valid double. Please try again.");
                }
            }
            Order newOrder = new Order(ticker, price, quantity, type, System.nanoTime());
            try {
                objectOutputStream.writeObject(newOrder);
                objectOutputStream.flush();
            }catch (IOException e){
                System.out.println("Failure creating new order from client and writing to stream.");
                closeEverything(socket, objectOutputStream, objectInputStream);
            }
        } else{
            listenForUpdates();
        }
    }

    private boolean validTicker(String ticker){
        try {
            Ticker.valueOf(ticker.toUpperCase());
            return true;
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    private boolean validOrderType(String orderType){
        try {
            OrderType.valueOf(orderType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e){
            return false;
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

    public static void main(String[] args) {
        try {
            System.out.println("Starting client main method. Starting to create socket connection");
            Socket socket = new Socket("localhost", 8080);
            System.out.println("Sucesssfully made client socket connection");
            System.out.println("Attempting to create Client object");

            Client client = new Client(socket);
            System.out.println("Successfully created client object");
            System.out.println("Client object created");
            while(socket.isConnected()){
                client.createOrder();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
