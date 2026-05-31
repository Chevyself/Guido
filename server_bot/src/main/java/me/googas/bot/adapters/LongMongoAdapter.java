package me.googas.bot.adapters;

import com.google.gson.*;
import java.lang.reflect.Type;

public class LongMongoAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {

  @Override
  public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
    return new JsonPrimitive(src);
  }

  @Override
  public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) {
      JsonObject object = json.getAsJsonObject();
      try {
        return Long.valueOf(object.get("$numberLong").getAsString());
      } catch (NumberFormatException e) {
        return -1L;
      }
    } else {
      return json.getAsLong();
    }
  }
}
