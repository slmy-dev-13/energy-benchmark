package com.slmy.form_pwa.solar

import com.slmy.form_pwa.SolarController
import com.slmy.form_pwa.chart.highchartsDiv
import com.slmy.form_pwa.formatDecimal
import com.slmy.form_pwa.js.*
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.update
import io.kvision.core.Container
import io.kvision.html.*
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import kotlin.math.round

private fun Tr.moneyCell(value: Double, isBold: Boolean = false): Td =
    td(content = "${formatDecimal(value)} €", className = "text-bold".takeIf { isBold })

private const val currentColor = "#c66"
private const val optimizedColor = "#9d5"
private const val savingsColor = "#99dd5550"

private fun series(name: String, color: String, rawData: List<Number>): SeriesOptions {
    return SeriesOptions(
        name = name,
        color = color,
        data = rawData.map { NumberDataPoint(it) }
    )
}

private val emptyData = List(10) { 0.0 }

private val defaultChartOptions: HighchartsOptions = HighchartsOptions(
    chart = ChartOptions(type = "line"),
    title = TitleOptions("", ""),
    xAxis = XAxisOptions(
        categories = List(emptyData.size) { index ->
            val years = index + 1
            if (years == 1) {
                "1 an"
            } else {
                "$years ans"
            }
        },
        labels = LabelsOptions(enabled = true, style = TextStyleOptions(fontWeight = "bold"))
    ),
    yAxis = YAxisOptions(
        min = 0,
        title = TitleOptions("", ""),
        labels = LabelsOptions(enabled = true, format = "{value} €", style = TextStyleOptions(fontWeight = "bold"))
    ),
    series = listOf(
        series(name = "Actuel", color = currentColor, rawData = emptyData),
        series(name = "Avec la PAC", color = optimizedColor, rawData = emptyData)
    )
)

data class ProjectionData(val currentCost: Double = 0.0, val optimizedCost: Double = 0.0) {

    private fun getOverTenYears(cost: Double): List<Double> {
        var updatedCost = cost

        return buildList {
            repeat(10) {
                add(round(updatedCost * 100) / 100.0)
                updatedCost *= 1.06
            }
        }
    }

    val currentOverTenYears: List<Double> = getOverTenYears(currentCost)
    val optimizedOverTenYears: List<Double> = getOverTenYears(optimizedCost)
}

private fun computeSeries(projectionData: ProjectionData): List<SeriesOptions> {
    return listOf(
        series(name = "Actuel", color = currentColor, rawData = projectionData.currentOverTenYears),
        series(name = "Avec Panneaux solaires", color = optimizedColor, rawData = projectionData.optimizedOverTenYears)
    )
}

fun Container.solarOptimalSavings(controller: SolarController) {
    val chartOptionsStore = ObservableValue(defaultChartOptions)
    val projectionDataStore = ObservableValue(ProjectionData())

    val updateProjection = {
        val state = controller.stateObservable.getState()

        val projectionData = ProjectionData(
            currentCost = state.electricityCost,
            optimizedCost = state.electricityCost - state.costSavings
        )

        projectionDataStore.update { projectionData }

        chartOptionsStore.update {
            defaultChartOptions.copy(series = computeSeries(projectionData))
        }
    }

    controller.stateObservable.subscribe { updateProjection() }


    card(
        headerContent = { h3("Amortissement sur 10 ans") },
        bodyContent = {
            highchartsDiv("projectionsChart", options = chartOptionsStore.value) {

            }.bind(chartOptionsStore) {
                options = it
            }

            br()


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
