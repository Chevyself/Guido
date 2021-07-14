package me.googas.guido.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import me.googas.commands.annotations.Optional;
import me.googas.commands.annotations.Required;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import me.googas.guido.Discord;
import me.googas.guido.Pagination;
import me.googas.starbox.Strings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

public class EloCommands {

  private static final Pattern PATTERN = Pattern.compile("\\(.*?\\)");

  @Command(aliases = "register", description = "Register to the elo system")
  public Result register(
      Member member, @Required(name = "nickname", description = "Minecraft nickname") String nick) {
    if (this.isRegistered(member)) {
      return new Result(ResultType.USAGE, "You are registered");
    } else {
      member.modifyNickname(nick + " (0)").queue();
      return new Result("You have been registered!");
    }
  }

  @Command(aliases = "current", description = "Check you elo or someone else's")
  public Result current(
      Member sender,
      @Optional(name = "who", description = "Who do you want to check elo from") Member member) {
    boolean isSelf = member == null;
    Member toCheck = member != null ? member : sender;
    if (this.isRegistered(toCheck)) {
      if (isSelf) {
        return new Result("You have " + this.getElo(toCheck) + " elo");
      } else {
        return new Result(toCheck.getAsMention() + " has " + this.getElo(toCheck) + " elo");
      }
    } else {
      if (isSelf) {
        return new Result("You are not registered");
      } else {
        return new Result(toCheck.getAsMention() + " is not registered");
      }
    }
  }

  @Command(aliases = "average", description = "Calculate the average of the mentioned members")
  public Result average(Message message) {
    if (message.getMentionedMembers().isEmpty()) {
      return new Result("Please mention at least one member");
    } else {
      return new Result("The team would have " + this.getElo(message.getMentionedMembers()));
    }
  }

  @Command(
      aliases = "match",
      description = "Registers a match as finished",
      permission = Permission.ADMINISTRATOR)
  public Result match(
      Message message,
      @Required(name = "players", description = "The players per team") int size,
      @Optional(name = "tie", description = "Whether there has been a tie") boolean tie) {
    List<Member> mentioned = message.getMentionedMembers();
    if (size * 2 != mentioned.size()) {
      return new Result(
          ResultType.USAGE,
          Strings.format(
              "The number of mentioned players ({0}) does not match the expected players per team ({0})",
              mentioned.size(), size * 2));
    } else {
      // Mention winners first
      List<Member> winners = new ArrayList<>(size);
      List<Member> losers = new ArrayList<>(size);
      for (int i = 0; i < size * 2; i++) {
        if (i > size - 1) {
          losers.add(mentioned.get(i));
        } else {
          winners.add(mentioned.get(i));
        }
      }
      double winnerElo = this.getElo(winners);
      double losersElo = this.getElo(losers);
      double winnerExpected = 1 / (1 + Math.pow(10, (losersElo - winnerElo) / 400));
      double losersExpected = 1 / (1 + Math.pow(10, (winnerElo - losersElo) / 400));
      double win = tie ? 0.5 : 1;
      double lose = tie ? 0.5 : 0;
      double newWinners = winnerElo + 32 * (win - winnerExpected);
      double newLosers = losersElo + 32 * (lose - losersExpected);
      double winnersDifference =
          newWinners > winnerElo ? newWinners - winnerElo : winnerElo - newWinners;
      double losersDifference =
          newLosers > losersElo ? newLosers - losersElo : losersElo - newLosers;
      List<String> winnersTag = new ArrayList<>();
      List<String> losersTag = new ArrayList<>();
      winners.forEach(winner -> winnersTag.add(winner.getAsMention()));
      losers.forEach(loser -> losersTag.add(loser.getAsMention()));
      this.updateElo(winners, winnerElo, newWinners, winnersDifference);
      this.updateElo(losers, losersElo, newLosers, losersDifference);
      return new Result(
          Discord.prettyToString(winnersTag)
              + this.printElo(winnerElo, newWinners, winnersDifference)
              + " - "
              + Discord.prettyToString(losersTag)
              + this.printElo(losersElo, newLosers, losersDifference));
    }
  }

  @NonNull
  public String printElo(double oldElo, double newElo, double difference) {
    return newElo > oldElo ? "+" + (int) difference : "-" + (int) difference;
  }

  @Command(
      aliases = {"leaderboard", "lb"},
      description = "See the leaderboard of the server")
  public Result leaderboard(
      Guild guild,
      @Optional(
              name = "Page",
              description = "The page of the leaderboard you want to see",
              suggestions = "1")
          int page) {
    LinkedHashMap<Member, Integer> elos = new LinkedHashMap<>();
    guild
        .getMembers()
        .forEach(
            member -> {
              if (this.isRegistered(member)) {
                elos.put(member, this.getElo(member));
              }
            });
    List<Member> members = new ArrayList<>();
    elos.entrySet().stream()
        .sorted(Map.Entry.comparingByValue())
        .forEach(entry -> members.add(entry.getKey()));
    Collections.reverse(members);
    List<Member> toSee = new Pagination<>(members, 10).getPage(page);
    StringBuilder builder = new StringBuilder();
    builder.append("Leaderboard:").append("\n");
    for (int i = 0; i < toSee.size(); i++) {
      builder.append(i + 1).append(". ").append(members.get(i).getAsMention()).append("\n");
    }
    return new Result(builder.toString());
  }

  private boolean isRegistered(@NonNull Member member) {
    String nickname = member.getNickname();
    return nickname != null && EloCommands.PATTERN.matcher(nickname).find();
  }

  private void updateElo(
      List<Member> members, double teamElo, double newTeamElo, double losersDifference) {
    for (Member member : members) {
      String nickname = member.getNickname();
      if (nickname != null) {
        String nick = nickname.split(" ")[0];
        int elo = this.getElo(member);
        int newElo =
            (int)
                (newTeamElo > teamElo
                    ? elo + losersDifference
                    : elo - losersDifference < 0 ? 0 : elo - losersDifference);
        member.modifyNickname(nick + " (" + newElo + ")").queue();
      }
    }
  }

  private int getElo(@NonNull Member member) {
    if (member.getNickname() != null) {
      Matcher matcher = EloCommands.PATTERN.matcher(member.getNickname());
      if (matcher.find()) {
        String elo = matcher.group().replace("(", "").replace(")", "");
        try {
          return Integer.parseInt(elo);
        } catch (NumberFormatException e) {
          System.out.println(member + " has a wrong format of elo");
          return 0;
        }
      }
    }
    return 0;
  }

  private int getElo(@NonNull List<Member> members) {
    AtomicInteger total = new AtomicInteger();
    members.forEach(member -> total.set(total.get() + this.getElo(member)));
    return total.get() / members.size();
  }
}
