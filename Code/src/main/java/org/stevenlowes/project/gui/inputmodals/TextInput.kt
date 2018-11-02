package org.stevenlowes.project.gui.inputmodals

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class TextInput(dialogText: String) : Fragment() {
    val binding = SimpleStringProperty()

    override val root = stackpane {
        form {
            fieldset {
                field {
                    text = dialogText
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
    }

    fun getInput(): String? {
        openModal(block = true)
        return binding.get()
    }

    fun getInputBlankIsNull(): String? {
        val input = getInput() ?: ""
        return if(input.isBlank()){
            null
        }
        else{
            input
        }
    }
}