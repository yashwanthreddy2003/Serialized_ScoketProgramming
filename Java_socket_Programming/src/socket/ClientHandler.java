package socket;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName;
    private Set<ClientHandler> clients;

    public ClientHandler(Socket socket, Set<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Enter your name: ");
            clientName = in.readLine();
            System.out.println(clientName + " joined the chat.");
            broadcast(clientName + " joined the chat!", this);

            String message;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
                System.out.println(clientName + ": " + message);

                broadcast(clientName + ": " + message, this);
            }
            System.out.println(clientName + " left the chat.");
            broadcast(clientName + " left the chat.", this);
            clients.remove(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.out.println(message);
            }
        }
    }
}
