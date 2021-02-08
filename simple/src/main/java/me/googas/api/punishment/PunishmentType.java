package me.googas.api.punishment;

/** Used to differentiate punishments */
public enum PunishmentType {
  /** A punishment that might be deprecated or the type is incorrect */
  UNKNOWN,
  /** A low importance punishment */
  WARN,
  /** An unique type of punishment which will not allow an user to talk */
  MUTE,
  /** A medium importance punishment the user got kicked out of a service */
  KICK,
  /** A high importance punishment the user cannot use a service for a period of time */
  BAN,
  /** Just like a ban but now the user cannot use the service ever */
  BLACKLIST
}
