package me.googas.api.stats;

import java.util.Map;
import java.util.UUID;
import lombok.NonNull;

public record PlayerStatsResponse(
    @NonNull UUID uuid,
    @NonNull String nickname,
    @NonNull String context,
    boolean linked,
    boolean online,
    @NonNull Map<String, Double> stats) {}
