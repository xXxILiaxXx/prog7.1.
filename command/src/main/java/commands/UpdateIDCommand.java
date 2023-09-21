package commands;

import collection.CollectionManager;
import collection.Organization;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;
import file.OrganizationReader;
import io.User;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;

public class UpdateIDCommand extends Command {
    /**
     * Поле, хранящее ссылку на объект класса CollectionManager.
     */
    private CollectionManager collectionManager;

    private CollectionDatabaseHandler cdh;
    /**
     * Поле, хранящее ссылку на объект класса UserIO.
     */
    private User user;

    private OrganizationReader organizationReader;

    public UpdateIDCommand() {
        super("update_by_id");
    }

    public UpdateIDCommand(CollectionManager collectionManager, OrganizationReader organizationReader) {
        this.collectionManager = collectionManager;
        this.organizationReader = organizationReader;
    }

    /**
     * Метод, исполняющий команду. При вызове изменяется указанной элемент коллекции до тех пор, пока не будет передана пустая строка. В случае некорректного ввода высветится ошибка.
     * @param invocationEnum режим, с которым должна быть исполнена данная команда.
     * @param printStream поток вывода.
     * @param arguments аргументы команды.
     */
    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream,
                        UserData userData, Lock locker) throws CannotExecuteCommandException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            if (arguments.length != 1) {
                throw new CannotExecuteCommandException("Количество аргументов данной команды должно равняться 1.");
            }
            result.add(Integer.parseInt(arguments[0]));
            Organization organization = organizationReader.read();
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) { //id - result[0], arguments - result[1] name;value
            Integer id = (Integer) this.getResult().get(0);

            Organization organization = collectionManager.getByID(id);
            try {

                if (organization == null) throw new NoSuchElementException();
                collectionManager.removeById(id);
                collectionManager.add(organizationReader.read());

                printStream.println("Указанные поля были заменены.");
            } catch (NoSuchElementException e) {
                printStream.println("Элемента с указанным id не существует");
            } catch (Exception e) {
                printStream.println(e);
            }
        }
    }


    /**
     * Метод, возвращающий описание команды.
     *
     * @return Метод, возвращающий описание команды.
     * @see Command
     */
    @Override
    public String getDescription() {
        return "изменяет указанное поле выбранного по id элемента коллекции";
    }
}