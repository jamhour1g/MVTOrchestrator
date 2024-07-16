package com.jamhour.ui

import com.jamhour.util.Defaults.DEFAULT_TITLE
import com.jamhour.util.Defaults.HOME_PAGE_PATH
import com.jamhour.util.loadStoredParams
import javafx.application.Application
import javafx.application.Application.setUserAgentStylesheet
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

fun main() = Application.launch(ApplicationLauncher::class.java)

class ApplicationLauncher : Application() {

    override fun start(primaryStage: Stage) {

        val homePage = FXMLLoader.load<BorderPane>(javaClass.getResource(HOME_PAGE_PATH))
        primaryStage.apply {
            title = DEFAULT_TITLE
            scene = Scene(homePage)
            centerOnScreen()
            show()
        }

        setUserAgentStylesheet(loadStoredParams().settings.theme.theme.userAgentStylesheet)
    }
}