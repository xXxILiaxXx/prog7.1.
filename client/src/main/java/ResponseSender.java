
import database.UserData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Base64;

/**
 * Класс, отвечающий за отправку объектов типа CommandContainer по сети через UDP-сокет.
 */
public class ResponseSender {

    private final DatagramChannel clientChannel;

    /**
     * Конструктор класса
     * @param clientChannel объект DatagramChannel, через который будет отправляться сообщение.
     */
    public ResponseSender(DatagramChannel clientChannel) {
        this.clientChannel = clientChannel;
    }

    /**
     * Отправляет объект CommandContainer по сети на указанный адрес.
     * @param inetSocketAddress адрес и порт, на который будет отправлен объект.
     * @throws IOException если произошла ошибка при отправке данных.
     */
    public void sendUserData(UserData userData, InetSocketAddress inetSocketAddress) throws IOException {
        // создание буфера для данных
        ByteBuffer byteBuffer = ByteBuffer.allocate(4096);
        byteBuffer.clear();

        // Создание потока для сериализации объекта CommandContainer в массив байтов
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        // Сериализация объекта CommandContainer и запись в поток baos
        oos.writeObject(userData);

        // Получение массива байтов из потока baos и кодирование в Base64
        byte[] encodedBytes = Base64.getEncoder().withoutPadding().encode(baos.toByteArray());

        // Запись массива байтов в буфер
        byteBuffer.put(encodedBytes);

        // Подготовка буфера для чтения
        byteBuffer.flip();

        // Отправка буфера с данными по сети на указанный адрес
        clientChannel.send(byteBuffer, inetSocketAddress);
    }
}