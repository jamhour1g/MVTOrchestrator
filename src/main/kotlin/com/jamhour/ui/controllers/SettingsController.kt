package com.jamhour.ui.controllers

import atlantafx.base.controls.Tile
import com.jamhour.util.Defaults.SETTINGS_PATH
import com.jamhour.util.Defaults.defaultTheme
import com.jamhour.util.SettingsParam
import com.jamhour.util.Theme
import com.jamhour.util.createView
import com.jamhour.util.loadStoredParams
import javafx.application.Application.setUserAgentStylesheet
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.layout.VBox

class SettingsController {

    @FXML
    private lateinit var themeTile: Tile

    private val themesComboBox = ComboBox<Theme>().apply {
        items += Theme.entries
        selectionModel.select(defaultTheme)
        selectionModel.selectedItemProperty().addListener { _, _, newTheme ->
            newTheme?.let {
                setUserAgentStylesheet(newTheme.theme.userAgentStylesheet)
            }
        }
    }

    @FXML
    fun initialize() {
        themeTile.action = themesComboBox
        loadSettings(loadStoredParams().settings)
    }

    private fun loadSettings(settings: SettingsParam) {
        themesComboBox.selectionModel.select(settings.theme)
    }

    fun saveSettings() = SettingsParam(themesComboBox.value)

    companion object {
        fun loadSettingsView() = createView<VBox, SettingsController>(javaClass.getResource(SETTINGS_PATH)!!)
    }

}