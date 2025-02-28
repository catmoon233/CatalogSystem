package net.exmo.catalogsystem.util;

import io.netty.buffer.Unpooled;
import net.exmo.catalogsystem.Catalogsystem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class PlayerUtil {

    public static class MenuGen<T extends AbstractContainerMenu> {
        private final Class<T> menuClass;

        public MenuGen(Class<T> menuClass) {
            this.menuClass = menuClass;
        }
        private CompoundTag tag = new CompoundTag();

        public void OpenGui(Player player, Component displayName) {
            if (player == null)
                return;
            if (player instanceof ServerPlayer _ent) {
                BlockPos _bpos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
                tag.putDouble("x", player.getX());
                tag.putDouble("y", player.getY());
                tag.putDouble("z", player.getZ());
                NetworkHooks.openScreen(_ent, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return displayName;
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
                        try {
                            // Use reflection to create an instance of the menu class
                            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
                            buf.writeBlockPos(_bpos);  // Ensure this is called before passing the buffer

                            if (tag != null)
                                buf.writeNbt(tag);
//                            if (string != null && !string.isEmpty())

//                                buf.writeUtf(string);
                            Constructor<T> constructor = menuClass.getConstructor(int.class, Inventory.class, FriendlyByteBuf.class);
                            T menuInstance = constructor.newInstance(id, inventory,buf);
                            return menuInstance;
                        } catch (InvocationTargetException e) {
                            // Print the cause of the InvocationTargetException
                            Throwable targetException = e.getTargetException();
                            targetException.printStackTrace();
                            Catalogsystem.LOGGER.error("Failed to instantiate menu due to an exception in the constructor: " + targetException.getMessage(), targetException);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
//                        try {
//                            // Direct instantiation for testing purposes
//                            return new WeaponCatalogMenu(id, inventory, new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(_bpos));
//                        } catch (RuntimeException e) {
//                            e.printStackTrace();
//                            throw e;
//                        }
                        return null;
                    }
                }, _bpos);
            }
        }

        public CompoundTag getTag() {
            return tag;
        }

        private String string = "";
        public MenuGen<T> setTag(CompoundTag tag) {
            this.tag = tag;
            return this;
        }

        public String getString() {
            return string;
        }

        public MenuGen<T> setString(String string) {
            this.string = string;
            return this;
        }
    }
}

