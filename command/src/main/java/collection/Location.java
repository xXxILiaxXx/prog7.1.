package collection;

import java.io.Serializable;

/**
 * Класс - локации объекта Organization
 */
public class Location implements Serializable {
    private int x;
    private Float y; // Поле не может быть null
    private long z;

    /**
     * Конструктор класса Location
     *
     * @param x Координата x
     * @param y Координата y (не может быть null)
     * @param z Координата z
     */
    public Location(int x, Float y, long z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Метод возвращает координату x
     *
     * @return Координата x
     */
    public int getX() {
        return x;
    }

    /**
     * Метод устанавливает координату x
     *
     * @param x Координата x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Метод возвращает координату y
     *
     * @return Координата y
     */
    public Float getY() {
        return y;
    }

    /**
     * Метод устанавливает координату y
     *
     * @param y Координата y (не может быть null)
     */
    public void setY(Float y) {
        this.y = y;
    }

    /**
     * Метод возвращает координату z
     *
     * @return Координата z
     */
    public long getZ() {
        return z;
    }

    /**
     * Метод устанавливает координату z
     *
     * @param z Координата z
     */
    public void setZ(long z) {
        this.z = z;
    }
}

