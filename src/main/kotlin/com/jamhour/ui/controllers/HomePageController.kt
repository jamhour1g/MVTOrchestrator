package com.jamhour.ui.controllers

import com.jamhour.model.Hole
import com.jamhour.model.PCB
import com.jamhour.model.Process
import com.jamhour.process_management.MemoryOrchestrator
import com.jamhour.util.Defaults.CHOOSE_FILE_PATH
import com.jamhour.util.Defaults.SETTINGS_PATH
import com.jamhour.util.Params
import com.jamhour.util.saveParams
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.control.ListView
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import kotlinx.coroutines.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.collections.toList
import kotlin.jvm.javaClass

class HomePageController : CoroutineScope {

    @FXML
    private lateinit var jobQueueListView: ListView<Process>

    @FXML
    private lateinit var readyQueueListView: ListView<PCB>

    @FXML
    private lateinit var availableHolesListView: ListView<Hole>

    private lateinit var memoryOrchestrator: MemoryOrchestrator
    override val coroutineContext = Job() + Dispatchers.Main

    @FXML
    fun initialize() {
        launch {
            draw()
        }
    }

    private fun fillLists() {
        jobQueueListView.items = FXCollections.observableList(memoryOrchestrator.jobQueue.toList())
        readyQueueListView.items = FXCollections.observableList(memoryOrchestrator.readyProcesses.toList())
        availableHolesListView.items =
            FXCollections.observableList(memoryOrchestrator.readyProcesses.availableHoles())
    }

    suspend fun draw(readyQueueFilePath: Path? = null, jobQueueFilePath: Path? = null) = coroutineScope {
        launch {
            memoryOrchestrator = MemoryOrchestrator.Builder()
                .readyFilePath(readyQueueFilePath)
                .jobFilePath(jobQueueFilePath)
                .build()
        }.join()
        fillLists()
    }

    @FXML
    fun onChooseFileClicked() = Dialog<ButtonType>().apply {
        title = "Choose files"
        headerText = "Please choose ready queue and job queue files"

        val chooseFileContent = FXMLLoader().apply {

            location = this@HomePageController.javaClass.getResource(CHOOSE_FILE_PATH)
        }

        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
        dialogPane.content = chooseFileContent.load<GridPane>()

        showAndWait().ifPresent {
            val controller = chooseFileContent.getController<ChooseFilesController>()
            if (it == ButtonType.OK && controller.areContentsValid()) {
                launch {
                    draw(controller.readyFilePath, controller.jobFilePath)
                }
            }
        }
    }


    @FXML
    fun onRemoveClicked() = memoryOrchestrator.apply {
        removeScheduling()
        fillLists()
    }

    @FXML
    fun onSettingsClicked() = Dialog<ButtonType>().apply {
        title = "Settings"
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)

        val loader = FXMLLoader().apply {
            location = this@HomePageController.javaClass.getResource(SETTINGS_PATH)
        }
        dialogPane.content = loader.load<VBox>()

        showAndWait().ifPresent {
            if (it == ButtonType.OK) {
                val settingsController = loader.getController<SettingsController>()
                saveParams(Params(settingsController.saveSettings()))
            }
        }
    }

}