package com.slmy.form_pwa.timeline

import com.slmy.form_pwa.data.Month
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.update
import io.kvision.core.Container
import io.kvision.form.select.simpleSelect
import io.kvision.html.*
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.types.LocalDate
import kotlin.js.Date

private enum class Step(
    val title: String,
    val description: String?,
    val monthDelay: Int,
    val isEllipse: Boolean = false
) {
    Bon("Signature du bon de commande", null, 0),
    VisiteTech("Visite Technique", null, 1),
    Pose("Pose du matériel", null, 1),
    DelaiPaiement("Délai de paiement", null, 0, isEllipse = true),
    Paiement("Paiement", null, 7),
}

private data class TimeLineItem(
    val date: String,
    val title: String,
    val icon: String? = null,
    val content: String? = null,
    val isDetached: Boolean = false
)

private data class MonthYear(val month: Month, val year: Int)

private fun buildTimeLineItems(baseMonth: Int, baseYear: Int): List<TimeLineItem> {
    var month = baseMonth
    var year = baseYear

    return Step.values().map { step ->
        month += step.monthDelay

        if (month > 12) {
            year++
            month -= 12
        }

        TimeLineItem(
            date = "${Month.labelOf(month)} $year",
            title = step.title,
            content = step.description,
            isDetached = step.isEllipse
        )
    }
}

private fun currentMonthYear(): MonthYear {
    return Date(LocalDate.now()).let {
        MonthYear(Month.withNumber(it.getMonth() + 1), it.getFullYear())
    }
}

fun Container.timeLine() {
    val observableMonthYear = ObservableValue(currentMonthYear())

    vPanel(spacing = 16) {
        h2("Frise chronologique")

        simpleSelect(
            options = Month.values().map { it.number.toString() to it.label },
            value = observableMonthYear.value.month.number.toString(),
            label = "Mois de signature du bon de commande"
        ).subscribe { stringNumber ->
            stringNumber?.toInt()?.let { number ->
                observableMonthYear.update {
                    it.copy(month = Month.withNumber(number))
                }
            }
        }

        div(className = "timeline") {

        }.bind(observableMonthYear) { monthYear ->

            buildTimeLineItems(monthYear.month.number, monthYear.year).forEach { item ->
                val detachedClass = "detached".takeIf { item.isDetached } ?: ""

                div(className = "timeline-item $detachedClass") {
                    if (!item.isDetached) {
                        div(className = "date", content = item.date)
                    }

                    div(className = "content") {
                        card(
                            bodyContent = {
                                h4(item.title)
                                p(content = "Bonjour tout ça tout ça")
                            }
                        )
                    }
                }
            }
        }
    }
}