package com.amuzil.omegasource.skillapi.data.conditions.key;

import com.amuzil.omegasource.skillapi.activateable.KeyInfo;
import com.amuzil.omegasource.skillapi.data.conditions.StateCondition;

public interface KeyCondition extends StateCondition {

    KeyInfo getKeyInfo();
}
