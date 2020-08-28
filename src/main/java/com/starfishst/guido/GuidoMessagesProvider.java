package com.starfishst.guido;

import com.starfishst.commands.context.CommandContext;
import com.starfishst.commands.messages.MessagesProvider;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.utils.time.Time;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The messages provider for the guido bot */
@Deprecated
public class GuidoMessagesProvider implements MessagesProvider {
  @Override
  public @NotNull String invalidLong(@NotNull String s, @NotNull CommandContext commandContext) {
    return s + " no es un numero";
  }

  @Override
  public @NotNull String invalidInteger(@NotNull String s, @NotNull CommandContext commandContext) {
    return s + " no es un numero";
  }

  @Override
  public @NotNull String invalidDouble(@NotNull String s, @NotNull CommandContext commandContext) {
    return s + " no es un numero";
  }

  @Override
  public @NotNull String invalidBoolean(@NotNull String s, @NotNull CommandContext commandContext) {
    return s + " no es un boolean";
  }

  @Override
  public @NotNull String invalidTime(@NotNull String s, @NotNull CommandContext commandContext) {
    return s + " es invalido para tiempo";
  }

  @Override
  public @NotNull String missingArgument(
      @NotNull String s, @NotNull String s1, int i, @NotNull CommandContext context) {
    return "Te falta " + s + ": " + s1 + " en la posicion: " + i;
  }

  @Override
  public @NotNull String invalidNumber(@NotNull String s, @NotNull CommandContext commandContext) {
    return s + " no es un numero";
  }

  @Override
  public @NotNull String emptyDouble(@NotNull CommandContext commandContext) {
    return "Los numeros decimales no pueden estar vacios";
  }

  @Override
  public @NotNull String commandNotFound(
      @NotNull String s, @NotNull CommandContext commandContext) {
    return "No existe el comando: " + " :(";
  }

  @Override
  public @NotNull String footer(@Nullable CommandContext commandContext) {
    return "Radiator Springs";
  }

  @Override
  public @NotNull String getTitle(
      @NotNull ResultType resultType, @Nullable CommandContext commandContext) {
    switch (resultType) {
      case ERROR:
        return "Error: ";
      case USAGE:
        return "Usaste mal el comando: ";
      case GENERIC:
        return "Hecho!";
      case UNKNOWN:
        return "Error desconocido: ";
      case PERMISSION:
        return "Error de permisos: ";
      default:
        return resultType.getTitle(null, commandContext);
    }
  }

  @Override
  public @NotNull String response(
      @NotNull String s, @NotNull String s1, @Nullable CommandContext commandContext) {
    return s1 + " \n " + s1;
  }

  @Override
  public @NotNull String notAllowed(@NotNull CommandContext commandContext) {
    return "No tienes permitido usar este comando!";
  }

  @Override
  public @NotNull String guildOnly(@NotNull CommandContext commandContext) {
    return "Solo puedes ejecutar este comando en Radiator Springs! https://discord.gg/SRc5P9g";
  }

  @Override
  public @NotNull String thumbnailUrl(@Nullable CommandContext commandContext) {
    return "";
  }

  @Override
  public @NotNull String cooldown(@NotNull Time time, @Nullable CommandContext commandContext) {
    return "Estas en cooldown por los siguientes " + time.toEffectiveString();
  }

  @Override
  public @NotNull String invalidUser(@NotNull String s, @NotNull CommandContext commandContext) {
    return s + " no es un usuario valido";
  }

  @Override
  public @NotNull String invalidMember(@NotNull String s, @NotNull CommandContext commandContext) {
    return s + " no es un miembro valido";
  }

  @Override
  public @NotNull String invalidRole(@NotNull String s, @NotNull CommandContext commandContext) {
    return s + " no es un rol valido";
  }

  @Override
  public @NotNull String invalidTextChannel(String s, CommandContext commandContext) {
    return s + " no es un text channel";
  }
}
