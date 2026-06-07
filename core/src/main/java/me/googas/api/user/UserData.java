package me.googas.api.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.loader.Loader;

public interface UserData {
  @NonNull
  UUID getId();

  @NonNull
  default Collection<Linkable> getLinkedAccounts(@NonNull Loader loader) {
    List<Linkable> list = new ArrayList<>(2);
    loader.getMinecraftLinks().getByLinkedUser(this.getId()).ifPresent(list::add);
    loader.getDiscordLinks().getByLinkedUser(this.getId()).ifPresent(list::add);
    return list;
  }
}
