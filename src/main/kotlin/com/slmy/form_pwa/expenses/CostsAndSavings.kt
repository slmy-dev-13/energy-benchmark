package com.slmy.form_pwa.expenses

import com.slmy.form_pwa.AppController
import com.slmy.form_pwa.chart.highchartsDiv
import com.slmy.form_pwa.data.ConsumptionCostsForm
import com.slmy.form_pwa.data.SystemType
import com.slmy.form_pwa.js.*
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.update
import io.kvision.core.Container
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.*
import io.kvision.panel.hPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.sub

private const val waterColor = "#09c"
private const val heatColor = "#C66"
private const val diverseColor = "#ed0"
private const val savingsColor = "#9d5"

data class EnergyFactors(val heat: Double, val water: Double, val diverse: Double)

private val toggleButton: Container.(Boolean) -> Unit = { enable ->
    if (enable) {
        addCssClass("btn-success")
        removeCssClass("btn-error")
    } else {
        removeCssClass("btn-success")
        addCssClass("btn-error")
    }
}

private fun defaultChartOptions() = HighchartsOptions(
    chart = ChartOptions("column"),
    title = TitleOptions(text = "", align = "center"),
    xAxis = XAxisOptions(categories = listOf("Actuel", "Futur", "Économies")),
    yAxis = YAxisOptions(
        min = 0,
        title = TitleOptions("", ""),
        stackLabels = LabelsOptions(
            enabled = true,
            format = "{total} €",
            style = TextStyleOptions(
                fontWeight = "bold",
                color = "black",
                textOutline = "none"
            )
        ),
        labels = LabelsOptions(
            enabled = true,
            format = "{value} €",
        )
    ),
    plotOptions = PlotOptions(
        column = ColumnPlotOptions(
            stacking = "normal",
            borderRadius = 8,
            dataLabels = DataLabelsOptions(
                enabled = true,
                format = "{point.y} €",
                filter = Filter("y", ">", 0),
                style = TextStyleOptions(fontSize = "14px", textOutline = "none")
            )
        )
    ),
    legend = LegendOptions(align = "center", verticalAlign = "top"),
    series = listOf(
        columnSeries(name = "Économie", rawData = listOf(0.0, 0.0, 0.0), color = savingsColor),
        columnSeries(name = "Divers", rawData = listOf(0.0, 0.0, 0.0), color = diverseColor),
        columnSeries(name = "Eaux chaudes", rawData = listOf(0.0, 0.0, 0.0), color = waterColor),
        columnSeries(name = "Chauffage", rawData = listOf(0.0, 0.0, 0.0), color = heatColor),
    )
)

private fun columnSeries(name: String, rawData: List<Double>, color: String): SeriesOptions {
    return SeriesOptions(
        name = name,
        color = color,
        data = rawData.map { ColumnDataPoint(name, y = it, color = color) }
    )
}

private fun computeSeries(form: ConsumptionCostsForm, systemType: SystemType): List<SeriesOptions> {
    val factors = if (systemType == SystemType.Simple) {
        EnergyFactors(1.0, 0.4, 0.6)
    } else {
        EnergyFactors(0.7, 0.3, 1.0)
    }

    val heatCost = form.otherCost * factors.heat
    val diverseCost = form.electricityCost * factors.diverse

    val waterCost = if (systemType == SystemType.Simple) {
        form.electricityCost - diverseCost
    } else {
        form.otherCost - heatCost
    }

    val optimizedHeatCost = if (form.withHeatPump) heatCost * .3 else heatCost
    val optimizedWaterCost = if (form.withBalloonTD) waterCost * .3 else waterCost


    return listOf(
        columnSeries("Économies", listOf(0.0, 0.0, (heatCost + waterCost) - (optimizedHeatCost + optimizedWaterCost)), savingsColor),
        columnSeries("Divers", listOf(diverseCost, diverseCost, 0.0), diverseColor),
        columnSeries("Eaux chaudes", listOf(waterCost, optimizedWaterCost, 0.0), waterColor),
        columnSeries("Chauffage", listOf(heatCost, optimizedHeatCost, 0.0), heatColor),
    )
}

fun Container.costsAndSavings(appController: AppController) {
    val formObservable = ObservableValue(ConsumptionCostsForm())

    formObservable.subscribe {
        appController.updateEnergyCost(it.electricityCost, it.otherCost)
    }

    val chartOptionsStore = appController.stateObservable.sub { state ->
        defaultChartOptions().copy(series = computeSeries(formObservable.value, state.systemType))
    }

    card(
        headerContent = { h3("Coûts et économies réalisables") },
        bodyContent = {
            div(className = "columns column") {
                val state = appController.stateObservable.value

                simpleSpinner(value = state.energyCost.other, label = "Conso annuelle de GAZ ou Fioul en €") {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    subscribe { otherCost ->
                        formObservable.update { it.copy(otherCost = otherCost?.toDouble() ?: 0.0) }
                    }
                }

                simpleSpinner(value = state.energyCost.electricity, label = "Conso annuelle d’ Electricité en €") {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    subscribe { electricityCost ->
                        formObservable.update { it.copy(electricityCost = electricityCost?.toDouble() ?: 0.0) }
                    }
                }

                div(className = "divider col-12")

                br()
            }
        },
        footerContent = {

            highchartsDiv("savingsChart", options = HighchartsOptions(), className = "col-12") {

            }.bind(chartOptionsStore) {
                options = it
            }

            br()

            hPanel(spacing = 16, className = "col-12 mt-2 flex-centered") {
                button(
                    text = "Pompe à chaleur (air / eau)",
                    style = ButtonStyle.LIGHT,
                    className = "btn-lg"
                ).bind(formObservable) {
                    toggleButton(it.withHeatPump)
                }.onClick {
                    formObservable.update { it.copy(withHeatPump = !it.withHeatPump) }
                }

                button(
                    text = "Ballon thermodynamique",
                    style = ButtonStyle.LIGHT,
                    className = "btn-lg"
                ).bind(formObservable) {
                    toggleButton(it.withBalloonTD)
                }.onClick {
                    formObservable.update { it.copy(withBalloonTD = !it.withBalloonTD) }
                }
            }

            br()
        }
    )
}
