package com.slmy.form_pwa.js

import io.kvision.utils.obj

external class Highcharts {
    companion object {
        fun chart(elementId: String, options: dynamic): dynamic = definedExternally
    }
}

data class ChartOptions(
    val type: String
) {
    fun toJs(): dynamic = obj {
        this.type = type
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
    var type: String? = null,
    var name: String,
    var color: String? = null,
    var colorByPoint: Boolean = false,
    var data: List<DataPoint>
) {
    fun toJs(): dynamic {
        return obj {
            type?.let { this.type = it }
            this.name = name
            color?.let { this.color = it }
            this.colorByPoint = colorByPoint
            this.data = data.map { it.toJs() }.toTypedArray()
        }
    }
}

interface DataPoint {
    fun toJs(): dynamic
}

data class PieDataPoint(
    var name: String,
    var y: Double,
    var color: String
) : DataPoint {

    override fun toJs(): dynamic {
        return obj {
            this.name = name
            this.y = y
            this.color = color
        }
    }
}

data class ColumnDataPoint(
    var name: String,
    var y: Double,
    var color: String
) : DataPoint {
    override fun toJs(): dynamic {
        return obj {
            this.name = name
            this.y = y
            this.color = color
        }
    }
}

data class NumberDataPoint(var value: Number) : DataPoint {
    override fun toJs(): dynamic = value
}

data class PlotOptions(
    var pie: PiePlotOptions? = null,
    var column: ColumnPlotOptions? = null,
) {
    fun toJs(): dynamic {
        return obj {
            pie?.let { this.pie = it.toJs() }
            column?.let { this.column = it.toJs() }
        }
    }
}

data class PiePlotOptions(
    var allowPointSelect: Boolean,
    var cursor: String,
    var dataLabels: DataLabelsOptions,
    var showInLegend: Boolean? = null
) {
    fun toJs(): dynamic {
        return obj {
            this.allowPointSelect = allowPointSelect
            this.cursor = cursor
            this.dataLabels = dataLabels.toJs()
            showInLegend?.let { this.showInLegend = it }
        }
    }
}

data class ColumnPlotOptions(
    var stacking: String = "normal",
    var borderRadius: Int? = null,
    var dataLabels: DataLabelsOptions
) {
    fun toJs(): dynamic {
        return obj {
            this.stacking = stacking
            borderRadius?.let { this.borderRadius = it }
            this.dataLabels = dataLabels.toJs()
        }
    }
}

data class DataLabelsOptions(
    var enabled: Boolean = false,
    var format: String? = null,
    var distance: Int? = null,
    var filter: Filter? = null,
    var style: TextStyleOptions? = null,
) {
    fun toJs(): dynamic {
        return obj {
            this.enabled = enabled
            format?.let { this.format = it }
            distance?.let { this.distance = it }
            filter?.let { this.filter = it.toJs() }
            style?.let { this.style = it.toJs() }
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

data class XAxisOptions(
    var categories: List<String>,
    var labels: LabelsOptions? = null
) {
    fun toJs(): dynamic {
        return obj {
            this.categories = categories.toTypedArray()
            labels?.let { this.labels = it.toJs() }
        }
    }
}

data class TextStyleOptions(
    var color: String? = null,
    var fontSize: String? = null,
    var fontWeight: String? = null,
    var textOutline: String? = null
) {
    fun toJs(): dynamic {
        return obj {
            color?.let { this.color = it }
            fontSize?.let { this.fontSize = it }
            fontWeight?.let { this.fontWeight = it }
            textOutline?.let { this.textOutline = it }
        }
    }
}

data class LabelsOptions(
    var enabled: Boolean,
    var format: String? = null,
    var style: TextStyleOptions? = null
) {
    fun toJs(): dynamic {
        return obj {
            this.enabled = enabled
            format?.let { this.format = it }
            style?.let { this.style = it.toJs() }
        }
    }
}

data class YAxisOptions(
    var min: dynamic,
    var title: TitleOptions? = null,
    var stackLabels: LabelsOptions? = null,
    var labels: LabelsOptions? = null
) {
    fun toJs(): dynamic {
        return obj {
            this.min = min
            title?.let { this.title = it.toJs() }
            labels?.let { this.labels = it.toJs() }
            stackLabels?.let { this.stackLabels = it.toJs() }
        }
    }
}

data class LegendOptions(
    var align: String? = null,
    var verticalAlign: String? = null,
    var x: Int? = null,
    var y: Int? = null,
    var floating: Boolean? = null,
    var shadow: Boolean? = null,
) {
    fun toJs(): dynamic {
        return obj {
            align?.let { this.align = it }
            verticalAlign?.let { this.verticalAlign = it }
            x?.let { this.x = it }
            y?.let { this.y = it }
            floating?.let { this.floating = it }
            shadow?.let { this.shadow = it }
        }
    }
}

data class HighchartsOptions(
    var chart: ChartOptions? = null,
    var title: TitleOptions? = null,
    var xAxis: XAxisOptions? = null,
    var yAxis: YAxisOptions? = null,
    var legend: LegendOptions? = null,
    var series: List<SeriesOptions>? = null,
    var plotOptions: PlotOptions? = null
) {
    fun toJs(): dynamic {
        return obj {
            chart?.let { this.chart = it.toJs() }
            title?.let { this.title = it.toJs() }
            xAxis?.let { this.xAxis = it.toJs() }
            yAxis?.let { this.yAxis = it.toJs() }
            legend?.let { this.legend = it.toJs() }
            series?.let { this.series = it.map { seriesItem -> seriesItem.toJs() }.toTypedArray() }
            plotOptions?.let { this.plotOptions = it.toJs() }
        }
    }
}



