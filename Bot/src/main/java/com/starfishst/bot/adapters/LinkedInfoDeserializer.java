package com.starfishst.bot.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.starfishst.bot.handlers.data.GuidoLinkedInfo;
import com.starfishst.guido.api.data.links.LinkedInfo;
import java.lang.reflect.Type;

/** Deserializes linked info */
public class LinkedInfoDeserializer implements JsonDeserializer<LinkedInfo> {
  @Override
  public LinkedInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GuidoLinkedInfo.class);
  }
}
