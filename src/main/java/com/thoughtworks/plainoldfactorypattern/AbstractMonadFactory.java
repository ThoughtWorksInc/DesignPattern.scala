package com.thoughtworks.plainoldfactorypattern;

/**
 * @author 杨博 (Yang Bo)
 */
abstract class AbstractMonadFactory implements MonadFactory {
    abstract class AbstractMonad<A> implements Monad<A> {
        @Override
        public MonadFactory getFactory() {
            return AbstractMonadFactory.this;
        }
    }
}
