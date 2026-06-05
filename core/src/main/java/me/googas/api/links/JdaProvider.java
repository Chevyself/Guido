package me.googas.api.links;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;

public interface JdaProvider {
  @NonNull
  Guild getGuild();
}
