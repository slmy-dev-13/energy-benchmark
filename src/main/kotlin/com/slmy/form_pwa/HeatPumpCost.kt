package com.slmy.form_pwa

import com.slmy.form_pwa.ui.card
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.number.Spinner
import io.kvision.html.br
import io.kvision.html.div
import io.kvision.html.h2
import io.kvision.html.span
import io.kvision.panel.vPanel
import io.kvision.state.ObservableState
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import kotlin.math.roundToInt

fun Container.heatPumpCost(formObservable: ObservableValue<HeatPumpCostForm>, needPowerObservable: ObservableState<Double>) {
    vPanel(spacing = 16) {
        h2("Coût de la pompe à chaleur")

        card(
            bodyContent = {
                formPanel(className = "columns column") {

                    val subscriber: (Number?) -> Unit = {
                        formObservable.value = getData()
                    }

                    add(
                        key = HeatPumpCostForm::heatingDays,
                        control = Spinner(null, label = "Jours de chauffe") {
                            addCssClass("col-6")
                            addCssClass("col-xs-12")

                            subscribe(subscriber)
                        }
                    )
                    add(
                        key = HeatPumpCostForm::compressorHours,
                        control = Spinner(null, label = "Heures de compresseur") {
                            addCssClass("col-6")
                            addCssClass("col-xs-12")

                            subscribe(subscriber)
                        }
                    )

                    add(
                        key = HeatPumpCostForm::heatPumpCOP,
                        control = Spinner(null, label = "COP") {
                            addCssClass("col-6")
                            addCssClass("col-xs-12")

                            subscribe(subscriber)
                        }
                    )
                    setData(formObservable.value)
                }

                br()
            },
            extraContent = {
                div(className = "bg-gray p-2 text-right mt-2").bind(needPowerObservable) { neededPower ->
                    span("Coût calculé")
                    br()
                    span(" ${neededPower.roundToInt()} €", className = "h1")
                }
            }
        )
    }
}
