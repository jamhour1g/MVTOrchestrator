module MVTOrchestrator {

    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires java.logging;
    requires atlantafx.base;
    requires kotlinx.coroutines.core;
    requires kotlinx.coroutines.javafx;
    requires java.prefs;

    opens com.jamhour.ui.controllers to javafx.fxml, java.prefs;
    exports com.jamhour.ui;
    exports com.jamhour.util;
    exports com.jamhour.ui.controllers;
    exports com.jamhour.model;
    exports com.jamhour.process_management;
}