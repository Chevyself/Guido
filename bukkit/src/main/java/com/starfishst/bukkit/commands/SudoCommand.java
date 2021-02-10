package com.starfishst.bukkit.commands;

import com.starfishst.commands.bukkit.annotations.Command;
import com.starfishst.commands.bukkit.result.Result;
import com.starfishst.core.annotations.Multiple;
import com.starfishst.core.annotations.Required;
import com.starfishst.core.objects.JoinedStrings;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.entity.Player;

public class SudoCommand implements GuidoCommand {

  @Getter @Setter private boolean enabled;

  @Command(aliases = "sudo", description = "Sudo a chat to another player")
  public Result sudo(
      @Required(name = "player", description = "The player to run the command as") Player player,
      @Multiple @Required(name = "chat") JoinedStrings strings) {
    player.chat(strings.getString());
    return new Result("Done");
  }

  @Override
  public @NonNull String getName() {
    return "sudo";
  }
}
