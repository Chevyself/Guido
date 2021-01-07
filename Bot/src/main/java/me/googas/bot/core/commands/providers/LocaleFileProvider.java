package me.googas.bot.core.commands.providers;

import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.bot.api.Guido;

/** Provide the locale files for the commands */
public class LocaleFileProvider implements JdaExtraArgumentProvider<LocaleFile> {
  @NonNull
  @Override
  public LocaleFile getObject(@NonNull CommandContext commandContext) {
    return Guido.getHandlers().getLanguageHandler().getFile(commandContext);
  }

  @Override
  public @NonNull Class<LocaleFile> getClazz() {
    return LocaleFile.class;
  }
}
