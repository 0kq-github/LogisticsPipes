/*
 * Copyright (c) 2020  RS485
 *
 * "LogisticsPipes" is distributed under the terms of the Minecraft Mod Public
 * License 1.0.1, or MMPL. Please check the contents of the license located in
 * https://github.com/RS485/LogisticsPipes/blob/dev/LICENSE.md
 *
 * This file can instead be distributed under the license terms of the
 * MIT license:
 *
 * Copyright (c) 2020  RS485
 *
 * This MIT license was reworded to only match this file. If you use the regular
 * MIT license in your project, replace this copyright notice (this line and any
 * lines below and NOT the copyright line above) with the lines from the original
 * MIT license located here: http://opensource.org/licenses/MIT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this file and associated documentation files (the "Source Code"), to deal in
 * the Source Code without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Source Code, and to permit persons to whom the Source Code is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Source Code, which also can be
 * distributed under the MIT.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package network.rs485.logisticspipes.guidebook

import logisticspipes.LPConstants
import logisticspipes.utils.MinecraftColor
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import network.rs485.logisticspipes.gui.guidebook.IDrawable
import network.rs485.logisticspipes.util.math.Rectangle
import network.rs485.markdown.*
import java.util.*

const val DEBUG_AREAS = false

internal val DEFAULT_DRAWABLE_STATE = InlineDrawableState(EnumSet.noneOf(TextFormat::class.java), MinecraftColor.WHITE.colorCode)
internal val HEADER_LEVELS = listOf(2.0, 1.80, 1.60, 1.40, 1.20)

/**
 * Image token, stores a token list in case the image is not correctly loaded as well as the image's path
 * @param textTokens this is the alt text, only used in case the image provided via the URL fails to load.
 *
 */
data class DrawableImageParagraph(val textTokens: List<DrawableWord>, val imageParameters: String) : IDrawable {
    // TODO
    private val image: ResourceLocation
    private var imageAvailable: Boolean

    override val area = Rectangle(0, 0)
    override var isHovered = false

    init {
        val parameters = imageParameters.split(" ")
        image = ResourceLocation(LPConstants.LP_MOD_ID, parameters.first())
        imageAvailable = true
    }

    override fun setPos(x: Int, y: Int, maxWidth: Int): Int {
        area.setPos(x, y)
        return area.height
    }
}

/**
 * Menu token, stores the key and the type of menu in a page.
 */
data class DrawableMenuParagraph(val menuId: String, val options: String) : IDrawable {
    // TODO how to get the actual menu Map in here?
    override val area: Rectangle = Rectangle(0, 0)
    override var isHovered = false

    override fun draw(mouseX: Int, mouseY: Int, delta: Float, yOffset: Int, visibleArea: Rectangle) {
        super.draw(mouseX, mouseY, delta, yOffset, visibleArea)
    }

    private val menu = mutableMapOf<String, List<DrawableMenuParagraphTile>>()

    fun setContent(map: Map<String, List<String>>): DrawableMenuParagraph {
        menu.clear()
        menu.putAll(map.asSequence().associate { div -> div.key to div.value.map { page -> DrawableMenuParagraphTile(BookContents.get(page).metadata) } })
        return this
    }

    override fun setPos(x: Int, y: Int, maxWidth: Int): Int {
        area.setPos(x, y)
        var currentX = 0
        var currentY = 0
        for (division in menu) {
            currentY += Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT
            for (tile in division.value) {
                // Checks if the tile fits in the current row, if not skips to next row
                if (currentX + tile.tileSize + tile.tileSpacing > maxWidth) {
                    currentX = 0
                    currentY += tile.tileSize + tile.tileSpacing
                }
                // Sets the position of the tile
                tile.setPos(x + currentX, y + currentY, maxWidth)
                // Checks if the tile is the last on in the current list, if so add the height and spacing for the next division to be correctly drawn
                if (tile == division.value.last()) {
                    currentY += tile.tileSize + tile.tileSpacing
                }
            }
        }
        area.setSize(maxWidth, currentY)
        TODO("Not yet implemented")
    }

    override fun hovering(mouseX: Int, mouseY: Int, yOffset: Int) {
        super.hovering(mouseX, mouseY, yOffset)
    }

    // Make a custom inner class for the title of a division?

    private class DrawableMenuParagraphTile(metadata: YamlPageMetadata) : IDrawable {
        val tileSize = 40
        val tileSpacing = 5

        // Maybe there needs to be a constant Int defining the size of all the tiles
        override val area = Rectangle(tileSize, tileSize)
        override var isHovered = false

        override fun draw(mouseX: Int, mouseY: Int, delta: Float, yOffset: Int, visibleArea: Rectangle) {
            super.draw(mouseX, mouseY, delta, yOffset, visibleArea)
            // Draw tile bg
            // Draw icon
            // Draw tooltip
        }

        override fun setPos(x: Int, y: Int, maxWidth: Int): Int {
            area.setPos(x, y)
            return area.height
        }
    }
}

/**
 * List token, has several items that are shown in a list.
 */
data class DrawableListParagraph(val entries: List<List<DrawableWord>>) : IDrawable {
    override val area: Rectangle
        get() = TODO("Not yet implemented")
    override var isHovered: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun setPos(x: Int, y: Int, maxWidth: Int): Int {
        TODO("Not yet implemented")
    }
}

private fun toDrawables(elements: List<InlineElement>, scale: Double) = DEFAULT_DRAWABLE_STATE.let { state ->
    elements.mapNotNull { element ->
        element.changeDrawableState(state)
        when (element) {
            is Word -> DrawableWord(element.str, scale, state)
            is Space -> DrawableSpace(scale, state)
            Break -> DrawableBreak
            else -> null
        }
    }
}

private fun toDrawable(paragraph: Paragraph): IDrawable = when (paragraph) {
    is RegularParagraph -> DrawableRegularParagraph(toDrawables(paragraph.elements, 1.0))
    is HeaderParagraph -> DrawableHeaderParagraph(toDrawables(paragraph.elements, HEADER_LEVELS[paragraph.headerLevel - 1]), paragraph.headerLevel)
    is HorizontalLineParagraph -> TODO()
    else -> TODO() // MenuParagraph
}

fun asDrawables(paragraphs: List<Paragraph>) = paragraphs.map(::toDrawable)