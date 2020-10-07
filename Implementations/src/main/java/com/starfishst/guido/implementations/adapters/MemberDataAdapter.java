package com.starfishst.guido.implementations.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.implementations.data.MemberImpl;
import com.starfishst.guido.implementations.data.UnlinkedMemberDataImpl;
import com.starfishst.utils.gson.adapters.JsonAdapter;
import java.lang.reflect.Type;

/** Adapts members from json */
public class MemberDataAdapter implements JsonAdapter<MemberData> {
  @Override
  public JsonElement serialize(
      MemberData memberData, Type type, JsonSerializationContext jsonSerializationContext) {
    return null;
  }

  @Override
  public MemberData deserialize(
      JsonElement jsonElement, Type type, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();
    if (object.get("key") != null && object.get("value") != null) {
      return context.deserialize(object, UnlinkedMemberDataImpl.class);
    } else {
      return context.deserialize(object, MemberImpl.class);
    }
  }
}
