package com.thoughtworks.plainoldjavafactorydesignpattern;


import java.util.function.Function;

/**
 * @author 杨博 (Yang Bo)
 */
public interface MonadFactory {

    interface Monad<A> {

        MonadFactory getFactory();

        <B> Monad<B> flatMap(Function<A, Monad<B>> mapper);

        default <B> Monad<B> map(Function<A, B> mapper) {
            return flatMap(a -> getFactory().newInstance(mapper.apply(a)));
        }

    }

    <A> Monad<A> newInstance(A a);
}
