package com.starfishst.guido.responsive.role.adapter;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.starfishst.guido.responsive.role.RoleGiver;
import com.starfishst.guido.responsive.role.RoleInformation;
import com.starfishst.utils.gson.adapters.JsonAdapter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/** Adapter for role giver */
public class RoleGiverAdapter implements JsonAdapter<RoleGiver> {

  @Override
  public JsonElement serialize(
      RoleGiver roleGiver, Type type, JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    object.addProperty("id", roleGiver.getId());
    object.addProperty("type", "GIVER");
    JsonArray info = new JsonArray();
    List<RoleInformation> rolesInformation = roleGiver.getRolesInformation();
    rolesInformation.forEach(
        roleInformation -> {
          JsonObject infoObject = new JsonObject();
          infoObject.addProperty("unicode", roleInformation.getUnicode());
          infoObject.addProperty("id", roleInformation.getRoleId());
          info.add(infoObject);
        });
    object.add("roles", info);
    return object;
  }

  @Override
  public RoleGiver deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    long id = object.get("id").getAsLong();
    RoleGiver roleGiver = new RoleGiver(id);
    JsonArray arr = object.get("roles").getAsJsonArray();
    List<RoleInformation> list = new ArrayList<>();
    arr.forEach(
        infoElement -> {
          JsonObject infoJson = infoElement.getAsJsonObject();
          String unicode = infoJson.get("unicode").getAsString();
          long infoId = infoJson.get("id").getAsLong();
          list.add(new RoleInformation(roleGiver, unicode, infoId));
        });
    roleGiver.addRolesInformation(list);
    return roleGiver;
  }
}
