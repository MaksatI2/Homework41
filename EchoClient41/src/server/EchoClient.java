package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EchoClient {
    private final int port;
    private final String host;

    private EchoClient(int port, String host) {
        this.port = port;
        this.host = host;
    }

    public static EchoClient connectTo(int port) {
        return new EchoClient(port, "localhost");
    }

    public void run() {
        System.out.println("Напиши 'bye' чтобы выйти\n");

        try (Socket socket = new Socket(host, port);
             Scanner scanner = new Scanner(System.in, "UTF-8");
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             Scanner serverInput = new Scanner(socket.getInputStream(), "UTF-8")) {

            while (true) {
                System.out.print("Вы: ");
                String message = scanner.nextLine();
                writer.println(message);

                if (serverInput.hasNextLine()) {
                    String response = serverInput.nextLine();
                    System.out.println("Сервер: " + response);
                }

                if ("bye".equalsIgnoreCase(message)) {
                    System.out.println("Отключение...");
                    break;
                }
            }

        } catch (NoSuchElementException ex) {
            System.out.println("Соединение потеряно!");

        } catch (IOException e) {
            System.out.printf("Не удалось подключиться к %s:%d!%n", host, port);
            e.printStackTrace();
        }
    }
}
