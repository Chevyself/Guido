package me.googas.api.loader;

import lombok.NonNull;

public interface DataLoader {

  @NonNull
  Loader getLoader();
}
