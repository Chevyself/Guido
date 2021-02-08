package me.googas.api.token;

/** The level of authentication for a token */
public enum AuthLevel {
  /** Means that the token is not authenticated */
  NONE(0),
  /** The token only has read permissions */
  READ(1),
  /** The token has read and write permissions */
  READ_WRITE(2),
  /** The token has every permission, does not require any further authentication */
  ADMINISTRATIVE(3);

  /**
   * The permission of the level. This means that the higher the permission the most permissions it
   * has
   */
  private final int permission;

  /**
   * Create the auth level
   *
   * @param status the permission of the level
   */
  AuthLevel(int status) {
    this.permission = status;
  }

  /**
   * Get the permission of the level
   *
   * @return the permission
   */
  public int intValue() {
    return this.permission;
  }
}
