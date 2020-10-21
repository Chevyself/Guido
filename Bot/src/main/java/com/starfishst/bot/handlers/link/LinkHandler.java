package com.starfishst.bot.handlers.link;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.handlers.GuidoHandler;
import com.starfishst.bot.handlers.data.GuidoLinkedInfo;
import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;
import me.googas.commons.RandomUtils;
import me.googas.commons.time.Time;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Handles linking for accounts */
public class LinkHandler implements GuidoHandler {

  /** The set of queries created */
  @NotNull private final Set<LinkQuery> queries = new HashSet<>();

  /** Create the link handler */
  public LinkHandler() {
    Guido.getTimer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                LinkHandler.this.queries.removeIf(
                    query -> query.getToRemove() <= System.currentTimeMillis());
              }
            },
            1000L,
            1000L);
  }

  /**
   * Create the code for the given linked info
   *
   * @param info the info to create the code and link to an user
   * @return the created code if the data is found and it is linked
   */
  @Nullable
  public String createCode(@NotNull GuidoLinkedInfo info) {
    BotLinkedData data =
        Guido.getDataLoader().getLinkedData(info.getType(), info.getIdentification());
    // Check before if data is linked else it can be that the data is null which is an internal
    // error
    if (data != null && !data.isLinked()) {
      String code = this.nextCode();
      this.queries.add(
          new LinkQuery(code, info, System.currentTimeMillis(), Time.fromString("3m")));
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
  @NotNull
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
  private boolean contains(@NotNull String code) {
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
  @Nullable
  public GuidoLinkedInfo getInfo(String code) {
    for (LinkQuery query : this.queries) {
      if (query.getCode().equals(code)) {
        return query.getInfo();
      }
    }
    return null;
  }

  @Override
  public void close() {}

  @Override
  public void unregister() {}

  /** A link is an object used to identify the linking process for the given link and data */
  static class LinkQuery {

    /** The code that is used to identify the data */
    @NotNull private final String code;

    /** The information that will get the link data from the database */
    @NotNull private final GuidoLinkedInfo info;

    /** When this link will expire */
    private final long toRemove;

    /**
     * Create the link query
     *
     * @param code the link used to link the linked data
     * @param info the info to get the linked data
     * @param timeCreated when was the link query created
     * @param toRemove how long until it gets unloaded
     */
    LinkQuery(
        @NotNull String code,
        @NotNull GuidoLinkedInfo info,
        long timeCreated,
        @NotNull Time toRemove) {
      this.code = code;
      this.info = info;
      this.toRemove = timeCreated + toRemove.millis();
    }

    /**
     * Get the code used to link
     *
     * @return the code as a string
     */
    @NotNull
    public String getCode() {
      return this.code;
    }

    /**
     * Get the information of the data waiting to be linked
     *
     * @return the info
     */
    @NotNull
    public GuidoLinkedInfo getInfo() {
      return this.info;
    }

    /**
     * Get when the link should be removed in millis
     *
     * @return when the link should be removed in millis
     */
    public long getToRemove() {
      return this.toRemove;
    }
  }
}
