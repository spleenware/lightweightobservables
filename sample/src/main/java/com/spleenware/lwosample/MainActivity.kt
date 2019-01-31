package com.spleenware.lwosample

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.spleenware.lwo.AutoActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AutoActivity() {

    private val quantityBtnIds = intArrayOf(R.id.num_1, R.id.num_2, R.id.num_3, R.id.num_5, R.id.num_10, R.id.num_20)
    private val spendBtnIds = intArrayOf(R.id.spend_1, R.id.spend_2, R.id.spend_3, R.id.spend_4, R.id.spend_5, R.id.spend_10)

    // our model data
    private val bet = BetDetails()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val pq = intArrayOf(1, 2, 3, 5, 10, 20)
        for (i in 0.until(quantityBtnIds.size)) {   // map buttons to preset amounts
            val btn = findViewById<RadioButton>(quantityBtnIds[i])
            if (i < pq.size) {
                val q = pq[i]

                btn.visibility = View.VISIBLE
                btn.text = "$q"
                btn.tag = q
                btn.autoOnClickListener {
                    bet.quantity = q
                }
            } else {
                btn.visibility = View.GONE  // hide unused radio buttons
                btn.tag = 0
            }
        }

        val ps = intArrayOf(1, 2, 3, 4, 5, 10)
        for (i in 0.until(spendBtnIds.size)) {   // map buttons to preset amounts
            val btn = findViewById<RadioButton>(spendBtnIds[i])
            if (i < ps.size) {
                val s = ps[i]

                btn.visibility = View.VISIBLE
                btn.text = "$$s"
                btn.tag = s
                btn.autoOnClickListener {
                    bet.spendPerGame = s
                }
            } else {
                btn.visibility = View.GONE  // hide unused radio buttons
                btn.tag = 0
            }
        }

        spend_other.setOnClickListener { btn ->
            NumberInputDialog(this@MainActivity, 4999).show(getString(R.string.spend_per_draw), "Max $4999") { value ->
                bet.spendPerGame = value
                notifyChanged()
            }
            (btn as RadioButton).isChecked = false
        }
        num_other.setOnClickListener { btn ->
            NumberInputDialog(this@MainActivity, 200).show(getString(R.string.number_of_draws), "Max 200") { value ->
                bet.quantity = value
                notifyChanged()
            }
            (btn as RadioButton).isChecked = false
        }

        bonus_check.autoOnClickListener {
            bet.bonusMultiplier = if (bet.bonusMultiplier > 1) 1 else 2  // toggle from 1 <-> 2
        }

        submit_btn.setOnClickListener {
            Toast.makeText(this@MainActivity, "Click!", Toast.LENGTH_SHORT).show()
        }

        // update the Quantity box when model changes
        onChangeInt { bet.quantity }
                .then { quantity ->
                    var quantityIsOther = true
                    for (id in quantityBtnIds) {
                        val btn = findViewById<RadioButton>(id)
                        btn.isChecked = quantity == (btn.tag as? Int)
                        if (btn.isChecked) quantityIsOther = false
                    }
                    num_other.apply {
                        isChecked = quantityIsOther
                        text = if (quantityIsOther) "${quantity}" else "Other.."
                    }
                }

        // update the Spend box when model changes
        onChangeInt { bet.spendPerGame }
                .then { spend ->
                    var spendIsOther = true
                    for (id in spendBtnIds) {
                        val btn = findViewById<RadioButton>(id)
                        btn.isChecked = spend == (btn.tag as? Int)
                        if (btn.isChecked) spendIsOther = false
                    }
                    spend_other.apply {
                        isChecked = spendIsOther
                        text = if (spendIsOther) "$${spend}" else "Other.."
                    }
                }

        // render our custom check mark on/off
        onChangeInt { bet.bonusMultiplier }
                .then { mult ->
                    bonus_check.setImageResource(
                            if (mult > 1) R.drawable.ic_blue_check_on else R.drawable.ic_blue_check_off
                    )
                }

        // Have our submit button's text change whenever the bet total changes
        onChangeInt { bet.totalCost }
                .then { total -> submit_btn.text = "Play $${total}" }

        // enable/disable our submit button depending on validation rules
        onChangeInt { validate().size }
                .then { errCount -> submit_btn.isEnabled = errCount == 0 }
    }

    private fun validate(): List<String> {
        val errs = ArrayList<String>()
        if (bet.totalCost == 0) {
            errs.add("Must be non zero")
        }
        return errs
    }
}
