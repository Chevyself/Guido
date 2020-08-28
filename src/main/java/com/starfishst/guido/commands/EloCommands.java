package com.starfishst.guido.commands;

import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.utils.Atomic;
import com.starfishst.core.utils.Lots;
import com.starfishst.core.utils.Pagination;
import com.starfishst.core.utils.Strings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.starfishst.guido.data.GuidoMember;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/** Elo commands for a server */
public class EloCommands {

  /** This is the patter that allows to get the elo from a user */
  private static final Pattern PATTERN = Pattern.compile("\\(.*?\\)");

  /**
   * Register a member into the elo system
   *
   * @param member the member to register
   * @param nick the minecraft nick of the player
   * @return the result of the player being registered
   */
  @Command(aliases = "register", description = "Register en el sistema de elo")
  public Result register(
      Member member, @Required(name = "nickname", description = "Minecraft nickname") String nick) {
    if (this.isRegistered(member)) {
      return new Result("Ya estas registrado");
    } else {
      member.modifyNickname(nick + " (0)").queue();
      return new Result("Haz sido registrado");
    }
  }

  /**
   * Saves the result of a match
   *
   * @param message the message to get the mentioned member
   * @param size the players per team
   * @param tie whether or not the match ended in a tie
   * @return the changes in the elo
   */
  @Command(
      aliases = "match",
      description = "Muestra como una match termina",
      permission = @Perm(permission = Permission.ADMINISTRATOR))
  public Result match(
      Message message,
      @Required(name = "jugadores", description = "Los jugadores por team") int size,
      @Optional(name = "empate", description = "En caso de que haya sido un empate") boolean tie) {
    List<Member> mentioned = message.getMentionedMembers();
    if (size * 2 != mentioned.size()) {
      return new Result(
          "El numero de jugadores mencionados no es igual al numero de jugadores por team");
    } else {
      // Primero se menciona a los winners y luego a los losers
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
          Lots.pretty(winnersTag)
              + this.printElo(winnerElo, newWinners, winnersDifference)
              + " - "
              + Lots.pretty(losersTag)
              + this.printElo(losersElo, newLosers, losersDifference));
    }
  }

  /**
   * Updates the elo for the teams
   *
   * @param members the team to update the elo to
   * @param teamElo the current elo of the team
   * @param newTeamElo the new elo of the team
   * @param losersDifference the difference between the old elo and the new one
   */
  protected void updateElo(
      List<Member> members, double teamElo, double newTeamElo, double losersDifference) {
    members.forEach(
        loser -> {
          String nickname = loser.getNickname();
          if (nickname != null) {
            String nick = nickname.split(" ")[0];
            int elo = this.getElo(loser);
            int newElo =
                (int)
                    (newTeamElo > teamElo
                        ? elo + losersDifference
                        : elo - losersDifference < 0 ? 0 : elo - losersDifference);
            loser.modifyNickname(nick + " (" + newElo + ")").queue();
          }
        });
  }

  /**
   * Get the correct format to show the elo changes
   *
   * @param oldElo the old elo
   * @param newElo the new elo
   * @param difference the difference between the elos
   * @return gives the correct elo changes
   */
  @NotNull
  public String printElo(double oldElo, double newElo, double difference) {
    return newElo > oldElo ? "+" + (int) difference : "-" + (int) difference;
  }

  /**
   * Get the current elo of a member
   *
   * @param sender the member checking the elo
   * @param member the other member to check the elo to
   * @return the elo check
   */
  @Command(aliases = "current", description = "Revisa el elo que tienes o el de alguien mas")
  public Result current(
      Member sender,
      @Optional(name = "quien", description = "A quien quieres ver el elo") Member member) {
    boolean isSelf = member == null;
    Member toCheck = member != null ? member : sender;
    if (this.isRegistered(toCheck)) {
      if (isSelf) {
        return new Result("Tienes " + this.getElo(toCheck) + " de elo");
      } else {
        return new Result(toCheck.getAsMention() + " tiene " + this.getElo(toCheck) + " de elo");
      }
    } else {
      if (isSelf) {
        return new Result("No estas registrado");
      } else {
        return new Result(toCheck.getAsMention() + " no esta registrado");
      }
    }
  }

  /**
   * Get the average elo of a team
   *
   * @param message the nessage to get the mentioned members
   * @return the average elo
   */
  @Command(
      aliases = "average",
      description = "Calcula el elo que tendria el team de ciertos miembros")
  public Result average(Message message) {
    if (message.getMentionedMembers().isEmpty()) {
      return new Result("Por favor menciona al menos un miembro");
    } else {
      return new Result("Su team tendria un elo de " + this.getElo(message.getMentionedMembers()));
    }
  }

  /**
   * Display the leaderboard of the server
   *
   * @param guild the server to get the leaderboard
   * @param page the page of the leaderboard to see
   * @return the leaderboard
   */
  @Command(aliases = "leaderboard", description = "Mira la leaderboard del servidor")
  public Result leaderboard(
      Guild guild,
      @Optional(
              name = "pagina",
              description = "La pagina de leaderboard que quieres ver",
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
    int limit = 10;
    Pagination<Member> pagination = new Pagination<>(members);
    int maxPage = pagination.maxPage(limit);
    if (page < 1) {
      return new Result("La pagina tiene que ser 1 o mayor");
    } else {
      if (page <= maxPage) {
        List<Member> toSee = pagination.getPage(page, limit);
        StringBuilder builder = Strings.getBuilder();
        builder.append("Leaderboard:").append("\n");
        builder.append("**").append(page).append("**/").append(maxPage).append("\n");
        toSee.forEach(
            member ->
                builder
                    .append(pagination.getIndex(member) + 1)
                    .append(". ")
                    .append(member.getAsMention())
                    .append("\n"));
        return new Result(builder.toString());
      } else {
        return new Result("La pagina maxima es " + maxPage);
      }
    }
  }

  /**
   * Check whether a member is registered
   *
   * @param member the member to check if it is registered
   * @return true if the member is registered
   */
  private boolean isRegistered(@NotNull Member member) {
    return member.getNickname() != null && PATTERN.matcher(member.getNickname()).find();
  }

  /**
   * Get the current elo of a member
   *
   * @param member the member to get the elo
   * @return the elo of the member
   */
  private int getElo(@NotNull Member member) {
    if (member.getNickname() != null) {
      Matcher matcher = PATTERN.matcher(member.getNickname());
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

  /**
   * Get the average elo of some members
   *
   * @param members the members to see the average elo
   * @return the average elo
   */
  private int getElo(@NotNull List<Member> members) {
    Atomic<Integer> total = new Atomic<>(0);
    members.forEach(member -> total.set(total.get() + this.getElo(member)));
    return total.get() / members.size();
  }
}
