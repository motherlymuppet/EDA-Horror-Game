package org.stevenlowes.project.gui.inputmodals

import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import tornadofx.*

class ListInput<T : Any>(values: List<T>, stringConverter: (T) -> String = Any?::toString) : Fragment() {
    val binding = SimpleObjectProperty<T?>()

    override val root = stackpane {
        form {
            fieldset {
                field {
                    text = "Enter COM Port Name"
                    combobox(binding, values) {
                        placeholder = label {
                            text = "Select an Option"
                        }
                        converter = object : StringConverter<T?>() {
                            override fun toString(option: T?) = if (option == null) "Null" else stringConverter(option)
                            override fun fromString(string: String?) = throw NotImplementedError()
                        }
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
                    disableProperty().bind(binding.isNull)
                }
            }
        }
    }

    fun getInput(): T? {
        openModal(block = true)
        return binding.get()
    }
}