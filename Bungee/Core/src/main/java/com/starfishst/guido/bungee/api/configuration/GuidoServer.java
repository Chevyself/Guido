package com.starfishst.guido.bungee.api.configuration;

import com.starfishst.core.fallback.Fallback;
import java.net.InetSocketAddress;
import org.jetbrains.annotations.NotNull;

/** This object represents a server that can be connected to a bungee that has the guido plugin */
public interface GuidoServer {

  /**
   * Get the {@link #getAddress()} constructed
   *
   * @return the constructed Inet socket address
   */
  default InetSocketAddress constructAddress() {
    String address = this.getAddress();
    int port = 0;
    if (this.getAddress().contains(":")) {
      String[] split = address.split(":");
      address = split[0];
      try {
        port = Integer.parseInt(split[1]);
      } catch (NumberFormatException e) {
        Fallback.addError("Servers: " + Integer.parseInt(split[1]) + " is not a valid port");
      }
    }
    return new InetSocketAddress(address, port);
  }

  /**
   * Get the name of the server
   *
   * @return the name of the server
   */
  @NotNull
  String getName();

  /**
   * Get the address to which the server is linked to
   *
   * @return the address
   */
  @NotNull
  String getAddress();

  /**
   * Whether the setting restricted should be enabled
   *
   * @return the value of the setting
   */
  boolean isRestricted();
}
