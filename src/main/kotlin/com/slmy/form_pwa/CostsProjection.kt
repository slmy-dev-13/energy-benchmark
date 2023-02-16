package com.slmy.form_pwa

import com.slmy.form_pwa.data.ConsumptionCostsForm
import com.slmy.form_pwa.ui.card
import io.kvision.chart.*
import io.kvision.core.Color
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.html.*
import io.kvision.panel.vPanel
import io.kvision.state.ObservableState
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import kotlin.math.ceil

private val baseBarChartConfiguration = Configuration(
    type = ChartType.LINE,
    dataSets = emptyList(),
    labels = listOf("1 an", "5 ans", "10 ans", "20 ans"),
    options = ChartOptions(
        plugins = PluginsOptions(
            legend = LegendOptions(display = true, position = Position.TOP),
            tooltip = TooltipOptions(
                callbacks = TooltipCallback(label = { "${it.formattedValue} €" })
            )
        ),
        scales = mapOf(
            "y" to ChartScales(
                ticks = TickOptions(callback = { label, _, _ -> "$label €" })
            )
        ),
    )
)

private fun buildTimeSeriesDataSets(projectionData: ProjectionData): List<DataSets> {
    val currentColor = Color("#c66")
    val optimizedColor = Color("#9d5")
    val savingsColor = Color("#99dd5550")

    return listOf(
        DataSets(
            label = "Actuel",
            backgroundColor = listOf(currentColor),
            borderColor = listOf(currentColor),
            data = projectionData.currentOneFiveTenTwenty,
        ),
        DataSets(
            label = "Avec la PAC",
            backgroundColor = listOf(optimizedColor),
            borderColor = listOf(optimizedColor),
            data = projectionData.optimizedOneFiveTenTwenty,
        ),
        DataSets(
            label = "Économies",
            backgroundColor = listOf(savingsColor),
            borderColor = listOf(savingsColor),
            data = projectionData.optimizedOneFiveTenTwenty,
            fill = "-2"
        ),
    )
}

data class ProjectionData(val currentCost: Double, val optimizedCost: Double) {
    private fun getOneFiveTenTwenty(cost: Double): List<Double> =
        listOf(cost, cost * 5.0, cost * 10.0, cost * 20.0).map { ceil(it) }

    private fun getOneTenTwenty(cost: Double): List<Double> =
        listOf(cost, cost * 10.0, cost * 20.0).map { ceil(it) }

    val currentOneTenTwenty: List<Double> = getOneTenTwenty(currentCost)
    val optimizedOneTenTwenty: List<Double> = getOneTenTwenty(optimizedCost)
    val deltaOneTenTwenty: List<Double> = currentOneTenTwenty.zip(optimizedOneTenTwenty).map { it.first - it.second }

    val currentOneFiveTenTwenty: List<Double> = getOneFiveTenTwenty(currentCost)
    val optimizedOneFiveTenTwenty: List<Double> = getOneFiveTenTwenty(optimizedCost)
    val deltaOneFiveTenTwenty: List<Double> = currentOneFiveTenTwenty.zip(optimizedOneTenTwenty).map { it.first - it.second }

}

private fun Tr.moneyCell(value: Double, bold: Boolean = false) =
    if (bold) {
        th("$value €")
    } else {
        td("$value €")
    }

fun Container.costsProjection(formObservable: ObservableValue<ConsumptionCostsForm>, heatPumpCostObservable: ObservableState<Double>) {
    val chartConfigurationStore = ObservableValue(baseBarChartConfiguration)
    val projectionDataStore = ObservableValue(
        ProjectionData(
            currentCost = formObservable.value.fuelCost,
            optimizedCost = heatPumpCostObservable.getState()
        )
    )

    val update = {
        val projectionData = ProjectionData(
            currentCost = formObservable.value.fuelCost,
            optimizedCost = heatPumpCostObservable.getState()
        )

        chartConfigurationStore.update {
            baseBarChartConfiguration.copy(
                dataSets = buildTimeSeriesDataSets(projectionData)
            )
        }

        projectionDataStore.update { projectionData }
    }

    formObservable.subscribe { update() }
    heatPumpCostObservable.subscribe { update() }

    vPanel(spacing = 16) {
        h2("Graphes des actions sur 10 et 20 ans")

        card(
            bodyContent = {
                formPanel {
                    chart(chartConfigurationStore.getState())
                        .bind(chartConfigurationStore) {
                            configuration = it
                            update(UpdateMode.RESIZE)
                        }

                    br()

                    div(className = "divider col-12")

                    br()

                    table(className = "table table-striped table-bordered").bind(projectionDataStore) {
                        tr {
                            th("")
                            th("1 an")
                            th("10 ans")
                            th("20 ans")
                        }
                        tr {
                            td("Actuel")

                            projectionDataStore.value.currentOneTenTwenty.forEach {
                                moneyCell(ceil(it))
                            }
                        }
                        tr {
                            td("Futur")

                            projectionDataStore.value.optimizedOneTenTwenty.forEach {
                                moneyCell(ceil(it))
                            }
                        }
                        tr {
                            td("Gains")

                            projectionDataStore.value.deltaOneTenTwenty.forEach {
                                moneyCell(ceil(it), bold = true)
                            }
                        }
                    }

                    setData(formObservable.value)
                }
            }
        )
    }
}
