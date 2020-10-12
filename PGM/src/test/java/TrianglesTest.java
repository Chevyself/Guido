import me.googas.commons.math.MathUtils;

public class TrianglesTest {

  public static void main(String[] args) {
    double A = 1.6;
    double B = 2.6;
    double C = 2.1;
    double Al =
        Math.toDegrees(
            Math.acos(
                (MathUtils.square(C) + MathUtils.square(B) - MathUtils.square(A)) / (2 * C * B)));
    double Be =
        Math.toDegrees(
            Math.acos(
                (MathUtils.square(A) + MathUtils.square(B) - MathUtils.square(C)) / (2 * A * B)));
    double Y =
        Math.toDegrees(
            Math.acos(
                (MathUtils.square(A) + MathUtils.square(C) - MathUtils.square(B)) / (2 * A * C)));
    System.out.println(Al);
    System.out.println(Be);
    System.out.println(Y);
    System.out.println(Al + Be + Y);
  }
}
