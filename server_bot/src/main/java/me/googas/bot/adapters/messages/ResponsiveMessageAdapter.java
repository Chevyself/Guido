package me.googas.bot.adapters.messages;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.bot.core.handlers.responsive.roles.GiveRoleResponsiveMessage;

public class ResponsiveMessageAdapter
    implements JsonSerializer<ResponsiveMesage>, JsonDeserializer<ResponsiveMesage> {

  /** The type of message and the class of it */
  @NonNull private final Map<String, Class<? extends ResponsiveMesage>> types = new HashMap<>();

  /** Create the responsive messages adapter */
  public ResponsiveMessageAdapter() {
    this.types.put("give-role", GiveRoleResponsiveMessage.class);
  }

  @Override
  public JsonElement serialize(
      ResponsiveMesage src, Type typeOfSrc, JsonSerializationContext context) {
    JsonElement element = context.serialize(src);
    if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();
      JsonElement type = object.get("type");
      if (type == null) {
        object.addProperty("type", src.getType());
      }
    }
    return element;
  }

  @Override
  public ResponsiveMesage deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) {
      JsonObject object = json.getAsJsonObject();
      JsonElement typeElem = object.get("type");
      if (typeElem != null) {
        String type = typeElem.getAsString();
        return context.deserialize(json, this.types.get(type));
      }
    }
    return null;
  }
}
