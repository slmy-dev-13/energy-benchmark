package com.slmy.form_pwa

import com.slmy.form_pwa.data.NeededPowerForm
import com.slmy.form_pwa.data.computeNeededPower
import com.slmy.form_pwa.ui.card
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.br
import io.kvision.html.div
import io.kvision.html.h3
import io.kvision.html.span
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.sub

fun Container.neededPower(appController: AppController) {
    val formObservable = ObservableValue(NeededPowerForm())

    val neededPowerObservable = formObservable.sub {
        val state = appController.stateObservable.value
        it.computeNeededPower(state.isolationIndex)
    }

    neededPowerObservable.subscribe {
        appController.updateNeededPower(it)
    }

    appController.isolationIndexStore.subscribe { formObservable.notify() }

    card(
        headerContent = { h3("Puissance préconisée du matériel") },
        bodyContent = {
            formPanel(className = "columns column") {
                val subscriber: (Number?) -> Unit = {
                    formObservable.value = getData()
                }

                simpleSpinner(null, label = "Surface en m²") {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    bind(NeededPowerForm::surface)
                    subscribe(subscriber)
                }

                simpleSpinner(null, label = "Hauteur sous plafond en m") {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    bind(NeededPowerForm::heightUnderCeiling)
                    subscribe(subscriber)
                }

                br()

                setData(formObservable.value)
            }
        },
        extraContent = {
            div(className = "card-extra text-right mt-2").bind(neededPowerObservable) { neededPower ->
                span("Puissance préconisée")
                br()
                span("$neededPower KWh", className = "h1")
            }
        }
    )
}
