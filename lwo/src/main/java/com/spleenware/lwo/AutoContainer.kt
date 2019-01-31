package com.spleenware.lwo

import android.widget.EditText
import java.util.*

/**
 * Created by powellsc on 25/1/19.
 */
class AutoContainer {

    private val trackers = LinkedList<Tracker>()
    private val binders = LinkedList<Binder>()

    fun notifyChanged() {
        for (t in trackers) {
            t.check()
        }
    }

    fun bind(edit: EditText, getBlock: () -> String?, setBlock: (newValue: String?) -> Unit) {
        val t = Tracker.StringTracker(
                getBlock(),   // get initial state
                getBlock,
                { newValue -> edit.setText(newValue) }
        )
        edit.setText(t.oldValue)   // run Then block for initial state

        trackers.add(t)
        binders.add(Binder.EditTextBinder(edit, setBlock))
    }

    fun saveAll() {
        for (b in binders) {
            b.save()
        }
    }

    fun clear() {
        trackers.clear()
        binders.clear()
    }

    private sealed class Binder {
        abstract fun save()

        class EditTextBinder(val edit: EditText, val setBlock: (newValue: String?) -> Unit): Binder() {
            override fun save() {
                setBlock(edit.getText().toString())
            }
        }
    }

    private sealed class Tracker {
        abstract fun check()

        class IntTracker(var oldValue: Int, val valueBlock: () -> Int, val thenBlock: (newValue: Int) -> Unit): Tracker() {
            override fun check() {
                val newValue = valueBlock()
                if (newValue != oldValue) {
                    oldValue = newValue
                    thenBlock(newValue)
                }
            }
        }
        class IntArrayTracker(var oldValue: IntArray, val valueBlock: () -> IntArray, val thenBlock: (newValue: IntArray) -> Unit): Tracker() {
            override fun check() {
                val newValue = valueBlock()
                if (!Arrays.equals(newValue, oldValue)) {
                    oldValue = newValue
                    thenBlock(newValue)
                }
            }
        }
        class IdTracker(var oldValue: Any, val valueBlock: () -> Any, val thenBlock: (newValue: Any) -> Unit): Tracker() {
            override fun check() {
                val newValue = valueBlock()
                if (newValue !== oldValue) {
                    oldValue = newValue
                    thenBlock(newValue)
                }
            }
        }
        class StringTracker(var oldValue: String?, val valueBlock: () -> String?, val thenBlock: (newValue: String?) -> Unit): Tracker() {
            override fun check() {
                val newValue = valueBlock()
                if (newValue != oldValue) {
                    oldValue = newValue
                    thenBlock(newValue)
                }
            }
        }
    }

    inner class IntChangeBuilder(block: (() -> Int)? = null) {
        private var valueBlock: (() -> Int)? = block

        fun value(block: () -> Int): IntChangeBuilder {
            valueBlock = block
            return this
        }
        /**
         * registers this onChange... observer, with the given code block to be executed
         * every time the value expression changes.
         */
        fun then(block: (newValue: Int) -> Unit) {
            val vb = valueBlock ?: throw IllegalStateException()
            val t = Tracker.IntTracker(
                    vb(),   // get initial state
                    vb,
                    block
            )
            block(t.oldValue)   // run Then block for initial state

            trackers.add(t)
        }
    }

    inner class IntArrayChangeBuilder(block: (() -> IntArray)? = null) {
        private var valueBlock: (() -> IntArray)? = block

        fun value(block: () -> IntArray): IntArrayChangeBuilder {
            valueBlock = block
            return this
        }
        /**
         * registers this onChange... observer, with the given code block to be executed
         * every time the value expression changes.
         */
        fun then(block: (newValue: IntArray) -> Unit) {
            val vb = valueBlock ?: throw IllegalStateException()
            val t = Tracker.IntArrayTracker(
                    vb(),   // get initial state
                    vb,
                    block
            )
            block(t.oldValue)   // run Then block for initial state

            trackers.add(t)
        }
    }

    inner class IdChangeBuilder(block: (() -> Any)? = null) {
        private var valueBlock: (() -> Any)? = block

        fun value(block: () -> Any): IdChangeBuilder {
            valueBlock = block
            return this
        }
        /**
         * registers this onChange... observer, with the given code block to be executed
         * every time the value expression changes.
         */
        fun then(block: (newValue: Any) -> Unit) {
            val vb = valueBlock ?: throw IllegalStateException()
            val t = Tracker.IdTracker(
                    vb(),   // get initial state
                    vb,
                    block
            )
            block(t.oldValue)   // run Then block for initial state

            trackers.add(t)
        }
    }

    inner class StringChangeBuilder(block: (() -> String?)? = null) {
        private var valueBlock: (() -> String?)? = block

        fun value(block: () -> String?): StringChangeBuilder {
            valueBlock = block
            return this
        }

        /**
         * registers this onChange... observer, with the given code block to be executed
         * every time the value expression changes.
         */
        fun then(block: (newValue: String?) -> Unit) {
            val vb = valueBlock ?: throw IllegalStateException()
            val t = Tracker.StringTracker(
                    vb(),   // get initial state
                    vb,
                    block
            )
            block(t.oldValue)   // run Then block for initial state

            trackers.add(t)
        }
    }

}