package log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TestHandler implements InvocationHandler {

  public static void createProxy() {
    Object proxy =
        Proxy.newProxyInstance(
            TestHandler.class.getClassLoader(), new Class[] {Loggable.class}, new TestHandler());
  }

  @Override
  public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
    System.out.println(method + " has been invoked");
    return method.invoke(o, objects);
  }
}
