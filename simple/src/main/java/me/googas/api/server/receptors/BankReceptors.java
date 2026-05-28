package me.googas.api.server.receptors;

import java.util.HashMap;
import me.googas.api.API;
import me.googas.api.Requests;
import me.googas.api.economy.AbstractBank;
import me.googas.api.economy.AbstractRecord;
import me.googas.api.links.LinkableInfo;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;

public class BankReceptors {

  @Receptor(Requests.Banks.WITHDRAW)
  public boolean withdraw(
      @ParamName("id") String id,
      @ParamName("context") String context,
      @ParamName("amount") Number amount) {
    AbstractBank bank = this.getBank(id);
    if (bank == null || !bank.has(context, amount.doubleValue())) return false;
    return bank.withdraw(context, amount.doubleValue());
  }

  @Receptor(Requests.Banks.DEPOSIT)
  public boolean deposit(
      @ParamName("id") String id,
      @ParamName("context") String context,
      @ParamName("amount") Number amount) {
    AbstractBank bank = this.getBank(id);
    if (bank == null) return false;
    return bank.deposit(context, amount.doubleValue());
  }

  @Receptor(Requests.Banks.GET)
  private AbstractBank getBank(@ParamName("id") String id) {
    return API.getLoader().getBanks().getBank(id);
  }

  @Receptor(Requests.Banks.CREATE)
  public AbstractBank create(@ParamName("id") String id, @ParamName("owner") LinkableInfo owner) {
    return new AbstractBank(id, new HashMap<>(), owner).cache();
  }

  @Receptor(Requests.Banks.DELETE)
  public boolean delete(@ParamName("id") String id) {
    return API.getLoader().getBanks().delete(id);
  }

  @Receptor(Requests.Banks.ACCOUNTS)
  public AbstractRecord accounts(@ParamName("id") String id) {
    AbstractBank bank = this.getBank(id);
    if (bank == null) return null;
    return new AbstractRecord(bank.getAccounts());
  }
}
