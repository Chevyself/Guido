package me.googas.api.utility;

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

  public static @NonNull Random getRandom() {
    return RANDOM;
  }
}
