package com.thoughtworks.plainoldfactorypattern;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author 杨博 (Yang Bo)
 */
public class ContinuationFactory<R> implements MonadFactory {

    abstract class Continuation<A> implements Monad<A> {
        @Override
        public ContinuationFactory<R> getFactory() {
            return ContinuationFactory.this;
        }

        public abstract R listen(Function<A, R> handler);

        public <B> Continuation<B> flatMap(Function<A, Monad<B>> mapper) {

            return shift(handler ->
                    listen(a -> {
                        Continuation<B> continuationB = (Continuation<B>) mapper.apply(a);
                        return continuationB.listen(handler);
                    }));
        }
    }


    public <A> Continuation<A> shift(Function<Function<A, R>, R> launcher) {
        return new Continuation<A>() {
            @Override
            public R listen(Function<A, R> handler) {
                return launcher.apply(handler);
            }
        };
    }

    @Override
    public <A> Continuation<A> newInstance(A a) {
        return shift(handler -> handler.apply(a));
    }

    public <A> Continuation<A> delay(Supplier<A> run) {
        return shift(handler -> handler.apply(run.get()));
    }


    public static ContinuationFactory<Void> getVoidContinuationFactory() {
        return VOID_CONTINUATION_FACTORY;
    }

    public static final ContinuationFactory<Void> VOID_CONTINUATION_FACTORY = new ContinuationFactory<Void>();
}
