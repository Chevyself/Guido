package com.starfishst.bukkit;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.commons.scheduler.Countdown;
import me.googas.commons.scheduler.Repetitive;
import me.googas.commons.scheduler.RunLater;
import me.googas.commons.scheduler.Scheduler;
import me.googas.commons.scheduler.Task;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class GuidoScheduler implements Scheduler {

  @NonNull private final Plugin plugin;

  @NonNull private final Set<Task> tasks = new HashSet<>();

  public GuidoScheduler(@NonNull Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public @NonNull Countdown countdown(@NonNull Time time, @NonNull Countdown task) {
    return (Countdown) this.repeat(time, time, task);
  }

  @Override
  public @NonNull RunLater later(@NonNull Time time, @NonNull RunLater task) {
    new BukkitRunnable() {
      @Override
      public void run() {
        if (task.isCancelled()) {
          this.cancel();
          GuidoScheduler.this.tasks.remove(task);
          return;
        }
        task.run();
      }
    }.runTaskLater(this.plugin, time.getValue(Unit.MINECRAFT_TICKS));
    this.tasks.add(task);
    return task;
  }

  @Override
  public @NonNull Repetitive repeat(
      @NonNull Time initial, @NonNull Time period, @NonNull Repetitive task) {
    new BukkitRunnable() {
      @Override
      public void run() {
        if (task.isCancelled()) {
          this.cancel();
          GuidoScheduler.this.tasks.remove(task);
          return;
        }
        task.run();
      }
    }.runTaskTimer(
        this.plugin, initial.getValue(Unit.MINECRAFT_TICKS), period.getValue(Unit.MINECRAFT_TICKS));
    this.tasks.add(task);
    return task;
  }

  @Override
  public @NonNull Collection<Task> getTasks() {
    return this.tasks;
  }
}
