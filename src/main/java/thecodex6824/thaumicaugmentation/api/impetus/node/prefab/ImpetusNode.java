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

package thecodex6824.thaumicaugmentation.api.impetus.node.prefab;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk.EnumCreateEntityType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import thecodex6824.thaumicaugmentation.api.impetus.node.CapabilityImpetusNode;
import thecodex6824.thaumicaugmentation.api.impetus.node.IImpetusConsumer;
import thecodex6824.thaumicaugmentation.api.impetus.node.IImpetusGraph;
import thecodex6824.thaumicaugmentation.api.impetus.node.IImpetusNode;
import thecodex6824.thaumicaugmentation.api.util.DimensionalBlockPos;

public class ImpetusNode implements IImpetusNode {
    
    protected IImpetusGraph graph;
    protected int maxInputs;
    protected int maxOutputs;
    protected DimensionalBlockPos loc;
    protected Set<DimensionalBlockPos> inputs;
    protected Set<DimensionalBlockPos> outputs;
    protected Object2BooleanOpenHashMap<DimensionalBlockPos> tempStorage;
    
    public ImpetusNode(int totalInputs, int totalOutputs) {
        this(totalInputs, totalOutputs, new DimensionalBlockPos(new BlockPos(0, 0, 0), 0));
    }
    
    public ImpetusNode(int totalInputs, int totalOutputs, DimensionalBlockPos location) {
        maxInputs = totalInputs;
        maxOutputs = totalOutputs;
        loc = location;
        inputs = new HashSet<>();
        outputs = new HashSet<>();
        graph = new ImpetusGraph();
        if (loc != null)
            graph.addNode(this);
    }
    
    @Override
    public IImpetusGraph getGraph() {
        return graph;
    }
    
    @Override
    public void setGraph(IImpetusGraph newGraph) {
        graph = newGraph;
    }
    
    @Override
    public int getNumInputs() {
        return inputs.size();
    }
    
    @Override
    public int getNumOutputs() {
        return outputs.size();
    }
    
    @Override
    public int getMaxInputs() {
        return maxInputs;
    }
    
    @Override
    public int getMaxOutputs() {
        return maxOutputs;
    }
    
    @Override
    public boolean hasInput(IImpetusNode in) {
        return inputs.contains(in.getLocation());
    }
    
    @Override
    public boolean hasOutput(IImpetusNode out) {
        return outputs.contains(out.getLocation());
    }
    
    @Override
    public void addInput(IImpetusNode input) {
        addInputLocation(input.getLocation());
        input.addOutputLocation(loc);
        graph.addNode(input);
        onConnected(input);
    }
    
    @Override
    public void addOutput(IImpetusNode output) {
        addOutputLocation(output.getLocation());
        output.addInputLocation(loc);
        graph.addNode(output);
        onConnected(output);
    }
    
    @Override
    public boolean removeInput(IImpetusNode input) {
        onDisconnected(input);
        return inputs.remove(input.getLocation());
    }
    
    @Override
    public boolean removeOutput(IImpetusNode output) {
        onDisconnected(output);
        return outputs.remove(output.getLocation());
    }
    
    @Override
    public boolean canConnectNodeAsInput(IImpetusNode toConnect) {
        return true;
    }
    
    @Override
    public boolean canConnectNodeAsOutput(IImpetusNode toConnect) {
        return true;
    }
    
    @Override
    public void addInputLocation(DimensionalBlockPos toConnect) {
        if (inputs.size() == maxInputs && !inputs.contains(toConnect))
            throw new IndexOutOfBoundsException("Exceeded maximum amount of inputs for node (" + inputs.size() + ")");
        
        inputs.add(toConnect);
    }
    
    @Override
    public void addOutputLocation(DimensionalBlockPos toConnect) {
        if (outputs.size() == maxOutputs && !outputs.contains(toConnect))
            throw new IndexOutOfBoundsException("Exceeded maximum amount of outputs for node (" + outputs.size() + ")");
        
        outputs.add(toConnect);
    }
    
    @Override
    public boolean removeInputLocation(DimensionalBlockPos toRemove) {
        return inputs.remove(toRemove);
    }
    
    @Override
    public boolean removeOutputLocation(DimensionalBlockPos toRemove) {
        return outputs.remove(toRemove);
    }
    
