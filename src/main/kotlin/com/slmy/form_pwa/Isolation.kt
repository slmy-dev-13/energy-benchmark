package com.slmy.form_pwa

import com.slmy.form_pwa.data.IsolationDataForm
import com.slmy.form_pwa.ui.card
import com.slmy.form_pwa.ui.choiceButton
import io.kvision.core.Container
import io.kvision.core.FlexWrap
import io.kvision.form.check.checkBox
import io.kvision.form.formPanel
import io.kvision.html.br
import io.kvision.html.div
import io.kvision.html.h3
import io.kvision.html.span
import io.kvision.panel.flexPanel
import io.kvision.state.ObservableState
import io.kvision.state.ObservableValue
import io.kvision.state.bind

private fun wrapForLabel(labelContent: String): String {
    return "<span class='ml-2'>$labelContent</span>"
}

fun Container.isolation(formObservable: ObservableValue<IsolationDataForm>, isolationIndexObservable: ObservableState<Int>) {
    card(
        headerContent = { h3("Indice d'isolation") },
        bodyContent = {
            formPanel {
                val subscriber: (Boolean) -> Unit = {
                    formObservable.value = getData()
                }

                div(className = "columns").bind(formObservable) {
                    choiceButton(
                        "Maison construite après 2012",
                        "icons/2012.png",
                        isActive = formObservable.value.houseBuiltAfter2012,
                        extraClasses = "column"
                    ) {
                        formObservable.update {
                            it.copy(houseBuiltAfter2012 = !it.houseBuiltAfter2012)
                        }
                    }


                    div(className = "divider-vert hide-xs")

                    flexPanel(className = "column", spacing = 16, wrap = FlexWrap.WRAP) {
                        choiceButton(
                            "Double vitrage",
                            "icons/double_vitrage.png",
                            isActive = formObservable.value.doubleWindow,
                            extraClasses = "col-12 col-xs-12"
                        ) {
                            formObservable.update {
                                it.copy(doubleWindow = !it.doubleWindow)
                            }
                        }

                        choiceButton(
                            "Isolation des combles",
                            "icons/iso_comble.png",
                            isActive = formObservable.value.fillsIsolation,
                            extraClasses = "col-12 col-xs-12"
                        ) {
                            formObservable.update {
                                it.copy(fillsIsolation = !it.fillsIsolation)
                            }
                        }

                        choiceButton(
                            "Vide sanitaire",
                            "icons/vide_sanitaire.png",
                            isActive = formObservable.value.sanitaryVoid,
                            extraClasses = "col-12 col-xs-12"
                        ) {
                            formObservable.update {
                                it.copy(sanitaryVoid = !it.sanitaryVoid)
                            }
                        }
//                        checkBox(value = false, rich = true, label = wrapForLabel("Vide sanitaire")) {
//                            bind(IsolationDataForm::sanitaryVoid)
//                            subscribe(subscriber)
//                        }
                    }
                }

                br()

                setData(formObservable.value)
            }
        },
        extraContent = {
            div(className = "card-extra bg-gray p-2 text-right mt-2").bind(isolationIndexObservable) { isolationIndex ->
                span("Indice d'isolation")
                br()
                span("$isolationIndex", className = "h1")
            }
        }
    )
}
