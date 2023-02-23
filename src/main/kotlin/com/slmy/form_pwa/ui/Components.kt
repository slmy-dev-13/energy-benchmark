package com.slmy.form_pwa.ui

import io.kvision.core.Container
import io.kvision.core.onClick
import io.kvision.html.*

fun Container.card(
    headerContent: (Container.() -> Unit)? = null,
    bodyContent: (Container.() -> Unit)? = null,
    footerContent: (Container.() -> Unit)? = null,
    extraContent: (Container.() -> Unit)? = null,
): Div {
    return div(className = "card") {
        headerContent?.let {
            header(className = "card-header") { it.invoke(this) }
        }

        bodyContent?.let {
            div(className = "card-body") { it.invoke(this) }
        }

        footerContent?.let {
            footer(className = "card-footer") { it.invoke(this) }
        }

        extraContent?.invoke(this)
    }
}

fun Container.choiceButton(
    label: String,
    icon: String,
    isActive: Boolean,
    extraClasses: String = "",
    onClick: () -> Unit
) {
    val activeClass = if (isActive) "active" else ""

    div(className = "btn-choice flex-centered $activeClass $extraClasses") {
        image(icon, className = "icon")
        label(label)
        image("icons/ic_check_circle.svg", className = "check-icon")
    }.onClick {
        onClick()
    }
}