package collection;

import java.io.Serializable;

/**
 * Класс - координаты объекта Organization
 */
public class Coordinates implements Serializable {
    private float x; // Поле не может быть null
    private int y;

    /**
     * Конструктор класса координат
     * @param x - координата x
     * @param y - координата y
     */
    public Coordinates(float x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Метод возвращает координату X
     *
     * @return координату X
     */
    public float getX() {
        return x;
    }

    /**
     * Метод устанавливает координату X
     * @param x - координата X
     */
    public void setX(Float x) {
        this.x = x;
    }

    /**
     * Метод возвращает координату Y
     * @return координату Y
     */
    public int getY() {
        return y;
    }

    /**
     * Метод устанавливает координату Y
     * @param y - координата Y
     */
    public void setY(int y) {
        this.y = y;
    }
}