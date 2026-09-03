package net.whiterm.claysoldiersrebornmissfeatures.block.entity;

import com.matthewperiut.clay.entity.soldier.SoldierDollEntity;
import com.matthewperiut.clay.entity.soldier.teams.ITeam;
import com.matthewperiut.clay.item.common.ClayTag;
import com.matthewperiut.clay.item.soldier.SoldierDollItem;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.entity.*;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;
import net.whiterm.claysoldiersrebornmissfeatures.config.ModConfig;
import net.whiterm.claysoldiersrebornmissfeatures.screen.ClayNexusScreenHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class ClayNexusBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, GeoBlockEntity {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);

    public static List<ClayNexusBlockEntity> nexuses = new ArrayList<>();

    public int ticks = 0;
    private boolean nexusActive;
    private boolean redstoneActive = false;
    private Item currItem;
    private Item prevItem;
    private int droppedSoldierItems;
    //private Map<Item, Integer> droppedSoldiersMap = new HashMap<>();
    //private Map<Item, Integer> nexusSoldiers = new HashMap<>();
    public Map<Item, Integer> forPlayerItemSoldiers = new HashMap<>();
    public int withdrawMaxCapacity = 864; //(1x double chest full of Soldiers - 16 * 27 * 2)
    private final int MAX_HEALTH = ModConfig.NEXUS_HEALTH;
    private int health = MAX_HEALTH;
    public int backgroundColorText = 16711680;
    public int backgroundColorHealth = -4062204;
    private final int MAX_SOLDIERS_COUNT = ModConfig.MAX_SOLDIERS_COUNT;
    private int soldierDamage = 1;
    private final int SOLDIER_ATTACK_DELAY = ModConfig.SOLDIER_ATTACK_DELAY;
    private final int NEXUS_SPAWN_DELAY = ModConfig.NEXUS_SPAWN_DELAY;
    private ITeam soldierTeam;
    private Item soldierItem;
    private int[] nexusTeamColorRGB = new int[] {255, 255, 255};

    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String controllerName = "controller";

    private static final int INPUT_SOLDIER = 0;

    public ClayNexusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CLAY_NEXUS_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);

    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("gui." + ClaySoldiersRebornMissfeatures.MOD_ID + ".clay_nexus.display_name");
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    //Saving NBT
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, inventory);
        nbt.putBoolean("nexusActive", nexusActive);
        //nbt.putBoolean("redstoneActive", redstoneActive);
        nbt.putInt("health", health);
        //nbt.putIntArray("nexusTeamColor", getTeamColor());
        if (soldierItem != null) {
            nbt.put("soldierItem", soldierItem.getDefaultStack().writeNbt(new NbtCompound()));
        }

        NbtCompound forPlayerItemSoldiersNbt = new NbtCompound();
        for (var entry : forPlayerItemSoldiers.entrySet()) {
            forPlayerItemSoldiersNbt.putInt(String.valueOf(entry.getKey()), entry.getValue());
        }
        nbt.put("forPlayerItemSoldiers", forPlayerItemSoldiersNbt);
    }

    //Loading NBT
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, inventory);
        nexusActive = nbt.getBoolean("nexusActive");
        //redstoneActive = nbt.getBoolean("redstoneActive");
        health = nbt.getInt("health");
        //nexusTeamColorRGB = nbt.getIntArray("nexusTeamColor");
        if (nbt.contains("soldierItem")){
            soldierItem = ItemStack.fromNbt(nbt.getCompound("soldierItem")).getItem();
        }

        NbtCompound forPlayerItemSoldiersNbt = (NbtCompound) nbt.get("forPlayerItemSoldiers");
        if (forPlayerItemSoldiersNbt != null) {
            for (var key : forPlayerItemSoldiersNbt.getKeys()) {
                Identifier itemId = new Identifier("clay", key);
                if (Registries.ITEM.containsId(itemId)) {
                    forPlayerItemSoldiers.put(Registries.ITEM.get(itemId), forPlayerItemSoldiersNbt.getInt(key));
                }
            }
        }
    }

    //Add Nexus to nexuses if world is loaded
    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world.isClient()) return;
        nexuses.add(this);
        //System.out.println("add");
    }

    //Remove Nexus (setWorld is sometimes called when I don't want to it)
    @Override
    public void markRemoved() {
        if (!world.isClient()) {
            nexuses.remove(this);
            //System.out.println("rem");
        }
        super.markRemoved();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ClayNexusScreenHandler(syncId, playerInventory, this);
    }

    public void tick(World world, BlockPos pos, BlockState state) {
        ticks++;

        if (world.isClient()) {
            if (ticks == 1) {
                playAnim(nexusActive);
            }
            return;
        }

        int posDiffInt = 32;
        Vec3i posDiffVec3i = new Vec3i(posDiffInt, posDiffInt, posDiffInt);

        Item itemInSlot = inventory.get(0).getItem();
        // = itemInSlot.getDefaultStack().isIn(ClayTag.SOLDIERS) ? ((SoldierDollItem) itemInSlot) : (SoldierDollItem) ItemRegistry.CLAY_SOLDIER_ITEM.get();

        if (ticks % 2 == 0){
            prevItem = currItem;
        } else {
            currItem = itemInSlot;
        }

        if (currItem != prevItem){
            this.soldierItem = itemInSlot;
            updateDataAndClients();
        }

        if (!itemInSlot.getDefaultStack().isEmpty()) {
            SoldierDollItem soldierItem = ((SoldierDollItem) itemInSlot);
            EntityType<? extends PathAwareEntity> entityType = (EntityType<? extends PathAwareEntity>) soldierItem.getEntityType(soldierItem.getDefaultStack().getNbt());
            NbtCompound nbt = new NbtCompound();
            ITeam team = soldierItem.getTeam(soldierItem.getDefaultStack());

            //if the key does not exist in the map, it'll put it in
            //droppedSoldiersMap.putIfAbsent(soldierItem, 0);
            //nexusSoldiers.putIfAbsent(soldierItem, 0);
            forPlayerItemSoldiers.putIfAbsent(soldierItem, 0);

            //Count soldiers (for maintaining the soldier count)
            int soldiersCount = MAX_SOLDIERS_COUNT;
            if (nexusActive) {
                List<? extends SoldierDollEntity> soldiers =
                         world.getEntitiesByType((EntityType<? extends SoldierDollEntity>) entityType,
                                new Box(pos.subtract(posDiffVec3i), pos.add(posDiffVec3i)),
                                 LivingEntity::isAlive);
                if (currItem != prevItem){
                    soldiersCount = 0;
                    soldiers.clear();
                } else {
                    soldiersCount = soldiers.size();
                }
            }

            //Deal dmg to Nexus (Soldier attack)
            if (nexusActive) {
                if (ticks % SOLDIER_ATTACK_DELAY == 0) {
                    int nexusDmgHitbox = 1;
                    Vec3i nexusDmgHitboxBox = new Vec3i(nexusDmgHitbox, nexusDmgHitbox, nexusDmgHitbox);
                    List<? extends SoldierDollEntity> damagingSoldiers =
                            world.getEntitiesByType(TypeFilter.instanceOf(SoldierDollEntity.class),
                                    new Box(pos, pos.add(nexusDmgHitboxBox)),
                                    soldierEntity -> soldierEntity.isAlive() && (soldierEntity.getType() != entityType));
                    int damagingSoldiersCount = damagingSoldiers.size();

                    if (!damagingSoldiers.isEmpty() && health > 0){
                        if (damagingSoldiersCount > 1) {
                            health -= damagingSoldiersCount * soldierDamage;
                        } else {
                            health -= soldierDamage * 4;
                        }
                        if (health < 0) {
                            health = 0;
                        }
                        updateHealthBar(world, pos, backgroundColorHealth, backgroundColorText);
                        markDirty();
                    }
                }
            }

            //Turn off Nexus if 0 health
            if (health <= 0){
                setNexusActive(false);
            }

            //Spawn soldier
            if (nexusActive) {
                if (ticks % NEXUS_SPAWN_DELAY == 0) {
                    if (soldiersCount < MAX_SOLDIERS_COUNT && itemInSlot.getDefaultStack().isIn(ClayTag.SOLDIERS)){
                        Entity entity = entityType.spawnFromItemStack(
                                (ServerWorld) world,
                                soldierItem.getDefaultStack(),
                                null,
                                pos,
                                SpawnReason.SPAWN_EGG,
                                false,
                                false
                        );
                        if (entity != null) {
                            if (entity instanceof SoldierDollEntity soldier) {
                                soldier.setTeam(team);
                                soldier.writeNbt(nbt);
                                nbt.putString("DeathLootTable", "minecraft:empty");
                                soldier.readNbt(nbt);
                                //soldier.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
                                //nexusSoldiers.put(itemInSlot, nexusSoldiers.get(itemInSlot) + 1);
                            }
                        }
                    }
                }
            }

        /*System.out.println("droppedSoldiersMap: " + droppedSoldiersMap);
        System.out.println("soldiersCount: " + soldiersCount);
        System.out.println("nexusSoldiers: " + nexusSoldiers);
        System.out.println("otherThanNexusSoldiers: " + tets);
        System.out.println("forPlayerItemSoldiers: " + forPlayerItemSoldiers);*/
        }
    }

    public void addItemToWithdrawButton(Item item, int itemCount) {
        forPlayerItemSoldiers.put(item, forPlayerItemSoldiers.get(soldierItem) + itemCount);
        markDirty();
    }

    public ArrayList<ItemStack> beautifulStacks(boolean clearForPlayerItemSoldiers){
        ArrayList<ItemStack> list = new ArrayList<>(List.of());
        for (var entry : forPlayerItemSoldiers.entrySet()){
            Item entryKey = entry.getKey();
            double itemCount = forPlayerItemSoldiers.get(entryKey);
            double maxStackCount = 16d;

            double stackCount = Math.floor(itemCount / maxStackCount);
            double stackCountRest = ((itemCount / maxStackCount) - stackCount) * maxStackCount;

            ItemStack itemStack = entryKey.getDefaultStack();

            //make whole stack(s)
            for (int i = 0; i < stackCount; i++){
                ItemStack itemStack2 = entryKey.getDefaultStack();
                itemStack2.setCount(((int) maxStackCount));
                list.add(itemStack2);
            }
            //rest
            itemStack.setCount(((int) stackCountRest));
            if (stackCountRest != 0){
                list.add(itemStack);
            }

            //reset
            //droppedSoldiersMap.replace(entryKey, droppedSoldiersMap.get(entryKey) - forPlayerItemSoldiers.get(entryKey));
            if (clearForPlayerItemSoldiers) {
                forPlayerItemSoldiers.replace(entryKey, 0);
            }
            markDirty();
        }
        return list;
    }

    public int withdrawSoldiersCount() {
        int count = 0;
        for (var itemStack : beautifulStacks(false)) {
            count += itemStack.getCount();
        }
        return count;
    }

    /*------------------------------CREDITS--------------------------------
     * execute method taken from GiveCommand class - package net.minecraft.server.command;
     *----------------------------------------------------------------------*/
    public static int execute(ServerCommandSource source, @NotNull ItemStackArgument item, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
        int i = item.getItem().getMaxCount();
        int j = i * 100;
        ItemStack itemStack = item.createStack(count, false);
        if (count > j) {
            source.sendError(Text.translatable("commands.give.failed.toomanyitems", j, itemStack.toHoverableText()));
            return 0;
        }
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            int k = count;
            while (k > 0) {
                ItemEntity itemEntity;
                int l = Math.min(i, k);
                k -= l;
                ItemStack itemStack2 = item.createStack(l, false);
                boolean bl = serverPlayerEntity.getInventory().insertStack(itemStack2);
                if (!bl || !itemStack2.isEmpty()) {
                    itemEntity = serverPlayerEntity.dropItem(itemStack2, false);
                    if (itemEntity == null) continue;
                    itemEntity.resetPickupDelay();
                    itemEntity.setOwner(serverPlayerEntity.getUuid());
                    continue;
                }
                itemStack2.setCount(1);
                itemEntity = serverPlayerEntity.dropItem(itemStack2, false);
                if (itemEntity != null) {
                    itemEntity.setDespawnImmediately();
                }
                serverPlayerEntity.getWorld().playSound(null, serverPlayerEntity.getX(), serverPlayerEntity.getY(), serverPlayerEntity.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2f, ((serverPlayerEntity.getRandom().nextFloat() - serverPlayerEntity.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                serverPlayerEntity.currentScreenHandler.sendContentUpdates();
            }
        }
        if (targets.size() == 1) {
            source.sendFeedback(() -> Text.translatable("commands.give.success.single", count, itemStack.toHoverableText(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()), true);
        } else {
            source.sendFeedback(() -> Text.translatable("commands.give.success.single", count, itemStack.toHoverableText(), targets.size()), true);
        }
        return targets.size();
    }

    public void withdrawDroppedSoldiers(ServerPlayerEntity player) throws CommandSyntaxException {
        PlayerInventory playerInv = player.getInventory();
        MinecraftServer server = player.getServer();
        ServerCommandSource commandSource = server.getCommandSource()
                .withPosition(Vec3d.ofCenter(pos))
                .withWorld(((ServerWorld) player.getWorld()))
                .withSilent();
        List<ServerPlayerEntity> playerList = new ArrayList<>(List.of());
        playerList.add(player);
        int i = 0;
        for (var stack : beautifulStacks(true)) {
            i++;
            if (i == 1) {
                if (playerInv.getEmptySlot() == -1) {
                    player.sendMessage(Text.literal("Free up your inventory to withdraw the Nexus!").formatted(Formatting.RED));
                }
            }
            execute(commandSource, new ItemStackArgument(stack.getItem().getRegistryEntry(), new NbtCompound()), playerList, stack.getCount());
        }
    }

    public void dropDroppedSoldiers(World world, BlockPos pos) {
        for (var stack : beautifulStacks(true)) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
            world.spawnEntity(itemEntity);
        }
    }

    private void spawnTextDisplayWithText(BlockPos pos, MinecraftServer server, ServerCommandSource source, float barOffsetY,
                                          float transX, float transY, float transZ,
                                          float scaleX, float scaleY, float scaleZ,
                                          int bckgColor, int health) {

        String command = String.format(
                Locale.ROOT,
                """
                        summon text_display %d %f %d {
                         alignment:"right",
                         billboard:"center",
                         transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[%ff,%ff,%ff],scale:[%ff,%ff,%ff]},
                         background:%d,
                         text:"%d"
                         }""",
                pos.getX(), pos.getY() + barOffsetY, pos.getZ(),
                transX, transY, transZ,
                scaleX, scaleY, scaleZ,
                bckgColor, health

        );
        server.getCommandManager().executeWithPrefix(
                source,
                command
        );
    }

    private static void spawnTextDisplay(BlockPos pos, MinecraftServer server, ServerCommandSource source, float barOffsetY,
                                         float transX, float transY, float transZ,
                                         float scaleX, float scaleY, float scaleZ,
                                         int bckgColor) {

        String command = String.format(
                Locale.ROOT,
                """
                        summon text_display %d %f %d {
                         billboard:"center",
                         transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[%ff,%ff,%ff],scale:[%ff,%ff,%ff]},
                         background:%d
                         }""",
                pos.getX(), pos.getY() + barOffsetY, pos.getZ(),
                transX, transY, transZ, //translation
                scaleX, scaleY, scaleZ, //scale
                bckgColor
        );
        server.getCommandManager().executeWithPrefix(
                source,
                command
        );
    }

    public void createHealthBar(ServerWorld world, BlockPos pos, MinecraftServer server) {
        float barOffsetY = 1.2f;
        ServerCommandSource commandSource = server.getCommandSource()
                .withPosition(Vec3d.ofCenter(pos))
                .withWorld(world)
                .withSilent();
        //text
        spawnTextDisplayWithText(pos, server, commandSource, barOffsetY,
                -0.005f, -0.03f, 0.002f,
                0.55f, 0.55f, 1f,
                backgroundColorText, health
        );
        //made a change in health * 2 -> 40f
        //health
        spawnTextDisplay(pos, server, commandSource, barOffsetY,
                -0.5f, 0f, 0.001f,
                40f, 5f, 1f,
                backgroundColorHealth);
        //background
        spawnTextDisplay(pos, server, commandSource, barOffsetY,
                -0.5f, 0f, 0f,
                40f, 5f, 1f,
                -16777216);
    }

    public void updateHealthBar(World world, BlockPos pos, int backgroundColorHealth, int backgroundColorText) {
        int healthBarBoxSize = 1;
        Vec3i healthBarVec3i = new Vec3i(healthBarBoxSize, healthBarBoxSize, healthBarBoxSize);
        BlockPos barPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        List<DisplayEntity.TextDisplayEntity> healthBars =
                world.getEntitiesByType(TypeFilter.instanceOf(DisplayEntity.TextDisplayEntity.class),
                        new Box(barPos, barPos.add(healthBarVec3i)),
                        bar -> true);
        //If config is changed and the curr. health > MAX_HEALTH -> the X will be set to max (40)
        float healthBarX = health > MAX_HEALTH ? 40f : (float) ((40f / MAX_HEALTH) * health);
        float[] healthBarSize = new float[] {healthBarX, 5f, 1f};

        if (healthBars.isEmpty()) {
            createHealthBar((ServerWorld) world, pos, world.getServer());
        }

        for (var bar : healthBars) {
            NbtCompound nbt1 = new NbtCompound();
            bar.writeNbt(nbt1);
            int background = nbt1.getInt("background");
            if (background == backgroundColorHealth) {
                NbtCompound transformation = ((NbtCompound) nbt1.get("transformation"));
                NbtList newScale = new NbtList();
                newScale.add(NbtFloat.of(healthBarSize[0]));
                newScale.add(NbtFloat.of(healthBarSize[1]));
                newScale.add(NbtFloat.of(healthBarSize[2]));
                if (transformation != null) {
                    transformation.put("scale", newScale);
                }
                bar.readNbt(nbt1);
            } else if (background == backgroundColorText) {
                nbt1.putString("text", String.valueOf(health));
                bar.readNbt(nbt1);
            }
        }
    }

    public void killHealthBar(World world) {
        int healthBarBoxSize = 1;
        Vec3i healthBarVec3i = new Vec3i(healthBarBoxSize, healthBarBoxSize, healthBarBoxSize);
        BlockPos barPos = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
        List<DisplayEntity.TextDisplayEntity> healthBars =
                world.getEntitiesByType(TypeFilter.instanceOf(DisplayEntity.TextDisplayEntity.class),
                        new Box(barPos, barPos.add(healthBarVec3i)),
                        bar -> true);

        for (var bar : healthBars) {
            bar.kill();
        }
    }

    public void setActiveState() {
        nexusActive = !nexusActive;
        playAnim(nexusActive);
        markDirty();
    }

    public void setNexusActive(boolean state) {
        nexusActive = state;
        playAnim(nexusActive);
        markDirty();
    }

    public boolean getNexusActive() {
        return nexusActive;
    }

    public void setRedstoneActive(boolean redstoneActive) {
        this.redstoneActive = redstoneActive;
    }

    public boolean getRedstoneActive() {
        return redstoneActive;
    }

    public int getNexusHealth() {
        return health;
    }

    public void repair() {
        health = MAX_HEALTH;
        markDirty();
    }

    public Item getSoldierItem() {
        return this.soldierItem;
    }

    private void playAnim(boolean nexusActive) {
        if (nexusActive) {
            triggerAnim(controllerName, "open");
        } else {
            triggerAnim(controllerName, "close");
        }
    }

    public void updateDataAndClients() {
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
        }
    }

    //-----------------------------GECKOLIB-----------------------------

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, controllerName, state -> PlayState.STOP)
                .triggerableAnim("open", RawAnimation.begin().then("opening", Animation.LoopType.HOLD_ON_LAST_FRAME))
                .triggerableAnim("close", RawAnimation.begin().then("closing", Animation.LoopType.HOLD_ON_LAST_FRAME)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    //-----------------------------GECKOLIB-----------------------------

    //---------------SEND TEAM COLOR FOR CLAY_NEXUS_RENDER_LAYER---------------
    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
    //---------------SEND TEAM COLOR FOR CLAY_NEXUS_RENDER_LAYER---------------
}
