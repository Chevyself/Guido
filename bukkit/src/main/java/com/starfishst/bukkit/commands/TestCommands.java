package com.starfishst.bukkit.commands;

import com.starfishst.commands.bukkit.annotations.Command;
import com.starfishst.commands.bukkit.result.Result;
import lombok.NonNull;

/** Commands for testing */
public class TestCommands implements GuidoCommand {

  @Command(aliases = "test")
  public Result test() {
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
