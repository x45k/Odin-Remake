package me.odinmain.features.impl.dungeon

import me.odinmain.events.impl.RenderEntityModelEvent
import me.odinmain.features.Category
import me.odinmain.features.Module
import me.odinmain.features.settings.impl.BooleanSetting
import me.odinmain.features.settings.impl.NumberSetting
import me.odinmain.utils.addVec
import me.odinmain.utils.render.OutlineUtils
import me.odinmain.utils.render.RenderUtils
import me.odinmain.utils.render.RenderUtils.renderVec
import me.odinmain.utils.skyblock.dungeon.DungeonUtils
import me.odinmain.utils.skyblock.dungeon.DungeonUtils.dungeonTeammatesNoSelf
import net.minecraft.entity.Entity
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object TeammatesHighlight : Module(
    "Teammate Highlight",
    category = Category.DUNGEON,
    description = "Enhances visibility of your dungeon teammates and their name tags."
) {
    private val outline: Boolean by BooleanSetting("Outline", true, description = "Highlights teammates with an outline.")
    private val thickness: Float by NumberSetting("Line Width", 4f, 1.0, 10.0, 0.5)
    private val whenVisible: Boolean by BooleanSetting("When Visible", true, description = "Highlights teammates only when they are visible.")
    private val inBoss: Boolean by BooleanSetting("In Boss", true, description = "Highlights teammates in boss rooms.")
    @SubscribeEvent
    fun onRenderEntityModel(event: RenderEntityModelEvent) {
        if (!DungeonUtils.inDungeons || (!inBoss && DungeonUtils.inBoss) || !outline) return

        val teammate = DungeonUtils.dungeonTeammatesNoSelf.find { it.entity == event.entity } ?: return

        if (!whenVisible && mc.thePlayer.canEntityBeSeen(teammate.entity)) return

        OutlineUtils.outlineEntity(event, thickness, teammate.clazz.color, true)
    }

    @SubscribeEvent
    fun handleNames(event: RenderWorldLastEvent) {
        if (!DungeonUtils.inDungeons) return

        dungeonTeammatesNoSelf.forEach {
            if (it.entity == null || it.name == mc.thePlayer.name) return@forEach
            RenderUtils.drawStringInWorld(
                it.name, it.entity.renderVec.addVec(y = 2.6),
                color = it.clazz.color.rgba,
                depthTest = false, increase = false, renderBlackBox = false,
                scale = 0.05f
            )
        }
    }

    private fun getTeammates(entity: Entity): Int? {
        val teammate = dungeonTeammatesNoSelf.find { it.entity == entity } ?: return null

        return teammate.clazz.color.rgba
    }
}