package me.googas.api.server.receptors;

import java.util.Collection;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.links.LinkableInfo;
import me.googas.api.loader.PunishmentLoader;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;

public class PunishmentReceptors {

  @NonNull private final PunishmentLoader loader;

  public PunishmentReceptors(@NonNull PunishmentLoader loader) {
    this.loader = loader;
  }

  @Receptor(Requests.Punishments.PUNISHMENT)
  public Punishment getPunishment(@ParamName("id") String id) {
    return this.loader.getPunishment(id);
  }

  @Receptor(Requests.Punishments.PUNISHMENTS)
  public Collection<Punishment> getPunishments(
      @ParamName("link") LinkableInfo link, @ParamName("statuses") PunishmentStatus[] statuses) {
    return this.loader.getPunishments(link, statuses);
  }

  @Receptor(Requests.Punishments.CREATE)
  public Punishment create(
      @ParamName("type") PunishmentType type,
      @ParamName("status") PunishmentStatus status,
      @ParamName("information") Map<String, Map<String, Object>> information,
      @ParamName("punisher") LinkableInfo punisher,
      @ParamName("punished") LinkableInfo punished,
      @ParamName("getExpires") long expires) {
    return new Punishment(type, information, punisher, punished, status, expires);
  }

  @Receptor(Requests.Punishments.STATUS)
  public boolean status(@ParamName("id") String id, @ParamName("status") PunishmentStatus status) {
    Punishment punishment = this.getPunishment(id);
    if (punishment == null) return false;
    punishment.setStatus(status);
    return true;
  }

  @Receptor(Requests.Punishments.EXPIRES)
  public boolean expires(@ParamName("id") String id, @ParamName("getExpires") long expires) {
    Punishment punishment = this.getPunishment(id);
    if (punishment == null) return false;
    return punishment.setExpires(expires);
  }

  @Receptor(Requests.Punishments.DETAIL)
  public boolean detail(
      @ParamName("id") String id, @ParamName("key") String key, @ParamName("value") Object value) {
    Punishment punishment = this.getPunishment(id);
    if (punishment == null) return false;
    punishment.set(null, key, value);
    return true;
  }

  @Receptor(Requests.Punishments.REMOVE_DETAIL)
  public boolean removeDetail(@ParamName("id") String id, @ParamName("key") String key) {
    Punishment punishment = this.getPunishment(id);
    if (punishment == null) return false;
    punishment.set(null, key, null);
    return true;
  }
}
