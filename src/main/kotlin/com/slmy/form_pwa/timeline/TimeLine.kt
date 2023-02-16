package com.slmy.form_pwa.timeline

import com.slmy.form_pwa.data.Month
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.update
import io.kvision.core.Container
import io.kvision.form.form
import io.kvision.form.select.simpleSelect
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.*
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.types.LocalDate
import kotlin.js.Date
import kotlin.math.roundToInt

private const val bonDescription = """
    <ul>
        <li>Bon de Commande</li>
        <li>Accords Techniques</li>
        <li>Accords Financiers</li>
        <li>Accords Primes</li>
        <li>Accords ADV</li>
    </ul>
"""

private const val visiteTechDescription = """
    <ul>
        <li>Expertise sur l'installation actuelle et sur la faisabilité du projet</li>
        <li>Prise de mesures et derniers éléments de vérification</li>
    </ul>
"""

private const val poseDescription = """
    <ul>
        <li>Installation du matériel</li>
        <li>Mise en service du matériel</li>
        <li>Récupération de l'ancienne chaudière</li>
    </ul>
"""

private enum class Step(
    val title: String,
    val description: String?,
    val monthDelay: Int,
    val isEllipse: Boolean = false,
    val isFree: Boolean = false
) {
    Bon("Montage du dossier", bonDescription, 0, isFree = true),
    VisiteTech("Visite Technique", visiteTechDescription, 1, isFree = true),
    Pose("Pose du matériel", poseDescription, 1, isFree = true),
    Report("Report du paiement", "Période de 6 mois durant laquelle vous percevrez l'ensemble de vos aides.", 0, isEllipse = true),
    Paiement("Première mensualité", null, 7),
}

private data class TimeLineItem(
    val date: String,
    val title: String,
    val icon: String? = null,
    val content: String? = null,
    val isDetached: Boolean = false,
    val showFreeChip: Boolean = false,
    val isPaiement: Boolean = false
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
            isDetached = step.isEllipse,
            showFreeChip = step.isFree,
            isPaiement = step == Step.Paiement
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
    val finalCostObservable = ObservableValue(Pair(0.0, 0.0))

    val paiementForm: Container.() -> Unit = {
        div(className = "bg-gray p-2 text-right mt-2").bind(finalCostObservable) { pair ->

            val total = pair.first - pair.second

            span("Coût final")
            br()
            div {
                span(content ="${total.roundToInt()} €", className = "h1")
            }
        }
    }

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

        div(className = "timeline").bind(observableMonthYear) { monthYear ->
            buildTimeLineItems(monthYear.month.number, monthYear.year).forEach { item ->
                val detachedClass = "detached".takeIf { item.isDetached } ?: ""

                div(className = "timeline-item $detachedClass") {
                    if (!item.isDetached) {
                        div(className = "date", content = item.date)
                    }

                    div(className = "content") {
                        card(
                            bodyContent = {
                                h4(content = item.title, className = "d-inline-block")

                                if (item.showFreeChip) {
                                    span("Gratuit", className = "bg-success ml-2 text-small text-uppercase p-1 va-tb")
                                }

                                p(content = item.content ?: "", rich = true)

                                if (item.isPaiement) {
                                    form {
                                        simpleSpinner(value = finalCostObservable.value.first, label = "Sortie en €").subscribe {
                                            it?.let { nnNumber ->
                                                finalCostObservable.update { pair ->
                                                    pair.copy(first = nnNumber.toDouble())
                                                }
                                            }
                                        }

                                        simpleSpinner(value = finalCostObservable.value.second, label = "Entrée en €").subscribe {
                                            it?.let { nnNumber ->
                                                finalCostObservable.update { pair ->
                                                    pair.copy(second = nnNumber.toDouble())
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            extraContent = paiementForm.takeIf { item.isPaiement }
                        )
                    }
                }
            }
        }
    }
}