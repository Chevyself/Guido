package me.googas.api.client.adapters.matches.team;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.lang.reflect.Type;
import me.googas.api.client.data.matches.team.SimpleTeamMember;
import me.googas.api.matches.team.TeamMember;
import me.googas.commons.gson.adapters.JsonAdapter;

public class TeamMemberAdapter implements JsonAdapter<TeamMember> {
  @Override
  public JsonElement serialize(TeamMember src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src);
  }

  @Override
  public TeamMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleTeamMember.class);
  }
}
