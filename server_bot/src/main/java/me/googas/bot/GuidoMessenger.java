package me.googas.bot;

import java.util.Map;
import lombok.NonNull;
import me.googas.api.API;
import me.googas.api.links.Linkable;

// TODO implement
public class GuidoMessenger implements API.Messenger {
  @Override
  public @NonNull String getSingle(@NonNull Linkable linkable) {
    return "null";
  }

  @Override
  public void sendMessage(@NonNull Linkable linkable, @NonNull String message) {}

  @Override
  public void sendLocalized(@NonNull Linkable linkable, @NonNull String key) {}

  @Override
  public void sendLocalized(
      @NonNull Linkable linkable, @NonNull String key, @NonNull Map<String, String> placeholders) {}
}
