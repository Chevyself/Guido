package me.googas.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.LinkedTreeMap;
import java.lang.reflect.Type;
import java.util.Map;

public class MapDeserializer implements JsonDeserializer<Map<String, Object>> {
  @Override
  public Map<String, Object> deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    Map<String, Object> map = new LinkedTreeMap<>();
    if (json.isJsonObject()) {
      JsonObject jsonObject = json.getAsJsonObject();
      for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
        // In the future more objects can be added just add a map with the string that stats the
        // object
        // and then continue with the map
        JsonElement value = entry.getValue();
        if (value.isJsonObject()) {
          JsonObject object = value.getAsJsonObject();
          boolean mongo = false;
          for (Map.Entry<String, JsonElement> valueEntry : object.entrySet()) {
            if (valueEntry.getKey().startsWith("$numberLong")) {
              map.put(entry.getKey(), context.deserialize(valueEntry.getValue(), Long.class));
              mongo = true;
            } else {
              break;
            }
          }
          if (!mongo) map.put(entry.getKey(), context.deserialize(value, Object.class));
        } else {
          map.put(entry.getKey(), context.deserialize(value, Object.class));
        }
      }
    }
    return map;
  }
}
