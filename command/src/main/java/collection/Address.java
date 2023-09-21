package collection;

import java.io.Serializable;

/**
 * Класс - координаты адреса объекта Organization
 */
public class Address implements Serializable {
    private String zipCode; // Длина строки не должна быть больше 19, Поле не может быть null
    private Location town; // Поле не может быть null

    /**
     * Конструктор класса адреса
     * @param zipCode - почтовый индекс
     * @param town - город
     */
    public Address(String zipCode, Location town) {
        this.zipCode = zipCode;
        this.town = town;
    }

    /**
     * Метод возвращает почтовый индекс
     * @return почтовый индекс
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Метод устанавливает почтовый индекс
     * @param zipCode - почтовый индекс
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Метод возвращает город
     * @return город
     */
    public Location getTown() {
        return town;
    }

    /**
     * Метод устанавливает город
     * @param town - город
     */
    public void setTown(Location town) {
        this.town = town;
    }
}