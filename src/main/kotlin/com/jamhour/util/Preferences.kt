package com.jamhour.util

import com.jamhour.util.Defaults.DEFAULT_TITLE
import com.jamhour.util.Defaults.defaultTheme
import java.util.prefs.Preferences


fun prefNode(): Preferences = Preferences.userRoot().node(DEFAULT_TITLE)

data class SettingsParam(
    val theme: Theme = defaultTheme,
    val logs: Boolean = false
)

data class Params(
    val settings: SettingsParam
)

fun loadStoredParams() = prefNode().run {
    Params(
        SettingsParam(
            Theme.valueOf(this["theme", defaultTheme.name]),
            this["logs", "false"].toBoolean()
        )
    )
}

fun saveParams(params: Params) = prefNode().run {
    put("theme", params.settings.theme.toString())
    put("logs", params.settings.logs.toString())
    sync()
}
