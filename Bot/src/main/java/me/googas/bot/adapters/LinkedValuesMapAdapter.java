package me.googas.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.bot.core.GuidoLinkedValuesMap;
import me.googas.commons.gson.adapters.JsonAdapter;

public class LinkedValuesMapAdapter implements JsonAdapter<GuidoLinkedValuesMap> {
  @Override
  public JsonElement serialize(
      GuidoLinkedValuesMap src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src.getMap());
  }

  @Override
  public GuidoLinkedValuesMap deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    GuidoLinkedValuesMap map = new GuidoLinkedValuesMap();
    ValuesMapAdapter.appendValues(context, map, json.getAsJsonObject());
    return map;
  }
}
