package commands;

import collection.CollectionManager;
import commands.abstr.Command;
import commands.abstr.CommandContainer;
import commands.abstr.InvocationStatus;
import database.CollectionDatabaseHandler;
import database.UserData;
import exceptions.CannotExecuteCommandException;
import exceptions.RecursiveCallException;
import file.OrganizationReader;
import io.User;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;

public class ExecuteScriptCommand extends Command {

    private CollectionManager collectionManager;
    private CollectionDatabaseHandler cdh;
    private User user;
    private OrganizationReader organizationReader;
    private String scriptPath;
    private Script script;

    public ExecuteScriptCommand(User user, OrganizationReader organizationReader, Script script) {
        super("execute_script");
        this.user = user;
        this.organizationReader = organizationReader;
        this.script = script;
    }

    public ExecuteScriptCommand(CollectionManager collectionManager, CollectionDatabaseHandler cdh) {
        this.collectionManager = collectionManager;
        this.cdh = cdh;
    }

    public void execute(String[] arguments, InvocationStatus invocationEnum, PrintStream printStream, UserData userData,
                        Lock locker) throws CannotExecuteCommandException {
        if (invocationEnum.equals(InvocationStatus.CLIENT)) {
            result = new ArrayList<>();
            try {
                if (arguments.length == 1) {
                    scriptPath = arguments[0].trim();
                    if (script.scriptPaths.contains(scriptPath)) throw new RecursiveCallException(scriptPath);
                    else script.putScript(scriptPath);
                } else throw new IllegalArgumentException();

                File ioFile = new File(scriptPath);
                if (!ioFile.canWrite() || ioFile.isDirectory() || !ioFile.isFile()) throw new IOException();

                FileInputStream fileInputStream = new FileInputStream(scriptPath);
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                Scanner scanner = new Scanner(inputStreamReader);
                user = new User(scanner);
                CommandManager commandManager = new CommandManager(user, organizationReader, script);

                super.result.add(scriptPath);

                PrintStream nullStream = (new PrintStream(new OutputStream() {
                    public void write(int b) {
                        //DO NOTHING
                    }
                }));

                while (scanner.hasNext()) {
                    if (commandManager.executeClient(scanner.nextLine(), nullStream, userData)) {
                        super.result.add(commandManager.getLastCommandContainer());
                    }
                }
                script.removeScript(scriptPath);
                return;
            } catch (FileNotFoundException ex) {
                printStream.println("Файл скрипта не найден");
            } catch (NullPointerException ex) {
                printStream.println("Не выбран файл, из которого читать скрипт");
            } catch (IOException ex) {
                printStream.println("Доступ к файлу невозможен");
            } catch (IllegalArgumentException ex) {
                printStream.println("скрипт не передан в качестве аргумента команды, либо кол-во аргументов больше 1");
            } catch (RecursiveCallException ex) {
                printStream.println("Скрипт " + scriptPath + " уже существует (Рекурсивный вызов)");
            }
            script.removeScript(scriptPath);
            throw new CannotExecuteCommandException("Принудительное завершение работы команды execute_script");
        } else if (invocationEnum.equals(InvocationStatus.SERVER)) {
            printStream.println("Файл, который исполняется скриптом: " + this.getResult().get(0));
            Object[] arr = result.toArray();
            arr = Arrays.copyOfRange(arr, 1, arr.length);
            CommandContainer[] containerArray = Arrays.copyOf(arr, arr.length, CommandContainer[].class);

            CommandManager commandcommandManager = new CommandManager(collectionManager, cdh, locker);
            for (CommandContainer command : containerArray) {
                commandcommandManager.executeServer(command.getName(), command.getResult(), printStream, userData);
            }
        }
    }

    @Override
    public String getDescription() {
        return "выполняет команды, описанные в скрипте";
    }

    static class Script {

        private final ArrayList<String> scriptPaths = new ArrayList<>();

        public void putScript(String scriptPath) {
            scriptPaths.add(scriptPath);
        }

        public void removeScript(String scriptPath) {
            scriptPaths.remove(scriptPath);
        }
    }
}