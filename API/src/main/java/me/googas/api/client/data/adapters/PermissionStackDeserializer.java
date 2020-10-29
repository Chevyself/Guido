package me.googas.api.client.data.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import me.googas.api.PermissionStack;
import me.googas.api.client.data.PermissionStackImpl;

import java.lang.reflect.Type;

/** Deserializer for permission stack */
public class PermissionStackDeserializer implements JsonDeserializer<PermissionStack> {
  @Override
  public PermissionStack deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, PermissionStackImpl.class);
  }
}
