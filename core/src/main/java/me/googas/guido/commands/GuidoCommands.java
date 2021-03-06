package me.googas.guido.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.NonNull;
import me.googas.commands.annotations.Free;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import me.googas.guido.Discord;
import me.googas.starbox.Strings;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

public class GuidoCommands {

  @NonNull private final Random random = new Random();

  @Command(
      aliases = {"leaders", "capitanes", "lideres"},
      description = "Select the given number of captains")
  public Result leaders(
      Member sender,
      @Free(name = "number", description = "leaders.number.desc", suggestions = "2") int number) {
    if (number <= 0)
      return Result.forType(ResultType.USAGE)
          .setDescription("You must use a number greater than 0")
          .build();
    GuildVoiceState voiceState = sender.getVoiceState();
    if (voiceState != null && voiceState.getChannel() != null) {
      VoiceChannel channel = voiceState.getChannel();
      List<Member> members = channel.getMembers();
      if (members.size() >= number) {
        List<Member> random = this.getRandom(members, number);
        // Can be localized sometime
        return Result.builder()
            .setDescription(Strings.format("The leaders are {0}", Discord.pretty(random)))
            .build();
      } else {
        return Result.forType(ResultType.USAGE)
            .setDescription(
                Strings.format("There isn't enough players to select {0} captain(s)", number))
            .build();
      }
    } else {
      return Result.forType(ResultType.USAGE)
          .setDescription("You must be connected to a voice channel")
          .build();
    }
  }

  @NonNull
  public <T> List<T> getRandom(@NonNull List<T> list, int amount) {
    if (list.size() < amount) {
      throw new IllegalArgumentException(
          list + " has an smaller size than the given amount: " + amount);
    }
    List<T> newList = new ArrayList<>();
    while (newList.size() != amount) {
      T t = list.get(this.random.nextInt(list.size()));
      if (!newList.contains(t)) newList.add(t);
    }
    return newList;
  }
}
