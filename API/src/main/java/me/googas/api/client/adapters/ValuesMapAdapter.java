package me.googas.api.client.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Map;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.gson.adapters.JsonAdapter;

/** Adapts values map */
public class ValuesMapAdapter implements JsonAdapter<ValuesMap> {
  @Override
  public JsonElement serialize(ValuesMap src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src.getMap());
  }

  @Override
  public ValuesMap deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return new SimpleValuesMap(
        context.deserialize(json, new TypeToken<Map<String, Object>>() {}.getType()));
  }
}
