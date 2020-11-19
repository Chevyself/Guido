package log;

public interface Loggable {

  default void print() {
    System.out.println("Hi");
  }

  default String getMessage() {
    return "Asd";
  }
}