    @Override
    public Set<DimensionalBlockPos> getInputLocations() {
        return inputs;
    }
    
    @Override
    public Set<DimensionalBlockPos> getOutputLocations() {
        return outputs;
    }
    
    @Override
    public Set<IImpetusNode> getInputs() {
        return inputs.stream().map(loc -> graph.findNodeByPosition(loc)).filter(Objects::nonNull).collect(Collectors.toSet());
    }
    
    @Override
    public Set<IImpetusNode> getOutputs() {
        return outputs.stream().map(loc -> graph.findNodeByPosition(loc)).filter(Objects::nonNull).collect(Collectors.toSet());
    }
    
    @Override
    public DimensionalBlockPos getLocation() {
        return loc;
    }
    
    @Override
    public void setLocation(DimensionalBlockPos location) {
        if (loc != null)
            graph.removeNode(this);
        
        loc = location;
        graph.addNode(this);
    }
    
    @Override
    public void onConnected(IImpetusNode other) {}
    
    @Override
    public void onDisconnected(IImpetusNode other) {}
    
    @Override
    public void onTransaction(IImpetusConsumer originator, long impetusAmount) {}
    
    @Override
    public void destroy() {
        graph.removeNode(this);
    }
    
    @Override
    public void init() {
        if (tempStorage != null) {
            for (Map.Entry<DimensionalBlockPos, Boolean> entry : tempStorage.entrySet()) {
                DimensionalBlockPos pos = entry.getKey();
                if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                    WorldServer world = DimensionManager.getWorld(pos.getDimension());
                    if (world != null && world.isBlockLoaded(pos.getPos()) &&
                            world.getChunk(pos.getPos()).getTileEntity(pos.getPos(), EnumCreateEntityType.CHECK) != null) {
                        
                        TileEntity te = world.getTileEntity(pos.getPos());
                        if (te.hasCapability(CapabilityImpetusNode.IMPETUS_NODE, null)) {
                            if (entry.getValue())
                                addOutput(te.getCapability(CapabilityImpetusNode.IMPETUS_NODE, null));
                            else
                                addInput(te.getCapability(CapabilityImpetusNode.IMPETUS_NODE, null));
                            
                            continue;
                        }
                    }
                }
                else {
                    WorldClient world = Minecraft.getMinecraft().world;
                    if (world != null && world.provider.getDimension() == pos.getDimension() && world.isBlockLoaded(pos.getPos()) &&
                            world.getChunk(pos.getPos()).getTileEntity(pos.getPos(), EnumCreateEntityType.CHECK) != null) {
                        
                        TileEntity te = world.getTileEntity(pos.getPos());
                        if (te.hasCapability(CapabilityImpetusNode.IMPETUS_NODE, null)) {
                            if (entry.getValue())
                                addOutput(te.getCapability(CapabilityImpetusNode.IMPETUS_NODE, null));
                            else
                                addInput(te.getCapability(CapabilityImpetusNode.IMPETUS_NODE, null));
                            
                            continue;
                        }
                    }
                }
                
                if (entry.getValue())
                    addOutputLocation(pos);
                else
                    addInputLocation(pos);
            }
            
            tempStorage = null;
        }
    }
    
    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        tempStorage = new Object2BooleanOpenHashMap<>();
        NBTTagList list = nbt.getTagList("inputs", NBT.TAG_INT_ARRAY);
        for (int i = 0; i < list.tagCount(); ++i)
            tempStorage.put(new DimensionalBlockPos(list.getIntArrayAt(i)), false);
        
        list = nbt.getTagList("outputs", NBT.TAG_INT_ARRAY);
        for (int i = 0; i < list.tagCount(); ++i)
            tempStorage.put(new DimensionalBlockPos(list.getIntArrayAt(i)), true);
    }
    
    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList inputs = new NBTTagList();
        for (DimensionalBlockPos pos : this.inputs)
            inputs.appendTag(new NBTTagIntArray(pos.toArray()));
        
        NBTTagList outputs = new NBTTagList();
        for (DimensionalBlockPos pos : this.outputs)
            outputs.appendTag(new NBTTagIntArray(pos.toArray()));
        
        tag.setTag("inputs", inputs);
        tag.setTag("outputs", outputs);
        return tag;
    }
    
}