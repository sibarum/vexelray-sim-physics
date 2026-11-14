package dev.vexelray.sim.fluid.gui;

import dev.supirvast.vastir.tools.GpuContext;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * A simulation made in two halves on two threads, as an application sharing its device with a window makes one: the slow
 * half on a worker while the owning thread goes on stepping the simulation already running, and the rest on the owning
 * thread. It has to start empty, as a simulation made in one go does, and step the same water.
 */
class PreparedSimulationTest {

    private static final int N = 32;

    @Test
    void aPreparationNeverStartedIsFreedByClosingIt() throws Exception {
        assumeTrue(GpuContext.isAvailable(), "no GPU here");
        try (GpuContext context = GpuContext.open()) {
            ExecutorService worker = Executors.newSingleThreadExecutor();
            try {
                ParticleSimulation3.Prepared prepared = worker.submit(
                        () -> ParticleSimulation3.prepare(context, N, N, N, 64, false)).get();
                prepared.close();
                prepared.close();
                assertThrows(IllegalStateException.class, () -> new ParticleSimulation3(prepared));
            } finally {
                worker.shutdownNow();
            }
        }
    }
}
