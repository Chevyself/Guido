package com.starfishst.guido.api.data.implementations.data.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.starfishst.guido.api.data.Group;
import com.starfishst.guido.api.data.implementations.data.GroupImpl;
import java.lang.reflect.Type;

/** Deserializer for groups */
public class GroupDeserializer implements JsonDeserializer<Group<?, ?>> {

  @Override
  public Group<?, ?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    return context.deserialize(json, GroupImpl.class);
  }
}
