package com.starfishst.bukkit.util;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.bukkit.inventory.Inventory;

public class Numbers {

  public static double valueOfPercentage(double intValue, double percentage) {
    return (percentage * intValue) / 100;
  }

  @NonNull
  public static List<Integer> getSpaces(@NonNull Inventory inventory) {
    List<Integer> spaces = new ArrayList<>();
    for (int i = 0; i < inventory.getSize(); i++) {
      spaces.add(i);
    }
    return spaces;
  }

  @NonNull
  public static List<Integer> getBorders(@NonNull Inventory inventory) {
    List<Integer> spaces = new ArrayList<>();
    int i;
    for (i = 0; i < 8; i++) {
      spaces.add(i);
    }
    for (; i < inventory.getSize() - 9; i = i + 9) {
      spaces.add(i);
    }
    for (; i > inventory.getSize() - 9; i--) {
      spaces.add(i);
    }
    for (; i > 0; i = i - 9) {
      spaces.add(i);
    }
    return spaces;
  }
}
