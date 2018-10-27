package org.stevenlowes.project.gui.datacollection

import org.stevenlowes.project.gui.MainMenu
import tornadofx.*

class DataCollectionView: View("Data Recording"){
    override val root = borderpane {
        top = button {
            text = "back"
            action {
                replaceWith(MainMenu::class)
            }
        }
        center = DataCollectionFragment(4).root //TODO handle com port better
    }

    override fun onDock() {
        super.onDock()
        currentWindow?.sizeToScene()
    }
}