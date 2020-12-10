package com.starfishst.bungee.api.configuration;

import com.starfishst.bungee.api.Guido;
import java.net.InetSocketAddress;
import lombok.NonNull;
import me.googas.commons.fallback.Fallback;

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
    Guido.getLogger().info("Using " + address + " and port " + port);
    return new InetSocketAddress(address, port);
  }

  /**
   * Get the name of the server
   *
   * @return the name of the server
   */
  @NonNull
  String getName();

  /**
   * Get the address to which the server is linked to
   *
   * @return the address
   */
  @NonNull
  String getAddress();

  /**
   * Whether the setting restricted should be enabled
   *
   * @return the value of the setting
   */
  boolean isRestricted();
}
