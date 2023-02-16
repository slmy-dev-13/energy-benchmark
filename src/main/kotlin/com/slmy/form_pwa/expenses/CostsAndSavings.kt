package com.slmy.form_pwa.expenses

import com.slmy.form_pwa.AppController
import com.slmy.form_pwa.data.ConsumptionCostsForm
import com.slmy.form_pwa.data.SystemType
import com.slmy.form_pwa.ui.*
import com.slmy.form_pwa.update
import io.kvision.chart.*
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.*
import io.kvision.panel.hPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.sub

enum class Energy(val label: String) {
    Gaz("Gaz"),
    Fioul("Fioul");
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

    return listOf("Actuel", label, "Économies")
}

data class EnergyFactors(val heat: Double, val water: Double, val diverse: Double)

private fun buildBarDataSets(form: ConsumptionCostsForm, systemType: SystemType): List<DataSets> {
    val factors = if (systemType == SystemType.Simple) {
        EnergyFactors(1.0, 0.4, 0.6)
    } else {
        EnergyFactors(0.7, 0.3, 1.0)
    }

    val heatCost = form.otherCost * factors.heat
    val diverseCost = form.electricityCost * factors.diverse

    val waterCost = if (systemType == SystemType.Simple) {
        form.electricityCost - diverseCost
    } else {
        form.otherCost - heatCost
    }

    val optimizedHeatCost = if (form.withHeatPump) heatCost * .3 else heatCost
    val optimizedWaterCost = if (form.withBalloonTD) waterCost * .3 else waterCost

    return listOf(
        DataSets(
            label = "Chauffage",
            backgroundColor = listOf(heatColor),
            data = listOf(heatCost, optimizedHeatCost),
        ),
        DataSets(
            label = "Eaux chaudes",
            backgroundColor = listOf(waterColor),
            data = listOf(waterCost, optimizedWaterCost),
        ),
        DataSets(
            label = "Divers",
            backgroundColor = listOf(diverseColor),
            data = listOf(diverseCost, diverseCost),
        ),
        DataSets(
            label = "Économies",
            backgroundColor = listOf(savingsColor),
            data = listOf(0, 0, (heatCost + waterCost) - (optimizedHeatCost + optimizedWaterCost)),
        ),
    )
}

fun Container.costsAndSavings(appController: AppController) {
    val toggleButton: Container.(Boolean) -> Unit = { enable ->
        if (enable) {
            addCssClass("btn-success")
            removeCssClass("btn-error")
        } else {
            removeCssClass("btn-success")
            addCssClass("btn-error")
        }
    }

    val energyStore = ObservableValue(Energy.Gaz)
    val formObservable = ObservableValue(ConsumptionCostsForm())

    val barChartConfigurationStore = formObservable.sub {

        appController.updateEnergyCost(it.electricityCost, it.otherCost)

        baseBarChartConfiguration.copy(
            dataSets = buildBarDataSets(it, appController.systemTypeObservable.value),
            labels = buildLabels(it)
        )
    }

    card(
        headerContent = { h3("Coûts et économies réalisables") },
        bodyContent = {
            formPanel(className = "columns column") {
                appController.systemTypeObservable.subscribe {
                    formObservable.update { getData() }
                }

                hPanel(spacing = 16, className = "col-12 mb-2").bind(energyStore) { currentEnergy ->
                    Energy.values().forEach { energy ->
                        val style = if (energy == currentEnergy) ButtonStyle.SUCCESS else ButtonStyle.OUTLINESUCCESS

                        button(text = energy.label, style = style, className = "btn-lg").onClick {
                            energyStore.update { energy }
                        }
                    }
                }

                simpleSpinner(null) {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    bind(ConsumptionCostsForm::otherCost)
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

            br()

            hPanel(spacing = 16, className = "col-12 mt-2 flex-centered") {
                button(
                    text = "Pompe à chaleur (air / eau)",
                    style = ButtonStyle.LIGHT,
                    className = "btn-lg"
                ).bind(formObservable) {
                    toggleButton(it.withHeatPump)
                }.onClick {
                    formObservable.update { it.copy(withHeatPump = !it.withHeatPump) }
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