package com.starfishst.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.guido.api.data.ValuesMap;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import me.googas.commons.gson.adapters.JsonAdapter;
import me.googas.commons.maps.Maps;
import org.jetbrains.annotations.NotNull;

/** Adapts values map */
public class ValuesMapAdapter implements JsonAdapter<ValuesMap> {

  /**
   * Objects in mongo have a prefix when its not a json primitive that's why this contains the given
   * mongo name and the type to deserialize
   */
  @NotNull
  private final Map<String, Type> objectId = Maps.builder("numberLong", (Type) Long.class).build();

  @Override
  public JsonElement serialize(ValuesMap src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src.getMap());
  }

  @Override
  public ValuesMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    GuidoValuesMap map = new GuidoValuesMap();
    JsonObject object = json.getAsJsonObject();
    for (String key : object.keySet()) {
      JsonElement value = object.get(key);
      if (value.isJsonObject()) {
        JsonObject valueObject = value.getAsJsonObject();
        ArrayList<String> values = new ArrayList<>(valueObject.keySet());
        if (values.size() == 1 && values.get(0).startsWith("$")) {
          Type type = this.objectId.get(values.get(0).substring(1));
          if (type != null) {
            map.addValue(key, context.deserialize(valueObject.get(values.get(0)), type));
          } else {
            map.addValue(key, context.deserialize(valueObject.get(values.get(0)), Object.class));
          }
          continue;
        }
      }
      map.addValue(key, context.deserialize(object.get(key), Object.class));
    }
    return map;
  }
}
