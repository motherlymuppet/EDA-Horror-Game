package org.stevenlowes.project.gui

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.VBox
import org.stevenlowes.project.gui.datacollection.DataCollectionView
import org.stevenlowes.project.gui.songnearness.SongNearnessView
import tornadofx.*

class MainMenu: View("Main Menu"){
    override val root = vbox(spacing = 8, alignment = Pos.CENTER) {
        label("Options")
        button("Data Collection") {
            action { replaceWith(DataCollectionView::class) }
        }
        button("Song Nearness") {
            action { replaceWith(SongNearnessView::class) }
        }
    }

    override fun onDock() {
        super.onDock()
        currentWindow?.sizeToScene()
    }
}