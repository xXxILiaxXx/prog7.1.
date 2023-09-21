import collection.CollectionManager;
import collection.Organization;
import commands.CommandManager;
import database.CollectionDatabaseHandler;
import database.DatabaseConnection;
import database.UserDatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Application {
    /**
     * Корневой логгер для записи логов
     */
    private static final Logger rootLogger = LogManager.getRootLogger();

    private Connection dbConnection;

    public void start(int port) {
        this.createDatabaseConnection();
        UserDatabaseHandler udh = new UserDatabaseHandler(dbConnection);
        CollectionDatabaseHandler cdh = new CollectionDatabaseHandler(dbConnection);

        try {

            Organization[] organizations = cdh.loadInMemory();
            CollectionManager collectionManager = new CollectionManager(organizations);
            rootLogger.info("Коллекция была загружена из бд.");
            Lock locker = new ReentrantLock();
            CommandManager commandManager = new CommandManager(collectionManager, cdh, locker);
            rootLogger.info("Класс Application готов.");

            ServerConnection serverConnection = new ServerConnection();//здесь хранится datagramSocket сервера.
            serverConnection.createFromPort(port);

            RequestReader requestReader = new RequestReader(serverConnection.getServerSocket());
            ResponseSender responseSender = new ResponseSender(serverConnection.getServerSocket());
            CommandProcessor commandProcessor = new CommandProcessor(udh, cdh, commandManager);

            Server server = new Server(requestReader, responseSender, commandProcessor);
            new Thread(server).start();

        } catch (SQLException ex) {
            System.out.println("Ошибка при загрузке коллекции в память. Завершение работы сервера. " + ex);
            System.exit(-10);
        }
    }

//    /**
//     * Создается объект Selector.
//     * Создается ServerSocketChannel и устанавливается порт с помощью serverConnection.getServerPort().
//     * Канал работает в неблокирующем режиме.
//     */
//    private Selector selector;
//    private void setupSelector() throws IOException {
//        selector = Selector.open();
//        DatagramChannel datagramChannel = DatagramChannel.open();
//        datagramChannel.configureBlocking(false);
//    }


    private void createDatabaseConnection() {


        String jdbcURL = "jdbc:postgresql://localhost:5432/studs";
        String login = "s368236";
        String password = "lDJlX90dUXnvJJFR";
//        Scanner scanner = new Scanner(System.in);
//        try {
//            scanner = new Scanner(new FileReader("credentials.txt"));
//        } catch (FileNotFoundException ex) {
//            rootLogger.error("Не найден файл credentials.txt с данными для входа. Завершение работы.");
//            System.exit(-1);
//        }
//        try {
//            login = scanner.nextLine().trim();
//            password = scanner.nextLine().trim();
//        } catch (NoSuchElementException ex) {
//            rootLogger.error("Не найдены данные для входа. Завершение работы.");
//            System.exit(-1);
//        }
        DatabaseConnection databaseConnection = new DatabaseConnection(jdbcURL, login, password);

        try {
            dbConnection = databaseConnection.connectToDatabase();

            rootLogger.info("Соединение с бд установлено.");
        } catch (SQLException ex) {
            rootLogger.error("Соединение с бд не установлено. Завершение работы сервера " + ex);
            System.exit(-1);
        }
    }
}