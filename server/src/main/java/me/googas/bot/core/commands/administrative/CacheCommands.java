package me.googas.bot.core.commands.administrative;

import com.starfishst.commands.jda.annotations.Command;
import com.starfishst.commands.jda.result.Result;
import com.starfishst.core.annotations.Parent;
import java.lang.ref.SoftReference;
import me.googas.bot.api.Guido;
import me.googas.commons.cache.Catchable;

public class CacheCommands {

  @Parent
  @Command(aliases = "cache", node = "guido.cache")
  public Result cache() {
    for (SoftReference<Catchable> catchable : Guido.getCache().copy()) {
      System.out.println("Cache element: " + catchable.get());
    }
    return new Result("Check console for the output");
  }

  @Command(aliases = "clear", node = "guido.cache.clear")
  public Result cacheClear() {
    Guido.validated().clearCache();
    return new Result("Cache emptied");
  }
}
