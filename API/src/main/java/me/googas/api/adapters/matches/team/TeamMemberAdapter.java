package me.googas.api.adapters.matches.team;

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
    return context.serialize(src, SimpleTeamMember.class);
  }

  @Override
  public TeamMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    /*
    if (json.isJsonObject()) {
      JsonObject object = json.getAsJsonObject();
      TeamRole role = context.deserialize(object.get("role"), TeamRole.class);
      role = role != null ? role : TeamRole.NORMAL;
      System.out.println("object = " + object);
      System.out.println("object.get(\"linkInfo\") = " + object.get("linkInfo"));
      return new SimpleTeamMember(context.deserialize(object.get("linkInfo"), LinkableInfo.class), role);
    }
    return null;

     */
    return context.deserialize(json, SimpleTeamMember.class);
  }
}
