package me.googas.api;

/**
 * This object when implemented or extended means that it can expire -1 is for never getExpires else
 * check {@link #isExpired()}
 */
public interface Expirable {

  /**
   * Get the millis of the time when it getExpires
   *
   * @return the millis of the time when the permission getExpires
   */
  long getExpires();

  /**
   * Set when does this expire
   *
   * @param expires the new expire date
   * @return whether the expire date was updated
   */
  boolean setExpires(long expires);

  /**
   * Checks whether it has expired
   *
   * @return true if the permission expired
   */
  default boolean isExpired() {
    return this.getExpires() != -1 && this.getExpires() < System.currentTimeMillis();
  }
}
