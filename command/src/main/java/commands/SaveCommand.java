package commands;

import collection.CollectionManager;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.UserData;
import exceptions.CannotExecuteCommandException;

import java.io.PrintStream;
import java.util.concurrent.locks.Lock;

public class SaveCommand extends Command {
//    /**
//     * Поле, хранящее адрес файла, куда следует сохранить коллекцию.
//     */
//    private final String inputFile;
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private final CollectionManager collectionManager;

    /**
     * Конструктор класса.
     *
     * @param collectionManager Хранит ссылку на созданный в объекте Application объект CollectionManager.
     */
    public SaveCommand(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
//        this.inputFile = inputFile;
    }

    /**
     * Метод, сохраняющий коллекцию в указанном файле в формате XML. В случае некорректной работы высветится предупреждение.
     * @param invocationEnum режим, с которым должна быть исполнена данная команда.
     * @param printStream поток вывода.
     * @param arguments аргументы команды.
     */

    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData,
                        Lock locker) throws CannotExecuteCommandException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            throw new CannotExecuteCommandException("У данной команды для клиента нет выполнения.");
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            try {
                locker.lock();
                collectionManager.saveCollection();
            } finally {
                locker.unlock();
            }
            printStream.println("Коллекция " + collectionManager.getClass().getSimpleName() + " была сохранена.");
        }
    }

    /**
     * Метод, возвращающий описание команды.
     * @return Метод, возвращающий описание команды.
     */
    @Override
    public String getDescription() {
        return "сохраняет коллекцию в указанный файл";
    }
}