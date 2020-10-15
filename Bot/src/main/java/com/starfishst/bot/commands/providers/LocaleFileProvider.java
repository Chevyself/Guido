package com.starfishst.bot.commands.providers;

import com.starfishst.bot.Guido;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Provide the locale files for the commands
 */
public class LocaleFileProvider implements JdaArgumentProvider<LocaleFile> {
    @Override
    public @NotNull Class<LocaleFile> getClazz() {
        return LocaleFile.class;
    }

    @NotNull
    @Override
    public LocaleFile fromString(@NotNull String s, @NotNull CommandContext commandContext) {
        return Guido.getLanguageHandler().getFile(commandContext);
    }
}
