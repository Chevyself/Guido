package com.starfishst.bukkit.handlers.ui;

import com.starfishst.bukkit.util.ParsedMaterial;
import java.util.List;
import lombok.NonNull;

public interface Displayable {

  @NonNull
  String toConfig();

  @NonNull
  String toCompleteConfig();

  @NonNull
  ParsedMaterial getMaterial();

  @NonNull
  String getTitle();

  @NonNull
  List<String> getLore();
}
