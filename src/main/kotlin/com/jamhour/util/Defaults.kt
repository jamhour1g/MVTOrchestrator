package com.jamhour.util

import java.nio.file.Path

object Defaults {

    val DEFAULT_READY_FILE = System.getenv()["READY_QUEUE_FILE"] ?: "src/test/resources/ready.txt"
    val DEFAULT_JOB_FILE = System.getenv()["JOB_QUEUE_FILE"] ?: "src/test/resources/jobs_correct_format.txt"
    val readyFilePath: Path = Path.of(DEFAULT_READY_FILE)
    val jobFilePath: Path = Path.of(DEFAULT_JOB_FILE)

}
