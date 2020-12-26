package me.googas.api.client.adapters.punishment;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.punishment.SimplePunishment;
import me.googas.api.punishment.Punishment;
import me.googas.commons.gson.adapters.JsonAdapter;

public class PunishmentAdapter implements JsonAdapter<Punishment> {
  @Override
  public JsonElement serialize(Punishment src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src);
  }

  @Override
  public Punishment deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimplePunishment.class);
  }
}
