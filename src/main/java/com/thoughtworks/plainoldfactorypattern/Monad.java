package com.thoughtworks.plainoldfactorypattern;

import java.util.function.Function;

/**
 * @author 杨博 (Yang Bo)
 */
public interface Monad<
        This extends Monad<This, ThisFactory, T>,
        ThisFactory extends Monad.Factory<ThisFactory>,
        T> extends HasFactory<ThisFactory, T>, Functor<ThisFactory, T> {

    <U, That extends Monad<That, ThisFactory, U>> Monad<That, ThisFactory, U> flatMap(Function<T, Monad<That, ThisFactory, U>> mapper);

    default <U> Monad<?, ThisFactory, U> map(Function<T, U> mapper) {
        return flatMap(a -> getFactory().newInstance(mapper.apply(a)));
    }

    interface Factory<ThisFactory extends Monad.Factory<ThisFactory>> extends HasFactory.Factory<ThisFactory> {

        <T> Monad<?, ThisFactory, T> newInstance(T t);

    }
}
