package com.slmy.form_pwa

import com.slmy.form_pwa.data.*
import com.slmy.form_pwa.expenses.energyExpenses
import com.slmy.form_pwa.timeline.timeLine
import io.kvision.*
import io.kvision.html.div
import io.kvision.panel.root
import io.kvision.state.ObservableValue
import io.kvision.state.sub

class App : Application() {
    init {
        require("css/spectre.min.css")
        require("css/kvapp.css")
    }

    private val consumptionCostsFormObservable = ObservableValue(ConsumptionCostsForm())
    private val isolationDataFormObservable = ObservableValue(IsolationDataForm())
    private val neededPowerFormObservable = ObservableValue(NeededPowerForm())
    private val heatPumpCostFormObservable = ObservableValue(HeatPumpCostForm())
    private val systemTypeObservable = ObservableValue(SystemType.Mixed)

    private val isolationIndexStore = isolationDataFormObservable.sub { form ->
        form.computeIsolationIndex()
    }

    private val neededPowerStore = neededPowerFormObservable.sub { form ->
        form.computeNeededPower(isolationIndexStore.getState())
    }

    private val heatPumpCostStore = heatPumpCostFormObservable.sub { form ->
        form.computeCost(neededPowerStore.getState())
    }

    override fun start() {
        isolationIndexStore.subscribe {
            neededPowerFormObservable.update { it }
        }
        neededPowerStore.subscribe {
            heatPumpCostFormObservable.update { it }
        }

        root("kvapp") {
            div(className = "column col-mx-auto col-8 col-sm-12 col-md-10 col-lg-10 col-xl-10 mast my-2") {
                energyExpenses(consumptionCostsFormObservable, systemTypeObservable)

                isolation(isolationDataFormObservable, isolationIndexStore)

                neededPower(neededPowerFormObservable, neededPowerStore)

                heatPumpCost(heatPumpCostFormObservable, heatPumpCostStore)

                costsProjection(consumptionCostsFormObservable, heatPumpCostStore)

                timeLine()
            }
        }
    }
}

fun main() {
    startApplication(::App, module.hot, CoreModule)
}
