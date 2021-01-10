package me.googas.bot.adapters.links;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.links.LinkableInfo;
import me.googas.bot.adapters.SchemeDeserializer;
import me.googas.bot.adapters.schemes.Scheme;
import me.googas.bot.adapters.schemes.links.LatestLinkableInfoScheme;
import me.googas.bot.adapters.schemes.links.LegacyLinkableInfoScheme;

public class LinkedInfoDeserializer
    implements SchemeDeserializer<LinkableInfo>, JsonSerializer<LinkableInfo> {

  @NonNull
  private final Map<String, Class<? extends Scheme<LinkableInfo>>> schemes = new HashMap<>();

  @Getter @Setter private boolean emptyAsLatest;

  public LinkedInfoDeserializer(boolean emptyAsLatest) {
    this.schemes.put("legacy", LegacyLinkableInfoScheme.class);
    this.schemes.put("PRE-3", LatestLinkableInfoScheme.class);
    this.emptyAsLatest = emptyAsLatest;
  }

  @Override
  public JsonElement serialize(LinkableInfo src, Type typeOfSrc, JsonSerializationContext context) {
    return context.serialize(src, SimpleLinkableInfo.class);
  }

  @Override
  public @NonNull Map<String, Class<? extends Scheme<LinkableInfo>>> getSchemes() {
    return this.schemes;
  }

  @Override
  public @NonNull String getLatest() {
    return "PRE-3";
  }
}
