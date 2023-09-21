package file;

import collection.Organization;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.TreeSet;


/**
 * Класс для работы с файлами (чтение/запись)
 */
public class FileManager {
    /**
     * Парсер формата json
     */
    private Gson gson = new GsonBuilder()
            .setDateFormat("sss")
            .registerTypeAdapter(Organization.class, new CustomConverter())
            .create();

    /**
     * Метод записывает объекты из файла .json в коллекцию
     *
     * @param file - путь до файла
     * @return коллекция объектов
     */
    public Organization[] parseToCollection(String file) {
        gson = new GsonBuilder()
                .setDateFormat("SSS")
                .registerTypeAdapter(Organization.class, new CustomConverter())
                .create();
        TreeSet<Organization> organizationTreeSet = null;
        try {
            String json = Files.readString(Paths.get(file));
            Organization[] organizations = gson.fromJson(json, Organization[].class);
            organizationTreeSet = new TreeSet<>();
            for (Organization organization : organizations) {
                organizationTreeSet.add(organization);
            }
        } catch (JsonParseException e) {
            System.err.println("Ошибка парсинга JSON: " + e.getMessage());
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Ошибка с файлом " + e);
            throw new RuntimeException(e);
        }

        if (organizationTreeSet == null) {
            return new Organization[0];
        }

        return organizationTreeSet.toArray(new Organization[0]);
    }

    /**
     * Запись коллекции в файл
     *
     * @param collection - коллекция для записи в файл
     */
    public void writeCollection(Collection<Organization> collection) {

        try (OutputStream outputStream = new FileOutputStream("/Users/iliailiev/IdeaProjects/prog5.1/src/main/java/org/example/FileWork/test.json");
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream)) {
            String json = gson.toJson(collection);
            byte[] bytes = json.getBytes();
            bufferedOutputStream.write(bytes);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}