package com.slmy.form_pwa

import com.slmy.form_pwa.data.HeatPumpCostForm
import com.slmy.form_pwa.ui.card
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.spinner.SimpleSpinner
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
        h2("Coût annuel de la pompe à chaleur")

        card(
            bodyContent = {
                formPanel(className = "columns column") {

                    val subscriber: (Number?) -> Unit = {
                        formObservable.value = getData()
                    }

                    add(
                        key = HeatPumpCostForm::heatingDays,
                        control = SimpleSpinner(null, label = "Jours de chauffe") {
                            addCssClass("col-6")
                            addCssClass("col-xs-12")

                            subscribe(subscriber)
                        }
                    )
                    add(
                        key = HeatPumpCostForm::compressorHours,
                        control = SimpleSpinner(null, label = "Heures de marche du groupe extérieur") {
                            addCssClass("col-6")
                            addCssClass("col-xs-12")

                            subscribe(subscriber)
                        }
                    )

                    add(
                        key = HeatPumpCostForm::heatPumpCOP,
                        control = SimpleSpinner(null, label = "Coefficient optimum de performance") {
                            addCssClass("col-6")
                            addCssClass("col-xs-12")

                            subscribe(subscriber)
                        }
                    )

                    add(
                        key = HeatPumpCostForm::powerCost,
                        control = SimpleSpinner(null, label = "Prix du KWh en €") {
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
                div(className = "card-extra bg-gray p-2 text-right mt-2").bind(needPowerObservable) { neededPower ->
                    span("Coût calculé")
                    br()
                    div {
                        span(content = "(Puissance x Jours de chauffe x Heures de marche x Prix du KWh) / C.O.P.  = ", className = "text-small mr-2")
                        span(content ="${neededPower.roundToInt()} €", className = "h1")
                    }
                }
            }
        )
    }
}
