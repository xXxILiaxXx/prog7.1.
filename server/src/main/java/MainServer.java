import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Точка запуска программы сервера
 */
public class MainServer {
    /**
     * Корневой логгер для записи логов
     */
    private static final Logger rootLogger = LogManager.getRootLogger();

    /**
     * Метод, запускающий серверное приложение
     * @param args Аргументы командной строки
     */
    public static void main(String[] args) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Application application = new Application();

        // проверка на наличие аргументов командной строки
        if (args.length > 0) {
            try {
                int port = Integer.parseInt(args[0]);
                if (port <= 0) {
                    throw new IllegalArgumentException();
                } else {
                    System.out.println("Порт установлен: " + port);
                    application.start(port);
                }
            } catch (NumberFormatException ex) {
                rootLogger.error("Введенный порт должен быть числом. Завершение работы сервера.");
            } catch (IllegalArgumentException ex) {
                rootLogger.error("Порт должен быть больше нуля.");
            }
        } else {
            // Если аргументы командной строки отсутствуют, запрашиваем порт у пользователя
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите порт: ");
            String port = scanner.nextLine();
            System.out.println("Порт установлен: " + port);
            application.start(Integer.parseInt(port));
//            rootLogger.error("Должен быть указан порт, к которому подключаться");
        }
    }
}