package me.googas.bot.core.commands.middleware;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import lombok.NonNull;

@Retention(RetentionPolicy.RUNTIME)
public @interface GuidoJdaPermission {
  @NonNull
  String value();
}
