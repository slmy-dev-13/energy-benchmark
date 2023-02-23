package com.slmy.form_pwa

import com.slmy.form_pwa.chart.highchartsDiv
import com.slmy.form_pwa.js.*
import com.slmy.form_pwa.ui.card
import io.kvision.core.Container
import io.kvision.form.form
import io.kvision.html.*
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import kotlin.math.ceil

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

private val defaultChartOptions: HighchartsOptions = HighchartsOptions(
    chart = ChartOptions(type = "line"),
    title = TitleOptions("", ""),
    xAxis = XAxisOptions(categories = listOf("1 an", "5 ans", "10 ans", "20 ans")),
    yAxis = YAxisOptions(
        min = 0,
        title = TitleOptions("", ""),
        labels = LabelsOptions(enabled = true, format = "{value} €")
    ),
    series = listOf(
        series(name = "Actuel", color = currentColor, rawData = listOf(0, 0, 0, 0)),
        series(name = "Avec la PAC", color = optimizedColor, rawData = listOf(0, 0, 0, 0))
    )
)

data class ProjectionData(val currentCost: Double = 0.0, val optimizedCost: Double = 0.0) {
    private fun getOneFiveTenTwenty(cost: Double): List<Double> =
        listOf(cost, cost * 5.0, cost * 10.0, cost * 20.0).map { ceil(it) }

    private fun getOneTenTwenty(cost: Double): List<Double> =
        listOf(cost, cost * 10.0, cost * 20.0).map { ceil(it) }

    val currentOneTenTwenty: List<Double> = getOneTenTwenty(currentCost)
    val optimizedOneTenTwenty: List<Double> = getOneTenTwenty(optimizedCost)
    val deltaOneTenTwenty: List<Double> = currentOneTenTwenty.zip(optimizedOneTenTwenty).map { it.first - it.second }

    val currentOneFiveTenTwenty: List<Double> = getOneFiveTenTwenty(currentCost)
    val optimizedOneFiveTenTwenty: List<Double> = getOneFiveTenTwenty(optimizedCost)
}

private fun computeSeries(projectionData: ProjectionData): List<SeriesOptions> {
    return listOf(
        series(name = "Actuel", color = currentColor, rawData = projectionData.currentOneFiveTenTwenty),
        series(name = "Avec la PAC", color = optimizedColor, rawData = projectionData.optimizedOneFiveTenTwenty)
    )
}

private fun Tr.moneyCell(value: Double, bold: Boolean = false) =
    td("$value €", className = "text-bold".takeIf { bold })

fun Container.costsProjection(appController: AppController) {
    val chartOptionsStore = ObservableValue(defaultChartOptions)
    val projectionDataStore = ObservableValue(ProjectionData())

    val updateProjection = {
        val state = appController.stateObservable.getState()

        val projectionData = ProjectionData(
            currentCost = state.energyCost.other,
            optimizedCost = state.heatPumpCost
        )

        projectionDataStore.update { projectionData }

        chartOptionsStore.update {
            defaultChartOptions.copy(series = computeSeries(projectionData))
        }
    }

    appController.stateObservable.subscribe { updateProjection() }

    card(
        headerContent = { h3("Graphes des actions sur 10 et 20 ans") },
        bodyContent = {
            form {
                highchartsDiv("projectionsChart", options = chartOptionsStore.value) {

                }.bind(chartOptionsStore) {
                    options = it
                }

                br()
                div(className = "divider col-12")
                br()

                table(className = "table table-striped table-bordered")
                    .bind(projectionDataStore) { projectionData ->
                        tr {
                            th("")
                            th("1 an")
                            th("10 ans")
                            th("20 ans")
                        }
                        tr {
                            td("Actuel")

                            projectionData.currentOneTenTwenty.forEach {
                                moneyCell(ceil(it))
                            }
                        }
                        tr {
                            td("Futur")

                            projectionData.optimizedOneTenTwenty.forEach {
                                moneyCell(ceil(it))
                            }
                        }
                        tr {
                            td("Gains")

                            projectionData.deltaOneTenTwenty.forEach {
                                moneyCell(ceil(it), bold = true)
                            }
                        }
                    }
            }
        }
    )
}
