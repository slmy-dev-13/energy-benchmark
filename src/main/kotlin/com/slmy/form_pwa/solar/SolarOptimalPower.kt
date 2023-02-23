package com.slmy.form_pwa.solar

import com.slmy.form_pwa.SolarController
import com.slmy.form_pwa.data.Orientation
import com.slmy.form_pwa.data.SolarOptimalPowerForm
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.update
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.select.simpleSelect
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.*
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.sub

fun Container.solarOptimalPower(controller: SolarController) {
    val formObservable = ObservableValue(SolarOptimalPowerForm())

    formObservable.sub {
        controller.updateSunExposition(it.sunHours, it.orientation)
        controller.updateCurrentConsumption(it.currentConsumption)
    }

    card(
        headerContent = { h3("Puissance optimale & réalisable") },
        bodyContent = {
            formPanel(className = "columns column pb-2") {
                val subscriber: (Number?) -> Unit = {
                    formObservable.value = getData()
                }

                table(className = "col-12") {
                    tr { th(content = "Consommation", className = "text-left mt-2") }

                    tr {
                        td {
                            simpleSpinner(value = formObservable.value.currentConsumption, label = "Consommation actuelle en KWh") {
                                addCssClass("text-center")
                                bind(SolarOptimalPowerForm::currentConsumption)
                                subscribe(subscriber)
                            }
                        }
                    }

                    tr { th(content = "Exposition au soleil", className = "text-left mt-2") }

                    tr {
                        td {
                            simpleSpinner(value = formObservable.value.sunHours, label = "Heures d'ensoleillement annuelles") {
                                addCssClass("text-center")
                                bind(SolarOptimalPowerForm::sunHours)
                                subscribe(subscriber)
                            }
                        }

                        td {
                            simpleSelect(
                                options = Orientation.values().map { it.name to it.label },
                                value = formObservable.value.orientation.name,
                                label = "Orientation du toit"
                            ) {
                                subscribe { orientationName ->
                                    if (orientationName != null) {
                                        formObservable.update { it.copy(orientation = Orientation.valueOf(orientationName)) }
                                    }
                                }
                            }
                        }

                    }
                }
                setData(formObservable.value)
            }
        },
        extraContent = {
            div(className = "card-extra text-right mt-2").bind(controller.stateObservable) { state ->
                span("Puissance requise optimale")
                br()
                span("${state.optimalRequiredPower} KWh", className = "h1")
                br()
                span("Puissance réalisable")
                br()
                span("${state.maxPossiblePower} KWh", className = "h1")
            }
        }
    )
}
