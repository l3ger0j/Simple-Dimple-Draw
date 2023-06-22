package org.l3ger0j.simpledimpledraw.utils

import android.graphics.Path
import java.io.IOException
import java.io.ObjectInputStream
import java.io.Serializable
import java.util.*

class SpecialPath : Path(), Serializable {
    private val actions: MutableList<Action> = LinkedList()

    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(`in`: ObjectInputStream) {
        `in`.defaultReadObject()
        for (action in actions) {
            action.perform(this)
        }
    }

    override fun lineTo(x: Float, y: Float) {
        actions.add(Line(x, y))
        super.lineTo(x, y)
    }

    override fun moveTo(x: Float, y: Float) {
        actions.add(Move(x, y))
        super.moveTo(x, y)
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        actions.add(Quad(x1, y1, x2, y2))
        super.quadTo(x1, y1, x2, y2)
    }

    private interface Action : Serializable {
        fun perform(path: Path)
    }

    private class Move(
            private val x: Float,
            private val y: Float) : Action {

        override fun perform(path: Path) {
            path.moveTo(x, y)
        }
    }

    private class Line(
            private val x: Float,
            private val y: Float) : Action {

        override fun perform(path: Path) {
            path.lineTo(x, y)
        }
    }

    private class Quad(
            private val x1: Float,
            private val y1: Float,
            private val x2: Float,
            private val y2: Float) : Action {

        override fun perform(path: Path) {
            path.quadTo(x1, y1, x2, y2)
        }
    }

    companion object {
        private const val serialVersionUID = -7642039629858339221L
    }
}