package me.googas.bungee.listeners;

import lombok.NonNull;
import me.googas.bungee.events.GuidoListener;

public class TipsListener implements GuidoListener {

  @Override
  public @NonNull String getName() {
    return "tips";
  }

  /*
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
    GuidoBungee.getScheduler()
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


   */
}
