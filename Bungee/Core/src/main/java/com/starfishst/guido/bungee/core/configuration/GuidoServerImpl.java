package com.starfishst.guido.bungee.core.configuration;

import com.starfishst.guido.bungee.api.configuration.GuidoServer;
import org.jetbrains.annotations.NotNull;

/** An implementation for guido servers */
public class GuidoServerImpl implements GuidoServer {

  /** The name of the server */
  @NotNull private final String name;

  /** The address of the server */
  @NotNull private final String address;

  /** Whether the server is restricted */
  private final boolean restricted;

  /**
   * Create the implementation
   *
   * @param name the name of the server
   * @param address the address
   * @param restricted whether it is restricted
   */
  public GuidoServerImpl(@NotNull String name, @NotNull String address, boolean restricted) {
    this.name = name;
    this.address = address;
    this.restricted = restricted;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public @NotNull String getAddress() {
    return this.address;
  }

  @Override
  public boolean isRestricted() {
    return this.restricted;
  }

  @Override
  public String toString() {
    return "GuidoServerImpl{"
        + "name='"
        + name
        + '\''
        + ", address='"
        + address
        + '\''
        + ", restricted="
        + restricted
        + '}';
  }
}
