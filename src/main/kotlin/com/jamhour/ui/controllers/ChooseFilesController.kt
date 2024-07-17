package com.jamhour.ui.controllers

import com.jamhour.util.Defaults.CHOOSE_FILE_PATH
import com.jamhour.util.createView
import javafx.fxml.FXML
import javafx.scene.control.TextArea
import javafx.scene.layout.GridPane
import javafx.stage.FileChooser
import java.nio.file.Path

class ChooseFilesController {

    @FXML
    private lateinit var readyFilePathTextArea: TextArea

    @FXML
    private lateinit var jobFilePathTextArea: TextArea

    private val chooser = FileChooser()

    var readyFilePath: Path? = null; private set
    var jobFilePath: Path? = null; private set


    @FXML
    fun onChooseReadyfileClicked() = chooser.showOpenDialog(readyFilePathTextArea.scene.window)?.let {
        readyFilePathTextArea.text = it.absolutePath
        readyFilePath = it.toPath()
    }

    @FXML
    fun onChooseJobFileClicked() = chooser.showOpenDialog(jobFilePathTextArea.scene.window)?.let {
        jobFilePathTextArea.text = it.absolutePath
        jobFilePath = it.toPath()
    }

    fun areContentsValid() = readyFilePath != null && jobFilePath != null

    companion object {
        fun loadChooseFilesView() =
            createView<GridPane, ChooseFilesController>(javaClass.getResource(CHOOSE_FILE_PATH)!!)
    }

}