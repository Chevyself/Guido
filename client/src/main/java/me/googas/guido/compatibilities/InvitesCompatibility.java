package me.googas.guido.compatibilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commands.bukkit.StarboxBukkitCommand;
import me.googas.commands.bukkit.context.CommandContext;
import me.googas.commands.providers.type.StarboxContextualProvider;
import me.googas.starbox.compatibilities.Compatibility;
import me.googas.starbox.modules.Module;
import org.bukkit.plugin.Plugin;

public class InvitesCompatibility implements Compatibility {

  @Getter @Setter private boolean enabled;

  @Override
  public @NonNull Collection<Module> getModules(@NonNull Plugin plugin) {
    return Collections.singletonList(new LinkCheck());
  }

  @Override
  public Collection<StarboxContextualProvider<?, CommandContext>> getProviders() {
    return new ArrayList<>();
  }

  @Override
  public @NonNull Collection<StarboxBukkitCommand> getCommands() {
    return new ArrayList<>();
  }

  @Override
  public @NonNull String getName() {
    return "Invites";
  }
}
