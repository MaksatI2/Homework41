package server;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class EchoServer {
    private final int port;
    private final Map<String, Function<String, String>> commands = new HashMap<>();

    public static EchoServer bindToPort(int port) {
        return new EchoServer(port);
    }

    public EchoServer(int port) {
        this.port = port;
        commands.put("date", msg -> LocalDate.now().toString());
        commands.put("time", msg -> LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        commands.put("reverse", msg -> new StringBuilder(msg).reverse().toString());
        commands.put("upper", msg -> msg.toUpperCase());
        commands.put("bye", msg -> "BYE");
    }

    public void run() {
        System.out.println("Сервер запущен на порту " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Подключился клиент: " + socket.getInetAddress());

                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket) {
        try (Scanner scanner = new Scanner(socket.getInputStream());
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                String[] parts = input.split(" ", 2);
                String command = parts[0];
                String argument = parts.length > 1 ? parts[1] : "";

                if (commands.containsKey(command)) {
                    String response = commands.get(command).apply(argument);
                    writer.println(response);

                    if (command.equalsIgnoreCase("bye")) {
                        System.out.println("Клиент отключился: " + socket.getInetAddress());
                        break;
                    }
                } else {
                    writer.println(input);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
