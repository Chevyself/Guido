package me.googas.api.server.receptors;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.annotations.Nullable;
import me.googas.api.ValuesMap;
import me.googas.api.links.LinkableInfo;
import me.googas.api.loader.PunishmentLoader;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class PunishmentReceptors {

  @NonNull private final PunishmentLoader loader;
  @NonNull @Getter @Setter private PunishmentSupplier punishmentSupplier;

  public PunishmentReceptors(
      @NonNull PunishmentLoader loader, @NonNull PunishmentSupplier punishmentSupplier) {
    this.loader = loader;
    this.punishmentSupplier = punishmentSupplier;
  }

  @Receptor("punishment/create")
  public Punishment create(
      @ParamName("type") PunishmentType type,
      @ParamName("status") PunishmentStatus status,
      @ParamName("punisher") LinkableInfo punisher,
      @ParamName("punished") LinkableInfo punished,
      @ParamName("details") ValuesMap details,
      @ParamName("expires") long expires) {
    return this.punishmentSupplier.create(type, status, punisher, punished, details, expires);
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
    return this.loader.getPunishment(id);
  }

  interface PunishmentSupplier {
    @NonNull
    Punishment create(
        @NonNull PunishmentType type,
        @NonNull PunishmentStatus status,
        @NonNull LinkableInfo punisher,
        @NonNull LinkableInfo punished,
        @NonNull ValuesMap details,
        long expires);
  }
}
