package com.starfishst.bukkit.dependencies.events;

import com.starfishst.commands.bukkit.context.CommandContext;
import com.starfishst.core.providers.type.IContextualProvider;
import java.util.ArrayList;
import java.util.Collection;
import lombok.NonNull;
import me.googas.starbox.StarboxCommand;
import me.googas.starbox.compatibilities.Compatibility;
import me.googas.starbox.modules.Module;
import org.bukkit.plugin.Plugin;

public class EventsCompatibility implements Compatibility {

  private boolean enabled = false;

  @Override
  public Collection<IContextualProvider<?, CommandContext>> getProviders() {
    return new ArrayList<>();
  }

  @Override
  public @NonNull Collection<Module> getModules(@NonNull Plugin plugin) {
    return new ArrayList<>();
  }

  @Override
  public void setEnabled(boolean bol) {
    this.enabled = bol;
    if (bol) {
      GuidoTeamParser.enable();
    }
  }

  @Override
  public @NonNull Collection<StarboxCommand> getCommands() {
    return new ArrayList<>();
  }

  @Override
  public boolean isEnabled() {
    return this.enabled;
  }

  @Override
  public @NonNull String getName() {
    return "Events";
  }
}
