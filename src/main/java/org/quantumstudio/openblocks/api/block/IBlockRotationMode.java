package org.quantumstudio.openblocks.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.quantumstudio.openblocks.api.geometry.LocalDirections;
import org.quantumstudio.openblocks.api.geometry.Orientation;

import java.util.Set;

public interface IBlockRotationMode {
    Property<Orientation> getProperty();

    int getMask();

    Orientation fromValue(BlockState blockState);

    BlockState toValue(Orientation orientation);

    boolean isOrientationValid(Orientation orientation);

    Set<Orientation> getValidOrientations();

    Orientation getOrientationFacing(Direction side);

    Direction getFront(Orientation orientation);

    Direction getTop(Orientation orientation);

    LocalDirections getLocalDirections(Orientation orientation);

    Orientation getPlacementOrientationFromEntity(BlockPos pos, LivingEntity player);

    boolean toolRotationAllowed();

    Direction[] getToolRotationAxes();

    Orientation calculateToolRotation(Orientation current, Direction axis);
}
