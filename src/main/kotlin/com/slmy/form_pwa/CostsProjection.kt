package com.slmy.form_pwa

import io.kvision.chart.*
import io.kvision.core.Color
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.html.h2
import io.kvision.panel.vPanel
import io.kvision.state.ObservableState
import io.kvision.state.ObservableValue
import io.kvision.state.bind

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

private fun buildTimeSeriesDataSets(currentCost: Double, optimizedCost: Double): List<DataSets> {
    return listOf(
        DataSets(
            label = "Fioul",
            backgroundColor = listOf(Color("#ff7043")),
            borderColor = listOf(Color("#ff7043")),
            data = listOf(
                currentCost,
                5 * currentCost,
                10 * currentCost,
                20 * currentCost,
            ),
        ),
        DataSets(
            label = "Pompe à chaleur",
            backgroundColor = listOf(Color("#66bb6a")),
            borderColor = listOf(Color("#66bb6a")),
            data = listOf(
                optimizedCost,
                5 * optimizedCost,
                10 * optimizedCost,
                20 * optimizedCost,
            ),
        ),
    )
}

fun Container.costsProjection(formObservable: ObservableValue<ConsumptionCostsForm>, heatPumpCostObservable: ObservableState<Double>) {
    val chartConfigurationStore = ObservableValue(baseBarChartConfiguration)

    val updateChartConfiguration = {
        chartConfigurationStore.update {
            baseBarChartConfiguration.copy(
                dataSets = buildTimeSeriesDataSets(
                    formObservable.value.fuelCost,
                    heatPumpCostObservable.getState()
                )
            )
        }
    }

    formObservable.subscribe { updateChartConfiguration() }
    heatPumpCostObservable.subscribe { updateChartConfiguration() }


    vPanel(spacing = 16) {
        h2("Court, moyen et long terme")

        card(
            bodyContent = {
                formPanel {
                    chart(chartConfigurationStore.getState())
                        .bind(chartConfigurationStore) {
                            configuration = it
                            update(UpdateMode.RESIZE)
                        }

                    setData(formObservable.value)
                }
            }
        )
    }
}
