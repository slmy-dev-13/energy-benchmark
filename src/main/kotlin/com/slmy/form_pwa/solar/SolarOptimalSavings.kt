package com.slmy.form_pwa.solar

import com.slmy.form_pwa.SolarController
import com.slmy.form_pwa.formatDecimal
import com.slmy.form_pwa.ui.card
import io.kvision.core.Container
import io.kvision.html.*
import io.kvision.state.bind

private fun Tr.moneyCell(value: Double, isBold: Boolean = false): Td =
    td(content = "${formatDecimal(value)} €", className = "text-bold".takeIf { isBold })

fun Container.solarOptimalSavings(controller: SolarController) {
    card(
        headerContent = { h3("Amortissement sur 10 ans") },
        bodyContent = {
            table(className = "table table-scroll text-center").bind(controller.stateObservable) {
                tr {
                    td()

                    repeat(10) {
                        val years = it + 1
                        th(content = "$years ${if (years > 1) "ans" else "an"}")
                    }

                    th("Total")
                }
                tr(className = "current-line") {
                    th(content = "Coût actuel", className = "text-left")

                    var currentCost = it.electricityCost
                    var currentTotal = 0.0

                    repeat(10) {
                        currentTotal += currentCost

                        moneyCell(currentCost)
                        currentCost *= 1.06
                    }

                    moneyCell(currentTotal, isBold = true)
                }
                tr(className = "future-line") {
                    th(content = "Coût optimisé", className = "text-left")

                    var optimizedCost = it.electricityCost - it.costSavings
                    var optimizedTotal = 0.0

                    repeat(10) {
                        optimizedTotal += optimizedCost

                        moneyCell(optimizedCost)
                        optimizedCost *= 1.06
                    }

                    moneyCell(optimizedTotal, isBold = true)
                }

                tr(className = "gain-line") {
                    th(content = "Gains", className = "text-left")

                    var currentCost = it.electricityCost
                    var optimizedCost = it.electricityCost - it.costSavings
                    var totalSavings = 0.0

                    repeat(10) {
                        val savings = currentCost - optimizedCost
                        totalSavings += savings

                        moneyCell(savings)

                        currentCost *= 1.06
                        optimizedCost *= 1.06
                    }

                    moneyCell(totalSavings, isBold = true)
                }
            }
        },
    )
}
