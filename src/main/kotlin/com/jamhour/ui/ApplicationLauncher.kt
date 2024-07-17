package com.jamhour.ui

import com.jamhour.ui.controllers.HomePageController
import com.jamhour.util.Defaults.DEFAULT_TITLE
import com.jamhour.util.loadStoredParams
import javafx.application.Application
import javafx.application.Application.setUserAgentStylesheet
import javafx.scene.Scene
import javafx.stage.Stage

fun main() = Application.launch(ApplicationLauncher::class.java)

class ApplicationLauncher : Application() {

    override fun start(primaryStage: Stage) {

        val loadView = HomePageController.loadHomePageView()
        primaryStage.apply {
            title = DEFAULT_TITLE
            scene = Scene(loadView.content)
            centerOnScreen()
            show()
        }

        setUserAgentStylesheet(loadStoredParams().settings.theme.theme.userAgentStylesheet)
    }
}