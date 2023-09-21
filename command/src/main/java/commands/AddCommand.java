package commands;

import collection.CollectionManager;
import collection.Organization;
import commands.abstr.Command;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;
import file.OrganizationReader;

import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;

public class AddCommand extends Command {

    private CollectionManager collectionManager;

    private CollectionDatabaseHandler cdh;

    private OrganizationReader organizationReader;

    public AddCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.collectionManager = collectionManager;
        this.cdh = cdh;
    }

    public AddCommand(OrganizationReader organizationReader) {
        super("add");
        this.organizationReader = organizationReader;
    }

    @Override
    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData,
                        Lock locker) throws CannotExecuteCommandException, SQLException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            if (arguments.length > 0) {
                throw new CannotExecuteCommandException("У этой команды нет аргументов");
            }
            Organization organization = organizationReader.read();
            organization.setOwner(userData.getLogin());
            super.result.add(organization);
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            try {
                locker.lock();

                Organization organization = (Organization) this.getResult().get(0);
                if (!cdh.isAnyRowById(organization.getId())) {
                    cdh.insertRow(organization);
                    collectionManager.add(organization);
                    printStream.println("Элемент добавлен в коллекцию.");
                } else {
                    printStream.println("Элемент с указанным id уже существует.");
                }
            } finally {
                locker.unlock();
            }
        }
    }

    @Override
    public String getDescription() {
        return "добавляет новый элемент в коллекцию";
    }
}
