package me.googas.api.adapters.link;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import me.googas.api.client.data.links.SimpleLinkable;
import me.googas.api.links.Linkable;

public class LinkableDeserializer implements JsonDeserializer<Linkable> {
  @Override
  public Linkable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleLinkable.class);
  }
}
