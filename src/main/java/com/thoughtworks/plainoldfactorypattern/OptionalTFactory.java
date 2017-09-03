package com.thoughtworks.plainoldfactorypattern;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author 杨博 (Yang Bo)
 */
public class OptionalTFactory implements MonadFactory {
    public OptionalTFactory(MonadFactory underlyingFactory) {
        this.underlyingFactory = underlyingFactory;
    }

    private MonadFactory underlyingFactory;

    @Override
    public <A> OptionalT<A> newInstance(A a) {
        return new OptionalT<A>(underlyingFactory.newInstance(Optional.of(a)));
    }

    public <A> OptionalT<A> lift(Monad<Optional<A>> underlyingMonad) {
        return new OptionalT(underlyingMonad);
    }

    public class OptionalT<A> implements Monad<A> {

        public OptionalT(Monad<Optional<A>> underlyingMonad) {
            this.underlyingMonad = underlyingMonad;
        }

        private final Monad<Optional<A>> underlyingMonad;

        public Monad<Optional<A>> getUnderlyingMonad() {
            return underlyingMonad;
        }

        @Override
        public OptionalTFactory getFactory() {
            return OptionalTFactory.this;
        }

        @Override
        public <B> OptionalT<B> flatMap(final Function<A, Monad<B>> mapper) {
            Monad<Optional<B>> optionalMonadB = underlyingMonad.flatMap(optional -> {
                if (optional.isPresent()) {
                    OptionalT<B> optionalB = (OptionalT<B>) mapper.apply(optional.get());
                    return optionalB.underlyingMonad;
                } else {
                    return underlyingFactory.newInstance(Optional.empty());
                }
            });
            return new OptionalT<B>(optionalMonadB);

        }
    }

    public static OptionalTFactory getTaskFactory() {
        return TASK_FACTORY;
    }

    private static final OptionalTFactory TASK_FACTORY = new OptionalTFactory(ContinuationFactory.getVoidContinuationFactory());

}
