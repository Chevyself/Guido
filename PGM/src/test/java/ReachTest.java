import me.googas.commons.math.MathUtils;

public class ReachTest {

  public static void main(String[] args) {
    double pitch = 112.77103424072266; // + 90 fixes notches pitch
    System.out.println("Pitch: " + pitch);
    double hypotenuse = 3.5026856546331095;
    double feetToFeet = 2.7117411705524788;
    double headToFeet = 1.620000000000001;
    System.out.println("hypotenuse: " + hypotenuse);
    System.out.println("feetToFeet: " + feetToFeet);
    System.out.println("headToFeet: " + headToFeet);
    double k =
        MathUtils.square(headToFeet) - MathUtils.square(feetToFeet) + MathUtils.square(hypotenuse);
    double theta = Math.toDegrees(Math.acos(k / (2 * headToFeet * hypotenuse)));
    double omega = 180 - pitch - theta;
    double beta = pitch;
    double alpha = 180 - omega - beta;
    double sum = omega + beta + alpha;
    double distance =
        (hypotenuse * Math.sin(Math.toRadians(alpha))) / Math.sin(Math.toRadians(beta));
    System.out.println(k);
    System.out.println(k / (2 * hypotenuse * headToFeet));
    System.out.println(theta);
    System.out.println(omega);
    System.out.println(beta);
    System.out.println(alpha);
    System.out.println(sum);
    System.out.println(distance);
  }
}
