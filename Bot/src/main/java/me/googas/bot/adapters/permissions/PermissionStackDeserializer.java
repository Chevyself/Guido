package me.googas.bot.adapters.permissions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.core.permissions.GuidoPermissionStack;

public class PermissionStackDeserializer implements JsonDeserializer<PermissionStack> {

  @Override
  public PermissionStack deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoPermissionStack.class);
  }
}
