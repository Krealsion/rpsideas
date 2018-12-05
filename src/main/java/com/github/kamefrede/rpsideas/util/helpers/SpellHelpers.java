package com.github.kamefrede.rpsideas.util.helpers;
import com.github.kamefrede.rpsideas.items.ItemPsimetalRod;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import vazkii.psi.api.PsiAPI;
import vazkii.psi.api.internal.Vector3;
import vazkii.psi.api.spell.*;
import vazkii.psi.common.core.handler.PlayerDataHandler;
import vazkii.psi.common.item.ItemCAD;
import vazkii.psi.common.spell.trick.block.PieceTrickBreakBlock;
import vazkii.psi.common.spell.trick.block.PieceTrickPlaceBlock;

@SuppressWarnings("UnnecessaryUnboxing") //explicit unboxing is good for your brain
public class SpellHelpers {
    public static class Building {
        public static void addAllParams(SpellPiece piece, SpellParam... params) {
            for(SpellParam param : params) {
                piece.addParam(param);
            }
        }
    }

    public void castSpell(EntityPlayer player, ItemStack stack, Vec3d pos){
        PlayerDataHandler.PlayerData data = PlayerDataHandler.get(player);
        ItemStack playerCad = PsiAPI.getPlayerCAD(player);
        if(stack.getItem() instanceof ItemPsimetalRod){
            ItemPsimetalRod rod = (ItemPsimetalRod) stack.getItem();
            if(!playerCad.isEmpty()) {
                ItemStack bullet = rod.getBulletInSocket(stack, rod.getSelectedSlot(stack));
                ItemCAD.cast(player.getEntityWorld(), player, data, bullet, playerCad, 5, 10, 0.05F, (SpellContext context) -> {
                    context.tool = stack;
                    context.positionBroken = new RayTraceResult(pos, EnumFacing.UP);
                });
            }
        }

    }

    public static class Compilation {
        public static double ensurePositiveAndNonzero(SpellPiece piece, SpellParam param) throws SpellCompilationException {
            Double val = piece.getParamEvaluation(param);

            if(val == null || val <= 0) {
                throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, piece.x, piece.y);
            }

            return val.doubleValue();
        }

        public static double ensurePositiveOrZero(SpellPiece piece, SpellParam param) throws SpellCompilationException {
            Double val = piece.getParamEvaluation(param);

            if(val == null || val <= 0) {
                throw new SpellCompilationException(SpellCompilationException.NON_POSITIVE_VALUE, piece.x, piece.y);
            }

            return val.doubleValue();
        }
    }

    public static class Runtime {
        public static BlockPos getBlockPosFromVectorParam(SpellPiece piece, SpellContext context, SpellParam param) {
            return ((Vector3) piece.getParamValue(context, param)).toBlockPos();
        }

        public static void checkPos(SpellContext context, BlockPos pos) throws SpellRuntimeException {
            if(pos == null) throw new SpellRuntimeException(SpellRuntimeException.NULL_VECTOR);
            if(!isBlockPosInRadius(context, pos)) throw new SpellRuntimeException(SpellRuntimeException.OUTSIDE_RADIUS);
        }

        public static boolean isBlockPosInRadius(SpellContext context, BlockPos pos) {
            return context.isInRadius(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);
        }

        public static double getNumber(SpellPiece piece, SpellContext context, SpellParam param, double def) {
            Double value = piece.getParamValue(context, param);
            if(value == null) return def;
            else return value.doubleValue();
        }

        public static void placeBlockFromInventory(SpellContext context, BlockPos pos, boolean particles) {
            PieceTrickPlaceBlock.placeBlock(context.caster, context.caster.world, pos, context.targetSlot, particles);
        }

        public static boolean placeBlock(World world, BlockPos pos, IBlockState setState, boolean particles) {
            if(world.isRemote || !world.isBlockLoaded(pos)) return false;

            IBlockState currentState = world.getBlockState(pos);
            if(currentState != setState && (currentState.getBlock().isReplaceable(world, pos) || currentState.getBlock().isAir(currentState, world, pos))) {
                boolean couldPlace = world.setBlockState(pos, setState);
                if(couldPlace && particles) world.playEvent(2001, pos, Block.getStateId(setState));
                return couldPlace;
            }
            return false;
        }

        public static void breakBlock(SpellContext context, BlockPos pos, boolean particles) {
            PieceTrickBreakBlock.removeBlockWithDrops(context, context.caster, context.caster.world, context.tool, pos, particles);
        }
    }
}