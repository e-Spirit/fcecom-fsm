package to.be.renamed.executable;

import to.be.renamed.EcomConnectScope;
import to.be.renamed.bridge.BridgeService;
import to.be.renamed.bridge.TestConnectionRequest;
import to.be.renamed.module.ProjectAppHelper;
import to.be.renamed.module.ServiceFactory;
import to.be.renamed.module.projectconfig.connectiontest.BridgeTestResult;
import to.be.renamed.module.projectconfig.model.BridgeConfig;
import com.espirit.moddev.components.annotations.PublicComponent;

import de.espirit.common.base.Logging;
import de.espirit.firstspirit.access.BaseContext;
import de.espirit.firstspirit.access.script.Executable;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.Nullable;

import java.io.Writer;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

@PublicComponent(
    name = ProjectAppHelper.MODULE_NAME + " - Test Bridge Connection",
    displayName = ProjectAppHelper.MODULE_NAME + " - Executable: Test Bridge Connection")
public class TestConnectionExecutable extends ExecutableUtilities implements Executable {

    static final String PARAM_REQUESTS = "test_connection_requests";
    static final String PARAM_OVERALL_TIME = "test_connection_interval_overall_time";
    static final String PARAM_DELAY = "test_connection_interval_delay";

    private final Map<TestConnectionRequest, BridgeTestResult> lastResults = new ConcurrentHashMap<>();

    private BridgeService bridgeService;
    private BridgeConfig bridgeConfig;

    private static String getDuration(long seconds) {
        final long milliseconds = TimeUnit.MILLISECONDS.convert(seconds, SECONDS);
        return DurationFormatUtils.formatDurationWords(milliseconds, true, true);
    }

    @Override
    public Object execute(final Map<String, Object> parameters, final Writer out, final Writer err) {
        setParameters(parameters);

        final BaseContext baseContext = (BaseContext) parameters.get("context");
        if (baseContext == null) {
            throw new InvalidParameterException("Missing mandatory parameter 'context'");
        }

        init(baseContext);

        final @Nullable Long overallTime = getLongParam(PARAM_OVERALL_TIME);
        final @Nullable Long delay = getLongParam(PARAM_DELAY);

        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        ScheduledFuture<?> testConnectionSchedule = null;

        try {
            if (overallTime == null) {
                var summary = "\nRunning Test Connection once";

                Logging.logInfo(summary, getClass());
                resultCatcher.catchSummary(summary);

                getTestConnectionRunner().run();
                return null;
            }

            // Only intervals ranging from 2 seconds to 10 minutes are allowed
            final long delaySeconds = Math.min(Math.max(delay == null ? 2 : delay, 2), 600);

            // Maximum 100 runs are allowed
            final long overallTimeSeconds = Math.min(overallTime, delaySeconds * 99);

            // Add a buffer for the last requests to finish after the schedule was finished
            final long maxRunningTime = overallTimeSeconds + 10;

            var summary =
                "%nRunning Test Connection %n - for %s %n - with a delay of %s between each request %n - stopping after a max time of %s".formatted(
                    getDuration(overallTimeSeconds), getDuration(delaySeconds), getDuration(maxRunningTime));

            Logging.logInfo(summary, getClass());
            resultCatcher.catchSummary(summary);

            testConnectionSchedule = scheduler.scheduleAtFixedRate(getTestConnectionRunner(), 0, delaySeconds, SECONDS);

            // End Schedule after overallTime
            final ScheduledFuture<?> finalTestConnectionSchedule = testConnectionSchedule;
            scheduler.schedule(() -> {
                finalTestConnectionSchedule.cancel(true);
                scheduler.shutdownNow();
            }, overallTimeSeconds, SECONDS);

            return scheduler.awaitTermination(maxRunningTime, SECONDS);
        } catch (InterruptedException exception) {
            Logging.logWarning("Test Connection Executable got interrupted", exception, getClass());
            if (testConnectionSchedule != null && !testConnectionSchedule.isCancelled()) {
                testConnectionSchedule.cancel(true);
            }
            if (!scheduler.isShutdown()) {
                scheduler.shutdownNow();
            }
            Thread.currentThread().interrupt();
        }

        return null;
    }

    private void init(final BaseContext baseContext) {
        EcomConnectScope scope = EcomConnectScope.create(baseContext);
        this.bridgeConfig = ServiceFactory.getProjectAppConfigurationService(scope.getBroker()).loadConfiguration().getBridgeConfig();
        this.bridgeService = ServiceFactory.getBridgeService(scope.getBroker());
    }

    private Runnable getTestConnectionRunner() {
        final AtomicInteger runs = new AtomicInteger(1);

        final List<TestConnectionRequest> requests = getListParam(PARAM_REQUESTS);

        return () -> {
            var result = "%nRequest No. %d%n".formatted(runs.getAndIncrement()) + requests.stream().map(testConnectionRequest -> {
                final BridgeTestResult bridgeTestResult = bridgeService.testConnection(bridgeConfig, testConnectionRequest);

                final BridgeTestResult lastResult = lastResults.get(testConnectionRequest);
                final boolean isSameResultAsBefore = bridgeTestResult.equals(lastResult);

                final String summary = bridgeTestResult.minimalSummary();

                lastResults.put(testConnectionRequest, bridgeTestResult);

                final int index = requests.indexOf(testConnectionRequest);

                if (isSameResultAsBefore) {
                    return "%s) Same result as before".formatted(getPaddedIndex(requests.size(), index));
                }

                return "%s) %s".formatted(getPaddedIndex(requests.size(), index), summary);
            }).collect(Collectors.joining("\n"));

            Logging.logInfo(result, getClass());

            resultCatcher.catchResult(result);
        };
    }

    private String getPaddedIndex(int listSize, int index) {
        final int maxIntSize = String.valueOf(listSize).length();
        final int currentIntSize = String.valueOf(index).length();

        final int padding = maxIntSize - currentIntSize;

        if (padding == 0) {
            return String.valueOf(index + 1);
        }

        return "%% %dd".formatted(padding).formatted(index + 1);
    }

    // Enable unit test
    private final ResultsCatcher resultCatcher;

    public TestConnectionExecutable() {
        this.resultCatcher = new ResultsCatcher();
    }

    public TestConnectionExecutable(ResultsCatcher resultCatcher) {
        this.resultCatcher = resultCatcher != null ? resultCatcher : new ResultsCatcher();
    }

    /**
     * This is a workaround to make JUnit Tests work.
     * Some implementation inside Mockito is not compatible with scheduled Threads
     * when adding elements to Lists of any kind.
     * <p>
     * With the ResultCatcher, it is possible to do verifications on methods instead.
     */
    public static class ResultsCatcher {

        void catchResult(String result) {
        }

        void catchSummary(String summary) {
        }
    }
}
