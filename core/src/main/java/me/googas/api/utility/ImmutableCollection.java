package me.googas.api.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public final class ImmutableCollection<E> implements Iterable<E> {

  @NonNull private final List<E> elements;

  public ImmutableCollection() {
    this(new ArrayList<>(), false);
  }

  public ImmutableCollection(@NonNull List<E> elements) {
    this(elements, true);
  }

  public ImmutableCollection(@NonNull Collection<E> elements) {
    this(List.copyOf(elements), false);
  }

  private ImmutableCollection(@NonNull List<E> elements, boolean copy) {
    if (copy) {
      this.elements = List.copyOf(elements);
    } else {
      this.elements = elements;
    }
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

  public E get(int index) {
    return elements.get(index);
  }

  public static <T, R> ImmutableCollection<R> map(
      @NonNull Iterable<T> iterable, @NonNull Function<T, R> mapper) {
    List<R> list = new ArrayList<>();
    for (T t : iterable) {
      list.add(mapper.apply(t));
    }
    return new ImmutableCollection<>(list, false);
  }

  public static <T, R> ImmutableCollection<R> flatMap(
      @NonNull Iterable<T> iterable, @NonNull Function<T, Iterable<? extends R>> flatMapper) {
    List<R> list = new ArrayList<>();
    for (T t : iterable) {
      for (R r : flatMapper.apply(t)) {
        list.add(r);
      }
    }
    return new ImmutableCollection<R>(list, false);
  }

  public List<E> copy() {
    return new ArrayList<>(this.elements);
  }
}
