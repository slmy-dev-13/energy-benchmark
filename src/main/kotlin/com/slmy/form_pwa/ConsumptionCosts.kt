package com.slmy.form_pwa

import io.kvision.chart.*
import io.kvision.core.Color
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.*
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.sub

private val baseBarChartConfiguration = Configuration(
    type = ChartType.BAR,
    dataSets = emptyList(),
    labels = listOf("Actuel", "Actuel"),
    options = ChartOptions(
        scales = mapOf(
            "x" to ChartScales(stacked = true),
            "y" to ChartScales(
                stacked = true,
                ticks = TickOptions(callback = { label, _, _ -> "$label €" })
            )
        ),
        plugins = PluginsOptions(
            legend = LegendOptions(display = true, position = Position.TOP),
            tooltip = TooltipOptions(
                callbacks = TooltipCallback(label = { "${it.formattedValue} €" })
            )
        )
    )
)

private fun buildLabels(form: ConsumptionCostsForm): List<String> {
    val optionsLabels = buildList {
        if (form.withHeatPump) {
            add("PAC")
        }
        if (form.withBalloonTD) {
            add("BTD")
        }
        if (form.withPVSystem) {
            add("PV")
        }
    }

    val label = if (optionsLabels.isNotEmpty()) {
        optionsLabels.joinToString(" + ")
    } else {
        "Actuel"
    }

    return listOf("Actuel", label)
}

private fun buildBarDataSets(form: ConsumptionCostsForm): List<DataSets> {
    val heatFuelCost = form.fuelCost * .7
    val waterFuelCost = form.fuelCost - heatFuelCost

    val optimizedHeatCost = if (form.withHeatPump) heatFuelCost * .3 else heatFuelCost

    val optimizedWaterCost = if (form.withBalloonTD) waterFuelCost * .3 else waterFuelCost

    val optimizedElectricityCost = if (form.withPVSystem) form.electricityCost * .3 else form.electricityCost

    return listOf(
        DataSets(
            label = "Chauffage",
            backgroundColor = listOf(Color("#ff8a65")),
            data = listOf(heatFuelCost, optimizedHeatCost),
        ),
        DataSets(
            label = "Eaux chaudes",
            backgroundColor = listOf(Color("#4fc3f7")),
            data = listOf(waterFuelCost, optimizedWaterCost),
        ),
        DataSets(
            label = "Divers",
            backgroundColor = listOf(Color("#fff176")),
            data = listOf(form.electricityCost, optimizedElectricityCost),
        )
    )
}

fun Container.consumptionCosts(formObservable: ObservableValue<ConsumptionCostsForm>) {
    val barChartConfigurationStore = formObservable.sub {
        baseBarChartConfiguration.copy(
            dataSets = buildBarDataSets(it),
            labels = buildLabels(it)
        )
    }

    val toggleButton: Container.(Boolean) -> Unit = { enable ->
        if (enable) {
            addCssClass("btn-success")
            removeCssClass("btn-error")
        } else {
            removeCssClass("btn-success")
            addCssClass("btn-error")
        }
    }

    vPanel(spacing = 16) {
        h2("Consommation")

        section(className = "card") {
            header(className = "card-header") {
                h3("Proportions")
            }
            div(className = "card-body columns") {
                chart(
                    Configuration(
                        type = ChartType.DOUGHNUT,
                        dataSets = listOf(
                            DataSets(
                                backgroundColor = listOf(Color("#fff176")),
                                data = listOf(100)
                            ),
                        ),
                        labels = listOf("Divers"),
                        options = ChartOptions(
                            plugins = PluginsOptions(LegendOptions(display = true, position = Position.BOTTOM))
                        )
                    ),
                    className = "col-6 col-xs-12"
                )
                chart(
                    Configuration(
                        type = ChartType.DOUGHNUT,
                        dataSets = listOf(
                            DataSets(
                                backgroundColor = listOf(Color("#ff8a65"), Color("#4fc3f7")),
                                data = listOf(30, 70),
                            ),
                        ),
                        labels = listOf("Eaux chaudes", "Chauffage"),
                        options = ChartOptions(
                            plugins = PluginsOptions(LegendOptions(display = true, position = Position.BOTTOM))
                        )
                    ),
                    className = "col-6 col-xs-12"
                )
            }
        }

        section(className = "card") {
            header(className = "card-header") {
                h3("Coûts et économies possibles")
            }

            div(className = "card-body") {
                formPanel(className = "columns column") {

                    simpleSpinner(null, label = "Fioul en €/an") {
                        addCssClass("col-6")
                        addCssClass("col-xs-12")

                        bind(ConsumptionCostsForm::fuelCost)
                        subscribe { formObservable.update { getData() } }
                    }

                    simpleSpinner(null, label = "Électricité en €/an") {
                        addCssClass("col-6")
                        addCssClass("col-xs-12")

                        bind(ConsumptionCostsForm::electricityCost)
                        subscribe { formObservable.update { getData() } }
                    }

                    div(className = "divider col-12")

                    br()

                    div(className = "btn-group btn-group-block col-12") {
                        button(
                            text = "Pompe à chaleur",
                            style = ButtonStyle.LIGHT,
                            className = "btn"
                        ).bind(formObservable) {
                            toggleButton(it.withHeatPump)
                        }.onClick {
                            formObservable.update { it.copy(withHeatPump = !it.withHeatPump) }
                        }

                        button(
                            text = "Ballon TD",
                            style = ButtonStyle.LIGHT,
                            className = "btn"
                        ).bind(formObservable) {
                            toggleButton(it.withBalloonTD)
                        }.onClick {
                            formObservable.update { it.copy(withBalloonTD = !it.withBalloonTD) }
                        }

                        button(
                            text = "Système PV",
                            style = ButtonStyle.LIGHT,
                            className = "btn"
                        ).bind(formObservable) {
                            toggleButton(it.withPVSystem)
                        }.onClick {
                            formObservable.update { it.copy(withPVSystem = !it.withPVSystem) }
                        }
                    }

                    setData(formObservable.value)
                }
            }

            br()

            chart(barChartConfigurationStore.getState(), className = "card-footer")
                .bind(barChartConfigurationStore) {
                    configuration = it
                    update(UpdateMode.RESIZE)
                }
        }
    }

}
