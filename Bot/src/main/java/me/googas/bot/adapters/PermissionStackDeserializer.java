package me.googas.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.PermissionStack;
import me.googas.bot.handlers.data.types.permissions.GuidoPermissionStack;
import me.googas.commons.gson.adapters.JsonAdapter;

/** Deserializes permission stacks */
public class PermissionStackDeserializer implements JsonAdapter<PermissionStack> {

  @Override
  public JsonElement serialize(
      PermissionStack src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src, GuidoPermissionStack.class);
  }

  @Override
  public PermissionStack deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoPermissionStack.class);
  }
}
