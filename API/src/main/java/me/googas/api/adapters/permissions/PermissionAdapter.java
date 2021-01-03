package me.googas.api.adapters.permissions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.permissions.SimplePermission;
import me.googas.api.permissions.Permission;
import me.googas.commons.gson.adapters.JsonAdapter;

public class PermissionAdapter implements JsonAdapter<Permission> {
  @Override
  public JsonElement serialize(Permission src, Type typeOfSrc, JsonSerializationContext context) {
    if (src.expires() != -1) {
      return context.serialize(src);
    }
    return new JsonPrimitive(src.getNodeAppended());
  }

  @Override
  public Permission deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) return context.deserialize(json, SimplePermission.class);
    String string = json.getAsString();
    if (string.startsWith("-")) {
      return new SimplePermission(string.substring(1), false, -1);
    } else {
      return new SimplePermission(string, true, -1);
    }
  }
}
