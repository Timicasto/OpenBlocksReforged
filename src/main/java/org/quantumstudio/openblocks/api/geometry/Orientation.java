package org.quantumstudio.openblocks.api.geometry;

import com.google.common.base.Preconditions;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import javax.vecmath.*;
import java.util.Locale;

public enum Orientation implements StringRepresentable {
	XN_YN(HalfAxis.NEG_X, HalfAxis.NEG_Y),
	XN_YP(HalfAxis.NEG_X, HalfAxis.POS_Y),
	XN_ZN(HalfAxis.NEG_X, HalfAxis.NEG_Z),
	XN_ZP(HalfAxis.NEG_X, HalfAxis.POS_Z),

	XP_YN(HalfAxis.POS_X, HalfAxis.NEG_Y),
	XP_YP(HalfAxis.POS_X, HalfAxis.POS_Y),
	XP_ZN(HalfAxis.POS_X, HalfAxis.NEG_Z),
	XP_ZP(HalfAxis.POS_X, HalfAxis.POS_Z),

	YN_ZP(HalfAxis.NEG_Y, HalfAxis.POS_Z),
	YN_XN(HalfAxis.NEG_Y, HalfAxis.NEG_X),
	YN_XP(HalfAxis.NEG_Y, HalfAxis.POS_X),
	YN_ZN(HalfAxis.NEG_Y, HalfAxis.NEG_Z),

	YP_XN(HalfAxis.POS_Y, HalfAxis.NEG_X),
	YP_XP(HalfAxis.POS_Y, HalfAxis.POS_X),
	YP_ZN(HalfAxis.POS_Y, HalfAxis.NEG_Z),
	YP_ZP(HalfAxis.POS_Y, HalfAxis.POS_Z),

	ZN_XP(HalfAxis.NEG_Z, HalfAxis.POS_X),
	ZN_XN(HalfAxis.NEG_Z, HalfAxis.NEG_X),
	ZN_YP(HalfAxis.NEG_Z, HalfAxis.POS_Y),
	ZN_YN(HalfAxis.NEG_Z, HalfAxis.NEG_Y),

	ZP_XN(HalfAxis.POS_Z, HalfAxis.NEG_X),
	ZP_XP(HalfAxis.POS_Z, HalfAxis.POS_X),
	ZP_YN(HalfAxis.POS_Z, HalfAxis.NEG_Y),
	ZP_YP(HalfAxis.POS_Z, HalfAxis.POS_Y);

	public static final Orientation[] VALUES = values();

	public static final TIntObjectMap<Orientation> LOOKUP_XY = new TIntObjectHashMap<>(VALUES.length);
	public static final TIntObjectMap<Orientation> LOOKUP_XZ = new TIntObjectHashMap<>(VALUES.length);
	public static final TIntObjectMap<Orientation> LOOKUP_YZ = new TIntObjectHashMap<>(VALUES.length);

	public static final Orientation[][] ROTATIONS = new Orientation[VALUES.length][HalfAxis.VALUES.length];

	private static int lookupKey(HalfAxis a, HalfAxis b) {
		return (a.ordinal() << 3) | (b.ordinal());
	}

	private static void addToLookup(TIntObjectMap<Orientation> lookup, Orientation o, HalfAxis a, HalfAxis b) {
		final int key = lookupKey(a, b);
		final Orientation prev = lookup.put(key, o);
		Preconditions.checkState(prev == null, "Key %s duplicate: %s -> %s", key, prev, 0);
	}

	static {
		for (Orientation o : VALUES) {
			addToLookup(LOOKUP_XY, o, o.x, o.y);
			addToLookup(LOOKUP_XZ, o, o.x, o.z);
			addToLookup(LOOKUP_YZ, o, o.y, o.z);
		}

		for (Orientation o : VALUES) {
			final int i = o.ordinal();

			ROTATIONS[i][HalfAxis.POS_X.ordinal()] = lookupXYNotNull(o.x, o.z.negate()/* , o.y */);
			ROTATIONS[i][HalfAxis.NEG_X.ordinal()] = lookupXYNotNull(o.x, o.z/* , o.y.negate() */);

			ROTATIONS[i][HalfAxis.POS_Y.ordinal()] = lookupXYNotNull(o.z, o.y/* , o.x.negate() */);
			ROTATIONS[i][HalfAxis.NEG_Y.ordinal()] = lookupXYNotNull(o.z.negate(), o.y/* , o.x */);

			ROTATIONS[i][HalfAxis.POS_Z.ordinal()] = lookupXYNotNull(o.y.negate(), o.x/* , o.z */);
			ROTATIONS[i][HalfAxis.NEG_Z.ordinal()] = lookupXYNotNull(o.y, o.x.negate()/* , o.z */);
		}
	}

	public final HalfAxis x, y, z;

	private final String name, lowercaseName;

	private final Direction[] localToGlobal = new Direction[Direction.values().length];
	private final Direction[] globalToLocal = new Direction[Direction.values().length];

