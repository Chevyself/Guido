package com.starfishst.bungee.core.configuration;

import com.starfishst.bungee.api.configuration.GuidoServer;
import lombok.NonNull;

/** An implementation for guido servers */
public class SimpleGuidoServer implements GuidoServer {

  /** The name of the server */
  @NonNull private final String name;

  /** The address of the server */
  @NonNull private final String address;

  /** Whether the server is restricted */
  private final boolean restricted;

  /**
   * Create the implementation
   *
   * @param name the name of the server
   * @param address the address
   * @param restricted whether it is restricted
   */
  public SimpleGuidoServer(@NonNull String name, @NonNull String address, boolean restricted) {
    this.name = name;
    this.address = address;
    this.restricted = restricted;
  }

  @Override
  public @NonNull String getName() {
    return this.name;
  }

  @Override
  public @NonNull String getAddress() {
    return this.address;
  }

  @Override
  public boolean isRestricted() {
    return this.restricted;
  }

  @Override
  public String toString() {
    return "SimpleGuidoServer{"
        + "name='"
        + this.name
        + '\''
        + ", address='"
        + this.address
        + '\''
        + ", restricted="
        + this.restricted
        + '}';
  }
}
