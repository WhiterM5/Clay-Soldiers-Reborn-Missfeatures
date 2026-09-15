package net.whiterm.claysoldiersrebornmissfeatures.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.whiterm.claysoldiersrebornmissfeatures.ClaySoldiersRebornMissfeatures;
import net.whiterm.claysoldiersrebornmissfeatures.block.entity.ClayNexusBlockEntity;
import net.whiterm.claysoldiersrebornmissfeatures.network.ModPackets;

public class ClayNexusScreen extends HandledScreen<ClayNexusScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "textures/gui/clay_nexus_gui.png");
    private int withdrawButtonX = 148;
    private int withdrawButtonY = 45; //if it would be 54 -> ideal height to match the Inventory title

    public ClayNexusScreen(ClayNexusScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        ClayNexusBlockEntity blockEntity = handler.getBlockEntity();
        BlockPos pos = blockEntity.getPos();
        //Update soldiers count display
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        ClientPlayNetworking.send(ModPackets.NEXUS_SEND_WITHDRAW_SOLDIERS_COUNT_ID, buf);
        titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
        titleY = 5;
        //--------Withdraw Button---------
        Text buttonWithdrawText = Text.translatable("gui.claysoldiersmissf.clay_nexus.button.withdraw_dead_bodies");
        int withdrawButtonSize = 18;
        this.addDrawableChild(
                ButtonWidget.builder(Text.of(""), button -> {
                    //Withdraw soldiers
                    PacketByteBuf buffer = PacketByteBufs.create();
                    buffer.writeBlockPos(pos);
                    ClientPlayNetworking.send(ModPackets.NEXUS_WITHDRAW_SOLDIERS_ID, buffer);
                    //Update soldiers count display
                    PacketByteBuf buffer1 = PacketByteBufs.create();
                    buffer1.writeBlockPos(pos);
                    ClientPlayNetworking.send(ModPackets.NEXUS_SEND_WITHDRAW_SOLDIERS_COUNT_ID, buffer1);
                }).dimensions(this.x + withdrawButtonX, this.y + withdrawButtonY, withdrawButtonSize, withdrawButtonSize).tooltip(Tooltip.of(buttonWithdrawText)).build()
        );
        //--------Withdraw Button---------
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    int i = 0;

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        ClayNexusBlockEntity blockEntity = this.getScreenHandler().getBlockEntity();
        //--------Withdraw Button Soldiers Count indicator---------
        Text indicatorText = Text.of(blockEntity.withdrawSoldiersCount() + "/" + blockEntity.withdrawMaxCapacity);
        int indicatorTextWidth = - indicatorText.getString().length() * 5 - indicatorText.getString().length();
        context.drawText(
                this.textRenderer,
                indicatorText,
                withdrawButtonX - 11 + indicatorTextWidth + 30,
                withdrawButtonY + 14 + 5,
                0x404040,
                false
                );
        //--------Withdraw Button Soldiers Count indicator---------
        //--------Withdraw Button---------
        Identifier withdrawButtonImage = new Identifier(ClaySoldiersRebornMissfeatures.MOD_ID, "textures/gui/withdraw_button.png");
        context.drawTexture(withdrawButtonImage, withdrawButtonX + 2, withdrawButtonY + 2, 0, 0, 14, 14, 14, 14);
        //--------Withdraw Button---------
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
