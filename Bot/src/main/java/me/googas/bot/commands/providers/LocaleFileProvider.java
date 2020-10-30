package me.googas.bot.commands.providers;

import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
import me.googas.api.lang.LocaleFile;
import me.googas.bot.Guido;
import org.jetbrains.annotations.NotNull;

/** Provide the locale files for the commands */
public class LocaleFileProvider implements JdaExtraArgumentProvider<LocaleFile> {
  @NotNull
  @Override
  public LocaleFile getObject(@NotNull CommandContext commandContext) {
    return Guido.getLanguageHandler().getFile(commandContext);
  }

  @Override
  public @NotNull Class<LocaleFile> getClazz() {
    return LocaleFile.class;
  }
}
