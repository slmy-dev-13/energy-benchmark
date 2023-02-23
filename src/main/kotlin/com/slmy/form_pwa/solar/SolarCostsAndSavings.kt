package com.slmy.form_pwa.solar

import com.slmy.form_pwa.SolarController
import com.slmy.form_pwa.chart.highchartsDiv
import com.slmy.form_pwa.data.SolarConsumptionCostsForm
import com.slmy.form_pwa.data.EnergyFactors
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

private val toggleButton: Container.(Boolean) -> Unit = { enable ->
    if (enable) {
        addCssClass("btn-success")
        removeCssClass("btn-neutral")
    } else {
        removeCssClass("btn-success")
        addCssClass("btn-neutral")
    }
}

private fun defaultChartOptions() = HighchartsOptions(
    chart = ChartOptions("column"),
    title = TitleOptions(text = "", align = "center"),
    xAxis = XAxisOptions(
        categories = listOf("Actuel", "Futur", "Économies"),
        labels = LabelsOptions(
            enabled = true,
            style = TextStyleOptions(fontWeight = "bold", fontSize = "14px")
        )
    ),
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
            style = TextStyleOptions(fontWeight = "bold", fontSize = "14px")
        )
    ),
    plotOptions = PlotOptions(
        column = ColumnPlotOptions(
            stacking = "normal",
            borderRadius = 8,
            dataLabels = DataLabelsOptions(
                enabled = true,
                format = "{point.y:.1f} €",
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

private fun computeSeries(form: SolarConsumptionCostsForm): List<SeriesOptions> {
    val factors = EnergyFactors(0.6, 0.3, 0.1)

    val heatCost = form.electricityCost * factors.heat
    val diverseCost = form.electricityCost * factors.diverse
    val waterCost = form.electricityCost - (heatCost + diverseCost)

    val optimizedHeatCost = if (form.withPanels) heatCost * .3 else heatCost
    val optimizedDiverseCost = if (form.withPanels) diverseCost * .3 else diverseCost
    val optimizedWaterCost = if (form.withBalloonTD) waterCost * .3 else waterCost

    return listOf(
        columnSeries("Économies", listOf(0.0, 0.0, (heatCost + waterCost + diverseCost) - (optimizedHeatCost + optimizedWaterCost + optimizedDiverseCost)), savingsColor),
        columnSeries("Divers", listOf(diverseCost, optimizedDiverseCost, 0.0), diverseColor),
        columnSeries("Eaux chaudes", listOf(waterCost, optimizedWaterCost, 0.0), waterColor),
        columnSeries("Chauffage", listOf(heatCost, optimizedHeatCost, 0.0), heatColor),
    )
}

fun Container.solarCostsAndSavings(controller: SolarController) {
    val formObservable = ObservableValue(SolarConsumptionCostsForm())

    formObservable.subscribe {
        controller.updateEnergyCost(it.electricityCost)
    }

    val chartOptionsStore = formObservable.sub { form ->
        defaultChartOptions().copy(series = computeSeries(form))
    }

    card(
        headerContent = { h3("Coûts et économies réalisables") },
        bodyContent = {
            div(className = "column") {
                val state = controller.stateObservable.value

                simpleSpinner(value = state.energyCost.electricity, label = "Conso annuelle d’ Electricité en €") {
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
                    text = "Panneau Photovoltaïque",
                    style = ButtonStyle.LIGHT,
                    className = "btn-lg"
                ).bind(formObservable) {
                    toggleButton(it.withPanels)
                }.onClick {
                    formObservable.update { it.copy(withPanels = !it.withPanels) }
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
