package com.slmy.form_pwa.heat_pump

import com.slmy.form_pwa.AppController
import com.slmy.form_pwa.data.HeatPumpCostForm
import com.slmy.form_pwa.data.computeCost
import com.slmy.form_pwa.notify
import com.slmy.form_pwa.ui.card
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.spinner.SimpleSpinner
import io.kvision.html.br
import io.kvision.html.div
import io.kvision.html.h3
import io.kvision.html.span
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.sub
import kotlin.math.ceil
import kotlin.math.roundToInt

fun Container.heatPumpCost(appController: AppController) {
    val formObservable = ObservableValue(HeatPumpCostForm())

    val heatPumpCostObservable = formObservable.sub {
        val state = appController.stateObservable.value
        ceil(it.computeCost(state.neededPower))
    }

    heatPumpCostObservable.subscribe { appController.updateHeatPumpCost(it) }

    appController.neededPowerStore.subscribe { formObservable.notify() }

    card(
        headerContent = { h3("Coût annuel de la pompe à chaleur") },
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
            div(className = "card-extra text-right mt-2").bind(heatPumpCostObservable) { heatPumpCost ->
                span("Coût calculé")
                br()
                div {
                    span(content = "(Puissance x Jours de chauffe x Heures de marche x Prix du KWh) / C.O.P.  = ", className = "text-small mr-2")
                    span(content = "${heatPumpCost.roundToInt()} €", className = "h1")
                }
            }
        }
    )
}
