package me.googas.guido.compatibilities;

import java.util.Optional;
import lombok.NonNull;
import me.googas.commands.bukkit.utils.BukkitUtils;
import me.googas.guido.Guido;
import me.googas.invites.TeamMember;
import me.googas.invites.events.InvitesCancellable;
import me.googas.invites.events.teams.AsyncSwitchTeamEvent;
import me.googas.invites.events.teams.AsyncTeamPreCreationEvent;
import me.googas.net.api.exception.MessengerListenFailException;
import me.googas.net.api.messages.RequestBuilder;
import me.googas.starbox.modules.Module;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class LinkCheck implements Module {

  // TODO this is hard coded atm but it must be changed to config for further tournaments
  private final int max = 7;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onAsyncTeamPreCreation(AsyncTeamPreCreationEvent event) {
    this.check(event.getUser(), event);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onAsyncTeamSwitch(AsyncSwitchTeamEvent event) {
    TeamMember member = event.getMember();
    this.check(member, event);
    if (!event.isCancelled() && event.getTeam().isPresent()) {
      if (event.getTeam().get().getMembers().size() > this.max) {
        member.send(BukkitUtils.format("&cTeam is full"));
      }
    }
  }

  public void check(@NonNull TeamMember member, @NonNull InvitesCancellable cancellable) {
    if (cancellable.isCancelled()) return;
    Guido.getClient()
        .getSocket()
        .ifPresent(
            client -> {
              boolean check = true;
              try {
                Optional<Boolean> response =
                    client.sendRequest(
                        new RequestBuilder<>(Boolean.class, "links/has")
                            .put("uuid", member.getUniqueId())
                            .build());
                if (response.isPresent()) {
                  if (!response.get()) {
                    member.send(BukkitUtils.format("&cYou must link a Discord account first"));
                    cancellable.setCancelled(true);
                  }
                } else {
                  cancellable.setCancelled(true);
                  check = false;
                }
              } catch (MessengerListenFailException e) {
                Guido.handle(e);
                cancellable.setCancelled(true);
                check = false;
              }
              if (!check) {
                member.send(
                    BukkitUtils.format(
                        "&cWe could not check if you have a linked Discord account. Please try again later"));
              }
            });
  }

  @Override
  public @NonNull String getName() {
    return "link-check";
  }
}
