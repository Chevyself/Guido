package me.googas.api.links;

/** The type of link of the data */
public enum LinkableDataType {
  /** Has a linked minecraft user */
  MINECRAFT,
  /** Has a linked user of discord */
  DISCORD,
  /** Has a linked member inside a guild */
  DISCORD_GUILD,
  /** Is not a known type */
  NONE
}
