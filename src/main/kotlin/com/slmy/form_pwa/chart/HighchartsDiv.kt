package com.slmy.form_pwa.chart

import com.slmy.form_pwa.js.Highcharts
import com.slmy.form_pwa.js.HighchartsOptions
import com.slmy.form_pwa.js.SeriesOptions
import io.kvision.core.Container
import io.kvision.html.Div
import io.kvision.snabbdom.VNode

class HighchartsDiv(
    id: String,
    options: HighchartsOptions,
    className: String? = null,
    init: (HighchartsDiv.() -> Unit)? = null
) : Div(className = className) {

    private var jsHighcharts: dynamic = null

    var options: HighchartsOptions = options
        set(value) {
            field = value
            jsHighcharts?.update(value.toJs(), true, true, true)
        }

    init {
        useSnabbdomDistinctKey()

        this.id = id
        init?.invoke(this)
    }

    override fun afterInsert(node: VNode) {
        jsHighcharts = Highcharts.chart(
            id ?: "",
            options.toJs()
        )
    }

    fun updateSeries(seriesOptionsList: List<SeriesOptions>) {
        jsHighcharts?.update(
            HighchartsOptions(series = seriesOptionsList).toJs(), true, true, true
        )
    }
}

fun Container.highchartsDiv(
    id: String,
    options: HighchartsOptions,
    className: String? = null,
    init: (HighchartsDiv.() -> Unit)? = null
): HighchartsDiv {
    return HighchartsDiv(id, options, className, init).also {
        this.add(it)
    }
}