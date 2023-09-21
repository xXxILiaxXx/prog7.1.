package collection;

import java.io.Serializable;
import java.util.Date;

public class Organization implements Comparable<Organization>, Serializable {
    private Integer id;
    private String user_login;
    private String name;
    private Coordinates coordinates;
    private Date creationDate;
    private long annualTurnover;
    private long employeesCount;
    private OrganizationType organizationType;
    private Address postalAddress;
    private String owner;

    public Organization(Integer id, String name, Coordinates coordinates, Date creationDate, long annualTurnover, long employeesCount, OrganizationType organizationType, Address postalAddress) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.annualTurnover = annualTurnover;
        this.employeesCount = employeesCount;
        this.organizationType = organizationType;
        this.postalAddress = postalAddress;

    }

    public static Organization createOrganization(
            Integer id,
            String name,
            float x,
            int y,
            Date creationDate,
            long annualTurnover,
            long employeesCount,
            OrganizationType organizationType,
            String zipCode,
            int townX,
            float townY,
            long townZ,
            String owner
    ) {
        Coordinates coordinates = new Coordinates(x, y);
        Address postalAddress = new Address(zipCode, new Location(townX, townY, townZ));

        Organization organization = new Organization(id, name, coordinates, creationDate, annualTurnover, employeesCount, organizationType, postalAddress);
        organization.setOwner(owner);
        return organization;
    }

    // Геттеры и сеттеры

    @Override
    public String toString() {
        return "=============\n" +
                "id:\n " + id +
                ",\nname: \n" + name +
                ",\ncoordinates: \n " + coordinates +
                ",\ncreationDate: \n " + creationDate +
                ",\nannualTurnover: \n " + annualTurnover +
                ",\nemployeesCount: \n " + employeesCount +
                ",\ntype: \n " + organizationType +
                ",\npostalAddress: \n " + postalAddress;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public long getAnnualTurnover() {
        return annualTurnover;
    }

    public Long getEmployeesCount() {
        return employeesCount;
    }

    public OrganizationType getType() {
        return organizationType;
    }

    public Address getPostalAddress() {
        return postalAddress;
    }

    @Override
    public int compareTo(Organization other) {
        return this.id.compareTo(other.getId());
    }

    public String getOwner() {
        return user_login;
    }

    public void setOwner(String owner) {
        this.user_login = owner;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Метод устанавливает время создания объекта
     *
     * @param creationDate - время создания объекта
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}