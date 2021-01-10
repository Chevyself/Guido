package com.starfishst.bukkit.dependencies.pgm.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.dependencies.pgm.PGMHostedMatch;
import com.starfishst.bukkit.dependencies.pgm.listeners.matches.PGMMatchMakingHandler;
import com.starfishst.bukkit.result.Result;
import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Settings;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.client.data.matches.SimpleMatch;
import me.googas.api.client.data.matches.SimpleMatchTeam;
import me.googas.api.client.data.matches.team.SimpleTeamMember;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.team.TeamMember;
import me.googas.api.matches.team.TeamRole;
import me.googas.commons.Lots;
import me.googas.commons.RandomUtils;
import me.googas.commons.UUIDUtils;

public class MatchCommands implements GuidoCommand {

  @Settings("async")
  @Command(aliases = "fake", description = "Creates a fake match", permission = "guido.fake")
  public Result fake(
      @Optional(suggestions = {"1v1", "3v3", "5v5"}) String ladder,
      @Optional(suggestions = {"1", "3", "5"}) int size) {
    PGMMatchMakingHandler handler =
        Guido.getHandlerRegistry().getHandler(PGMMatchMakingHandler.class);
    if (handler == null) return new Result("Match making is disabled");
    Set<TeamMember> players = new HashSet<>();
    List<UUID> fakePlayers = this.fakePlayers();
    if (fakePlayers.size() < size * 2)
      return new Result("There's not enough players to make a fake match");
    for (int i = 0; i < size * 2; i++) {
      players.add(
          new SimpleTeamMember(
              new SimpleLinkableInfo(
                  LinkableType.MINECRAFT,
                  new SimpleValuesMap("uuid", UUIDUtils.trim(fakePlayers.get(i)))),
              TeamRole.NORMAL));
    }
    Set<MatchTeam> teams = Lots.set(new SimpleMatchTeam(-2, "participants", players));
    SimpleMatch match =
        new SimpleMatch(
            RandomUtils.nextString(2),
            718281601112604675L,
            teams,
            -1,
            new SimpleValuesMap("type", "pgm").put("ladder", ladder),
            MatchStatus.WAITING);
    if (handler.canHost(match)) {
      handler.host(match);
      return new Result("Handler is going to host the match");
    } else {
      return new Result("Handler cannot host the match");
    }
  }

  @Command(aliases = "info", description = "Get info about the match", permission = "guido.info")
  public void info() {
    PGMMatchMakingHandler handler =
        Guido.getHandlerRegistry().getHandler(PGMMatchMakingHandler.class);
    for (PGMHostedMatch match : handler.getMatches()) {
      System.out.println("match = " + match);
    }
  }

  @NonNull
  public List<UUID> fakePlayers() {
    return Lots.list(
        UUID.fromString("5eed208d-de58-4022-9ba7-6ccb5ea7e92a"),
        UUID.fromString("a1af8d6e-edb6-406b-b3a1-a95a42fdb03f"),
        UUID.fromString("ad83deef-c145-4b43-8756-1f099d5d4140"),
        UUID.fromString("554ee007-7cc6-4ae5-89fb-60d0e1e75871"),
        UUID.fromString("b3b98592-716d-4382-892d-578c96bfecbf"),
        UUID.fromString("f2b112e4-49e5-4b47-890b-5ec7103777f1"));
  }

  @Override
  public @NonNull String getName() {
    return "match";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
