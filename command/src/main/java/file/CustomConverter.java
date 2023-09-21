package file;

import collection.*;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

public class CustomConverter implements JsonSerializer<Organization>, JsonDeserializer<Organization> {
    Gson gson = new Gson();
    CollectionManager collectionManager = new CollectionManager();

    @Override
    public Organization deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Integer id = collectionManager.generateNewId();
        String name = jsonObject.get("name").getAsString();
        Long employeesCount = jsonObject.get("employeesCount").getAsLong();
        OrganizationType orgType = OrganizationType.valueOf(jsonObject.get("type").getAsString());
        Address postalAddress = gson.fromJson(jsonObject.get("postalAddress"), Address.class);
        Date creationDate = new Date();
        JsonObject coordinates = jsonObject.get("coordinates").getAsJsonObject();
        float x = coordinates.get("x").getAsFloat();
        int y = coordinates.get("y").getAsInt();
        Coordinates orgCoordinates = new Coordinates(x, y);
        long annualTurnover = jsonObject.get("annualTurnover").getAsLong();

        return new Organization(id, name, orgCoordinates, creationDate, annualTurnover, employeesCount, orgType, postalAddress);
    }

    @Override
    public JsonElement serialize(Organization organization, Type type, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", organization.getId());
        jsonObject.addProperty("name", organization.getName());
        jsonObject.add("coordinates", gson.toJsonTree(organization.getCoordinates()));
        jsonObject.addProperty("annualTurnover", organization.getAnnualTurnover());
        jsonObject.addProperty("employeesCount", organization.getEmployeesCount());
        jsonObject.addProperty("type", organization.getType().toString());
        jsonObject.add("officialAddress", gson.toJsonTree(organization.getPostalAddress()));

        return jsonObject;
    }
}

