package com.slmy.form_pwa.solar

import com.slmy.form_pwa.SolarController
import com.slmy.form_pwa.data.SolarNeedForm
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
import kotlin.math.floor

enum class Orientation {
    N, NE, E, SE, S, SW, W, NW
}

fun Container.solarNeed(controller: SolarController) {
    val formObservable = ObservableValue(SolarNeedForm())

    formObservable.subscribe {
        val surface = it.length * it.width
        controller.updateSurfaceAndOrientation(surface, it.orientation)
    }

    card(
        headerContent = { h3("Capacité maximum en panneau") },
        bodyContent = {
            formPanel(className = "columns column") {
                val subscriber: (Number?) -> Unit = {
                    formObservable.value = getData()
                }

                simpleSpinner(null, label = "Longueur en m") {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    bind(SolarNeedForm::length)
                    subscribe(subscriber)
                }

                simpleSpinner(null, label = "Largeur en m") {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")

                    bind(SolarNeedForm::width)
                    subscribe(subscriber)
                }

                br()

                setData(formObservable.value)
            }
        },
        extraContent = {
            div(className = "card-extra text-right mt-2").bind(formObservable) { neededPower ->
                val surface = (neededPower.length * neededPower.width)
                val maxCapacity = floor(surface / (1.16 * 1.69))
                span("Capacité maximum")
                br()
                span("$maxCapacity panneaux", className = "h1")
            }
        }
    )
}
