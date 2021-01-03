package me.googas.api.adapters.matches.team;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import me.googas.api.client.data.matches.team.SimpleTeamMember;
import me.googas.api.matches.team.TeamMember;

public class TeamMemberDeserializer implements JsonDeserializer<TeamMember> {

  @Override
  public TeamMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, SimpleTeamMember.class);
  }
}
