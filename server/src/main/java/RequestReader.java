import commands.abstr.CommandContainer;
import database.UserData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.Callable;

/**
 * Чтение команд из DatagramSocket
 */
public class RequestReader implements Callable<UserData> {
    /**
     * Корневой логгер для записи логов
     */
    private static final Logger rootLogger = LogManager.getRootLogger();
    /**
     * Сокет сервера
     */
    private final DatagramSocket serverSocket;
    private final DatagramPacket dp;
    private byte[] byteUPD = new byte[4096];

    private InetAddress senderAddress;
    private int senderPort;


    private CommandContainer commandContainer;

    /**
     * Конструктор класса
     * @param serverSocket - сокет сервера
     */
    public RequestReader(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
        dp = new DatagramPacket(byteUPD, byteUPD.length);
    }

    @Override
    public UserData call() throws Exception {
        serverSocket.receive(dp);
        byteUPD = dp.getData();


        String str = new String(byteUPD);
        str = str.replace("\0", "");
        byte[] byteArr = str.getBytes(StandardCharsets.UTF_8);

        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.getDecoder().decode(byteArr));
        ObjectInputStream ois = new ObjectInputStream(bais);

        rootLogger.info("Получен пакет с командой от " + dp.getAddress().getHostAddress() + " " + dp.getPort());

        UserData userData = (UserData) ois.readObject();
        userData.setInetAddress(dp.getAddress());
        userData.setPort(dp.getPort());
        return userData;
    }
}