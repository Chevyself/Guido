package me.googas.bot.core.handlers.link;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.bot.api.Guido;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.commons.RandomUtils;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Handles linking for accounts */
public class LinkHandler implements GuidoHandler {

  /** The set of queries created */
  @NonNull private final Set<LinkQuery> queries = new HashSet<>();

  /**
   * Create the code for the given linked info
   *
   * @param info the info to create the code and link to an user
   * @return the created code if the data is found and it is linked
   */
  public String createCode(@NonNull LinkableInfo info) {
    Linkable data =
        Guido.getHandlers()
            .getLoader()
            .getLinks()
            .getLink(info.getType(), info.getIdentification());
    if (data != null && !data.isLinked()) {
      String code = this.nextCode();
      LinkQuery linkQuery = new LinkQuery(code, info);
      this.queries.add(linkQuery);
      Guido.getScheduler()
          .countdown(new Time(3, Unit.MINUTES), second -> {}, () -> this.queries.remove(linkQuery));
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
   * @param code the code to get the info
   * @return the linked info if there is one for the given code else null
   */
  public LinkableInfo getInfo(String code) {
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

    /** The information that will get the link data from the database */
    @NonNull @Getter private final LinkableInfo info;

    /**
     * Create the link query
     *
     * @param code the link used to link the linked data
     * @param info the info to get the linked data
     */
    LinkQuery(@NonNull String code, @NonNull LinkableInfo info) {
      this.code = code;
      this.info = info;
    }
  }

  /**
   * Create a link code for the linked given info
   *
   * @param info the information of the link to link
   * @return the link
   */
  @Receptor(Requests.Server.LINK_CODE)
  public String linkCode(@ParamName("link") LinkableInfo info) {
    return Guido.getHandlers().getHandler(LinkHandler.class).createCode(info);
  }

  @Override
  public boolean hasReceptors() {
    return true;
  }
}
