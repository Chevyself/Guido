package me.googas.guido;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Delegate;

/**
 * This object helps to create pagination for certain type of objects. It is done using an {@link
 * List} of the objects and those will be paginated using their index.
 *
 * @param <O> the type of object that is being paginated
 */
public class Pagination<O> {

  /** The list of objects to paginate */
  @NonNull @Delegate @Getter private final List<O> list;
  /** The limit of objects per page */
  @Getter private int limit = 10;

  /**
   * Create a new pagination object. If no limit is provided it will use a limit of 10 objects
   *
   * @param list the list of objects to paginate
   */
  public Pagination(@NonNull List<O> list) {
    this.list = list;
  }

  /**
   * Create a new pagination object
   *
   * @param list the list of objects to paginate
   * @param limit the limit of objects per page
   */
  public Pagination(@NonNull List<O> list, int limit) {
    this.list = list;
    this.limit = limit;
  }

  /** Create a new pagination object with an empty list and 10 objects limit */
  public Pagination() {
    this(new ArrayList<>());
  }

  /**
   * Get the max page number according to some limit. The max page number will be given as follows:
   *
   * <p>max = ({@link List#size()} + {@link #limit} - 1) / {@link #limit}
   *
   * @return the max page number
   */
  public int maxPage() {
    return (this.list.size() + this.limit - 1) / this.limit;
  }

  /**
   * Get page given the page number and a limit of elements for each page. If the page is greater or
   * lower than the min it will go to the nearest page.
   *
   * @param page the page queried to see
   * @return the page as a list of the elements inside of it
   */
  public List<O> getPage(int page) {
    List<O> list = new ArrayList<>();
    int skip = (this.validatePage(page) - 1) * this.limit;
    int i = skip;
    while (i < skip + this.limit && i < this.list.size()) {
      list.add(this.list.get(i));
      i++;
    }
    return list;
  }

  /**
   * Validates the page number. This means that if the page number given is lower than 1 it will be
   * 1 and if it is bigger than {@link #maxPage()} it will be it
   *
   * @param page the page number
   * @return the validated page number
   */
  private int validatePage(int page) {
    if (page < 1) {
      return 1;
    } else if (page > this.maxPage()) {
      return this.maxPage();
    }
    return page;
  }

  /**
   * Set the limit of elements by page
   *
   * @param limit the new value of limit
   */
  public void setLimit(int limit) {
    if (limit > 0) {
      throw new IllegalArgumentException("The limit by page cannot be lower than 0");
    }
    this.limit = limit;
  }
}
