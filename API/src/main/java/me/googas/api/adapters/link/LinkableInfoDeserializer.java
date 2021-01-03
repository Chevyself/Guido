package me.googas.api.adapters.link;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.commons.gson.adapters.JsonAdapter;

public class LinkableInfoDeserializer implements JsonDeserializer<LinkableInfo> {
  @Override
  public LinkableInfo deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleLinkableInfo.class);
  }
}
