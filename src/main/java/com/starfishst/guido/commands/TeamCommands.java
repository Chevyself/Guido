package com.starfishst.guido.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.RandomUtils;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

/** Commands for team creation */
public class TeamCommands {

  @Command(aliases = "teams", description = "Crea dos equipos para jugar matches")
  public Result teams(Member sender) {
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
        return new Result("Team 1: " + Lots.pretty(tags1) + " Team 2: " + Lots.pretty(tags2));
      } else {
        return new Result("No hay un numero de usuarios par");
      }
    } else {
      return new Result("No estas conectado en ningun canal de voz");
    }
  }

  @Command(aliases = "capitanes", description = "Da los capitanes para hacer equipos")
  public Result capitanes(
      Member sender,
      @Optional(name = "numero", description = "El numero de lideres para dar", suggestions = "2")
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
        return new Result("Los lideres son: " + Lots.pretty(tags));
      } else {
        return new Result("No hay usuarios suficientes para decidir " + numb + " lideres");
      }
    } else {
      return new Result("No estas conectado en ningun canal de voz");
    }
  }
}
