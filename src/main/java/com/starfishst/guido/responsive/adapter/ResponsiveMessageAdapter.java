package com.starfishst.guido.responsive.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import com.starfishst.guido.responsive.role.RoleGiver;
import com.starfishst.utils.gson.adapters.JsonAdapter;
import java.lang.reflect.Type;

/** Create a */
public class ResponsiveMessageAdapter implements JsonAdapter<ResponsiveMessage> {
  @Override
  public JsonElement serialize(
      ResponsiveMessage responsiveMessage, Type type, JsonSerializationContext context) {
    return context.serialize(responsiveMessage);
  }

  @Override
  public ResponsiveMessage deserialize(
      JsonElement jsonElement, Type aType, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    String type = object.get("type").getAsString();
    if (type.equalsIgnoreCase("GIVER")) {
      return context.deserialize(jsonElement, RoleGiver.class);
    }
    throw new JsonParseException("There's no adapter for " + type);
  }
}
