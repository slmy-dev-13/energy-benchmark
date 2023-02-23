package com.slmy.form_pwa

import io.kvision.state.ObservableValue

inline fun <T> ObservableValue<T>.update(block: (T) -> T) {
    value = block(value)
}

fun <T> ObservableValue<T>.notify() {
    value = value
}