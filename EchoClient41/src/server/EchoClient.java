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
        System.out.printf("напиши 'bye' чтобы выйти%n%n%n");

        try (var socket = new Socket(host, port)) {

            var scanner = new Scanner(System.in, "UTF-8");
            var output = socket.getOutputStream();
            var writer = new PrintWriter(output);

            try (scanner; writer) {
                while (true) {

                    String message = scanner.nextLine();
                    writer.write(message);
                    writer.write(System.lineSeparator());
                    writer.flush();

                    if ("bye".equals(message.toLowerCase())) {
                        return;
                    }
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.printf("Соединение потеряно!%n");
        } catch (IOException e) {
            var msg = "Не удалось подключиться к %s:%s!%n";
            System.out.printf(msg, host, port);
            e.printStackTrace();

        }

    }
}
