package com.spleenware.lwo

import android.support.v4.app.Fragment
import android.view.View
import android.widget.EditText

/**
 * Helper class supporting the use of 'light weight' observables.
 * With the use of the various onChange...() functions, client fragments can observe
 * changes in ANY arbitrary expression, and execute UI updates in a .then block.
 *
 * NOTE: all onChange...() listeners are De-registered in Fragment's onDestroyView() lifecycle callback.
 *
 * Created by powellsc on 23/1/19.
 */
abstract class AutoFragment : Fragment() {

    protected val container = AutoContainer()

    /**
     * triggers the calculation of ALL onChange... value blocks, comparing against previous values,
     * executing .then blocks for any detected differences.
     */
    protected fun notifyChanged() {
        container.notifyChanged()
    }

    /**
     * executes all registered bind()ers 'setBlock's, to update all model values from the UI.
     */
    protected fun saveAll() {
        container.saveAll()
    }

    /**
     * registers a two-way binder mechanism, using the getBlock to obtain value to load into given EditText,
     * and the setBlock for getting value out of EditText (via the saveAll() method)
     */
    protected fun bind(edit: EditText, getBlock: () -> String?, setBlock: (newValue: String?) -> Unit) {
        container.bind(edit, getBlock, setBlock)
    }

    /**
     * creates a builder for monitoring changes in an expression that returns an Int.
     */
    protected fun onChangeInt(block: (() -> Int)? = null): AutoContainer.IntChangeBuilder {
        return container.IntChangeBuilder(block)
    }
    /**
     * creates a builder for monitoring changes in an expression that returns an IntArray.
     */
    protected fun onChangeIntArray(block: (() -> IntArray)? = null): AutoContainer.IntArrayChangeBuilder {
        return container.IntArrayChangeBuilder(block)
    }
    /**
     * creates a builder for monitoring changes in an expression that returns an Object reference.
     * Change is true if the Instance pointer changes (not contents of object)
     */
    protected fun onChangeId(block: (() -> Any)? = null): AutoContainer.IdChangeBuilder {
        return container.IdChangeBuilder(block)
    }
    /**
     * creates a builder for monitoring changes in an expression that returns a String.
     */
    protected fun onChangeString(block: (() -> String?)? = null): AutoContainer.StringChangeBuilder {
        return container.StringChangeBuilder(block)
    }

    /**
     * sets an onClickListener on given View, with notifyChanged() method called after 'listener' is executed.
     */
    protected fun View.autoOnClickListener(listener: () -> Unit) {
        this.setOnClickListener { listener(); container.notifyChanged() }
    }

    override fun onDestroyView() {
        container.clear()
        super.onDestroyView()
    }

}