package com.starfishst.bukkit.modules;

import com.starfishst.bukkit.Guido;
import lombok.NonNull;
import me.googas.starbox.Starbox;
import me.googas.starbox.modules.data.DataModule;
import me.googas.starbox.modules.data.type.Profile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class StartMoneyModule implements GuidoModule {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    DataModule dataModule = Starbox.getModuleRegistry().get(DataModule.class);
    if (dataModule == null) return;
    Profile profile = dataModule.getPlayersHandler().getPlayer(player);
    this.checkAccount(profile, Starbox.buildContext(player));
    this.checkAccount(profile, Starbox.getContext());
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
    Player player = event.getPlayer();
    DataModule dataModule = Starbox.getModuleRegistry().get(DataModule.class);
    if (dataModule == null) return;
    Profile profile = dataModule.getPlayersHandler().getPlayer(player);
    this.checkAccount(profile, Starbox.buildContext(player));
  }

  public void checkAccount(@NonNull Profile profile, @NonNull String context) {
    if (!profile.hasAccount(context)) {
      profile.createAccount(context);
      profile.deposit(Guido.getConfiguration().getCurrency().getStart(), context);
    }
  }

  @Override
  public @NonNull String getName() {
    return "Start Money";
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
