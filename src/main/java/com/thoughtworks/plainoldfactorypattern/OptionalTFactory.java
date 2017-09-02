package com.thoughtworks.plainoldfactorypattern;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author 杨博 (Yang Bo)
 */
public class OptionalTFactory extends AbstractMonadFactory {
    private MonadFactory underlyingFactory;

    @Override
    public <A> OptionalT<A> newInstance(A a) {
        return new OptionalT<A>(underlyingFactory.newInstance(Optional.of(a)));
    }

    public class OptionalT<A> extends AbstractMonad<A> {

        public OptionalT(Monad<Optional<A>> underlyingMonad) {
            this.underlyingMonad = underlyingMonad;
        }

        private final Monad<Optional<A>> underlyingMonad;

        public Monad<Optional<A>> getUnderlyingMonad() {
            return underlyingMonad;
        }

        @Override
        public <B> OptionalT<B> flatMap(final Function<A, Monad<B>> mapper) {
            return new OptionalT<B>(underlyingMonad.flatMap(optional -> {
                        if (optional.isPresent()) {
                            OptionalT<B> optionalB = (OptionalT<B>) mapper.apply(optional.get());
                            return optionalB.underlyingMonad;
                        } else {
                            return underlyingFactory.newInstance(Optional.empty());
                        }
                    }
            ));

        }
    }
}
