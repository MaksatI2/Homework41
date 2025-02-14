package server;

import javax.sound.sampled.ReverbType;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoServer {

    private final int port;

    private EchoServer(int port) {
        this.port = port;
    }


    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public void run() {

        try (var server = new ServerSocket(port)) {
            while (true) {
                try (var clientSocket = server.accept()) {
                    handle(clientSocket);
                }
            }

        } catch (IOException e) {
            var formatMsg = "Вероятнее всего порт %s занят.%n";
            System.out.printf(formatMsg, port);
            e.printStackTrace();

        }

    }

    public static String reverseString(String str) {
        return new StringBuilder(str).reverse().toString();
    }

    private void handle(Socket socket) throws IOException {
        try (Scanner scanner = new Scanner(socket.getInputStream(), "UTF-8");
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            while (scanner.hasNextLine()) {
                String message = scanner.nextLine().strip();
                System.out.printf("Получено: %s%n", message);

                writer.println("Отправлено: " + reverseString(message));

                if (message.equalsIgnoreCase("bye")) {
                    System.out.println("Клиент отключился.");
                    break;
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println("Клиент неожиданно отключился.");
        }
    }



}
