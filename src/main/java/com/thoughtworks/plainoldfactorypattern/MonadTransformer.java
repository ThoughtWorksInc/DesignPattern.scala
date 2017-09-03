//package com.thoughtworks.plainoldfactorypattern;
//
///**
// * @author 杨博 (Yang Bo)
// */
//public interface MonadTransformer<
//        Factory extends MonadTransformer.Factory<Factory, ?>, T> extends Monad<Factory, T> {
//
//    interface Factory<
//            Factory extends MonadTransformer.Factory<Factory, UnderlyingFactory>,
//            UnderlyingFactory extends Monad.Factory<UnderlyingFactory>> extends Monad.Factory<Factory> {
//
//        UnderlyingFactory getUnderlyingFactory();
//
//        @Override
//        default <T> MonadTransformer<Factory, T> newInstance(T t) {
//            return lift(getUnderlyingFactory().newInstance(t));
//        }
//
//        <T> MonadTransformer<Factory, T> lift(Monad<UnderlyingFactory, T> underlyingMonad);
//    }
//}
