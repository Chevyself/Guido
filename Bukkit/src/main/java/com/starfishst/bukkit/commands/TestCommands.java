package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.result.Result;
import java.util.Iterator;
import lombok.NonNull;
import tc.oc.pgm.api.PGM;
import tc.oc.pgm.api.match.Match;

/** Commands for testing */
public class TestCommands implements GuidoCommand {

  @Command(aliases = "test")
  public Result test() {
    Iterator<Match> matches = PGM.get().getMatchManager().getMatches();
    while (matches.hasNext()) {
      System.out.println(matches.next());
    }
    return new Result();
  }

  @Override
  public void setEnabled(boolean bol) {}

  @Override
  public @NonNull String getName() {
    return "test-commands";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
