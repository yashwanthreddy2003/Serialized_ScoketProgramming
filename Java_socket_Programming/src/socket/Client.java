package socket;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 12345);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            // Read the server's first message (asking for name)
            Message serverMessage = (Message) in.readObject();
            System.out.println(serverMessage.getSender() + ": " + serverMessage.getContent());

            // Send the client's name
            System.out.print("Enter your name: ");
            String name = scanner.nextLine();
            out.writeObject(new Message(name, name));
            out.flush();

            // Start a thread to continuously listen for incoming messages
            Thread listenThread = new Thread(() -> {
                try {
                    while (true) {
                        Message receivedMessage = (Message) in.readObject();
                        System.out.println(receivedMessage.getSender() + ": " + receivedMessage.getContent());
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            listenThread.start();

            // Read user input and send messages
            while (true) {
                String userMessage = scanner.nextLine();
                if (userMessage.equalsIgnoreCase("exit")) {
                    break;
                }

                out.writeObject(new Message(name, userMessage));
                out.flush();
            }

            System.out.println("You left the chat.");
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
