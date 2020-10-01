package com.starfishst.bot.server;

import com.starfishst.guido.api.implementations.messaging.Messenger;
import com.starfishst.guido.api.implementations.messaging.Server;
import java.util.HashSet;
import java.util.Set;
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
