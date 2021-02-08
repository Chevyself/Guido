package me.googas.api;

/** This object represents a range. A range is from an minimum integer value to a maximum */
public interface Range {

  /**
   * Check if the given number is inside the range. This means that the number must be more or
   * equals to the minimum and less or equals to the maximum
   *
   * @param num the number to check
   * @return true if the number is inside the minimum and maximum value
   */
  default boolean isBound(int num) {
    return num >= this.getMin() && num <= this.getMax();
  }

  /**
   * Get the minimum value of the range
   *
   * @return the minimum value of the range
   */
  int getMin();

  /**
   * Get the maximum value of the range
   *
   * @return the maximum value of the range
   */
  int getMax();
}
