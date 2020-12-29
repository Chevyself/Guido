package me.googas.api;

/**
 * This object when implemented or extended means that it can expire -1 is for never expires else
 * check {@link #isExpired()}
 */
public interface Expirable {

  /**
   * Get the millis of the time when it expires
   *
   * @return the millis of the time when the permission expires
   */
  long expires();

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
    return this.expires() != -1 && this.expires() < System.currentTimeMillis();
  }
}
