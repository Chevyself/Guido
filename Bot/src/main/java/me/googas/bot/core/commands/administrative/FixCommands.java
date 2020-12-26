package me.googas.bot.core.commands.administrative;

import com.starfishst.core.annotations.Parent;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import java.util.Collection;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.bot.Guido;

/** Commands for fixing */
public class FixCommands {

  @Parent
  @Command(aliases = "fix", description = "Different fixing commands", node = "guido.fix")
  public Result fix() {
    return new Result("Use a subcommand");
  }

  @Command(
      aliases = "minecraft",
      description = "Fix the Minecraft linkable",
      node = "guido.fix.minecraft")
  public Result minecraft() {
    Collection<LinkableInfo> links = Guido.getDataLoader().getLinks(-1, -1, LinkableType.MINECRAFT);
    for (LinkableInfo linkInfo : links) {
      Linkable link = linkInfo.getLink();
      if (link != null) {
        String nickname = link.getIdentification().get("nickname", String.class);
        if (nickname != null) {
          link.getRecognition().put("nickname", nickname);
        }
        link.getIdentification().remove("nickname");
      }
    }
    Guido.clearCache();
    return new Result("Minecraft links have been updated");
  }
}
