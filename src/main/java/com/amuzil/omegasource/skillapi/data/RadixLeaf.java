package com.amuzil.omegasource.skillapi.data;

public interface RadixLeaf<T> {

    void burn();

    void reset();

    T measure();

}
