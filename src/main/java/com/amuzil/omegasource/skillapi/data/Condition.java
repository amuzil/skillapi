package com.amuzil.omegasource.skillapi.data;

public interface Condition {

    void register(Runnable success, Runnable expire);

    void unregister();
}
