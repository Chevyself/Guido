package me.googas.bungee.commands;

/** Commands for linking minecraft accounts */
// TODO fix link command
public class LinkCommand {

  /*
  @Settings("async")
  @Command(aliases = "link")
  public void link(ProxiedPlayer player, BungeeLocaleFile locale, JsonClient client) {
    // TODO make proxied offline player as an extra argument
    ProxiedOfflinePlayer offline = new ProxiedOfflinePlayer(player.getUniqueId(), player.getName());
    LinkableInfo link = offline.getLink();
    Requests.Links.isLinked(link)
        .send(
            client,
            Requests.ifPresentElse(
                linked -> {
                  if (!linked) {
                    Requests.Server.linkCode(link)
                        .send(
                            client,
                            Requests.ifPresentElse(
                                code ->
                                    player.sendMessage(
                                        locale.getComponent(
                                            "link.code", Maps.singleton("code", code))),
                                () -> {
                                  // TODO could not retrieve code
                                }));
                  } else {
                    player.sendMessage(locale.getComponent("link.linked"));
                  }
                },
                () -> {
                  // TODO could not check if is linked
                }));
  }

     */
}
