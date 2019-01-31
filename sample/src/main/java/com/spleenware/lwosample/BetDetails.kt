package com.spleenware.lwosample

/**
 * Created by powellsc on 31/1/19.
 */
class BetDetails {
    var quantity: Int = 0
    var spendPerGame: Int = 0   // DOLLARS
    var bonusMultiplier: Int = 1

    val ticketCost: Int
        get() { return quantity * spendPerGame }

    val totalCost: Int
        get() { return ticketCost * bonusMultiplier }
}