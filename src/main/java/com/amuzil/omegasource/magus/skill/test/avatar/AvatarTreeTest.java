package com.amuzil.omegasource.magus.skill.test.avatar;

/**
 * Tests out techniques using the radix tree.
 */
public class AvatarTreeTest {

    public static void registerTechniques() {

        //Air Gust: Arc > Strike
        // So it's tree would have 2 nodes: One for Arc, One for Strike *For the Effects*.
        // So a tree reliant on Effects would be reliant on EventConditionals, stemming from
        // listening to the 'onSuccess' event for OnSkillUse.
    }
}
