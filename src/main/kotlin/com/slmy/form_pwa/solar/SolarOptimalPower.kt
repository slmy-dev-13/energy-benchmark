package com.slmy.form_pwa.solar

import com.slmy.form_pwa.SolarController
import com.slmy.form_pwa.data.FranceDepartment
import com.slmy.form_pwa.data.SolarOptimalPowerForm
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.update
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.JustifyContent
import io.kvision.form.formPanel
import io.kvision.form.select.simpleSelect
import io.kvision.form.spinner.simpleSpinner
import io.kvision.html.*
import io.kvision.panel.hPanel
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
        headerContent = { h3("Puissance optimale & réalisable") },
        bodyContent = {
            formPanel(className = "columns column pb-2") {
                val subscriber: (Number?) -> Unit = {
                    formObservable.value = getData()
                }

                div(className = "col-6 col-xs-12")

                simpleSpinner(value = formObservable.value.currentConsumption, label = "Consommation actuelle en KWh") {
                    addCssClass("col-6")
                    addCssClass("col-xs-12")
                    bind(SolarOptimalPowerForm::currentConsumption)
                    subscribe(subscriber)
                }

                div(className = "col-12 divider")

                hPanel(justify = JustifyContent.SPACEBETWEEN, alignItems = AlignItems.CENTER, spacing = 8, className = "col-12") {
                    div {
                        label(content = "Ensoleillement:")
                        br()

                        span(className = "text-large text-bold").bind(formObservable) {
                            content = it.sunHours.toString()
                        }
                        span(content = "heures / an", className = "ml-2")
                    }

                    simpleSelect(
                        options = departmentOptions,
                        value = departmentOptions.first().first,
                        label = "Département"
                    ) {
                        addCssClass("col-6")
                        addCssClass("col-xs-12")

                        subscribe { departmentId ->
                            val department = departmentId?.let { FranceDepartment.valueById(it) }

                            console.log(department)

                            if (department != null) {
                                formObservable.update { it.copy(sunHours = department.sunHours) }
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
