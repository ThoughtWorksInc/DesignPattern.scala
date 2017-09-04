package com.thoughtworks.designpattern.lite;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Yang Bo
 */
public class TaskTest {

    <A> OptionalTFactory.OptionalT<A> asyncTask(Function<Function<Optional<A>, Void>, Void> launcher) {
        return OptionalTFactory.getTaskFactory().lift(ContinuationFactory.getVoidContinuationFactory().shift(launcher));
    }

    <A> OptionalTFactory.OptionalT<A> delayTask(Supplier<Optional<A>> launcher) {
        return OptionalTFactory.getTaskFactory().lift(ContinuationFactory.getVoidContinuationFactory().delay(launcher));
    }

    <A> OptionalTFactory.OptionalT<A> breakTask() {
        return delayTask(Optional::empty);
    }


    <A> OptionalTFactory.OptionalT<A> nio(BiConsumer<Function<Optional<A>, Void>, CompletionHandler<A, Function<Optional<A>, Void>>> start) {
        return asyncTask(handler -> {
            start.accept(handler, new CompletionHandler<A, Function<Optional<A>, Void>>() {
                @Override
                public void completed(A result, Function<Optional<A>, Void> attachment) {
                    attachment.apply(Optional.<A>of(result));
                }

                @Override
                public void failed(Throwable exc, Function<Optional<A>, Void> attachment) {
                    exc.printStackTrace();
                    attachment.apply(Optional.empty());
                }
            });
            return null;
        });
    }

    OptionalTFactory.OptionalT<Integer> asyncWrite(AsynchronousFileChannel channel, ByteBuffer data, long offset) {
        return nio((attachment, completionHandler) -> channel.write(data, offset, attachment, completionHandler));
    }

    OptionalTFactory.OptionalT<Integer> asyncRead(AsynchronousFileChannel channel, ByteBuffer data, long offset) {
        return nio((attachment, completionHandler) -> channel.read(data, offset, attachment, completionHandler));
    }

    volatile boolean isComplete = false;

    @Test
    public void testTask() throws InterruptedException {
        OptionalTFactory.OptionalT<String> task = delayTask(() -> {
            try {
                Path filePath = Files.createTempFile("testTask", "txt");
                return Optional.of(AsynchronousFileChannel.open(filePath, StandardOpenOption.WRITE, StandardOpenOption.READ));
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }).flatMap(channel ->
                asyncWrite(channel, ByteBuffer.wrap("Hello, World!".getBytes()), 0L)
                        .flatMap(numberOfBytesWritten -> {
                            final ByteBuffer readBuffer = ByteBuffer.allocate(5);
                            return asyncRead(channel, readBuffer, 0L).map(numberOfBytesRead ->
                                    new String(readBuffer.array())
                            );
                        })
        ).flatMap(result -> {
            Assert.assertEquals("Hello", result);
            return this.<Void>breakTask()
                    .<String>map(empty ->
                            {
                                throw new RuntimeException("Should never been executed.");
                            }
                    );
        });

        ((ContinuationFactory<Void>.Continuation<Optional<String>>) task.getUnderlyingMonad())
                .listen(result -> {
                    Assert.assertEquals(Optional.empty(), result);
                    synchronized (this) {
                        isComplete = true;
                        notify();
                    }
                    return null;
                });
        synchronized (this) {
            while (!isComplete) {
                wait();
            }
        }
    }
}
