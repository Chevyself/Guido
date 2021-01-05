package me.googas.bot.adapters.permissions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

import com.google.gson.JsonSerializationContext;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.core.permissions.GuidoPermissionStack;
import me.googas.commons.gson.adapters.JsonAdapter;

public class PermissionStackAdapter implements JsonAdapter<PermissionStack> {

  @Override
  public JsonElement serialize(PermissionStack src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src, GuidoPermissionStack.class);
  }

  @Override
  public PermissionStack deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoPermissionStack.class);
  }
}
