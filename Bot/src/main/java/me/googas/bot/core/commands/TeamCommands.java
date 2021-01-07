package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.result.Result;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.bot.api.Guido;
import me.googas.bot.core.lang.GuidoLanguageHandler;
import me.googas.bot.core.util.Discord;
import me.googas.commons.RandomUtils;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

/** Commands for team creation */
public class TeamCommands {

  /** The handler to localize the messages of the command */
  @NonNull private final GuidoLanguageHandler handler = Guido.getHandlers().getLanguageHandler();

  /**
   * Get two event teams from a voice channel. This will get the channel where the sender executed
   * the command, then, check if the number of people inside of it is event, if it is, this will
   * return two teams with an even number of players
   *
   * @param context the context the command
   * @param sender the sender of the command
   * @return the result of the command execution
   */
  @Command(
      aliases = {"teams", "equipos"},
      description = "teams.desc")
  public Result teams(CommandContext context, Member sender) {
    GuildVoiceState voiceState = sender.getVoiceState();
    if (voiceState != null && voiceState.getChannel() != null) {
      VoiceChannel channel = voiceState.getChannel();
      List<Member> members = channel.getMembers();
      if (members.size() % 2 == 0) {
        List<Member> team1 = RandomUtils.getRandom(members, members.size() / 2);
        List<Member> team2 = new ArrayList<>();
        for (Member member : members) {
          if (!team1.contains(member)) {
            team2.add(member);
          }
        }
        return new Result(
            this.handler
                .getFile(context)
                .get(
                    "teams.result",
                    Maps.builder("team1", Discord.pretty(team1))
                        .append("team2", Discord.pretty(team2))));
      } else {
        return new Result(this.handler.getFile(context).get("teams.not-even"));
      }
    } else {
      return new Result(this.handler.getFile(context).get("teams.not-connected"));
    }
  }

  /**
   * This will get the number given of captains. From the text channel where the sender executed the
   * command it will return the number given of random people, obviously, if the number given is
   * higher than the people in the channel it will not be completed
   *
   * @param context the context of the command
   * @param sender the sender of the command
   * @param number the number of leaders to get
   * @return the result of the command
   */
  @Command(
      aliases = {"leaders", "capitanes"},
      description = "leaders.desc")
  public Result capitanes(
      CommandContext context,
      Member sender,
      @Optional(name = "leaders.number", description = "leaders.number.desc", suggestions = "2")
          int number) {
    GuildVoiceState voiceState = sender.getVoiceState();
    if (voiceState != null && voiceState.getChannel() != null) {
      VoiceChannel channel = voiceState.getChannel();
      List<Member> members = channel.getMembers();
      if (members.size() >= number) {
        List<Member> random = RandomUtils.getRandom(members, number);
        return new Result(
            this.handler
                .getFile(context)
                .get("leaders.result", Maps.singleton("leaders", Discord.pretty(random))));
      } else {
        return new Result(
            this.handler
                .getFile(context)
                .get("leaders.not-enough", Maps.singleton("number", String.valueOf(number))));
      }
    } else {
      return new Result(
          this.handler
              .getFile(context)
              .get("leaders.not-connected", Maps.singleton("number", String.valueOf(number))));
    }
  }
}
