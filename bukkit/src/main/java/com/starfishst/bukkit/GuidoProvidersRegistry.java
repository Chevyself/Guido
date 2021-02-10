package com.starfishst.bukkit;

import com.starfishst.commands.bukkit.messages.MessagesProvider;
import lombok.NonNull;
import me.googas.starbox.StarboxProvidersRegistry;

public class GuidoProvidersRegistry extends StarboxProvidersRegistry {

  public GuidoProvidersRegistry(@NonNull MessagesProvider messages) {
    super(messages);
  }
}
