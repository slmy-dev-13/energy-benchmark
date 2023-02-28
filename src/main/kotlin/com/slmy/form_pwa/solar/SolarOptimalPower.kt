package com.slmy.form_pwa.solar

import com.slmy.form_pwa.SolarController
import com.slmy.form_pwa.data.FranceDepartment
import com.slmy.form_pwa.data.SolarOptimalPowerForm
import com.slmy.form_pwa.formatDecimal
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.update
import io.kvision.core.Container
import io.kvision.form.formPanel
import io.kvision.form.select.simpleSelect
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.br
import io.kvision.html.div
import io.kvision.html.h3
import io.kvision.html.span
import io.kvision.state.ObservableValue
import io.kvision.state.bind
import io.kvision.state.sub

fun Container.solarOptimalPower(controller: SolarController) {

    val departmentOptions = FranceDepartment.values().map {
        it.id to "${it.id} - ${it.fullName}"
    }

    val formObservable = ObservableValue(SolarOptimalPowerForm())

    formObservable.sub {
        controller.updateSunExposition(it.sunHours, it.orientation)
        controller.updateCurrentConsumption(it.currentConsumption)
    }

    card(
        headerContent = { h3("Puissance optimale requise") },
        bodyContent = {
            formPanel(className = "columns column pb-2") {
                div(className = "col-5 col-xs-12") {
                    simpleSpinner(value = formObservable.value.currentConsumption, label = "Consommation actuelle (KWh)") {
                        bind(SolarOptimalPowerForm::currentConsumption)
                        subscribe { newValue ->
                            newValue?.let { consumption ->
                                formObservable.update { it.copy(currentConsumption = consumption.toInt()) }
                            }
                        }
                    }
                }

                div(className = "col-1")

                div(className = "col-6 col-xs-12 column px-2") {
                    simpleSelect(
                        options = departmentOptions,
                        value = departmentOptions.first().first,
                        label = "Ensoleillement par département",
                        rich = true
                    ) {
                        addCssClass("col-12")
                        subscribe { departmentId ->
                            val department = departmentId?.let { FranceDepartment.valueById(it) }

                            if (department != null) {
                                formObservable.update { it.copy(sunHours = department.sunHours) }
                            }
                        }
                    }
                    div(className = "col-12 text-large text-bold text-right").bind(formObservable) {
                        content = "${it.sunHours} heures / an"
                    }
                }
                setData(formObservable.value)
            }
        },
        extraContent = {
            div(className = "card-extra text-right mt-2").bind(controller.stateObservable) { state ->
                span("${state.optimalPanelCount} panneaux", className = "h1")
                br()
                span("${formatDecimal(state.optimalRequiredPower, 3)} KWc", className = "h1")
            }
        }
    )
}
