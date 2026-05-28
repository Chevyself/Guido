package com.starfishst.bukkit.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.result.Result;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;

public class SudoCommand implements GuidoCommand {

  @Getter @Setter private boolean enabled;

  @Command(aliases = "sudo", description = "Sudo a chat to another player")
  public Result sudo(
      @Free(name = "player", description = "The player to run the command as") Player player,
      @Free(name = "chat") String string) {
    player.chat(string);
    return Result.of("Done");
  }

  @Override
  public @NonNull String getName() {
    return "sudo";
  }
}
