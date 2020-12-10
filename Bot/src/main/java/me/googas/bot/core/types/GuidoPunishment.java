package me.googas.bot.core.types;

import lombok.NonNull;
import me.googas.api.links.LinkableInfo;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

public class GuidoPunishment implements Punishment {

  @Override
  public void onRemove() throws Throwable {}

  @Override
  public @NonNull Time getToRemove() {
    return new Time(3, Unit.MINUTES);
  }

  @Override
  public @NonNull PunishmentType getType() {
    return null;
  }

  @Override
  public @NonNull PunishmentStatus getStatus() {
    return null;
  }

  @Override
  public LinkableInfo getPunisher() {
    return null;
  }

  @Override
  public @NonNull LinkableInfo getPunished() {
    return null;
  }

  @Override
  public @NonNull ValuesMap getDetails() {
    return null;
  }
}
