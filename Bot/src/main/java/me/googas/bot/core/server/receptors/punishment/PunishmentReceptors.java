package me.googas.bot.core.server.receptors.punishment;

import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.bot.Guido;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.punishment.GuidoPunishment;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class PunishmentReceptors {

  @Receptor("punishment/create")
  public Punishment create(
      @ParamName("type") PunishmentType type,
      @ParamName("status") PunishmentStatus status,
      @ParamName("punisher") LinkableInfo punisher,
      @ParamName("punished") LinkableInfo punished,
      @ParamName("details") GuidoValuesMap details,
      @ParamName("expires") long expires) {
    return new GuidoPunishment(type, status, punisher, punished, details, expires).cache();
  }

  @Receptor("punishment/status")
  public boolean status(@ParamName("id") String id, @ParamName("status") PunishmentStatus status) {
    Punishment punishment = this.getPunishment(id);
    if (punishment == null) return false;
    return punishment.setStatus(status);
  }

  @Receptor("punishment/expires")
  public boolean expires(@ParamName("id") String id, @ParamName("expires") long expires) {
    Punishment punishment = this.getPunishment(id);
    if (punishment == null) return false;
    return punishment.setExpires(expires);
  }

  @Receptor("punishment/detail")
  public boolean detail(
      @ParamName("id") String id, @ParamName("key") String key, @ParamName("value") Object value) {
    Punishment punishment = this.getPunishment(id);
    if (punishment == null) return false;
    punishment.getDetails().put(key, value);
    return true;
  }

  @Receptor("punishment/remove-detail")
  public boolean removeDetail(@ParamName("id") String id, @ParamName("key") String key) {
    Punishment punishment = this.getPunishment(id);
    if (punishment == null) return false;
    punishment.getDetails().remove(key);
    return true;
  }

  @Nullable
  public Punishment getPunishment(@NonNull String id) {
    return Guido.getDataLoader().getPunishment(id);
  }
}
