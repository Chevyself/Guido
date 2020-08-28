package com.starfishst.guido.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;

import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.Guido;
import com.starfishst.guido.lang.GuidoLanguageHandler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

/** Commands for team creation */
public class TeamCommands {

  /** The handler to localize the messages of the command */
  @NotNull
  private final GuidoLanguageHandler handler = Guido.getLanguageHandler();

  @Command(aliases = "teams", description = "cmd.teams.desc")
  public Result teams(CommandContext context, Member sender) {
    GuildVoiceState voiceState = sender.getVoiceState();
    if (voiceState != null && voiceState.getChannel() != null) {
      VoiceChannel channel = voiceState.getChannel();
      List<Member> members = channel.getMembers();
      if (members.size() % 2 == 0) {
        List<Member> team1 = RandomUtils.getRandom(members, members.size() / 2);
        List<Member> team2 = new ArrayList<>();
        members.forEach(
            member -> {
              if (!team1.contains(member)) {
                team2.add(member);
              }
            });
        List<String> tags1 = new ArrayList<>();
        team1.forEach(
            member -> {
              tags1.add(member.getAsMention());
            });
        List<String> tags2 = new ArrayList<>();
        team2.forEach(
            member -> {
              tags2.add(member.getAsMention());
            });
        return new Result(handler.getFile(context).get("cmd.teams.result", Maps.builder("team1", Lots.pretty(tags1)).append("team2", Lots.pretty(tags2))));
      } else {
        return new Result(handler.getFile(context).get("cmd.teams.not-even"));
      }
    } else {
      return new Result(handler.getFile(context).get("cmd.teams.not-connected"));
    }
  }

  @Command(aliases = {"leaders", "capitanes"}, description = "cmd.leaders.desc")
  public Result capitanes(
          CommandContext context,
      Member sender,
      @Optional(name = "number", description = "number", suggestions = "2")
          int numb) {
    GuildVoiceState voiceState = sender.getVoiceState();
    if (voiceState != null && voiceState.getChannel() != null) {
      VoiceChannel channel = voiceState.getChannel();
      List<Member> members = channel.getMembers();
      if (members.size() >= numb) {
        List<Member> random = RandomUtils.getRandom(members, numb);
        List<String> tags = new ArrayList<>();
        random.forEach(
            member -> {
              tags.add(member.getAsMention());
            });
        return new Result(handler.getFile(context).get("cmd.leaders.result",  Maps.singleton("leaders", Lots.pretty(tags))));
      } else {
        return new Result(handler.getFile(context).get("cmd.leaders.not-enough",  Maps.singleton("number", String.valueOf(numb))));
      }
    } else {
      return new Result(handler.getFile(context).get("cmd.leaders.not-connected",  Maps.singleton("number", String.valueOf(numb))));
    }
  }
}
