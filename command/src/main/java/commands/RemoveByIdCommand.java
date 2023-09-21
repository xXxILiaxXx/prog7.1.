package commands;

import collection.CollectionManager;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class RemoveByIdCommand extends Command {
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;

    private CollectionDatabaseHandler cdh;

    /**
     * Конструктор класса.
     */
    public RemoveByIdCommand() {
        super("remove_by_id");
    }

    public RemoveByIdCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.collectionManager = collectionManager;
        this.cdh = cdh;
    }

    /**
     * Метод, исполняющий команду.
     * В случае успешного выполнения удалится элемент коллекции, значение уникального
     * идентификатор которого соответствует переданному в качестве аргумента, иначе предупреждение.
     * @param invocationEnum режим, с которым должна быть исполнена данная команда.
     * @param printStream поток вывода.
     * @param arguments аргументы команды.
     */
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream,
                        UserData userData, Lock locker) throws CannotExecuteCommandException, SQLException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            if (arguments.length != 1) {
                throw new CannotExecuteCommandException("Введены неверные аргументы команды");
            }
            result.add(Integer.parseInt(arguments[0]));
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            Integer id = (Integer) this.getResult().get(0);
            try {
                locker.lock();

                if (cdh.isOwner(id, userData)) {
                    cdh.deleteRowById(id);
                    collectionManager.removeById(id);
                    printStream.println("Элемент с id = " + id + " был удален.");
                } else {
                    printStream.println("Элемента с указанным id не существует.");
                }
            } finally {
                locker.unlock();
            }
        }
    }

    /**
     * Метод, возвращающий описание команды.
     *
     * @return Возвращает описание команды.
     * @see Command
     */
    @Override
    public String getDescription() {
        return "удаляет элемент с указанным ключом";
    }
}