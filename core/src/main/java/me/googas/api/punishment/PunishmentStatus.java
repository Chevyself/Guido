package me.googas.api.punishment;

/** The status of a punishment */
public enum PunishmentStatus {
  /** The status of the punishment is not known */
  UNKNOWN,
  /** The punishment is still active */
  ACTIVE,
  /** The punishment can be forgotten */
  ARCHIVED
}
