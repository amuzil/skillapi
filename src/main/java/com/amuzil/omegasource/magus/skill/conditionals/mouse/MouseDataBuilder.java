package com.amuzil.omegasource.magus.skill.conditionals.mouse;

public class MouseDataBuilder {

    /**
     * Number corresponds to the MC mouse wheel direction.
     */
    enum Direction {

        // Away from the user
        FORWARDS(1),
        // Towards the user
        BACK(-1),
        NEUTRAL(0);

        private final int dir;
        Direction(int dir) {
            this.dir = dir;
        }

        public int getDirection() {
            return this.dir;
        }
    }
}
