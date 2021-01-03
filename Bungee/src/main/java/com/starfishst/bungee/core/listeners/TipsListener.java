package com.starfishst.bungee.core.listeners;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.api.events.GuidoListener;
import com.starfishst.bungee.utils.BungeeUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import me.googas.commons.RandomUtils;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TipsListener implements GuidoListener {

  @NonNull private final List<BaseComponent[]> messages = new ArrayList<>();

  @NonNull
  private List<String> getMessages() {
    return this.getSettings().getListSetting("messages");
  }

  @Override
  public void onEnable() {
    for (String message : this.getMessages()) {
      this.messages.add(BungeeUtils.getComponent(BungeeUtils.build(message)));
    }
    Guido.getScheduler()
        .repeat(
            new Time(5, Unit.MINUTES),
            new Time(5, Unit.MINUTES),
            () -> {
              if (this.messages.isEmpty()) return;
              for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
                BaseComponent[] random = RandomUtils.getRandom(this.messages);
                if (player.hasPermission("guido.tips")) return;
                player.sendMessage(random);
              }
            });
  }

  @Override
  public @NonNull String getName() {
    return "tips";
  }
}
