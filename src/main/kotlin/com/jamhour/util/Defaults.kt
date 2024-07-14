package com.jamhour.util

import java.nio.file.Path

object Defaults {
    const val DEFAULT_TITLE = "MVT Orchestrator"
    
    val DEFAULT_READY_FILE = System.getenv()["READY_QUEUE_FILE"] ?: "src/test/resources/ready.txt"
    val DEFAULT_JOB_FILE = System.getenv()["JOB_QUEUE_FILE"] ?: "src/test/resources/jobs_correct_format.txt"
    val readyFilePath: Path = Path.of(DEFAULT_READY_FILE)
    val jobFilePath: Path = Path.of(DEFAULT_JOB_FILE)

    val defaultTheme = Theme.PRIMER_LIGHT

    const val HOME_PAGE_PATH = "/com/jamhour/ui/home_page.fxml"
    const val CHOOSE_FILE_PATH = "/com/jamhour/ui/choose_files.fxml"
    const val SETTINGS_PATH = "/com/jamhour/ui/settings.fxml"
}
