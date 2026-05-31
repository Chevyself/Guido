package me.googas.bot.core.commands.middleware;

import lombok.NonNull;

public @interface GuidoJdaPermission {
  @NonNull
  String value();
}
