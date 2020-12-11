package me.googas.api.client.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.SimpleTeamData;
import me.googas.api.matches.TeamData;
import me.googas.commons.gson.adapters.JsonAdapter;

public class TeamDataAdapter implements JsonAdapter<TeamData> {
  @Override
  public JsonElement serialize(TeamData src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src);
  }

  @Override
  public TeamData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleTeamData.class);
  }
}
