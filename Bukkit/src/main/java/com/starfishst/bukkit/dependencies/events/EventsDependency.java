package com.starfishst.bukkit.dependencies.events;

import com.starfishst.bukkit.api.commands.GuidoCommand;
import com.starfishst.bukkit.api.dependencies.Dependency;
import com.starfishst.bukkit.api.events.GuidoListener;
import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.core.providers.type.IContextualProvider;
import java.util.ArrayList;
import java.util.Collection;
import lombok.NonNull;
import org.bukkit.plugin.Plugin;

public class EventsDependency implements Dependency {

  private boolean enabled = false;

  @Override
  public @NonNull Collection<GuidoListener> getListeners(@NonNull Plugin plugin) {
    return new ArrayList<>();
  }

  @Override
  public Collection<IContextualProvider<?, CommandContext>> getProviders() {
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
  public @NonNull Collection<GuidoCommand> getCommands() {
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
