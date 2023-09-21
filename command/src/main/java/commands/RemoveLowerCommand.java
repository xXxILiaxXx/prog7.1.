package commands;

import collection.CollectionManager;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.UserData;
import exceptions.CannotExecuteCommandException;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class RemoveLowerCommand extends Command {

    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;

    public RemoveLowerCommand() {
        super("remove_by_id");
    }

    public RemoveLowerCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData,
                        Lock locker) throws CannotExecuteCommandException {
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
                collectionManager.removeLower(id);
            } finally {
                locker.unlock();
            }
            printStream.println("Элементы с id ниже " + id + " были удалены.");
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
        return "удаляет элемент с id меньше указанного";
    }
}