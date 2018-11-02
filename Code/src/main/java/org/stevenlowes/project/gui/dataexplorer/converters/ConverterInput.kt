package org.stevenlowes.project.gui.dataexplorer.converters

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import tornadofx.*
import kotlin.reflect.KClass

class ConverterInput: Fragment() {
    private val binding = SimpleObjectProperty<KClass<out DataPointConverter>?>()
    private val movingAvgSecs = SimpleDoubleProperty(1.0)

    private val values = listOf(
            AbsConverter::class,
            GradientConverter::class,
            MovingAverageConverter::class)

    override val root = stackpane {
        form {
            fieldset {
                field {
                    text = "Select a Converter"
                    combobox(binding, values) {
                        placeholder = label {
                            text = "Select an Option"
                        }
                        converter = object: StringConverter<KClass<out DataPointConverter>?>(){
                            override fun toString(value: KClass<out DataPointConverter>?) = value?.simpleName ?: "None Selected"
                            override fun fromString(string: String?) = throw NotImplementedError()
                        }
                    }
                }

                field {
                    text = "Enter number of seconds to average over"
                    textfield {
                        bind(movingAvgSecs)
                    }
                    visibleWhen {
                        binding.isNotNull.and(binding.isEqualTo(MovingAverageConverter::class))
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

    fun getInput(): DataPointConverter? {
        openModal(block = true)
        return when(binding.get()){
            AbsConverter::class -> AbsConverter()
            GradientConverter::class -> GradientConverter()
            MovingAverageConverter::class -> MovingAverageConverter((movingAvgSecs.get() * 1000).toLong())
            else -> null
        }
    }
}