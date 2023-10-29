package com.pokeskies.skieskits.config.actions.types

import com.pokeskies.skieskits.config.Kit
import com.pokeskies.skieskits.config.actions.Action
import com.pokeskies.skieskits.config.actions.ActionType
import com.pokeskies.skieskits.config.requirements.RequirementOptions
import com.pokeskies.skieskits.data.KitData
import com.pokeskies.skieskits.utils.Utils
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound

class TakeItem(
    type: ActionType = ActionType.GIVE_XP,
    delay: Long = 0,
    chance: Double = 0.0,
    requirements: RequirementOptions? = RequirementOptions(),
    val item: Item = Items.BARRIER,
    val amount: Int = 1,
    val nbt: NbtCompound? = null,
    val strict: Boolean = true
) : Action(type, delay, chance, requirements) {
    override fun executeAction(player: ServerPlayerEntity, kitId: String, kit: Kit, kitData: KitData) {
        Utils.printDebug("Attempting to execute a ${type.identifier} Action: $this")
        var removed = 0
        for ((i, stack) in player.inventory.main.withIndex()) {
            if (!stack.isEmpty) {
                if (isItem(stack)) {
                    val stackSize = stack.count
                    if (removed + stackSize >= amount) {
                        player.inventory.main[i].decrement(amount - removed)
                        break
                    } else {
                        player.inventory.main[i].decrement(stackSize)
                    }
                    removed += stackSize
                }
            }
        }
    }

    private fun isItem(checkItem: ItemStack): Boolean {
        if (!checkItem.item.equals(item)) {
            return false
        }

        if (strict && nbt != null) {
            val checkNBT = checkItem.nbt ?: return false

            if (checkNBT != nbt)
                return false
        }

        return true
    }

    override fun toString(): String {
        return "TakeItem(item=$item, amount=$amount, nbt=$nbt, strict=$strict)"
    }
}