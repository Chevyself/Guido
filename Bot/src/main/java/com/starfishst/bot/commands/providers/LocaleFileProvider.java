package com.starfishst.bot.commands.providers;

import com.starfishst.bot.Guido;
import me.googas.api.lang.LocaleFile;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaExtraArgumentProvider;
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
