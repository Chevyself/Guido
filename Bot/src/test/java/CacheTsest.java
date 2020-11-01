import java.util.Collection;
import me.googas.api.links.LinkedData;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.user.UserData;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.loader.JsongoDataLoader;
import me.googas.commons.cache.Cache;
import me.googas.commons.cache.ICatchable;

public class CacheTsest {

  public static void main(String[] args) {
    JsongoDataLoader jsongo =
        new JsongoDataLoader(
            "mongodb+srv://Chevy:1004133609@cluster0.t2p9j.mongodb.net/<dbname>?retryWrites=true&w=majority",
            "testing-database2");
    UserData user = jsongo.getUserData("rvzmfv");
    Guido.setDataLoader(jsongo);
    CacheTsest.printCache();
    if (user != null) {
      System.out.println(jsongo.getLinks(user));
      CacheTsest.printCache();
      System.out.println(Cache.contains(user));

      Collection<LinkedData> links = jsongo.getLinks(user);
      for (LinkedData link : links) {
        Collection<Match> participating =
            jsongo.getParticipating(link.getType(), link.getIdentification());
      }
      for (LinkedData link : links) {
        Collection<Match> participating =
            jsongo.getParticipating(link.getType(), link.getIdentification(), MatchStatus.values());
      }
      for (LinkedData link : links) {
        Collection<Match> participating =
            jsongo.getParticipating(link.getType(), link.getIdentification(), MatchStatus.values());
      }
      for (LinkedData link : links) {

        Collection<Match> participating =
            jsongo.getParticipating(link.getType(), link.getIdentification(), MatchStatus.values());
      }
      CacheTsest.printCache();
    }
    CacheTsest.printCache();

    for (ICatchable catchable : Cache.copy()) {
      catchable.unload(false);
    }

    CacheTsest.printCache();
  }

  public static void printCache() {
    System.out.println("----------- CACHE        -----------------");
    for (ICatchable catchable : Cache.copy()) {
      System.out.println(catchable);
    }
    System.out.println("----------- END CACHE        -----------------");
  }
}
