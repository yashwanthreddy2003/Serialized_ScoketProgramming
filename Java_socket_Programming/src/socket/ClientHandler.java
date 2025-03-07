package socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String clientName;
    private Set<ClientHandler> clients;

    public ClientHandler(Socket socket, Set<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            out.writeObject(new Message("Server", "Enter your name:"));
            out.flush();

            clientName = ((Message) in.readObject()).getContent();
            System.out.println(clientName + " joined the chat.");
            broadcast(new Message("Server", clientName + " joined the chat."));

            Message message;
            while ((message = (Message) in.readObject()) != null) {
                if (message.getContent().equalsIgnoreCase("exit")) {
                    break;
                }

                System.out.println(clientName + ": " + message.getContent());
                broadcast(new Message(clientName, message.getContent()));
            }

            System.out.println(clientName + " left the chat.");
            broadcast(new Message("Server", clientName + " left the chat."));
            clients.remove(this);
            socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(Message message) {
        for (ClientHandler client : clients) {
            try {
                client.out.writeObject(message);
                client.out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
