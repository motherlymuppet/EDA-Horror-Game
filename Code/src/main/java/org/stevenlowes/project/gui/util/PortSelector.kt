package org.stevenlowes.project.gui.util

import gnu.io.CommPortIdentifier
import javafx.scene.Parent
import javafx.scene.control.Dialog
import org.stevenlowes.project.gui.datacollection.DataCollectionChart
import org.stevenlowes.project.gui.inputmodals.ListInput
import tornadofx.*
import javax.swing.JOptionPane

class PortSelector(){
    companion object {
        fun getPort(): CommPortIdentifier?{
            val ports = CommPortIdentifier.getPortIdentifiers().asSequence().map { it as CommPortIdentifier }.toList()

            if (ports.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No Devices Found")
                return null
            }
            else {
                return ListInput(ports, CommPortIdentifier::getName).getInput()
            }
        }
    }
}