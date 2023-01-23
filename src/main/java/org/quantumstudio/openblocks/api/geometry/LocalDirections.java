package org.quantumstudio.openblocks.api.geometry;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.minecraft.core.Direction;

public class LocalDirections {

	public final Direction front;

	public final Direction back;

	public final Direction top;

	public final Direction bottom;

	public final Direction left;

	public final Direction right;

	private LocalDirections(Direction front, Direction top) {
		this.front = front;
		this.back = front.getOpposite();
		this.top = top;
		this.bottom = top.getOpposite();

		final HalfAxis frontHa = HalfAxis.fromDirection(front);
		final HalfAxis topHa = HalfAxis.fromDirection(top);

		this.right = frontHa.cross(topHa).direction;
		this.left = topHa.cross(frontHa).direction;
	}

	private static final Table<Direction, Direction, LocalDirections> frontAndTop = HashBasedTable.create();

	static {
		for (Direction front : Direction.values())
			for (Direction top : Direction.values()) {
				if (top.getAxis() != front.getAxis())
					frontAndTop.put(front, top, new LocalDirections(front, top));
			}
	}

	public static LocalDirections fromFrontAndTop(Direction front, Direction top) {
		return frontAndTop.get(front, top);
	}
}
