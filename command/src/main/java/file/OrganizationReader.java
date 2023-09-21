package file;

import collection.*;
import io.User;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.Date;

/**
 * Класс, предназначенный для чтения данных для объекта Organization.
 */
public class OrganizationReader {
    private User user;
    private CollectionManager collectionManager = new CollectionManager();

    public OrganizationReader(User user) {
        this.user = user;
    }

    public OrganizationReader(String user) {
    }

    public Organization read() {
        Instant time = Instant.now();
        return new Organization(
                id(),
                readName(),
                readCoordinates(),
                readCreationDate(),
                readAnnualTurnover(),
                readEmployeesCount(),
                readType(),
                readPostalAddress()
        );
    }

    public int id() {
        return collectionManager.generateNewId();
    }

    public String readName() {
        String name;

        while (true) {
            user.printCommand("Введите имя (не null):");
            name = user.readLine().trim();
            if (!name.isEmpty()) {
                return name;
            } else {
                user.printError("Значение поля не может быть пустым или null.");
            }
        }
    }

    public Float readCoordinateX() {
        Float x;
        while (true) {
            try {
                user.printCommand("Введите координату X (с плавающей запятой, не null):");
                x = Float.parseFloat(user.readLine().trim());
                if (x != null) {
                    return x;
                } else {
                    user.printError("Координата X не может быть null.");
                }
            } catch (NumberFormatException e) {
                user.printError("Введите число с плавающей запятой.");
            }
        }
    }

    public int readCoordinateY() {
        int y;
        while (true) {
            try {
                user.printCommand("Введите координату Y (целое число):");
                y = Integer.parseInt(user.readLine().trim());
                return y;
            } catch (NumberFormatException e) {
                user.printError("Введите целое число.");
            }
        }
    }

    public Coordinates readCoordinates() {
        return new Coordinates(readCoordinateX(), readCoordinateY());
    }

    public Date readCreationDate() {
        while (true) {
            try {
                return new Date();
            } catch (DateTimeException e) {
                user.printError("Проблема со временем.");
            }
        }
    }

    public long readAnnualTurnover() {
        long annualTurnover;
        while (true) {
            try {
                user.printCommand("Введите годовой оборот (целое число):");
                String input = user.readLine().trim();
                annualTurnover = Long.parseLong(input);
                return annualTurnover;
            } catch (NumberFormatException e) {
                user.printError("Введите целое число.");
            }
        }
    }

    public Long readEmployeesCount() {
        Long count;
        while (true) {
            try {
                user.printCommand("Введите количество сотрудников (целое число, не null, больше 0):");
                String input = user.readLine().trim();
                count = Long.parseLong(input);
                if (count != null && count > 0) {
                    return count;
                } else {
                    user.printError("Количество сотрудников должно быть больше 0.");
                }
            } catch (NumberFormatException e) {
                user.printError("Введите целое число.");
            }
        }
    }

    public OrganizationType readType() {
        String input;
        while (true) {
            try {
                user.printCommand("Выберите тип организации из списка (коммерческая, государственная, акционерное общество):");
                input = user.readLine().trim().toLowerCase();
                if (input.equals("коммерческая")) {
                    return OrganizationType.COMMERCIAL;
                } else if (input.equals("государственная")) {
                    return OrganizationType.PUBLIC;
                } else if (input.equals("акционерное общество")) {
                    return OrganizationType.OPEN_JOINT_STOCK_COMPANY;
                } else {
                    user.printError("Некорректный тип организации. Пожалуйста, выберите из предложенных вариантов.");
                }
            } catch (Exception e) {
                user.printError("Произошла ошибка при чтении типа организации.");
            }
        }
    }

    public Address readPostalAddress() {
        String zipCode = null;
        Location town = null;

        try {
            user.printCommand("Введите почтовый индекс:");
            zipCode = user.readLine().trim();

            int x, z;
            Float y;

            user.printCommand("Введите координату X:");
            x = Integer.parseInt(user.readLine().trim());

            user.printCommand("Введите координату Y:");
            y = Float.parseFloat(user.readLine().trim());

            user.printCommand("Введите координату Z:");
            z = Integer.parseInt(user.readLine().trim());

            town = new Location(x, y, z);
        } catch (NumberFormatException e) {
            user.printError("Ошибка при чтении координат.");
        }

        return new Address(zipCode, town);
    }
}










