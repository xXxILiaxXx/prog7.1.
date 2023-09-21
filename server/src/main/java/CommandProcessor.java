import commands.CommandManager;
import commands.abstr.CommandContainer;
import database.CollectionDatabaseHandler;
import database.UserData;
import database.UserDatabaseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintStream;

/**
 * Передает команду в CommandManager для выполнения команды
 */
public class CommandProcessor {

    private final CommandManager commandManager;

    /**
     * Корневой логгер для записи логов
     */
    private static final Logger rootLogger = LogManager.getRootLogger();

    private final UserDatabaseHandler udh;
    private final CollectionDatabaseHandler cdh;

    /**
     * Конструктор класса
     * @param commandManager менеджер команд
     */
    public CommandProcessor(UserDatabaseHandler udh, CollectionDatabaseHandler cdh, CommandManager commandManager) {
        this.commandManager = commandManager;
        this.udh = udh;
        this.cdh = cdh;
    }

    /**
     * Выполняет команду и записывает результат выполнения в указанный PrintStream
     * @param command     контейнер команды
     * @param printStream вывод результатов команды
     */
    public void executeCommand(CommandContainer command, PrintStream printStream, UserData userData) {

        if (commandManager.executeServer(command.getName(), command.getResult(), printStream, userData)) {
            rootLogger.info("Была исполнена команда " + command.getName());
        } else {
            rootLogger.info("Не была исполнена команда " + command.getName());
        }
    }

    public void putCommandArguments(PrintStream printStream, UserData userData) {

    }

    public UserDatabaseHandler getUdh() {
        return udh;
    }

    public CollectionDatabaseHandler getCdh() {
        return cdh;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}