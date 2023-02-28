package com.slmy.form_pwa.solar

import com.slmy.form_pwa.chart.highchartsDiv
import com.slmy.form_pwa.js.*
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.ui.diverseColor
import com.slmy.form_pwa.ui.heatColor
import com.slmy.form_pwa.ui.waterColor
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.h3

private fun defaultPieOptions(name: String, title: String) = HighchartsOptions(
    title = TitleOptions(title, "center"),
    series = listOf(
        SeriesOptions(
            type = "pie",
            name = name,
            colorByPoint = true,
            data = listOf(
                PieDataPoint("Divers", 10.0, diverseColor),
                PieDataPoint("Eaux chaudes sanitaires", 30.0, waterColor),
                PieDataPoint("Chauffage", 60.0, heatColor),
            )
        ),
    ),
    plotOptions = PlotOptions(
        pie = PiePlotOptions(
            allowPointSelect = false,
            cursor = "pointer",
            dataLabels = DataLabelsOptions(
                enabled = true,
                format = "{point.percentage:.0f} %",
                distance = -50,
                filter = Filter("percentage", ">", 0),
                style = TextStyleOptions(
                    fontSize = "16px",
                    textOutline = "none"
                )
            ),
            showInLegend = true
        )
    )
)

private fun solarPieOptions(): HighchartsOptions {
    return defaultPieOptions("Électricité", "Électricité")
}

fun Container.solarRatios() {
    card(
        headerContent = { h3("Proportions des dépenses énergétiques") },
        bodyContent = {

            div(className = "columns col-12 mt-2 py-2") {
                highchartsDiv("solarChart", solarPieOptions(), className = "col-12")
            }
        }
    )
}

