package com.starfishst.bukkit.commands;

import com.starfishst.bukkit.annotations.Command;
import com.starfishst.bukkit.api.Guido;
import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.dependencies.protocol.VelocityImprovement;
import com.starfishst.core.annotations.Required;
import lombok.NonNull;

public class KnockbackCommand implements GuidoCommand {
  @Command(
      aliases = {"knockback", "kb"},
      description = "Change the kb of the server",
      permission = "guido.kb")
  public void knockback(
      @Required double horizontal,
      @Required double vertical,
      @Required double verticalAir,
      @Required double sprint,
      @Required double walk,
      @Required double friction) {
    VelocityImprovement listener = Guido.getListener(VelocityImprovement.class);
    if (listener == null) return;
    listener.setMultiplierHorizontal(horizontal);
    listener.setMultiplierVertical(vertical);
    listener.setMultiplierVerticalAir(vertical);
    listener.setMultiplierSprint(sprint);
    listener.setMultiplierWalk(walk);
    listener.setFriction(friction);
  }

  @Override
  public void setEnabled(boolean bol) {}

  @Override
  public @NonNull String getName() {
    return "knockback";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
