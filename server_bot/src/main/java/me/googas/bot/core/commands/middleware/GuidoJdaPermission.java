package me.googas.bot.core.commands.middleware;

import lombok.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface GuidoJdaPermission {
  @NonNull
  String value();
}
