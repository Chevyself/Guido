package me.googas.bot.core.handlers.link;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.links.Linkable;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.utility.RandomUtils;
import me.googas.bot.api.Guido;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;

/** Handles linking for accounts */
public class LinkHandler implements GuidoHandler {

  @NonNull private final GuidoBotRuntime runtime;

  /** The set of queries created */
  @NonNull private final Set<LinkQuery> queries = new HashSet<>();

  public LinkHandler(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  /**
   * Create the code for the given linkable
   *
   * @param data the info to create the code and link to a user
   * @return the created code if the data is found and it is linked
   */
  public String createCode(@NonNull Linkable data) {
    if (!data.isLinked()) {
      String code = this.nextCode();
      LinkQuery linkQuery = new LinkQuery(code, data);
      this.queries.add(linkQuery);
      Guido.getScheduler()
          .countdown(Time.of(3, Unit.MINUTES), second -> {}, () -> this.queries.remove(linkQuery));
      return code;
    } else {
      return null;
    }
  }

  /**
   * Get the next code to link an account
   *
   * @return the next code
   */
  @NonNull
  private String nextCode() {
    String code = RandomUtils.nextString(4);
    while (this.contains(code)) {
      code = RandomUtils.nextString(4);
    }
    return code;
  }

  /**
   * Check whether this contains certain code
   *
   * @param code the code to check if it is contained
   * @return true if there's already a code like it
   */
  private boolean contains(@NonNull String code) {
    for (LinkQuery query : this.queries) {
      if (query.getCode().equalsIgnoreCase(code)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get the linked info given the code
   *
   * @param code the code to getId the info
   * @return the linked info if there is one for the given code else null
   */
  public Linkable getLinkable(String code) {
    for (LinkQuery query : this.queries) {
      if (query.getCode().equals(code)) {
        return query.getInfo();
      }
    }
    return null;
  }

  @Override
  public void onDisable() {}

  @Override
  public void unregister() {}

  /** A link is an object used to identify the linking process for the given link and data */
  static class LinkQuery {

    /** The code that is used to identify the data */
    @NonNull @Getter private final String code;

    /** The information that will getId the link data from the database */
    @NonNull @Getter private final Linkable info;

    /**
     * Create the link query
     *
     * @param code the link used to link the linked data
     * @param info the info to getId the linked data
     */
    LinkQuery(@NonNull String code, @NonNull Linkable info) {
      this.code = code;
      this.info = info;
    }
  }

  @Receptor(Requests.MinecraftLinks.LINK_NEW)
  public String linkCode(@ParamName(Requests.MinecraftLinks.LINK_NEW) UUID minecraftId) {
    return runtime
        .getLoader()
        .getMinecraftLinks()
        .getById(minecraftId)
        .map(
            link -> {
              return runtime.getHandlers().getHandler(LinkHandler.class).createCode(link);
            })
        .orElse("");
  }

  @Receptor(Requests.MinecraftLinks.UPDATE_STATUS)
  public MinecraftLinkable updateStatus(
      @ParamName(Requests.MinecraftLinks.UPDATE_ONLINE_UUID) UUID minecraftId,
      @ParamName(Requests.MinecraftLinks.UPDATE_STATUS_NICKNAME) String nickname,
      @ParamName(Requests.MinecraftLinks.UPDATE_STATUS_IP) String ip,
      @ParamName(Requests.MinecraftLinks.UPDATE_STATUS_ONLINE) boolean online) {
    return runtime
        .getLoader()
        .getMinecraftLinks()
        .updateOrCreate(minecraftId, nickname, ip, online);
  }

  @Override
  public boolean hasReceptors() {
    return true;
  }
}
