package org.stevenlowes.project.gui.datascreenshot

import javafx.beans.property.SimpleStringProperty
import javafx.scene.Parent
import tornadofx.*

class PathEntryView : View("Name Image"){
    val name = SimpleStringProperty()

    override val root = borderpane {
        left = label {
            text = "Enter Name for Image"
        }

        right = textfield{
            bind(name)
        }
    }
}