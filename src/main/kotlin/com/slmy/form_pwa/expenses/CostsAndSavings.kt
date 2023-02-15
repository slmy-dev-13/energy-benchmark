package com.slmy.form_pwa.expenses

import com.slmy.form_pwa.ConsumptionCostsForm
import com.slmy.form_pwa.ui.*
import com.slmy.form_pwa.update
import io.kvision.chart.*
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.select.simpleSelect
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.*
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.sub

enum class Energy(val key: String, val label: String) {
    Gaz("gaz", "Gaz"),
    Fioul("fioul", "Fioul");

    companion object {
        fun fromKey(key: String): Energy =
            values().first { it.key == key }
    }

    fun asOption(): Pair<String, String> = key to label
}

private val baseBarChartConfiguration = Configuration(
    type = ChartType.BAR,
    dataSets = emptyList(),
    labels = listOf("Actuel", "Actuel"),
    options = ChartOptions(
        scales = mapOf(
            "x" to ChartScales(stacked = true),
            "y" to ChartScales(
                stacked = true,
                ticks = TickOptions(callback = { label, _, _ -> " $label €" })
            )
        ),
        plugins = PluginsOptions(
            legend = LegendOptions(display = true, position = Position.TOP),
            tooltip = TooltipOptions(
                callbacks = TooltipCallback(label = { " ${it.formattedValue} €" })
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
    }

    val label = if (optionsLabels.isNotEmpty()) {
        "Futur (${optionsLabels.joinToString(" + ")})"
    } else {
        "Futur"
    }

    return listOf("Actuel", label)
}

private fun buildBarDataSets(form: ConsumptionCostsForm): List<DataSets> {
    val heatFuelCost = form.fuelCost * .7
    val waterFuelCost = form.fuelCost - heatFuelCost

    val optimizedHeatCost = if (form.withHeatPump) heatFuelCost * .3 else heatFuelCost
    val optimizedWaterCost = if (form.withBalloonTD) waterFuelCost * .3 else waterFuelCost

    return listOf(
        DataSets(
            label = "Chauffage",
            backgroundColor = listOf(heatColor),
            data = listOf(heatFuelCost, optimizedHeatCost),
        ),
        DataSets(
            label = "Eaux chaudes",
            backgroundColor = listOf(waterColor),
            data = listOf(waterFuelCost, optimizedWaterCost),
        ),
        DataSets(
            label = "Divers",
            backgroundColor = listOf(diverseColor),
            data = listOf(form.electricityCost, form.electricityCost),
        ),
        DataSets(
            label = "Économies",
            backgroundColor = listOf(savingsColor),
            data = listOf(0, form.fuelCost - (optimizedHeatCost + optimizedWaterCost)),
        ),
    )
}

fun Container.costsAndSavings(formObservable: ObservableValue<ConsumptionCostsForm>) {
    val toggleButton: Container.(Boolean) -> Unit = { enable ->
        if (enable) {
            addCssClass("btn-success")
            removeCssClass("btn-error")
        } else {
            removeCssClass("btn-success")
            addCssClass("btn-error")
        }
    }

    val barChartConfigurationStore = formObservable.sub {
        baseBarChartConfiguration.copy(
            dataSets = buildBarDataSets(it),
            labels = buildLabels(it)
        )
    }

    val energyStore = ObservableValue(Energy.Gaz)

    card(
        headerContent = { h3("Coûts et économies réalisables") },
        bodyContent = {
            formPanel(className = "columns column") {
                simpleSelect(
                    options = listOf(Energy.Gaz.asOption(), Energy.Fioul.asOption()),
                    value = energyStore.value.key
                ) {
                    addCssClass("col-12")
                }.subscribe { newValue ->
                    if (newValue != null) {
                        energyStore.update { Energy.fromKey(newValue) }
                    }
                }

                simpleSpinner(null) {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    bind(ConsumptionCostsForm::fuelCost)
                    subscribe { formObservable.update { getData() } }
                }.bind(energyStore) {
                    label = "${it.label} en €/an"
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
                        text = "Pompe à chaleur (air / eau)",
                        style = ButtonStyle.LIGHT,
                        className = "btn"
                    ).bind(formObservable) {
                        toggleButton(it.withHeatPump)
                    }.onClick {
                        formObservable.update { it.copy(withHeatPump = !it.withHeatPump) }
                    }

                    button(
                        text = "Ballon thermodynamique",
                        style = ButtonStyle.LIGHT,
                        className = "btn"
                    ).bind(formObservable) {
                        toggleButton(it.withBalloonTD)
                    }.onClick {
                        formObservable.update { it.copy(withBalloonTD = !it.withBalloonTD) }
                    }
                }

                setData(formObservable.value)
            }
        },
        footerContent = {
            chart(
                configuration = barChartConfigurationStore.getState(),
            ).bind(barChartConfigurationStore) {
                configuration = it
                update(UpdateMode.RESIZE)
            }
        }
    )
}