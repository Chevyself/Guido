package me.googas.bot.adapters.schemes;

import me.googas.starbox.builders.Builder;

/**
 * An scheme is a builder for the latest version of the object. Legacy schemes are the ones that do
 * not have a version in their json The latest version of the serialization is given in the class of
 * the object
 */
public interface Scheme<T> extends Builder<T> {}
