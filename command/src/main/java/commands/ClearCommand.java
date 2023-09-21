package commands;

import collection.CollectionManager;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;

/**
 * Команда, очищающая коллекцию.
 */
public class ClearCommand extends Command {

    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;

    private CollectionDatabaseHandler cdh;

    /**
     * Конструктор класса
     */
    public ClearCommand() {
        super("clear");
    }

    /**
     * Конструктор класа, предназначенный для сервера.
     * @param collectionManager менеджер коллекции
     */
    public ClearCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.collectionManager = collectionManager;
        this.cdh = cdh;
    }

    /**
     * Метод, исполняющий команду. Выводит сообщение когда коллекция очищена.
     * @param arguments      аргументы команды.
     * @param invocationEnum режим работы команды
     * @param printStream поток, куда следует выводить результат команды
     */
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData,
                        Lock locker) throws CannotExecuteCommandException, SQLException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            if (arguments.length > 0) {
                throw new CannotExecuteCommandException("У данной команды нет аргументов.");
            }
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            try {
                locker.lock();

                cdh.deleteAllOwned(userData);
                collectionManager.clear();
                printStream.println("Элементы коллекции, принадлежащие пользователю " + userData.getLogin()
                        + " были удалены.");
            } finally {
                locker.unlock();
            }
        }
    }

    /**
     * Метод, возвращающий описание команды.
     * @return Описание команды
     */
    @Override
    public String getDescription() {
        return "очищает все элементы коллекции";
    }
}