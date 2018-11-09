package org.stevenlowes.project.gui.dataexplorer.converters

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.util.StringConverter
import tornadofx.*
import kotlin.reflect.KClass

class ConverterInput: Fragment() {
    private val binding = SimpleObjectProperty<KClass<out DataPointConverter>?>()
    private val movingMeanSecs = SimpleDoubleProperty(1.0)
    private val movingMedianSecs = SimpleDoubleProperty(1.0)
    private val destructionKeepEvery = SimpleIntegerProperty(10)

    private val values = listOf(
            AbsConverter::class,
            GradientConverter::class,
            MovingMeanConverter::class,
            DestructionConverter::class,
            MovingMedianConverter::class)

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
                    text = "Enter number of seconds to take the mean over"
                    textfield {
                        bind(movingMeanSecs)
                    }
                    visibleWhen {
                        binding.isNotNull.and(binding.isEqualTo(MovingMeanConverter::class))
                    }
                }

                field {
                    text = "Enter number of seconds to take the median over"
                    textfield {
                        bind(movingMedianSecs)
                    }
                    visibleWhen {
                        binding.isNotNull.and(binding.isEqualTo(MovingMedianConverter::class))
                    }
                }

                field {
                    text = "Keep one in every x data points. Enter x."
                    textfield {
                        bind(destructionKeepEvery)
                    }
                    visibleWhen {
                        binding.isNotNull.and(binding.isEqualTo(DestructionConverter::class))
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
            MovingMeanConverter::class -> MovingMeanConverter((movingMeanSecs.get() * 1000).toLong())
            MovingMedianConverter::class -> MovingMedianConverter((movingMedianSecs.get() * 1000).toLong())
            DestructionConverter::class -> DestructionConverter(destructionKeepEvery.get())
            else -> null
        }
    }
}