/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package net.anubis.client.util;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public final class SessionWorker<T>
implements AutoCloseable {
    private final String name;
    private ExecutorService executor;
    private Future<T> pending;

    public SessionWorker(String name) {
        this.name = name;
    }

    public boolean busy() {
        return this.pending != null;
    }

    public void submit(Callable<T> task) {
        if (this.pending != null) {
            throw new IllegalStateException("Worker is busy");
        }
        if (this.executor == null) {
            this.executor = Executors.newSingleThreadExecutor(r -> {
                Thread thread = new Thread(r, this.name);
                thread.setDaemon(true);
                return thread;
            });
        }
        this.pending = this.executor.submit(task);
    }

    public void poll(Consumer<T> apply) {
        if (this.pending == null || !this.pending.isDone()) {
            return;
        }
        Future<T> completed = this.pending;
        this.pending = null;
        try {
            apply.accept(completed.get());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        catch (ExecutionException e) {
            Throwable throwable = e.getCause();
            if (throwable instanceof Error) {
                Error fatal = (Error)throwable;
                throw fatal;
            }
            throw new IllegalStateException("Snapshot worker failed", e.getCause());
        }
        catch (CancellationException cancellationException) {
            // empty catch block
        }
    }

    @Override
    public void close() {
        if (this.pending != null) {
            this.pending.cancel(true);
        }
        this.pending = null;
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
        this.executor = null;
    }
}

