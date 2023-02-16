package com.slmy.form_pwa.expenses

import com.slmy.form_pwa.AppController
import com.slmy.form_pwa.data.SystemType
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.ui.diverseColor
import com.slmy.form_pwa.ui.heatColor
import com.slmy.form_pwa.ui.waterColor
import com.slmy.form_pwa.update
import io.kvision.chart.*
import io.kvision.core.Container
import io.kvision.html.*
import io.kvision.panel.hPanel
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

fun Container.ratios(controller: AppController) {
    card(
        headerContent = { h3("Proportions des dépenses énergétiques") },
        bodyContent = {
            hPanel(spacing = 16, className = "flex-centered").bind(controller.systemTypeObservable) { currentSystemType ->
                SystemType.values().forEach { systemType ->
                    val style = if (systemType == currentSystemType) {
                        ButtonStyle.SUCCESS
                    } else {
                        ButtonStyle.OUTLINESUCCESS
                    }

                    button(text = systemType.label, style = style, className = "btn-lg").onClick {
                        controller.updateSystemType(systemType)
                    }
                }
            }

            pieCharts(controller.systemTypeObservable)
        }
    )
}

private fun Container.pieCharts(installationType: ObservableValue<SystemType>) {
    val instantChartConfigurationStore = ObservableValue(getBasePieChartConfiguration("Instantané"))
    val electricalChartConfigurationStore = ObservableValue(getBasePieChartConfiguration("Électricité"))

    installationType.subscribe { type ->
        instantChartConfigurationStore.update {
            val dataset = if (type == SystemType.Simple) {
                DataSets(backgroundColor = listOf(heatColor), data = listOf(100))
            } else {
                DataSets(
                    backgroundColor = listOf(heatColor, waterColor),
                    data = listOf(type.heatRatio * 100, type.waterRatio * 100)
                )
            }

            val labels = if (type == SystemType.Simple) {
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
            val dataset = if (type == SystemType.Simple) {
                DataSets(
                    backgroundColor = listOf(diverseColor, waterColor),
                    data = listOf(type.heatRatio * 100, type.waterRatio * 100)
                )
            } else {
                DataSets(
                    backgroundColor = listOf(diverseColor),
                    data = listOf(100)
                )
            }

            val labels = if (type == SystemType.Simple) {
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
