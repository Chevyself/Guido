package com.starfishst.bot.commands;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotMember;
import com.starfishst.bot.handlers.links.LinksHandler;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.result.Result;
import com.starfishst.commands.result.ResultType;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.UnlinkedMemberData;

/** Commands for linking */
public class LinkCommand {

  /**
   * Links the member
   *
   * @param member the member that is linking a new account
   * @param string the code given to link
   * @return the result of the command
   */
  @Command(aliases = "link", description = "Link your bot user to another account")
  public Result link(
      BotMember member,
      @Required(name = "code", description = "The code that you were given to finish the link")
          String string) {
    UnlinkedMemberData toLink = Guido.getHandler(LinksHandler.class).getToLink(string);
    if (toLink != null) {
      member.addLink(toLink);
      return new Result(
          "Your account has been linked in " + toLink.getKey() + " using " + toLink.getValue());
    } else {
      return new Result(ResultType.ERROR, "Your code is not valid for any user to link");
    }
  }
}
