package me.googas.bot.core.handlers.responsive.command;

import lombok.NonNull;

public class SimpleCommandReactionResponse implements ExecuteCommandReactionResponse {

  /** The unicode or the name of the mote */
  @NonNull private final String unicode;

  /** The command that is being executed */
  @NonNull private final String command;

  /** The arguments to execute in the command execution */
  @NonNull private final String[] arguments;

  /**
   * Create the reaction response
   *
   * @param command the name of the command to execute
   * @param unicode the unicode or the name of the emote that executes it
   * @param arguments the arguments to include upon execution
   */
  public SimpleCommandReactionResponse(
      @NonNull String command, @NonNull String unicode, @NonNull String[] arguments) {
    this.command = command;
    this.unicode = unicode;
    this.arguments = arguments;
  }

  /** @deprecated This constructor may only be used by gson */
  public SimpleCommandReactionResponse() {
    this("", "", new String[0]);
  }

  @Override
  public @NonNull String getUnicode() {
    return this.unicode;
  }

  /**
   * Get the name of the command to execute
   *
   * @return the name of the command to execute
   */
  @Override
  public @NonNull String getCommandName() {
    return this.command;
  }

  /**
   * Get the arguments to use in the execution of the command
   *
   * @return the arguments
   */
  @NonNull
  @Override
  public String[] getArguments() {
    return this.arguments;
  }
}
