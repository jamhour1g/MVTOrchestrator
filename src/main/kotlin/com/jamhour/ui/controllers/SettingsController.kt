package com.jamhour.ui.controllers

import atlantafx.base.controls.Tile
import atlantafx.base.controls.ToggleSwitch
import com.jamhour.util.Defaults.defaultTheme
import com.jamhour.util.SettingsParam
import com.jamhour.util.Theme
import com.jamhour.util.loadStoredParams
import javafx.application.Application.setUserAgentStylesheet
import javafx.fxml.FXML
import javafx.scene.control.ComboBox

class SettingsController {

    @FXML
    private lateinit var themeTile: Tile

    @FXML
    private lateinit var logsTile: Tile

    private val themesComboBox = ComboBox<Theme>().apply {
        items += Theme.entries
        selectionModel.select(defaultTheme)
        selectionModel.selectedItemProperty().addListener { _, _, newTheme ->
            newTheme?.let {
                setUserAgentStylesheet(newTheme.theme.userAgentStylesheet)
            }
        }
    }

    private val logsSwitch = ToggleSwitch("Enabled").apply {
        setSelected(true)
        selectedProperty().addListener { _, _, newValue ->
            text = if (newValue && isSelected) "Enabled" else "Disabled"
        }
    }

    @FXML
    fun initialize() {
        themeTile.action = themesComboBox
        logsTile.action = logsSwitch

        loadSettings(loadStoredParams().settings)
    }

    private fun loadSettings(settings: SettingsParam) {
        themesComboBox.selectionModel.select(settings.theme)
        logsSwitch.isSelected = settings.logs
    }

    fun saveSettings() = SettingsParam(themesComboBox.value, logsSwitch.isSelected)

}