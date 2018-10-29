package org.stevenlowes.project.gui.util

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class TextInput : Fragment() {
    val binding = SimpleStringProperty()

    override val root = form {
        fieldset {
            field {
                text = "Enter COM Port Name"
                textfield {
                    bind(binding)
                }
            }
        }

        buttonbar {
            button {
                text = "Cancel"
                action {
                    binding.set(null)
                    close()
                }
            }

            button {
                text = "Ok"
                action {
                    close()
                }
                disableProperty().bind(binding.isNull.or(binding.isEmpty))
            }
        }
    }

    fun getInput(): String? {
        openModal(block = true)
        return binding.get()
    }
}