package zzik2.barched.nautilus;

import net.minecraft.SharedConstants;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.nautilus.AbstractNautilus;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockMakers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Exercise the real equipment/container methods without starting a client or server. */
public class NautilusEquipmentSyncTest {
    @BeforeClass
    public static void bootstrap() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void clientArmorPacketCannotErasePreviouslyReceivedSaddle() throws Exception {
        AbstractNautilus nautilus = create(true);
        receiveSaddle(nautilus, new ItemStack(Items.SADDLE));
        assertTrue(nautilus.getInventory().getItem(0).isEmpty());
        nautilus.setItemSlot(EquipmentSlot.BODY, new ItemStack(Items.IRON_HORSE_ARMOR));
        assertTrue("Armor packet must preserve steering eligibility", nautilus.isSaddled());
        assertTrue(nautilus.getBodyArmorItem().is(Items.IRON_HORSE_ARMOR));
        nautilus.setItemSlot(EquipmentSlot.BODY, ItemStack.EMPTY);
        assertTrue("Removing armor must also preserve the saddle", nautilus.isSaddled());
    }

    @Test
    public void clientContainerChangesCannotPublishEquipment() throws Exception {
        AbstractNautilus nautilus = create(true);
        nautilus.setItemSlot(EquipmentSlot.BODY, new ItemStack(Items.DIAMOND_HORSE_ARMOR));
        receiveSaddle(nautilus, new ItemStack(Items.SADDLE));
        nautilus.containerChanged(nautilus.getInventory());
        assertTrue(nautilus.isSaddled());
        assertTrue(nautilus.getBodyArmorItem().is(Items.DIAMOND_HORSE_ARMOR));
    }

    @Test
    public void serverSlotsRemainIndependentWhenEquippingReplacingAndRemoving() throws Exception {
        AbstractNautilus nautilus = create(false);
        SimpleContainer inventory = nautilus.getInventory();
        inventory.setItem(0, new ItemStack(Items.SADDLE));
        inventory.setItem(1, new ItemStack(Items.IRON_HORSE_ARMOR));
        assertTrue(nautilus.isSaddled());
        assertTrue(nautilus.getBodyArmorItem().is(Items.IRON_HORSE_ARMOR));
        nautilus.setItemSlot(EquipmentSlot.BODY, new ItemStack(Items.DIAMOND_HORSE_ARMOR));
        assertTrue(nautilus.isSaddled());
        assertTrue(inventory.getItem(1).is(Items.DIAMOND_HORSE_ARMOR));
        inventory.setItem(0, ItemStack.EMPTY);
        assertFalse(nautilus.isSaddled());
        assertTrue(nautilus.getBodyArmorItem().is(Items.DIAMOND_HORSE_ARMOR));
        inventory.setItem(0, new ItemStack(Items.SADDLE));
        inventory.setItem(1, ItemStack.EMPTY);
        assertTrue(nautilus.isSaddled());
        assertTrue(nautilus.getBodyArmorItem().isEmpty());
    }

    @Test
    public void bodyEquipmentCallbackOccursOncePerChange() throws Exception {
        AbstractNautilus nautilus = create(false);
        ItemStack armor = new ItemStack(Items.IRON_HORSE_ARMOR);
        nautilus.setItemSlot(EquipmentSlot.BODY, armor);
        verify(nautilus, times(1)).onEquipItem(EquipmentSlot.BODY, ItemStack.EMPTY, armor);
    }

    private static AbstractNautilus create(boolean client) throws Exception {
        // Mock world/sound side effects only; use actual entity equipment fields,
        // data storage, SimpleContainer listener, and all synchronization methods.
        AbstractNautilus nautilus = mock(AbstractNautilus.class,
                withSettings().defaultAnswer(CALLS_REAL_METHODS).mockMaker(MockMakers.SUBCLASS));
        Level level = mock(Level.class);
        when(level.isClientSide()).thenReturn(client);
        doReturn(level).when(nautilus).level();
        doNothing().when(nautilus).onEquipItem(any(), any(), any());
        doNothing().when(nautilus).onSyncedDataUpdated(any(EntityDataAccessor.class));
        doNothing().when(nautilus).barched$onSaddleEquipItem(any(), any());
        doNothing().when(nautilus).setDropChance(any(), anyFloat());
        setField(Mob.class, "bodyArmorItem", nautilus, ItemStack.EMPTY);
        SynchedEntityData.Builder builder = new SynchedEntityData.Builder(nautilus);
        // Entity normally defines these in its constructor before invoking the
        // subclass hook; constructor-free mocks need the same base entries.
        defineEntityData(builder, "DATA_SHARED_FLAGS_ID", (byte) 0);
        defineEntityData(builder, "DATA_AIR_SUPPLY_ID", 300);
        defineEntityData(builder, "DATA_CUSTOM_NAME", Optional.empty());
        defineEntityData(builder, "DATA_CUSTOM_NAME_VISIBLE", false);
        defineEntityData(builder, "DATA_SILENT", false);
        defineEntityData(builder, "DATA_NO_GRAVITY", false);
        defineEntityData(builder, "DATA_POSE", Pose.STANDING);
        defineEntityData(builder, "DATA_TICKS_FROZEN", 0);
        Method define = AbstractNautilus.class.getDeclaredMethod("defineSynchedData", SynchedEntityData.Builder.class);
        define.setAccessible(true);
        define.invoke(nautilus, builder);
        setField(Entity.class, "entityData", nautilus, builder.build());
        SimpleContainer inventory = new SimpleContainer(2);
        inventory.addListener(nautilus);
        setField(AbstractNautilus.class, "inventory", nautilus, inventory);
        return nautilus;
    }

    @SuppressWarnings("unchecked")
    private static <T> void defineEntityData(SynchedEntityData.Builder builder, String name, T value) throws Exception {
        Field field = Entity.class.getDeclaredField(name);
        field.setAccessible(true);
        builder.define((EntityDataAccessor<T>) field.get(null), value);
    }

    @SuppressWarnings("unchecked")
    private static void receiveSaddle(AbstractNautilus nautilus, ItemStack saddle) throws Exception {
        Field field = AbstractNautilus.class.getDeclaredField("SADDLE_ITEM");
        field.setAccessible(true);
        nautilus.getEntityData().set((EntityDataAccessor<ItemStack>) field.get(null), saddle);
    }

    private static void setField(Class<?> owner, String name, Object instance, Object value) throws Exception {
        Field field = owner.getDeclaredField(name);
        field.setAccessible(true);
        field.set(instance, value);
    }
}
