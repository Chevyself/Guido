package me.googas.api.matches;

/** The status of the match */
public enum MatchStatus {
  /** When the match is being prepared */
  WAITING,
  /** When the match is ready to start */
  READY,
  /** When the match is about to start */
  STARTING,
  /** When the match is playing */
  PLAYING,
  /** When the match is finished */
  FINISHED,
}
