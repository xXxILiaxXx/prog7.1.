package collection;

import file.FileManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

/**
 * Менеджер коллекции объектов типа Organization
 */
public class CollectionManager {
    /**
     * Коллекция Organization
     */
    TreeSet<Organization> organizationTreeSet;
    /**
     * Время инициализации коллекции
     */
    LocalDateTime initializationDate;

    /**
     * Конструктор, который создает объект менеджера
     */
    public CollectionManager(Organization[] organizations) {
        organizationTreeSet = new TreeSet<>();
        initializationDate = LocalDateTime.now();
    }

    public CollectionManager() {

    }

    /**
     * Метод, который выводит информацию о коллекции
     *
     * @return
     */
    public boolean info() {
        System.out.println("Тип коллекции: " + organizationTreeSet.getClass().getName());
        System.out.println("Тип элементов коллекции: " + Organization.class.getName());
        System.out.println("Дата инициализации коллекции: " + initializationDate);
        System.out.println("Количество элементов в коллекции: " + organizationTreeSet.size());
        return false;
    }

    /**
     * Метод, который выводит информацию об элементах коллекции
     *
     * @return
     */
    public String show() {
        StringBuilder sb = new StringBuilder();
        if (organizationTreeSet.isEmpty()) {
            System.out.println("В коллекции нет элементов");
        } else {
            for (Organization organization : organizationTreeSet) {
                sb.append(organization);
            }
        }
        return sb.toString();
    }

    /**
     * Метод, удаляющий все элементы коллекции
     */
    public void clear() {
        organizationTreeSet.clear();
    }

    /**
     * Метод, который добавляет объект класса Organization в коллекцию
     *
     * @param organization элемент, который добавляется в коллекцию
     */
    public void add(Organization organization) {
        organizationTreeSet.add(organization);
    }

    /**
     * Счетчик id
     */
    private static int idCounter = 0;

    /**
     * Метод генерирует уникальный id
     *
     * @return уникальный id и прибавляет счетчик
     */
    public static synchronized int generateNewId() {
        return ++idCounter;
    }

    /**
     * Метод, удаляющий все объекты с ID, меньшим заданного
     *
     * @param id ID, меньше которого все объекты удаляются
     */
    public void removeLower(Integer id) {
        Iterator<Organization> iterator = organizationTreeSet.iterator();
        while (iterator.hasNext()) {
            Organization organization = iterator.next();
            if (organization.getId() < id) {
                iterator.remove();
            }
        }
    }

    /**
     * Метод, удаляющий объект с заданным ID
     *
     * @param id ID объекта, который нужно удалить
     */
    public void removeById(Integer id) {
        Iterator<Organization> iterator = organizationTreeSet.iterator();
        boolean removed = false;
        while (iterator.hasNext()) {
            Organization organization = iterator.next();
            if (organization.getId().equals(id)) {
                iterator.remove();
                removed = true;
                break;
            }
        }
        if (!removed) {
            System.out.println("Элемент с ID " + id + " не найден");
        }
    }

    /**
     * Метод, возвращающий организацию по заданному ID
     *
     * @param id ID организации
     * @return объект Organization с заданным ID или null, если организация не найдена
     */
    public Organization getById(Integer id) {
        for (Organization organization : organizationTreeSet) {
            if (organization.getId().equals(id)) {
                return organization;
            }
        }
        return null;
    }

    /**
     * Метод, возвращающий количество организаций с заданным типом
     *
     * @param type тип организации
     * @return количество организаций с заданным типом
     */
    public int countByType(OrganizationType type) {
        int count = 0;
        for (Organization organization : organizationTreeSet) {
            if (organization.getType() == type) {
                count++;
            }
        }
        return count;
    }

    /**
     * Метод, возвращающий организацию с наименьшим годовым оборотом
     *
     * @return объект Organization с наименьшим годовым оборотом или null, если коллекция пуста
     */
    public Organization getMinByAnnualTurnover() {
        if (organizationTreeSet.isEmpty()) {
            return null;
        }
        return organizationTreeSet.first();
    }

    /**
     * Метод, который сохраняет коллекцию в файл
     */
    public void saveCollection() {
        if (organizationTreeSet != null) {
            FileManager fileManager = new FileManager();
            fileManager.writeCollection(organizationTreeSet);
        } else {
            System.err.println("Коллекция не инициализирована.");
        }
    }

    /**
     * Метод находит организацию с наибольшим annualTurnover в коллекции
     *
     * @return организация с наибольшим annualTurnover
     */
    public Organization getMaxByAnnualTurnover() {
        Organization maxOrganization = null;
        long maxAnnualTurnover = Long.MIN_VALUE;
        for (Organization organization : organizationTreeSet) {
            if (organization.getAnnualTurnover() > maxAnnualTurnover) {
                maxOrganization = organization;
                maxAnnualTurnover = organization.getAnnualTurnover();
            }
        }
        return maxOrganization;
    }

    /**
     * Метод находит количество элементов, значение поля type которых больше заданного
     *
     * @param type - значение поля type для поиска совпадений
     * @return количество элементов, значение поля type которых больше заданного, в коллекции
     */
    public int countGreaterThanType(String type) {
        int count = 0;
        for (Organization organization : organizationTreeSet) {
            if (organization.getType() != null && organization.getType().toString().compareTo(type) > 0) {
                count++;
            }
        }
        return count;
    }

    public Organization removeAnyByType(String type) {
        Iterator<Organization> iterator = organizationTreeSet.iterator();
        while (iterator.hasNext()) {
            Organization organization = iterator.next();
            if (organization.getType().toString().equalsIgnoreCase(type)) {
                iterator.remove();
                return organization;
            }
        }
        return null;
    }

    public List<Organization> filterStartsWithName(String prefix) {
        List<Organization> filteredOrganizations = new ArrayList<>();
        for (Organization organization : organizationTreeSet) {
            if (organization.getName().startsWith(prefix)) {
                filteredOrganizations.add(organization);
            }
        }
        return filteredOrganizations;
    }

    public Organization getByID(Integer id) {
        return null;
    }

    public int count_greater_than_type(String typeToRemove) {
        return 0;
    }

    public List<Organization> getOrganizations() {
        return new ArrayList<>(organizationTreeSet);
    }

    public void remove(Organization organizationToRemove) {
        organizationTreeSet.remove(organizationToRemove);
    }
}


