package com.slmy.form_pwa

import com.slmy.form_pwa.heat_pump.*
import com.slmy.form_pwa.solar.*
import com.slmy.form_pwa.ui.card
import io.kvision.*
import io.kvision.core.Container
import io.kvision.core.Cursor
import io.kvision.core.onClick
import io.kvision.html.*
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.state.ObservableValue
import io.kvision.state.bind

private enum class Page {
    Choice, HeatPump, Solar
}

class App : Application() {
    init {
        require("css/spectre.min.css")
        require("css/kvapp.css")
    }

    private val appController = AppController()
    private val solarController = SolarController()

    private val pageObservable = ObservableValue(Page.Choice)

    override fun start() {
        root("kvapp") {
            header(className = "navbar") {
                section(className = "navbar-section") {
                    image("images/jea-logo-black.png").onClick {
                        appController.reset()
                        solarController.reset()
                        pageObservable.update { Page.Choice }
                    }
                }
                section(className = "navbar-center")
                section(className = "navbar-section").bind(pageObservable) {
                    navPageButton("Pompe à Chaleur", isSelected = it == Page.HeatPump) {
                        pageObservable.update { Page.HeatPump }
                    }
                    navPageButton("Panneau Photovoltaïque", isSelected = it == Page.Solar) {
                        pageObservable.update { Page.Solar }
                    }
                }
            }

            vPanel(spacing = 24, className = "column col-mx-auto col-8 col-sm-12 col-md-10 col-lg-10 col-xl-10 mast my-2")
                .bind(pageObservable) { page ->
                    when (page) {
                        Page.Choice -> pageChoice()
                        Page.HeatPump -> {
                            ratios(appController)
                            costsAndSavings(appController)
                            isolation(appController)
                            neededPower(appController)
                            heatPumpCost(appController)
                            costsProjection(appController)
                            timeLine()
                        }
                        Page.Solar -> {
                            solarRatios()
                            solarCostsAndSavings(solarController)
                            solarMaxCapacity(solarController)
                            solarOptimalPower(solarController)
                            solarOptimalSavings(solarController)
                        }
                    }
                }
        }
    }

    private fun Container.pageButton(title: String, onClick: () -> Unit) {
        card(
            bodyContent = {
                div(className = "text-center p-2") {
                    cursor = Cursor.POINTER
                    span(content = title, className = "text-large text-bold p-2")
                }
            }
        ).onClick { onClick() }
    }

    private fun Container.pageChoice() {
        pageButton("Pompe à Chaleur") {
            pageObservable.update { Page.HeatPump }
        }
        pageButton("Panneau Photovoltaïque") {
            pageObservable.update { Page.Solar }
        }
    }

    private fun Container.navPageButton(text: String, isSelected: Boolean, onClick: () -> Unit) {

        val extraClasses = if (isSelected) {
            "text-underline text-bold"
        } else {
            ""
        }

        span(content = text, className = "p-2 mr-2 $extraClasses") {
            cursor = Cursor.POINTER
        }.onClick { onClick() }
    }
}

fun main() {
    startApplication(::App, module.hot, CoreModule)
}
