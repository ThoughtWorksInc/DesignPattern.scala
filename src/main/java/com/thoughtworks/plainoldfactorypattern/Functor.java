package com.thoughtworks.plainoldfactorypattern;

import java.util.function.Function;

/**
 * @author 杨博 (Yang Bo)
 */
public interface Functor<ThisFactory, T> {

    <U> Functor<ThisFactory, U> map(Function<T, U> mapper);

}
