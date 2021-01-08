package me.googas.bot;

import com.starfishst.jda.CommandManager;
import com.starfishst.jda.ManagerOptions;
import java.util.Set;
import lombok.NonNull;
import me.googas.bot.api.Guido;
import me.googas.bot.core.commands.HelpCommand;
import me.googas.bot.core.commands.LadderCommands;
import me.googas.bot.core.commands.LangCommands;
import me.googas.bot.core.commands.LeaderboardCommands;
import me.googas.bot.core.commands.MatchCommands;
import me.googas.bot.core.commands.ProvisionalTeamCommands;
import me.googas.bot.core.commands.QueueCommands;
import me.googas.bot.core.commands.SeasonCommands;
import me.googas.bot.core.commands.TeamCommands;
import me.googas.bot.core.commands.TokenCommands;
import me.googas.bot.core.commands.UserCommands;
import me.googas.bot.core.commands.administrative.AdministrationCommands;
import me.googas.bot.core.commands.administrative.CategoryCommands;
import me.googas.bot.core.commands.administrative.ChannelCommands;
import me.googas.bot.core.commands.administrative.DeveloperCommands;
import me.googas.bot.core.commands.administrative.FixCommands;
import me.googas.bot.core.commands.administrative.VoiceChannelCommands;
import me.googas.bot.core.util.Colors;
import me.googas.commons.Lots;
import me.googas.commons.ProgramArguments;
import net.dv8tion.jda.api.JDA;

public class GuidoCommandManager extends CommandManager {

  @NonNull
  private final Set<Object> commands =
      Lots.set(
          new AdministrationCommands(),
          new CategoryCommands(),
          new ChannelCommands(),
          new DeveloperCommands(),
          new FixCommands(),
          new VoiceChannelCommands(),
          new HelpCommand(),
          new LadderCommands(),
          new LangCommands(),
          new LeaderboardCommands(),
          new MatchCommands(),
          new ProvisionalTeamCommands(),
          new QueueCommands(),
          new SeasonCommands(),
          new TeamCommands(),
          new TokenCommands(),
          new UserCommands());

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
            registry.getLanguageHandler(),
            registry.getLoader(),
            Guido.getHandlers().getDiscordLoader()));
  }

  @NonNull
  public GuidoCommandManager register() {
    for (Object command : this.commands) {
      this.registerCommand(command);
    }
    return this;
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
