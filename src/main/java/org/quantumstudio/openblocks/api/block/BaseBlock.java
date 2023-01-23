package org.quantumstudio.openblocks.api.block;

import net.minecraft.world.level.block.Block;

public class BaseBlock extends Block {
    public enum PlacementMode {
        ENTITY_ANGLE,
        SURFACE
    }

//    public final ;

    public BaseBlock(Properties arg) {
        super(arg.strength(1.0F));
    }


}
