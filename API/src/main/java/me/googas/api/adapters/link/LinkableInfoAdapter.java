package me.googas.api.adapters.link;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.commons.gson.adapters.JsonAdapter;

public class LinkableInfoAdapter implements JsonAdapter<LinkableInfo> {
  @Override
  public JsonElement serialize(LinkableInfo src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src);
  }

  @Override
  public LinkableInfo deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleLinkableInfo.class);
  }
}
