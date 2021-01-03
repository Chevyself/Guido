package com.starfishst.bukkit.dependencies.protocol.tab;

import java.util.UUID;
import lombok.NonNull;
import me.googas.commons.UUIDUtils;
import org.bukkit.GameMode;

/** An empty tab list entry */
public class TabListEmptyEntry extends TabListSkinEntry {

  /** The unique id of the entry */
  @NonNull private final UUID uuid = UUID.randomUUID();

  /**
   * Get the uuid of the entry
   *
   * @return the uuid of the entry
   */
  @Override
  public @NonNull UUID getUUID() {
    return this.uuid;
  }

  /**
   * Get the name of the entry
   *
   * @return the name of the entry
   */
  @Override
  public @NonNull String getName() {
    return UUIDUtils.trim(this.uuid).substring(0, 16);
  }

  /**
   * Get the display name for the entry
   *
   * @param tab the tab that requires the name
   * @return the display name of the entry
   */
  @Override
  public @NonNull String getDisplayName(@NonNull CustomTab tab) {
    return "";
  }

  /**
   * Get the latency of the entry
   *
   * @return the latency of the entry (ping)
   */
  @Override
  public int getLatency() {
    return 0;
  }

  /**
   * Get the game mode that the entry is in
   *
   * @return the game mode that the entry is in
   */
  @Override
  public @NonNull GameMode getGameMode() {
    return GameMode.SURVIVAL;
  }

  /**
   * Get the skin base 64
   *
   * @return the skin base 64
   */
  @NonNull
  @Override
  public String getSkin() {
    return "ewogICJ0aW1lc3RhbXAiIDogMTU5NzI4NTk0ODAwNCwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2ZjYzAxNGYyODdlOTQ1OWQ4NTFmYjE1NjBlNDU2ZGYxMzFhNTY4Njc4NTRkZDVlNjc1OTBjOTNlMDYwZTU5NCIKICAgIH0KICB9Cn0=";
  }

  /**
   * Get the skin signature base 64
   *
   * @return the skin signature base 64
   */
  @NonNull
  @Override
  public String getSignature() {
    return "V6OrU8iylqslPJzyy7UWbCpRgF0KWn9xqeHkg9GQRiiE79PBXNmekWJnKya72yH4eeIr9yVdERmzDiBxqDRYi79/kbH6BRGU7v9rkTiVDgElXxCcb+IDpvHftk8GWhWz1Vp60XAYSfIW0S+B2Hz9ccX3zQEodLZJFotKjYSypj/9V43FAQ3w25JR7HriPi4MsTg0m5yOqeu5d3rbZhgoPprxgMvTE6eLnGNMVEVslCsQrL77MHFYMQHXXStMW4qx4AzQoOvcdc2XPJXJy1F/RGStrZ8zQWCEVX3WPXsMLKuI0hjPvtn5ZCRR1KJx3xAmoqQLshWqoPCEeNEjkP26849gV+vHK+B3/tr8xa3r7wIy2a+bo78rk3aheSqVRB458kqrKHP9Npp1tqrLmDTGxFg9vZbtNRND0gnfVbYHxpnFneqHtzcXNTIkVU8YxoYLEXrrieKOPjE/RpiWtRVuqM8TLewz4+HA/X0jna5YHpXJhDGjgUXkVGRBXXbl46hsyJVYbYSZwSEMrAFQFpR6EdtB/8edOOVVgdJ7YQBZrNR6uvkyUtrG6BNgPfp6ShUiU42oByllzDeCiU5C3Q9DnnBNkivv6oY1AVNRJOyuWRJyvPz1yleKrzu3LtI/23PVHFka6tQkKf9alJmWzRpOxzzAy3m3FMIwjT/S1+Gn2MA=";
  }

  @Override
  public String toString() {
    return "TabListEmptyEntry{" + "uuid=" + this.uuid + "} " + super.toString();
  }
}