	private final Matrix3f blockCenterToWorld;
	private final Matrix4f blockCornerToWorld;

	private void addDirectionMapping(Direction local, Direction global) {
		localToGlobal[local.ordinal()] = global;
		globalToLocal[global.ordinal()] = local;
	}

	private void addDirectionMappings(Direction local, Direction global) {
		addDirectionMapping(local, global);
		addDirectionMapping(local.getOpposite(), global.getOpposite());
	}

	private static Matrix4f blockCenterToCorner(Matrix4f transform) {
		Matrix4f ret = new Matrix4f(transform);
		Matrix4f tmp = new Matrix4f();
		tmp.setIdentity();
		tmp.m03 = tmp.m13 = tmp.m23 = 0.5f;
		ret.mul(tmp, ret);
		tmp.m03 = tmp.m13 = tmp.m23 = -0.5f;
		ret.mul(tmp);
		return ret;
	}

	public static Orientation lookupXY(HalfAxis x, HalfAxis y) {
		final int key = lookupKey(x, y);
		return LOOKUP_XY.get(key);
	}

	public static Orientation lookupXZ(HalfAxis x, HalfAxis z) {
		final int key = lookupKey(x, z);
		return LOOKUP_XZ.get(key);
	}

	public static Orientation lookupYZ(HalfAxis y, HalfAxis z) {
		final int key = lookupKey(y, z);
		return LOOKUP_YZ.get(key);
	}

	private static Orientation lookupXYNotNull(HalfAxis x, HalfAxis y) {
		Orientation v = lookupXY(x, y);
		if (v == null) throw new NullPointerException(x + ":" + y);
		return v;
	}

	public static Orientation rotateAround(Orientation orientation, HalfAxis axis) {
		return ROTATIONS[orientation.ordinal()][axis.ordinal()];
	}

	public Orientation rotateAround(HalfAxis axis) {
		return rotateAround(this, axis);
	}

	Orientation(HalfAxis x, HalfAxis y) {
		this.x = x;
		this.y = y;
		this.z = x.cross(y);
		
		addDirectionMappings(Direction.EAST, x.direction);
		addDirectionMappings(Direction.UP, y.direction);
		addDirectionMappings(Direction.SOUTH, z.direction);
		
		this.name = x.shortName + "_" + y.shortName + "_" + z.shortName;
		this.lowercaseName = name().toLowerCase(Locale.ROOT);
		
		this.blockCenterToWorld = new Matrix3f(
				x.x, y.x, z.x,
				x.y, y.y, z.y,
				x.z, y.z, z.z
		);
		final Matrix4f tmp = new Matrix4f();
		tmp.set(this.blockCenterToWorld);
		this.blockCornerToWorld = blockCenterToCorner(tmp);
	}

	public Direction localToGlobalDirection(Direction local) {
		return localToGlobal[local.ordinal()];
	}

	public Direction globalToLocalDirection(Direction global) {
		return globalToLocal[global.ordinal()];
	}

	public Direction north() {
		return localToGlobalDirection(Direction.NORTH);
	}

	public Direction south() {
		return localToGlobalDirection(Direction.SOUTH);
	}

	public Direction east() {
		return localToGlobalDirection(Direction.EAST);
	}

	public Direction west() {
		return localToGlobalDirection(Direction.WEST);
	}

	public Direction up() {
		return localToGlobalDirection(Direction.UP);
	}

	public Direction down() {
		return localToGlobalDirection(Direction.DOWN);
	}

	public double transformX(double x, double y, double z) {
		return this.x.x * x + this.y.x * y + this.z.x * z;
	}

	public int transformX(int x, int y, int z) {
		return this.x.x * x + this.y.x * y + this.z.x * z;
	}

	public double transformY(double x, double y, double z) {
		return this.x.y * x + this.y.y * y + this.z.y * z;
	}

	public int transformY(int x, int y, int z) {
		return this.x.y * x + this.y.y * y + this.z.y * z;
	}

	public double transformZ(double x, double y, double z) {
		return this.x.z * x + this.y.z * y + this.z.z * z;
	}

	public int transformZ(int x, int y, int z) {
		return this.x.z * x + this.y.z * y + this.z.z * z;
	}

	/**
	 * Returns transformation for case when middle of the block is in center of local space
	 *
	 * @return local (block centered) to world transformation
	 */
	public Matrix3f getLocalToWorldMatrix() {
		return (Matrix3f)blockCenterToWorld.clone();
	}

	/**
	 * Returns transformation for case when (-0.5,-0.5,-0.5) corner of the block is in center of local space
	 *
	 * @return local (block corner centered) to world transformation
	 */
	public Matrix4f getBlockLocalToWorldMatrix() {
		return (Matrix4f)blockCornerToWorld.clone();
	}

	@Override
	public @NotNull String getSerializedName() {
		return lowercaseName;
	}

	@Override
	public String toString() {
		return name;
	}
}
