package me.googas.api.client.data.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import me.googas.api.Group;
import me.googas.api.client.data.GroupImpl;

import java.lang.reflect.Type;

/** Deserializer for groups */
public class GroupDeserializer implements JsonDeserializer<Group> {

  @Override
  public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GroupImpl.class);
  }
}
