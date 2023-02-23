package com.slmy.form_pwa.expenses

import com.slmy.form_pwa.AppController
import com.slmy.form_pwa.chart.highchartsDiv
import com.slmy.form_pwa.data.SystemType
import com.slmy.form_pwa.js.*
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.ui.choiceButton
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h3
import io.kvision.panel.hPanel
import io.kvision.state.ObservableState
import io.kvision.state.bind
import io.kvision.state.sub

private const val waterColor = "#09c"
private const val heatColor = "#C66"
private const val diverseColor = "#ed0"
private const val savingsColor = "#9d5"

val SystemType.icon: String
    get() = when (this) {
        SystemType.Simple -> "icons/simple.png"
        SystemType.Mixed  -> "icons/mixed.png"
    }

fun Container.ratios(controller: AppController) {
    val systemTypeStore = controller.stateObservable.sub { it.systemType }

    card(
        headerContent = { h3("Proportions des dépenses énergétiques") },
        bodyContent = {
            hPanel(spacing = 16, className = "flex-centered").bind(systemTypeStore) { currentSystemType ->
                SystemType.values().forEach { systemType ->

                    choiceButton(
                        label = systemType.label,
                        icon = systemType.icon,
                        isActive = currentSystemType == systemType,
                        extraClasses = "col-6",
                        onClick = { controller.updateSystemType(systemType) }
                    )
                }
            }

            pieCharts(systemTypeStore)
        }
    )
}

private fun defaultPieOptions(name: String, title: String) = HighchartsOptions(
    title = TitleOptions(title, "center"),
    series = listOf(
        SeriesOptions(
            type = "pie",
            name = name,
            colorByPoint = true,
            data = listOf(
                PieDataPoint("Chauffage", 0.0, heatColor),
                PieDataPoint("Eaux chaudes sanitaires", 0.0, waterColor),
            )
        ),
    ),
    plotOptions = PlotOptions(
        pie = PiePlotOptions(
            allowPointSelect = true,
            cursor = "pointer",
            dataLabels = DataLabelsOptions(
                enabled = true,
                format = "<b>{point.name}</b>: {point.percentage:.1f} %",
                distance = -75,
                filter = Filter("percentage", ">", 0)
            )
        )
    )
)

private fun instantPieOptions(type: SystemType): HighchartsOptions {
    val options = defaultPieOptions("Instantané", "Instantané")

    val instantData = if (type == SystemType.Simple) {
        listOf(
            PieDataPoint("Chauffage", 100.0, heatColor),
            PieDataPoint("Eaux chaudes sanitaires", 0.0, waterColor)
        )
    } else {
        listOf(
            PieDataPoint("Chauffage", type.heatRatio * 100, heatColor),
            PieDataPoint("Eaux chaudes sanitaires", type.waterRatio * 100, waterColor)
        )
    }

    return options.copy(
        series = options.series?.first()?.copy(data = instantData)?.let { listOf(it) }
    )
}

private fun electricalPieOptions(type: SystemType): HighchartsOptions {
    val options = defaultPieOptions("Électricité", "Électricité")

    val instantData = if (type == SystemType.Simple) {
        listOf(
            PieDataPoint("Divers", type.diverseRatio * 100, diverseColor),
            PieDataPoint("Eaux chaudes sanitaires", type.waterRatio * 100, waterColor),
        )
    } else {
        listOf(
            PieDataPoint("Divers", 100.0, diverseColor),
            PieDataPoint("Eaux chaudes sanitaires", 0.0, waterColor),
        )
    }

    return options.copy(
        series = options.series?.first()?.copy(data = instantData)?.let { listOf(it) }
    )
}

private fun Container.pieCharts(installationType: ObservableState<SystemType>) {
    val instantChartOptionsStore = installationType.sub { instantPieOptions(it) }
    val electricalChartOptionsStore = installationType.sub { electricalPieOptions(it) }

    div(className = "columns col-12 mt-2 py-2") {
        highchartsDiv("instantChart", HighchartsOptions(), className = "col-6 col-xs-12")
            .bind(instantChartOptionsStore) {
                this.options = it
            }

        highchartsDiv("electricChart", HighchartsOptions(), className = "col-6 col-xs-12")
            .bind(electricalChartOptionsStore) {
                this.options = it
            }
    }
}
