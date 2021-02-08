package me.googas.api.adapters.permissions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.permissions.AbstractPermission;
import me.googas.commons.gson.adapters.JsonAdapter;

public class WrappedPermissionAdapter implements JsonAdapter<AbstractPermission> {
  @Override
  public JsonElement serialize(
      AbstractPermission src, Type typeOfSrc, JsonSerializationContext context) {
    if (src.getExpires() != -1) {
      return context.serialize(src);
    }
    return new JsonPrimitive(src.getNodeAppended());
  }

  @Override
  public AbstractPermission deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) return context.deserialize(json, AbstractPermission.class);
    String string = json.getAsString();
    if (string.startsWith("-")) {
      return new AbstractPermission(string.substring(1), false, -1);
    } else {
      return new AbstractPermission(string, true, -1);
    }
  }
}
