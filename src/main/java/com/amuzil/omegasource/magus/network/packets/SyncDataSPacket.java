package com.amuzil.omegasource.magus.network.packets;

import com.amuzil.omegasource.magus.skill.util.capability.entity.Data;

public class SyncDataSPacket {
    private Data data;

    public SyncDataSPacket(Data data) {
        this.data = data;
    }


}
