package me.googas.bot;

import com.starfishst.jda.CommandManager;
import com.starfishst.jda.ManagerOptions;
import lombok.NonNull;
import me.googas.bot.api.Guido;
import me.googas.bot.core.util.Colors;
import me.googas.commons.ProgramArguments;
import net.dv8tion.jda.api.JDA;

public class GuidoCommandManager extends CommandManager {

  public GuidoCommandManager(
      @NonNull JDA jda,
      @NonNull ProgramArguments arguments,
      @NonNull GuidoHandlerRegistry registry) {
    super(
        jda,
        arguments.getProperty("prefix", "-"),
        GuidoCommandManager.createOptions(),
        registry.getLanguageHandler(),
        new GuidoProvidersRegistry(registry.getLanguageHandler()),
        new GuidoPermissionChecker(
            registry.getLanguageHandler(), registry.getLoader(), Guido.getDiscordLoader()));
  }

  @NonNull
  public static ManagerOptions createOptions() {
    ManagerOptions options = new ManagerOptions();
    options.setDeleteCommands(false);
    options.setDeleteErrors(false);
    options.setDeleteSuccess(false);
    options.setEmbedMessages(true);
    options.setSuccess(Colors.getColor("#f48d0e"));
    options.setError(Colors.getColor("#db150a"));
    return options;
  }
}
