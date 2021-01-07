package me.googas.bot.core.util;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.lang.Localized;
import me.googas.api.matches.queue.Queueable;
import me.googas.bot.api.Guido;
import me.googas.bot.core.lang.GuidoLanguageHandler;
import me.googas.commons.maps.MapBuilder;

/** Static utilities for language */
public class Lang {

  @NonNull
  public static LocaleFile getLocale(@NonNull CommandContext context) {
    return Guido.getHandlers().getLanguageHandler().getFile(context);
  }

  @NonNull
  public static LocaleFile getLocale(@NonNull Queueable queueable) {
    GuidoLanguageHandler handler = Guido.getHandlers().getLanguageHandler();
    if (queueable instanceof Localized) return handler.getFile((Localized) queueable);
    return handler.getDefault();
  }

  @NonNull
  public static ArgumentProviderException getException(
      @NonNull String key, @NonNull CommandContext context) {
    return new ArgumentProviderException(Lang.getLocale(context).get(key));
  }

  @NonNull
  public static ArgumentProviderException getException(
      @NonNull String key,
      @NonNull Map<String, String> placeholders,
      @NonNull CommandContext context) {
    return new ArgumentProviderException(Lang.getLocale(context).get(key, placeholders));
  }

  @NonNull
  public static ArgumentProviderException getException(
      @NonNull String key,
      @NonNull MapBuilder<String, String> placeholders,
      @NonNull CommandContext context) {
    return new ArgumentProviderException(Lang.getLocale(context).get(key, placeholders));
  }
}
