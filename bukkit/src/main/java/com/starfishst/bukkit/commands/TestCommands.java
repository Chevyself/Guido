package com.starfishst.bukkit.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.result.Result;
import lombok.NonNull;

/** Commands for testing */
public class TestCommands implements GuidoCommand {

  @Command(aliases = "test")
  public Result test() {
    return Result.of("");
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
