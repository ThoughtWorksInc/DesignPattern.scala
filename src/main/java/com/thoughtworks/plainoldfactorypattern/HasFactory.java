package com.thoughtworks.plainoldfactorypattern;

/**
 * @author 杨博 (Yang Bo)
 */
public interface HasFactory<ThisFactory extends HasFactory.Factory<ThisFactory>, T> {
    ThisFactory getFactory();

    interface Factory<This extends HasFactory.Factory<This>> {

        <T> HasFactory<This, T> newInstance(T t);
    }
}
