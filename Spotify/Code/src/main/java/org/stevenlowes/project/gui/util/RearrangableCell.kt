package org.stevenlowes.project.gui.util

import javafx.beans.InvalidationListener
import javafx.event.EventHandler
import javafx.scene.control.ListCell
import javafx.scene.input.ClipboardContent
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import tornadofx.*


class RearrangableCell<T>(private val stringConverter: (T?) -> String) : ListCell<T>() {
    override fun updateItem(item: T, empty: Boolean) {
        super.updateItem(item, empty)
        if(empty){
            text = ""
        }
        else{
            text = stringConverter(item)
        }
    }

    init {
        setOnDragDetected { event ->
            if (item != null) {
                val dragboard = startDragAndDrop(TransferMode.MOVE)
                val content = ClipboardContent()
                content.putString(index.toString())
                dragboard.setContent(content)

                event.consume()
            }
        }

        setOnDragOver { event ->
            if (event.gestureSource != this && event.dragboard.hasString()) {
                event.acceptTransferModes(TransferMode.MOVE)
            }

            event.consume()
        }

        setOnDragEntered { event ->
            if (event.gestureSource != this && event.dragboard.hasString()) {
                opacity = 0.3
            }
        }

        setOnDragExited { event ->
            if (event.gestureSource != this && event.dragboard.hasString()) {
                opacity = 1.0
            }
        }

        setOnDragDropped { event ->
            if (item == null) {
                return@setOnDragDropped
            }

            val db = event.dragboard
            var success = false

            if (db.hasString()) {
                val items = listView.items
                val draggedIdx = db.string.toInt()
                val thisIdx = index

                val item = items[draggedIdx]
                items.removeAt(draggedIdx)
                items.add(thisIdx, item)
                items.invalidate()

                success = true
            }
            event.isDropCompleted = success

            event.consume()
        }

        onDragDone = EventHandler<DragEvent> { it.consume() }
    }
}