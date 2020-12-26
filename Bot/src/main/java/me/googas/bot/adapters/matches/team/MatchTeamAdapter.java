package me.googas.bot.adapters.matches.team;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.matches.MatchTeam;
import me.googas.bot.core.matches.GuidoMatchTeam;
import me.googas.commons.gson.adapters.JsonAdapter;

public class MatchTeamAdapter implements JsonAdapter<MatchTeam> {
  @Override
  public JsonElement serialize(MatchTeam src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src, GuidoMatchTeam.class);
  }

  @Override
  public MatchTeam deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoMatchTeam.class);
  }
}
