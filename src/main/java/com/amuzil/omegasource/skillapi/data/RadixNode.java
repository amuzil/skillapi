package com.amuzil.omegasource.skillapi.data;

import java.util.Map;

public class RadixNode {
    RadixNode parent;

    Map<Condition, RadixNode> children;
    Runnable fallback;
    Condition fallbackCondition;
}
