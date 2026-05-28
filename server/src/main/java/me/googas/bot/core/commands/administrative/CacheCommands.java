package me.googas.bot.core.commands.administrative;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.result.Result;
import java.lang.ref.SoftReference;
import me.googas.bot.api.Guido;
import me.googas.bungee.commands.middleware.GuidoJdaPermission;
import me.googas.net.cache.Catchable;

public class CacheCommands {

  @Parent
  @GuidoJdaPermission("guido.cache")
  @Command(aliases = "cache")
  public Result cache() {
    for (SoftReference<Catchable> catchable : Guido.getCache().keySetCopy()) {
      System.out.println("Cache element: " + catchable.get());
    }
    return Result.of("Check console for the output");
  }

  @GuidoJdaPermission("guido.cache.clear")
  @Command(aliases = "clear")
  public Result cacheClear() {
    Guido.validated().clearCache();
    return Result.of("Cache emptied");
  }
}
