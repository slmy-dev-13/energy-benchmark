package com.slmy.form_pwa.ui

import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.html.footer
import io.kvision.html.header

fun Container.card(
    headerContent: (Container.() -> Unit)? = null,
    bodyContent: (Container.() -> Unit)? = null,
    footerContent: (Container.() -> Unit)? = null,
    extraContent: (Container.() -> Unit)? = null,
) {
    div(className = "card") {
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