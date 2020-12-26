package stats;

import java.util.Map;
import lombok.NonNull;
import me.googas.api.Stateable;
import me.googas.commons.maps.Maps;

public class StatsTest {

  public static void main(String[] args) {
    Map<String, Float> map =
        Maps.builder("PGM-Kills", 10f)
            .append("PGM-Deaths", 40f)
            .append("PGM-Wools", 50f)
            .append("PGMRanked-Kills", 30f)
            .append("PGMRanked-Deaths", 50f)
            .append("1v1-elo", 50f)
            .append("5v5-elo", 40f)
            .build();
    StateableImpl stateable = new StateableImpl(map);
    System.out.println(stateable.getOrganized(null));
  }

  /** Implementation for stateable */
  static class StateableImpl implements Stateable {

    /** The stats map */
    @NonNull private final Map<String, Float> stats;

    /**
     * Create instance
     *
     * @param stats the stats map
     */
    public StateableImpl(@NonNull Map<String, Float> stats) {
      this.stats = stats;
    }

    @Override
    public @NonNull Map<String, Float> getStats() {
      return this.stats;
    }
  }
}
