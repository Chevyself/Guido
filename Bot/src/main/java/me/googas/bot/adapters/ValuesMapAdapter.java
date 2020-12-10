package me.googas.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.utility.ValuesMap;
import me.googas.bot.core.types.maps.GuidoValuesMap;
import me.googas.commons.gson.adapters.JsonAdapter;
import me.googas.commons.maps.Maps;

/** Adapts values map */
public class ValuesMapAdapter implements JsonAdapter<ValuesMap> {

  /**
   * Objects in mongo have a prefix when its not a json primitive that's why this contains the given
   * mongo name and the type to deserialize
   */
  @NonNull
  static final Map<String, Type> objectId = Maps.builder("numberLong", (Type) Long.class).build();

  @Override
  public JsonElement serialize(ValuesMap src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src.getMap());
  }

  /**
   * Add the values from a json object to a guido values map
   *
   * @param context the context of the object being deserialize
   * @param map the map to append the values to
   * @param object the object to get the values from
   */
  static void appendValues(
      @NonNull JsonDeserializationContext context,
      @NonNull GuidoValuesMap map,
      @NonNull JsonObject object) {
    for (String key : object.keySet()) {
      JsonElement value = object.get(key);
      if (value.isJsonObject()) {
        JsonObject valueObject = value.getAsJsonObject();
        ArrayList<String> values = new ArrayList<>(valueObject.keySet());
        if (values.size() == 1 && values.get(0).startsWith("$")) {
          Type type = ValuesMapAdapter.objectId.get(values.get(0).substring(1));
          if (type != null) {
            map.put(key, context.deserialize(valueObject.get(values.get(0)), type));
          } else {
            map.put(key, context.deserialize(valueObject.get(values.get(0)), Object.class));
          }
          continue;
        }
      }
      map.put(key, context.deserialize(object.get(key), Object.class));
    }
  }

  @Override
  public ValuesMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    GuidoValuesMap map = new GuidoValuesMap();
    ValuesMapAdapter.appendValues(context, map, json.getAsJsonObject());
    return map;
  }
}
