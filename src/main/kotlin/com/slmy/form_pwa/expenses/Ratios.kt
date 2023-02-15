package com.slmy.form_pwa.expenses

import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.ui.diverseColor
import com.slmy.form_pwa.ui.heatColor
import com.slmy.form_pwa.ui.waterColor
import com.slmy.form_pwa.update
import io.kvision.chart.*
import io.kvision.core.Container
import io.kvision.form.select.simpleSelect
import io.kvision.html.div
import io.kvision.html.h3
import io.kvision.state.ObservableValue
import io.kvision.state.bind

private fun getBasePieChartConfiguration(
    title: String,
    dataSets: DataSets = DataSets(),
    labels: List<String> = listOf(),
) = Configuration(
    type = ChartType.PIE,
    dataSets = listOf(dataSets),
    labels = labels,
    options = ChartOptions(
        plugins = PluginsOptions(
            legend = LegendOptions(display = true, position = Position.BOTTOM),
            title = TitleOptions(text = listOf(title), display = true),
            tooltip = TooltipOptions(
                enabled = true,
                callbacks = TooltipCallback(label = { " ${it.formattedValue} %" })
            )
        )
    )
)

private const val DEFAULT_TYPE = "combine"

fun Container.ratioCard() {
    val installationType = ObservableValue(DEFAULT_TYPE)

    card(
        headerContent = { h3("Proportions") },
        bodyContent = {
            div(className = "col-12") {
                simpleSelect(
                    options = listOf("simple" to "Simple", "combine" to "Combiné"),
                    value = installationType.value
                ).subscribe { newValue ->
                    installationType.update { newValue ?: DEFAULT_TYPE }
                }
            }

            pieCharts(installationType)
        }
    )
}

private fun Container.pieCharts(installationType: ObservableValue<String>) {
    val instantChartConfigurationStore = ObservableValue(getBasePieChartConfiguration("Instantané"))
    val electricalChartConfigurationStore = ObservableValue(getBasePieChartConfiguration("Électricité"))

    installationType.subscribe { type ->
        instantChartConfigurationStore.update {
            val dataset = if (type == "simple") {
                DataSets(backgroundColor = listOf(heatColor), data = listOf(100))
            } else {
                DataSets(backgroundColor = listOf(heatColor, waterColor), data = listOf(70, 30))
            }

            val labels = if (type == "simple") {
                listOf("Chauffage")
            } else {
                listOf("Chauffage", "Eaux chaudes sanitaires")
            }

            it.copy(
                dataSets = listOf(dataset),
                labels = labels
            )
        }

        electricalChartConfigurationStore.update {
            val dataset = if (type == "simple") {
                DataSets(
                    backgroundColor = listOf(diverseColor, waterColor),
                    data = listOf(60, 40)
                )
            } else {
                DataSets(
                    backgroundColor = listOf(diverseColor),
                    data = listOf(100)
                )
            }

            val labels = if (type == "simple") {
                listOf("Divers", "Eaux chaudes sanitaires")
            } else {
                listOf("Divers")
            }

            it.copy(dataSets = listOf(dataset), labels = labels)
        }
    }

    div(className = "columns col-12") {
        chart(
            configuration = instantChartConfigurationStore.value,
            className = "col-6 col-xs-12"
        ).bind(instantChartConfigurationStore) {
            configuration = it
            update(UpdateMode.RESIZE)
        }

        chart(
            configuration = electricalChartConfigurationStore.value,
            className = "col-6 col-xs-12"
        ).bind(electricalChartConfigurationStore) {
            configuration = it
            update(UpdateMode.RESIZE)
        }
    }
}
