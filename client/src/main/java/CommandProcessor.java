
import commands.CommandManager;
import database.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandProcessor {

    private static final Logger rootLogger = LogManager.getRootLogger();

    private final CommandManager commandManager;

    /**
     * Конструктор класса
     * @param commandManager объект CommandManager, который будет использоваться для выполнения команд.
     */
    public CommandProcessor(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Метод для выполнения команд
     * @param firstCommandLine строка с первой командой, которую нужно выполнить.
     * @return true, если команда выполнена успешно, иначе false.
     */
    public boolean executeCommand(String firstCommandLine, UserData userData) {

        if (!commandManager.executeClient(firstCommandLine, System.out, userData)) {
            rootLogger.warn("Команда не была исполнена");
            return false;
        } else {
            // Проверяем, была ли последняя команда "help"
            // Если "help", то команда не отправляет на сервер
            return !commandManager.getLastCommandContainer().getName().equals("help");
        }
    }
}