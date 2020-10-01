package com.starfishst.guido.api;

/** A simple ping to send between messengers */
public class Ping {

  /** The ping given in milliseconds */
  private final double millis;

  /**
   * Create the ping
   *
   * @param millis the ping given in milliseconds
   */
  public Ping(double millis) {
    this.millis = millis;
  }

  /**
   * Get the milliseconds that took the ping
   *
   * @return the seconds that took the ping
   */
  public double getMillis() {
    return millis;
  }
}
