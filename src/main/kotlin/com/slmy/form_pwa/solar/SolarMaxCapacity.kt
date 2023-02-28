package com.slmy.form_pwa.solar

import com.slmy.form_pwa.SolarController
import com.slmy.form_pwa.data.SolarMaxCapacityForm
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

fun Container.solarMaxCapacity(controller: SolarController) {
    val formObservable = ObservableValue(SolarMaxCapacityForm())

    formObservable.sub {
        val surface = it.length * it.width
        controller.updateSurface(surface)
    }

    card(
        headerContent = { h3("Capacité maximum en panneau") },
        bodyContent = {
            span(content = "Dimensions du toit", className = "column col-12 text-bold text-left mt-2")

            formPanel(className = "cols pb-2") {
                val subscriber: (Number?) -> Unit = {
                    formObservable.value = getData()
                }

                div(className = "col-3")


                div(className = "col-4") {
                    simpleSpinner(null, label = "Longueur en m") {
                        addCssClass("text-center")
                        bind(SolarMaxCapacityForm::length)
                        subscribe(subscriber)
                    }
                }

                span(content = "x", className = "col-1 text-large  flex-centered text-bold")

                div(className = "col-4") {
                    simpleSpinner(null, label = "Largeur en m") {
                        addCssClass("text-center")
                        bind(SolarMaxCapacityForm::width)
                        subscribe(subscriber)
                    }
                }
                setData(formObservable.value)
            }
        },
        extraContent = {
            div(className = "card-extra text-right mt-2").bind(controller.stateObservable) { state ->
                span("Capacité maximum")
                br()
                span("${state.maxPanelCapacity} panneaux", className = "h1")
            }
        }
    )
}
