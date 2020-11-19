package log;

public class LogTest {

  public static void main(String[] args) {
    TestHandler.createProxy();
    SimpleLoggable loggable = new SimpleLoggable();
    System.out.println(loggable.getMessage());
    loggable.print();
  }
}
