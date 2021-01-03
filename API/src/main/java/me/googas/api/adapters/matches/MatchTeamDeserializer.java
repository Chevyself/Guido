package me.googas.api.adapters.matches;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import me.googas.api.client.data.matches.SimpleMatchTeam;
import me.googas.api.matches.MatchTeam;

public class MatchTeamDeserializer implements JsonDeserializer<MatchTeam> {

  @Override
  public MatchTeam deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleMatchTeam.class);
  }
}
