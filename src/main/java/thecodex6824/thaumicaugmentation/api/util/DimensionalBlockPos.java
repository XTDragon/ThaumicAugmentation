/**
 *  Thaumic Augmentation
 *  Copyright (c) 2019 TheCodex6824.
 *
 *  This file is part of Thaumic Augmentation.
 *
 *  Thaumic Augmentation is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Thaumic Augmentation is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Thaumic Augmentation.  If not, see <https://www.gnu.org/licenses/>.
 */

package thecodex6824.thaumicaugmentation.api.util;

import com.google.common.base.MoreObjects;

import net.minecraft.util.math.BlockPos;

public class DimensionalBlockPos {

    protected BlockPos pos;
    protected int dim;
    
    public DimensionalBlockPos(int x, int y, int z, int dimension) {
        this(new BlockPos(x, y, z), dimension);
    }
    
    public DimensionalBlockPos(BlockPos position, int dimension) {
        pos = position;
        dim = dimension;
    }
    
    public DimensionalBlockPos(int[] components) {
        if (components.length != 4)
            throw new ArrayIndexOutOfBoundsException("DimensionalBlockPos component array has wrong size");
        
        pos = new BlockPos(components[0], components[1], components[2]);
        dim = components[3];
    }
    
    public BlockPos getPos() {
        return pos;
    }
    
    public int getDimension() {
        return dim;
    }
    
    public int[] toArray() {
        return new int[] {pos.getX(), pos.getY(), pos.getZ(), dim};
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("x", pos.getX()).add("y", pos.getY()).add("z", pos.getZ()).add("dim", dim).toString();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DimensionalBlockPos) {
            return ((DimensionalBlockPos) obj).getPos().equals(pos) && 
                    ((DimensionalBlockPos) obj).getDimension() == dim;
        }
        else
            return false;
    }
    
    @Override
    public int hashCode() {
        return pos.hashCode() * 31 + dim;
    }
    
}