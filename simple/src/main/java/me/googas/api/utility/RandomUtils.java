package me.googas.api.utility;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import lombok.NonNull;

public class RandomUtils {

  private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final String ALPHABET_LOWER = ALPHABET.toLowerCase();
  private static final String ALPHANUMERIC = ALPHABET + ALPHABET_LOWER;
  private static final Random RANDOM = new Random();

  public static @NonNull String nextString(int length) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < length; i++) {
      int index = (int) (Math.random() * ALPHANUMERIC.length());
      stringBuilder.append(ALPHANUMERIC.charAt(index));
    }
    return stringBuilder.toString();
  }

  public static <E> E getRandom(List<E> list) {
    if (list.isEmpty()) throw new IllegalArgumentException("Collection cannot be empty");
    return list.get(RANDOM.nextInt(list.size()));
  }

  public static <E> List<E> getRandom(List<E> list, int amount) {
    if (list.isEmpty()) throw new IllegalArgumentException("Collection cannot be empty");
    if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
    if (amount > list.size())
      throw new IllegalArgumentException("Amount cannot be bigger than the collection size");
    return list.stream().sorted((a, b) -> RANDOM.nextInt(3) - 1).limit(amount).toList();
  }

  public static @NonNull Random getRandom() {
    return RANDOM;
  }
}
