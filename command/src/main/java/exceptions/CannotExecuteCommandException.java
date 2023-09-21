package exceptions;

/**
 * Исключение, выбрасываемое в случае, если команда не может быть исполнена в поданными ей аргументами.
 */
public class CannotExecuteCommandException extends Exception {

    public CannotExecuteCommandException(String mes) {
        super(mes);
    }
}