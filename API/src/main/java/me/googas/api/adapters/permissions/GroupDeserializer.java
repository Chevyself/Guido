package me.googas.api.adapters.permissions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.permissions.SimpleGroup;
import me.googas.api.permissions.Group;
import me.googas.commons.gson.adapters.JsonAdapter;

public class GroupDeserializer implements JsonDeserializer<Group> {

  @Override
  public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleGroup.class);
  }
}
