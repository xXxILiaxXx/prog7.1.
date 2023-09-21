package database;

import collection.*;

import java.sql.*;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class CollectionDatabaseHandler {

    private final Connection connection;

    public CollectionDatabaseHandler(Connection connection) {
        this.connection = connection;
    }

    public void insertRow(Organization organization) throws SQLException {
        String sql = "INSERT INTO organizationcollection (name, coordinates_x, coordinates_y, creation_date, annual_turnover, employees_count, organization_type, postal_address_zipcode, postal_address_town_x, postal_address_town_y, postal_address_town_z, owner) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, organization.getName());
        ps.setFloat(2, organization.getCoordinates().getX());
        ps.setInt(3, organization.getCoordinates().getY());
        ps.setTimestamp(4, new Timestamp(organization.getCreationDate().getTime()));
        ps.setLong(5, organization.getAnnualTurnover());
        ps.setLong(6, organization.getEmployeesCount());
        ps.setString(7, organization.getType().toString());
        ps.setString(8, organization.getPostalAddress().getZipCode());
        ps.setInt(9, organization.getPostalAddress().getTown().getX());
        ps.setFloat(10, organization.getPostalAddress().getTown().getY());
        ps.setLong(11, organization.getPostalAddress().getTown().getZ());
        ps.setString(12, organization.getOwner());

        ps.executeUpdate();
        ps.close();
    }


    public void deleteRowById(Integer id) throws SQLException {
        String sql = "DELETE FROM ORGANIZATION_COLLECTION WHERE ID = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        int delRows = ps.executeUpdate();
        if (delRows == 1) {
            System.out.println("Строка была удалена.");
        } else System.out.println("Не было удалено строк.");
    }

    public boolean isAnyRowById(Integer id) throws SQLException {
        String sql = "SELECT * FROM ORGANIZATION_COLLECTION WHERE ID = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    public boolean isOwner(Integer id, UserData userData) throws SQLException {
        String sql = "SELECT * FROM ORGANIZATION_COLLECTION WHERE ID = ? AND OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.setString(2, userData.getLogin());
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    public void deleteAllOwned(UserData userData) throws SQLException {
        String sql = "DELETE FROM ORGANIZATION_COLLECTION WHERE OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userData.getLogin());
        int delRows = ps.executeUpdate();
        System.out.println("Было удалено " + delRows + " строк.");
    }

    public Organization[] loadInMemory() throws SQLException {
        ArrayDeque<Organization> arrayDeque = new ArrayDeque<>();
        String sql = "SELECT * FROM ORGANIZATION_COLLECTION";
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);
        if (rs.isBeforeFirst()) {
            while (rs.next()) {
                Organization organization = this.createOrganizationFromCurrentRow(rs);
                arrayDeque.add(organization);
            }
        }
        return arrayDeque.toArray(new Organization[0]);
    }

    private Organization createOrganizationFromCurrentRow(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        float coordinates_x = rs.getFloat("coordinates_x");
        int coordinates_y = rs.getInt("coordinates_y");
        Date creationDate = rs.getDate("creationDate");
        long annualTurnover = rs.getLong("annual_turnover");
        int employeesCount = rs.getInt("employees_count");
        String organizationType = rs.getString("organization_type");
        String zipCode = rs.getString("postal_address_zipcode");
        int townX = rs.getInt("postal_address_town_x");
        float townY = rs.getFloat("postal_address_town_y");
        long townZ = rs.getLong("postal_address_town_z");
        String owner = rs.getString("owner");

        Organization organization = Organization.createOrganization(
                id,
                name,
                coordinates_x,
                coordinates_y,
                creationDate,
                annualTurnover,
                employeesCount,
                OrganizationType.valueOf(organizationType),
                zipCode,
                townX,
                townY,
                townZ,
                owner
        );

        return organization;
    }

    public Integer[] getAllOwner(UserData userData) throws SQLException {
        String sql = "SELECT * FROM ORGANIZATION_COLLECTION WHERE OWNER = ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, userData.getLogin());
        ResultSet rs = ps.executeQuery();
        ArrayList<Integer> ids = new ArrayList<>();
        while (rs.next()) {
            ids.add(rs.getInt(1));
        }
        return ids.toArray(new Integer[0]);
    }
}