package me.googas.bungee.utility;

import com.starfishst.commands.bungee.context.CommandContext;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.util.Map;
import lombok.NonNull;
import me.googas.bungee.GuidoBungee;
import me.googas.bungee.lang.BungeeLocaleFile;
import me.googas.commons.maps.MapBuilder;
import net.md_5.bungee.api.CommandSender;

/** Dependencies for chat */
public class Chat {

  public static ArgumentProviderException exception(
      @NonNull String key, @NonNull CommandContext context) {
    return new ArgumentProviderException(
        GuidoBungee.getLanguageHandler().getFile(context).get(key));
  }

  public static ArgumentProviderException exception(
      @NonNull String key,
      @NonNull Map<String, String> placeholders,
      @NonNull CommandContext context) {
    return new ArgumentProviderException(
        GuidoBungee.getLanguageHandler().getFile(context).get(key, placeholders));
  }

  public static ArgumentProviderException exception(
      @NonNull String key,
      @NonNull MapBuilder<String, String> placeholders,
      @NonNull CommandContext context) {
    return new ArgumentProviderException(
        GuidoBungee.getLanguageHandler().getFile(context).get(key, placeholders));
  }

  @NonNull
  public static BungeeLocaleFile getLocale(@NonNull CommandSender sender) {
    return GuidoBungee.getLanguageHandler().getFile(sender);
  }
}
