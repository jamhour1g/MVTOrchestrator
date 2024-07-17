package com.jamhour.util

import javafx.fxml.FXMLLoader
import javafx.scene.Node
import java.net.URI
import java.net.URL

interface ViewContainer<out T : Node, out R : Any> {
    val content: T
    val controller: R
}

data class DefaultViewContainer<out T : Node, out R : Any>(val viewURI: URI) : ViewContainer<T, R> {

    constructor(viewURI: URL) : this(viewURI.toURI())

    private val loader = FXMLLoader().apply {
        location = viewURI.toURL()
    }
    override val content: T = loader.load()
    override val controller: R = loader.getController()
}

fun <T : Node, R : Any> createView(viewURI: URI): ViewContainer<T, R> = DefaultViewContainer(viewURI)
fun <T : Node, R : Any> createView(viewURL: URL): ViewContainer<T, R> = createView(viewURL.toURI())