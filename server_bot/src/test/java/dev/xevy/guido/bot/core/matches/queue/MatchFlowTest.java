package dev.xevy.guido.bot.core.matches.queue;

import dev.xevy.guido.bot.GuidoTestRuntime;
import dev.xevy.guido.bot.JClientContext;
import lombok.NonNull;
import me.googas.api.Requests;
import me.googas.api.immutable.ImmutableMinecraftMatch;
import me.googas.api.immutable.ImmutableMinecraftMatchTeam;
import me.googas.api.immutable.ImmutableMinecraftMatchTeamMember;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.matches.minecraft.MinecraftMatch;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.api.matches.queue.MinecraftQueue;
import me.googas.api.matches.queue.QueueResult;
import me.googas.api.utility.ImmutableCollection;
import me.googas.api.utility.RandomUtils;
import me.googas.bot.core.handlers.queue.QueueHandler;
import me.googas.net.api.exception.MessengerListenFailException;
import me.googas.net.sockets.json.client.JsonClient;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class MatchFlowTest {

    private GuidoTestRuntime runtime;
    private JsonClient client;
    private CountDownLatch clientCompleted;

    @BeforeEach
    void setUp() throws IOException {
        runtime = GuidoTestRuntime.createRuntime();
        client  = setupClient(runtime);
        clientCompleted = new CountDownLatch(1);
    }

    @AfterEach
    void tearDown() throws IOException {
        runtime.close();
        client.close();
    }

    private JsonClient setupClient(@NonNull GuidoTestRuntime runtime) throws IOException {
        return runtime.joinWithClient(
                runtime.listen(
                        Requests.MatchServer.CAN_HOST,
                        (context) -> true),
                runtime.listen(
                        Requests.MatchServer.HOST,
                        this::processHostRequest));
    }

    @NotNull
    private String processHostRequest(JClientContext context) throws MessengerListenFailException {
        try {
            ImmutableMinecraftMatch match = context.get(
                    Requests.MatchServer.HOST_MATCH, ImmutableMinecraftMatch.class);
            Ladder ladder = Requests.MinecraftMatches
                    .getLadder(match.getLadderName())
                    .send(context.getMessenger()).orElseThrow();

            Requests.MinecraftMatches
                    .updateStatus(match.getId(), MatchStatus.STARTING)
                    .send(context.getMessenger());

            List<ImmutableMinecraftMatchTeam> requestTeams = new ArrayList<>(ladder.teamsPerMatch());
            ImmutableCollection<ImmutableMinecraftMatchTeamMember> participants =
                    match.getParticipants();

            for (int i = 0; i < ladder.teamsPerMatch(); i++) {
                Set<ImmutableMinecraftMatchTeamMember> members =
                        new HashSet<>(ladder.playersPerTeam());
                for (int j = 0; j < ladder.playersPerTeam(); j++) {
                    members.add(participants.get(i * ladder.playersPerTeam() + j));
                }
                ImmutableMinecraftMatchTeam team = new ImmutableMinecraftMatchTeam(
                        MatchTeam.NO_TEAM, members, "Team " + i);
                requestTeams.add(team);
            }

            List<ImmutableMinecraftMatchTeam> responseTeams = Requests.MinecraftMatches
                    .setTeams(match.getId(), new Requests.SetTeamsData(requestTeams))
                    .send(context.getMessenger())
                            .orElseThrow()
                                    .getTeams();

            Requests.MinecraftMatches
                    .setMap(match.getId(), "Le epic map")
                    .send(context.getMessenger());
            Requests.MinecraftMatches
                    .updateStatus(match.getId(), MatchStatus.PLAYING)
                    .send(context.getMessenger());
            Requests.MinecraftMatches
                    .onFinish(match.getId(), RandomUtils.getRandom(responseTeams).getId())
                    .send(context.getMessenger());

            return ladder.getName();
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        } finally {
            clientCompleted.countDown();
        }
    }

    private void awaitClient() throws InterruptedException {
        boolean completed = clientCompleted.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "HOST handler did not complete within timeout");
    }

    private Ladder setupLadder(int playersPerTeam, int teamsPerMatch) {
        String name = playersPerTeam + "v" + playersPerTeam;
        return runtime.getJdaProvider()
                .getGuidoGuild()
                .addLadder(name, playersPerTeam, 500, teamsPerMatch, 1f, 1f,
                        MinecraftTeamSelectionType.RANDOM)
                .orElseThrow();
    }

    @NonNull
    private Optional<? extends MinecraftMatch> fillQueue(MinecraftQueue queue, int total) {
        Optional<? extends MinecraftMatch> result = Optional.empty();
        for (int i = 0; i < total; i++) {
            MinecraftLinkable player = runtime.createMinecraftLinkable("Player" + i, true);
            QueueResult queueResult = queue.join(player);
            if (queueResult.getMatch().isPresent()) {
                result = queueResult.getMatch();
            }
        }
        return result;
    }

    @Test
    @DisplayName("Match reaches FINISHED after all players join the queue")
    void matchReachesFinishedStatus() throws InterruptedException {
        Ladder ladder = setupLadder(2, 2);
        MinecraftQueue queue = runtime.getHandlers()
                .getHandler(QueueHandler.class)
                .createQueue(ladder);

        Optional<? extends MinecraftMatch> match = fillQueue(queue, ladder.playersPerTeam() * ladder.teamsPerMatch());

        assertTrue(match.isPresent(), "A match should have been created");
        awaitClient();

        MinecraftMatch reload = runtime.getLoader().getMinecraftMatches().getById(match.get().getId()).orElseThrow();
        assertEquals(MatchStatus.FINISHED, reload.getStatus(),
                "Match should be FINISHED after the full flow");
    }

    @Test
    @DisplayName("Match is assigned the map set by the server client")
    void matchMapIsSet() throws InterruptedException {
        Ladder ladder = setupLadder(2, 2);
        MinecraftQueue queue = runtime.getHandlers()
                .getHandler(QueueHandler.class)
                .createQueue(ladder);

        Optional<? extends MinecraftMatch> match = fillQueue(queue, ladder.playersPerTeam() * ladder.teamsPerMatch());

        assertTrue(match.isPresent());
        awaitClient();

        MinecraftMatch reload = runtime.getLoader().getMinecraftMatches().getById(match.get().getId()).orElseThrow();
        assertEquals("Le epic map", reload.getMap(),
                "Match map should match what the server client set");
    }

    @Test
    @DisplayName("Correct number of teams and members are created for a 2v2")
    void teamsAndMembersAreCorrect() throws InterruptedException {
        int playersPerTeam = 2;
        int teamsPerMatch  = 2;
        Ladder ladder = setupLadder(playersPerTeam, teamsPerMatch);
        MinecraftQueue queue = runtime.getHandlers()
                .getHandler(QueueHandler.class)
                .createQueue(ladder);

        Optional<? extends MinecraftMatch> match = fillQueue(queue, playersPerTeam * teamsPerMatch);

        assertTrue(match.isPresent());
        awaitClient();
        MinecraftMatch reload = runtime.getLoader().getMinecraftMatches().getById(match.get().getId()).orElseThrow();

        ImmutableCollection<? extends MinecraftMatchTeam> teams = reload.getTeams();

        assertEquals(teamsPerMatch, teams.size(),
                "Should have exactly " + teamsPerMatch + " teams");

        for (MinecraftMatchTeam team : teams) {
            assertEquals(playersPerTeam, team.getMembers().size(),
                    "Each team should have exactly " + playersPerTeam + " members");
        }
    }

    @Test
    @DisplayName("Match transitions through STARTING → PLAYING → FINISHED in order")
    void matchStatusTransitionsAreOrdered() throws InterruptedException {
        Ladder ladder = setupLadder(2, 2);
        MinecraftQueue queue = runtime.getHandlers()
                .getHandler(QueueHandler.class)
                .createQueue(ladder);

        Optional<? extends MinecraftMatch> match = fillQueue(queue, ladder.playersPerTeam() * ladder.teamsPerMatch());
        assertTrue(match.isPresent());
        awaitClient();

        MinecraftMatch reload = runtime.getLoader().getMinecraftMatches().getById(match.get().getId()).orElseThrow();
        assertEquals(MatchStatus.FINISHED, reload.getStatus());
    }

    @Test
    @DisplayName("3v3 match creates correct team sizes")
    void threeVsThreeTeamSizes() throws InterruptedException {
        int playersPerTeam = 3;
        int teamsPerMatch  = 2;
        Ladder ladder = setupLadder(playersPerTeam, teamsPerMatch);
        MinecraftQueue queue = runtime.getHandlers()
                .getHandler(QueueHandler.class)
                .createQueue(ladder);

        Optional<? extends MinecraftMatch> match = fillQueue(queue, playersPerTeam * teamsPerMatch);

        assertTrue(match.isPresent());
        awaitClient();

        MinecraftMatch reload = runtime.getLoader().getMinecraftMatches().getById(match.get().getId()).orElseThrow();
        assertEquals(teamsPerMatch, reload.getTeams().size());
        match.get().getTeams().forEach(team ->
                assertEquals(playersPerTeam, team.getMembers().size()));
    }
}
