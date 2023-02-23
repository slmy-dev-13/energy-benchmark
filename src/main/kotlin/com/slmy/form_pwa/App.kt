package com.slmy.form_pwa

import com.slmy.form_pwa.expenses.costsAndSavings
import com.slmy.form_pwa.expenses.ratios
import com.slmy.form_pwa.expenses.solarCostsAndSavings
import com.slmy.form_pwa.expenses.solarRatios
import com.slmy.form_pwa.timeline.timeLine
import io.kvision.*
import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.html.header
import io.kvision.html.image
import io.kvision.html.section
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
            header(className = "navbar bg-dark") {
                section(className = "navbar-section")
                section(className = "navbar-center") {
                    image("images/jea-logo.png")
                }
                section(className = "navbar-section")
            }

            vPanel(spacing = 24, className = "column col-mx-auto col-8 col-sm-12 col-md-10 col-lg-10 col-xl-10 mast my-2") {

            }.bind(pageObservable) { page ->
                when (page) {
                    Page.Choice   -> pageChoice()
                    Page.HeatPump -> {
                        ratios(appController)
                        costsAndSavings(appController)
                        isolation(appController)
                        neededPower(appController)
                        heatPumpCost(appController)
                        costsProjection(appController)
                        timeLine()
                    }
                    Page.Solar    -> {
                        solarRatios()
                        solarCostsAndSavings(solarController)
                    }
                }
            }
        }
    }

    private fun Container.pageChoice() {
        button("Pompe à Chaleur").onClick {
            pageObservable.update { Page.HeatPump }
        }
        button("Panneau Photovoltaïque").onClick {
            pageObservable.update { Page.Solar }
        }
    }
}

fun main() {
    startApplication(::App, module.hot, CoreModule)
}
