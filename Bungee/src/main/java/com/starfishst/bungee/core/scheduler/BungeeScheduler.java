package com.starfishst.bungee.core.scheduler;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.commons.scheduler.Countdown;
import me.googas.commons.scheduler.Repetitive;
import me.googas.commons.scheduler.RunLater;
import me.googas.commons.scheduler.Scheduler;
import me.googas.commons.scheduler.Task;
import me.googas.commons.time.Time;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.HashSet;
import java.util.Set;

public class BungeeScheduler implements Scheduler {

    @NonNull @Getter
    private final Plugin plugin;
    @Getter
    private final Set<ScheduledTask> scheduledTasks = new HashSet<>();
    @Getter
    private final Set<Task> tasks = new HashSet<>();

    public BungeeScheduler(@NonNull Plugin plugin) {
        this.plugin = plugin;
    }

    public TaskScheduler bungee() {
        return ProxyServer.getInstance().getScheduler();
    }

    @Override
    public @NonNull Countdown countdown(@NonNull Time time, @NonNull Countdown countdown) {
        return (Countdown) this.repeat(time, time, countdown);
    }

    @Override
    public @NonNull RunLater later(@NonNull Time time, @NonNull RunLater runLater) {
        this.bungee().schedule(this.plugin, () -> {
            if (!runLater.isCancelled()) runLater.run();
        }, time.getValue(), time.getUnit().toTimeUnit());
        this.tasks.add(runLater);
        return runLater;
    }

    @Override
    public @NonNull Repetitive repeat(@NonNull Time initial, @NonNull Time period, @NonNull Repetitive repetitive) {
        this.bungee().schedule(this.plugin, () -> {
            if (!repetitive.isCancelled()) repetitive.run();
        }, initial.getValue(), initial.getValue(), initial.getUnit().toTimeUnit());
        this.tasks.add(repetitive);
        return repetitive;
    }
}
