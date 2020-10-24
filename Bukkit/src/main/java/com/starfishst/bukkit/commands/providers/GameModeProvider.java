package com.starfishst.bukkit.commands.providers;

import com.starfishst.bukkit.context.CommandContext;
import com.starfishst.bukkit.providers.type.BukkitArgumentProvider;
import com.starfishst.bukkit.utils.BukkitUtils;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.googas.commons.Lots;
import org.bukkit.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Provides the command manager with {@link GameMode} */
public class GameModeProvider implements BukkitArgumentProvider<GameMode> {

  @Override
  public @NotNull Class<GameMode> getClazz() {
    return GameMode.class;
  }

  @NotNull
  @Override
  public GameMode fromString(@NotNull String s, @NotNull CommandContext context)
      throws ArgumentProviderException {
    GuidoGameMode guidoGameMode = GuidoGameMode.fromString(s);
    if (guidoGameMode != null) {
      return guidoGameMode.getBukkit();
    } else {
      throw new ArgumentProviderException(BukkitUtils.build("&e" + s + " &cis not a gamemode!"));
    }
  }

  @Override
  public @NotNull List<String> getSuggestions(@NotNull String s, CommandContext commandContext) {
    List<String> names = new ArrayList<>();
    for (GameMode value : GameMode.values()) {
      names.add(value.toString().toLowerCase());
    }
    return names;
  }

  /** An implementation to easily get gamemodes */
  enum GuidoGameMode {
    SURVIVAL(Lots.set("0", "s", "survival"), GameMode.SURVIVAL),

    CREATIVE(Lots.set("1", "c", "creative"), GameMode.CREATIVE),

    ADVENTURE(Lots.set("2", "a", "adventure"), GameMode.ADVENTURE),

    SPECTATOR(Lots.set("3", "o", "spectator"), GameMode.SPECTATOR);

    /** The set of aliases that can be used to identify the gamemode */
    @NotNull private final Set<String> aliases;

    /** The bukkit gamemode */
    private final GameMode bukkit;

    /**
     * Create the guido gamemode
     *
     * @param aliases the aliases for the gamemode
     * @param bukkit the bukkit gamemode
     */
    GuidoGameMode(@NotNull Set<String> aliases, GameMode bukkit) {
      this.aliases = aliases;
      this.bukkit = bukkit;
    }

    /**
     * Get the guido gamemode from a string
     *
     * @param string the string to get it from
     * @return the guido gamemode if matches else null
     */
    @Nullable
    public static GuidoGameMode fromString(@NotNull String string) {
      for (GuidoGameMode value : GuidoGameMode.values()) {
        for (String alias : value.getAliases()) {
          if (alias.equalsIgnoreCase(string)) {
            return value;
          }
        }
      }
      return null;
    }

    /**
     * Get the set of aliases of this gamemode
     *
     * @return the list of aliases
     */
    @NotNull
    public Set<String> getAliases() {
      return this.aliases;
    }

    /**
     * Get the bukkit counterpart from this gamemode
     *
     * @return the bukkit counterpart
     */
    @NotNull
    public GameMode getBukkit() {
      return this.bukkit;
    }
  }
}
