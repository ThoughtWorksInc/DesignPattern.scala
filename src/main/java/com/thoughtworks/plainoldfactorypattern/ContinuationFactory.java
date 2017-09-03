package com.thoughtworks.plainoldfactorypattern;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author 杨博 (Yang Bo)
 */
public class ContinuationFactory<R> implements Monad.Factory<ContinuationFactory<R>> {
    @Override
    public <T> Monad<Continuation<T>, ContinuationFactory<R>, T> newInstance(T t) {
        return null;
    }

    abstract class Continuation<T> implements Monad<Continuation<T>, ContinuationFactory<R>, T> {

        public ContinuationFactory<R> getFactory() {
            return ContinuationFactory.this;
        }

        @Override
        public <U, That extends Monad<That, ContinuationFactory<R>, U>> Monad<That, ContinuationFactory<R>, U> flatMap(Function<T, Monad<That, ContinuationFactory<R>, U>> mapper) {
            return null;
        }

        public abstract R listen(Function<T, R> handler);

        public <B, That> Continuation<B> flatMap(Function<T, Monad<ContinuationFactory<R>, B>> mapper) {

            return shift(handler ->
                    listen(a -> {
                        return mapper.apply(a).listen(handler);
                    }));
        }
    }


    public <T> Continuation<T> shift(Function<Function<T, R>, R> launcher) {
        return new Continuation<T>() {
            @Override
            public R listen(Function<T, R> handler) {
                return launcher.apply(handler);
            }
        };
    }
//
//    @Override
//    public <T> Continuation<T> newInstance(T a) {
//        return shift(handler -> handler.apply(a));
//    }
//
//    public <T> Continuation<T> delay(Supplier<T> run) {
//        return shift(handler -> handler.apply(run.get()));
//    }
//
//
//    public static ContinuationFactory<Void> getVoidContinuationFactory() {
//        return VOID_CONTINUATION_FACTORY;
//    }
//
//    private static final ContinuationFactory<Void> VOID_CONTINUATION_FACTORY = new ContinuationFactory<Void>();
}
