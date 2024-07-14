package com.jamhour.util

import com.jamhour.util.Defaults.DEFAULT_JOB_FILE
import com.jamhour.util.Defaults.DEFAULT_READY_FILE
import com.jamhour.util.Defaults.DEFAULT_TITLE
import com.jamhour.util.Defaults.defaultTheme
import com.jamhour.util.Defaults.jobFilePath
import com.jamhour.util.Defaults.readyFilePath
import java.nio.file.Path
import java.util.prefs.Preferences


fun prefNode(): Preferences = Preferences.userRoot().node(DEFAULT_TITLE)

data class FilesParam(
    val jobQueueFilePath: Path = readyFilePath,
    val readyQueueFilePath: Path = jobFilePath
)

data class SettingsParam(
    val theme: Theme = defaultTheme,
    val logs: Boolean = false
)

data class Params(
    val settings: SettingsParam,
    val files: FilesParam
)

fun loadStoredParams() = prefNode().run {
    Params(
        SettingsParam(
            Theme.valueOf(this["theme", defaultTheme.name]),
            this["logs", "false"].toBoolean()
        ),
        FilesParam(
            Path.of(this["jobQueueFilePath", DEFAULT_READY_FILE]),
            Path.of(this["readyQueueFilePath", DEFAULT_JOB_FILE])
        )
    )
}

fun saveParams(params: Params) = prefNode().run {
    put("theme", params.settings.theme.toString())
    put("logs", params.settings.logs.toString())
    put("jobQueueFilePath", params.files.jobQueueFilePath.toString())
    put("readyQueueFilePath", params.files.readyQueueFilePath.toString())
    sync()
}
