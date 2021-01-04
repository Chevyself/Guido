import me.googas.bot.core.handlers.matches.MatchEloCalculator;

public class SeasonResetTest {

  public static void main(String[] args) {
    MatchEloCalculator calculator = new MatchEloCalculator();
    float base = 500;
    float wins = 94;
    float loses = 78;
    float elo = base;
    for (int i = 0; i < wins; i++) {
      double expected = calculator.calculateExpected(elo, elo, base);
      int difference = (int) ((calculator.newElo(elo, expected, 1) - elo) * 1.5);
      System.out.println("Win difference: " + difference);
      elo += difference;
    }
    System.out.println("Before loses: " + elo);
    for (int i = 0; i < loses; i++) {
      double expected = calculator.calculateExpected(elo, elo, base);
      int difference = (int) ((elo - calculator.newElo(elo, expected, 1)) * 1.5);
      System.out.println("Lose difference: " + difference);
      elo -= -difference;
    }
    System.out.println(elo);
  }
}
