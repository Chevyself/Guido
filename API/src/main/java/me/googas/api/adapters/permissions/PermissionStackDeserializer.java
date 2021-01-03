package me.googas.api.adapters.permissions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.permissions.SimplePermissionStack;
import me.googas.api.permissions.PermissionStack;
import me.googas.commons.gson.adapters.JsonAdapter;

public class PermissionStackDeserializer implements JsonDeserializer<PermissionStack> {

  @Override
  public PermissionStack deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimplePermissionStack.class);
  }
}
