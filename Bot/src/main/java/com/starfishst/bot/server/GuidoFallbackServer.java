package com.starfishst.bot.server;

import java.util.HashSet;
import java.util.Set;
import me.googas.messaging.api.Messenger;
import me.googas.messaging.api.Server;
import org.jetbrains.annotations.NotNull;

/** A fallback server in case {@link GuidoServer does not work} */
public class GuidoFallbackServer implements Server {

  @Override
  public @NotNull Set<? extends Messenger> getClients() {
    return new HashSet<>();
  }

  @Override
  public boolean requiresAuthentication() {
    return false;
  }

  @Override
  public void setRequiresAuthentication(boolean bol) {}

  @Override
  public void start() {}

  @Override
  public void close() {}
}
