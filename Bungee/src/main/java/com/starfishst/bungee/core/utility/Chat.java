package com.starfishst.bungee.core.utility;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.core.lang.BungeeLocaleFile;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.util.Map;
import lombok.NonNull;
import me.googas.commons.maps.MapBuilder;
import net.md_5.bungee.api.CommandSender;

/** Dependencies for chat */
public class Chat {

  public static ArgumentProviderException exception(
      @NonNull String key, @NonNull CommandContext context) {
    return new ArgumentProviderException(Guido.getLanguageHandler().getFile(context).get(key));
  }

  public static ArgumentProviderException exception(
      @NonNull String key,
      @NonNull Map<String, String> placeholders,
      @NonNull CommandContext context) {
    return new ArgumentProviderException(
        Guido.getLanguageHandler().getFile(context).get(key, placeholders));
  }

  public static ArgumentProviderException exception(
      @NonNull String key,
      @NonNull MapBuilder<String, String> placeholders,
      @NonNull CommandContext context) {
    return new ArgumentProviderException(
        Guido.getLanguageHandler().getFile(context).get(key, placeholders));
  }

  @NonNull
  public static BungeeLocaleFile getLocale(@NonNull CommandSender sender) {
    return Guido.getLanguageHandler().getFile(sender);
  }
}
