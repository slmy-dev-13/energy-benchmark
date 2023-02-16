package com.slmy.form_pwa

import com.slmy.form_pwa.ui.card
import io.kvision.chart.*
import io.kvision.core.Color
import io.kvision.core.Container
import io.kvision.form.form
import io.kvision.html.*
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

private fun Tr.moneyCell(value: Double, bold: Boolean = false) =
    td("$value €", className = "text-bold".takeIf { bold })

fun Container.costsProjection(appController: AppController) {
    val chartConfigurationStore = ObservableValue(baseBarChartConfiguration)
    val projectionDataStore = ObservableValue(ProjectionData())

    val updateProjection = {
        val state = appController.stateObservable.getState()

        val projectionData = ProjectionData(
            currentCost = state.energyCost.other,
            optimizedCost = state.heatPumpCost
        )

        projectionDataStore.update { projectionData }

        chartConfigurationStore.update {
            baseBarChartConfiguration.copy(dataSets = buildTimeSeriesDataSets(projectionData))
        }
    }

    appController.stateObservable.subscribe { updateProjection() }

    card(
        headerContent = { h3("Graphes des actions sur 10 et 20 ans") },
        bodyContent = {
            form {
                chart(chartConfigurationStore.getState())
                    .bind(chartConfigurationStore) {
                        configuration = it
                        update(UpdateMode.RESIZE)
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
