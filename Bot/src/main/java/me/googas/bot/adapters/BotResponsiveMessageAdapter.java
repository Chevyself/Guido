package me.googas.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import me.googas.bot.api.types.BotResponsiveMessage;
import me.googas.bot.core.handlers.responsive.command.ExecuteCommandResponsiveMessage;
import me.googas.bot.core.handlers.responsive.queue.JoinQueueResponsiveMessage;
import me.googas.bot.core.handlers.responsive.roles.GiveRoleResponsiveMessage;
import me.googas.commons.gson.adapters.JsonAdapter;

/** Adapts responsive messages */
public class BotResponsiveMessageAdapter implements JsonAdapter<BotResponsiveMessage> {

  /** The type of message and the class of it */
  @NonNull private final Map<String, Class<? extends BotResponsiveMessage>> types = new HashMap<>();

  /** Create the responsive messages adapter */
  public BotResponsiveMessageAdapter() {
    this.types.put("queue", JoinQueueResponsiveMessage.class);
    this.types.put("execute", ExecuteCommandResponsiveMessage.class);
    this.types.put("give-role", GiveRoleResponsiveMessage.class);
  }

  @Override
  public JsonElement serialize(
      BotResponsiveMessage src, Type typeOfSrc, JsonSerializationContext context) {
    JsonElement element = context.serialize(src);
    if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();
      JsonElement type = object.get("type");
      if (type == null) {
        object.addProperty("type", src.getType());
      }
    }
    return element;
  }

  @Override
  public BotResponsiveMessage deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonObject()) {
      JsonObject object = json.getAsJsonObject();
      JsonElement typeElem = object.get("type");
      if (typeElem != null) {
        String type = typeElem.getAsString();
        return context.deserialize(json, this.types.get(type));
      }
    }
    return null;
  }
}
