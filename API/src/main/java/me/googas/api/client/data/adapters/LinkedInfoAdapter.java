package me.googas.api.client.data.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.LinkedInfoImpl;
import me.googas.api.links.LinkedInfo;
import me.googas.commons.gson.adapters.JsonAdapter;

/** Deserializes linked info */
public class LinkedInfoAdapter implements JsonAdapter<LinkedInfo> {
  @Override
  public JsonElement serialize(LinkedInfo src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src);
  }

  @Override
  public LinkedInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, LinkedInfoImpl.class);
  }
}
