package me.googas.api.utility;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public final class ImmutableCollection<E> implements Iterable<E> {

  @NonNull private final Collection<E> elements;

  public ImmutableCollection(@NonNull Collection<E> elements) {
    this.elements = List.copyOf(elements);
  }

  @NotNull
  @Override
  public Iterator<E> iterator() {
    return elements.iterator();
  }

  @NonNull
  public Stream<E> stream() {
    return elements.stream();
  }

  public void appendTo(@NonNull Collection<E> other) {
    other.addAll(elements);
  }

  public boolean isEmpty() {
    return elements.isEmpty();
  }

  public int size() {
    return elements.size();
  }
}
