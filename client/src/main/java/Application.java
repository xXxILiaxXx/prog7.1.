
import commands.CommandManager;
import database.UserData;
import io.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс, через который производится запуск данного приложения.
 */
public class Application {
    /**
     * Корневой логгер для записи логов
     */
    private static final Logger rootLogger = LogManager.getRootLogger();
    /**
     * Хранит ссылку на объект, производящий чтение и вывод команд
     */
    private static User user;
    /**
     * Объект, управляющий выполнением команд
     */
    private static CommandManager commandManager;

    private InetSocketAddress inetSocketAddress;

    private UserData userData;

    private InetAddress inetAddress;

    private final int port;

    /**
     * Конструктор класса Application
     *
     * @param port порт для подключения
     */
    public Application(Integer port) {

        try {
            inetSocketAddress = new InetSocketAddress("localhost", port);
        } catch (IllegalArgumentException e) {
            rootLogger.error("Указано недопустимое значение порта. Значение должно быть в интервале 0-65335.");
            System.exit(-1);
        }

        this.port = port;

        user = new User();
        commandManager = new CommandManager(user);
        rootLogger.info("Конструктор класса Application был загружен.");

    }

    /**
     * Метод, выполняющий запуск программы и управляющий её работой.
     */
    public void start() {

        try {
            InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", port);

            // Создание объектов для работы с клиентским подключением
            ClientConnection clientConnection = new ClientConnection();
            clientConnection.connect(inetSocketAddress);


            ResponseSender responseSender = new ResponseSender(clientConnection.getClientChannel());
            RequestReader requestReader = new RequestReader(clientConnection.getClientChannel());
            CommandProcessor commandProcessor = new CommandProcessor(commandManager);

            int count = 5;
            while (count > 0) {
                boolean isConnected = authenticate(responseSender, requestReader);
                userData.setIsConnected(isConnected);
                if (userData.getIsConnected()) {
                    rootLogger.info("Авторизация пользователя прошла успешно.");
                } else {
                    rootLogger.warn("Неверный логин или пароль. Повторите ввод. Осталось попыток:" + --count);
                    continue;
                }
                rootLogger.info("Клиент готов к чтению команд.");
                this.cycle(responseSender, requestReader, commandProcessor);
            }
            if (count == 0) {
                rootLogger.error("Попытки исчерпаны. Завершение работы.");
                System.exit(-1);
            }
            this.cycle(responseSender, requestReader, commandProcessor);

        } catch (NoSuchElementException ex) {
            rootLogger.error("\nАварийное завершение работы.");
        } catch (PortUnreachableException ex) {
            rootLogger.error("Ошибка подключения сокета к порту, или сокет не может быть открыт. port:"
                    + inetSocketAddress.getPort());
        } catch (IOException ex) {
            rootLogger.error("Ошибка ввода-вывода");
            ex.printStackTrace();
        } catch (Exception ex) {
            rootLogger.error("Неизвестная ошибка. Следует починить." + ex);
            ex.printStackTrace();
        }
    }

    private boolean authenticate(ResponseSender responseSender, RequestReader requestReader) throws IOException, InterruptedException, NoSuchElementException {
        Scanner scanner = new Scanner(System.in);
        Boolean isNewUser = null;
        System.out.println("Выберите действие:\n1) Авторизоваться\n2) Создать нового пользователя");

        while (isNewUser == null) {
            try {
                int result = Integer.parseInt(scanner.nextLine().trim());

                switch (result) {
                    case 1:
                        isNewUser = false;
                        break;
                    case 2:
                        isNewUser = true;
                        break;
                    default:
                        rootLogger.info("Действие не распознано. Выберите способ еще раз.");
                        break;
                }
            } catch (NumberFormatException ex) {
                rootLogger.warn("Введите целое число.");
            }
        }
        userData = new UserData(isNewUser);
        String login;
        String password = null;

        System.out.println("Введите логин:");
        login = scanner.next().trim();

        System.out.println("Введите пароль:");
        Console console = System.console();

        while (password == null || password.equals("")) {
            if (console != null) {
                char[] arrPass = console.readPassword();
                if (arrPass == null) {
                    rootLogger.warn("Пароль не может быть пустым. Повторите попытку");
                } else password = String.valueOf(arrPass);
            } else {
                password = scanner.next().trim();
            }
        }

        DataEncryptor dataEncryptor = new DataEncryptor();
        userData.setLogin(login);
        userData.setPassword(dataEncryptor.encryptStringSHA256(password));

        responseSender.sendUserData(userData, inetSocketAddress);

        ByteBuffer byteBuffer = requestReader.receiveBuffer();

        ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer.array(), 0, byteBuffer.limit());
        ObjectInputStream ois = new ObjectInputStream(bais);

        return ois.readBoolean();
    }

    public void cycle(ResponseSender responseSender, RequestReader requestReader, CommandProcessor commandProcessor)
            throws IOException, InterruptedException {
        boolean isConnected = true;
        boolean isNeedInput = true;
        boolean isCommandAcceptable = false;

        String line;

        while (isConnected) {
            if (isNeedInput) {
                System.out.println("Введите название команды:");
                user.PrintPreamble();
                line = user.readLine();
                isCommandAcceptable =  commandProcessor.executeCommand(line, userData);
            }
            try {
                if (isCommandAcceptable) {
                    // Отправка команды на сервер
                    userData.setCommandContainer(commandManager.getLastCommandContainer());
                    responseSender.sendUserData(userData, inetSocketAddress);
                    rootLogger.info("Данные были отправлены.");

                    // Получение ответа от сервера
                    ByteBuffer byteBuffer = requestReader.receiveBuffer();
                    rootLogger.info("Данные были получены.");
                    System.out.println(new String(byteBuffer.array()).trim() + "\n");

                    isNeedInput = true;
                }
            } catch (PortUnreachableException | SocketTimeoutException ex) {
                if (ex instanceof PortUnreachableException) {
                    rootLogger.warn("Порт " + port + " не доступен. Повторить отправку команды? y/n");
                } else {
                    rootLogger.warn("Сервер не отвечает. Повторить отправку команды? y/n");
                }
                String result = user.readLine().trim().toLowerCase(Locale.ROOT).split("\\s+")[0];
                if (result.equals("n")) {
                    rootLogger.info("Завершение работы клиента");
                    isConnected = false;
                } else {
                    isNeedInput = false;
                }
            }
        }
    }
}