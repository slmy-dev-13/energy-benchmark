package com.slmy.form_pwa.js

import io.kvision.utils.obj

external class Highcharts {
    companion object {
        fun chart(elementId: String, options: dynamic): dynamic = definedExternally
    }
}

data class TitleOptions(
    var text: String,
    var align: String
) {
    fun toJs(): dynamic {
        return obj {
            this.text = text
            this.align = align
        }
    }
}

data class SeriesOptions(
    var type: String,
    var name: String,
    var colorByPoint: Boolean,
    var data: List<PieDataPoint>
) {
    fun toJs(): dynamic {
        return obj {
            this.type = type
            this.name = name
            this.colorByPoint = colorByPoint
            this.data = data.map { it.toJs() }.toTypedArray()
        }
    }
}

data class PieDataPoint(
    var name: String,
    var y: Double,
    var color: String
) {
    fun toJs(): dynamic {
        return obj {
            this.name = name
            this.y = y
            this.color = color
        }
    }
}

data class PlotOptions(var pie: PiePlotOptions) {
    fun toJs(): dynamic {
        return obj {
            this.pie = pie.toJs()
        }
    }
}

data class PiePlotOptions(
    var allowPointSelect: Boolean,
    var cursor: String,
    var dataLabels: DataLabelsOptions
) {
    fun toJs(): dynamic {
        return obj {
            this.allowPointSelect = allowPointSelect
            this.cursor = cursor
            this.dataLabels = dataLabels.toJs()
        }
    }
}

data class DataLabelsOptions(
    var enabled: Boolean,
    var format: String,
    var distance: Int? = null,
    var filter: Filter? = null,
) {
    fun toJs(): dynamic {
        return obj {
            this.enabled = enabled
            this.format = format
            distance?.let { this.distance = it }
            filter?.let { this.filter = it.toJs() }
        }
    }
}

data class Filter(
    var property: String,
    var operator: String,
    var value: dynamic
) {
    fun toJs(): dynamic {
        return obj {
            this.property = property
            this.operator = operator
            this.value = value
        }
    }
}

data class HighchartsOptions(
    var title: TitleOptions? = null,
    var series: List<SeriesOptions>? = null,
    var plotOptions: PlotOptions? = null
) {
    fun toJs(): dynamic {
        return obj {
            title?.let { this.title = it.toJs() }
            series?.let { this.series = it.map { seriesItem -> seriesItem.toJs() }.toTypedArray() }
            plotOptions?.let { this.plotOptions = it.toJs() }
        }
    }
}



