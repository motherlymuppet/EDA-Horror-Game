package org.stevenlowes.project.gui.dataexplorer.transformers

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.scene.chart.XYChart
import javafx.util.StringConverter
import org.stevenlowes.project.gui.chart.GsrChart
import tornadofx.*
import tornadofx.controlsfx.rangeslider
import java.time.format.DateTimeFormatter
import kotlin.math.roundToLong
import kotlin.reflect.KClass

class TransformerInput private constructor(series: ObservableList<XYChart.Data<Number, Number>>) : Fragment() {
    private val binding = SimpleObjectProperty<KClass<out AbstractTransformer>?>()

    private val movingMeanSecs = SimpleDoubleProperty(1.0)
    private val movingMedianSecs = SimpleDoubleProperty(1.0)
    private val destructionKeepEvery = SimpleIntegerProperty(10)

    private val enableXFilterMin = SimpleBooleanProperty(true)
    private val enableXFilterMax = SimpleBooleanProperty(true)
    private val xFilterSliderMin: Double
    private val xFilterSliderMax: Double
    private val xFilterMin = SimpleDoubleProperty(0.0)
    private val xFilterMax = SimpleDoubleProperty(Double.MAX_VALUE)

    private val enableYFilterMin = SimpleBooleanProperty(true)
    private val enableYFilterMax = SimpleBooleanProperty(true)
    private val yFilterSliderMin: Double
    private val yFilterSliderMax: Double
    private val yFilterMin = SimpleDoubleProperty(Double.MIN_VALUE)
    private val yFilterMax = SimpleDoubleProperty(Double.MAX_VALUE)

    private val values = listOf(
            AbsTransformer::class,
            GradientTransformer::class,
            MovingMeanTransformer::class,
            DestructionTransformer::class,
            MovingMedianTransformer::class,
            YFilterTransformer::class,
            XFilterTransformer::class)

    init {
        if (series.isNotEmpty()) {
            val xValues = series.map { it.xValue.toDouble() }
            xFilterMin.set(xValues.min()!!)
            xFilterMax.set(xValues.max()!!)

            val yValues = series.map { it.yValue.toDouble() }
            yFilterMin.set(yValues.min()!!)
            yFilterMax.set(yValues.max()!!)
        }

        xFilterSliderMin = xFilterMin.get()
        xFilterSliderMax = xFilterMax.get()
        yFilterSliderMin = yFilterMin.get()
        yFilterSliderMax = yFilterMax.get()
    }

    override val root = stackpane {
        form {
            fieldset {
                field("Select a Converter") {
                    combobox(binding, values) {
                        placeholder = label("Select an Option")
                        converter = object : StringConverter<KClass<out AbstractTransformer>?>() {
                            override fun toString(value: KClass<out AbstractTransformer>?) = value?.simpleName
                                    ?: "None Selected"

                            override fun fromString(string: String?) = throw NotImplementedError()
                        }
                    }
                }

                stackpane {
                    field("Enter number of seconds to take the mean over") {
                        textfield(movingMeanSecs)
                        visibleWhen {
                            binding.isNotNull.and(binding.isEqualTo(MovingMeanTransformer::class))
                        }
                    }

                    field("Enter number of seconds to take the median over") {
                        textfield(movingMedianSecs)
                        visibleWhen {
                            binding.isNotNull.and(binding.isEqualTo(MovingMedianTransformer::class))
                        }
                    }

                    field("Keep one in every x data points. Enter x.") {
                        textfield(destructionKeepEvery)
                        visibleWhen {
                            binding.isNotNull.and(binding.isEqualTo(DestructionTransformer::class))
                        }
                    }

                    fieldset("Time Filter Config") {
                        field("Enable/Disable") {
                            checkbox("Enable Min", enableXFilterMin)
                            checkbox("Enable Max", enableXFilterMax)
                        }
                        field("Valid Time") {
                            vbox {
                                rangeslider(xFilterMin, xFilterMax, xFilterSliderMin, xFilterSliderMax) {
                                    blockIncrement = 1000.0
                                }
                                hbox {
                                    val timeConverter = GsrChart.timeConverter
                                    val formatter = DateTimeFormatter.ofPattern("EEE HH:mm:ss")
                                    val stringConverter: StringConverter<in Double> = object : StringConverter<Double>() {
                                        override fun toString(millis: Double?) =
                                                if (millis == null) {
                                                    "None"
                                                }
                                                else {
                                                    timeConverter.toTime(millis).format(formatter)
                                                }

                                        override fun fromString(string: String?) = throw NotImplementedError()
                                    }

                                    label(xFilterMin.asObject(), converter = stringConverter)
                                    label(" - ")
                                    label(xFilterMax.asObject(), converter = stringConverter)
                                }
                            }
                        }
                        visibleWhen {
                            binding.isNotNull.and(binding.isEqualTo(XFilterTransformer::class))
                        }
                    }

                    fieldset("Value Filter Config") {
                        field("Enable/Disable") {
                            checkbox("Enable Min", enableYFilterMin)
                            checkbox("Enable Max", enableYFilterMax)
                        }
                        field("Valid Values") {
                            vbox {
                                rangeslider(yFilterMin, yFilterMax, yFilterSliderMin, yFilterSliderMax) {
                                    blockIncrement = 100.0
                                }

                                hbox {
                                    val stringConverter: StringConverter<in Double> = object : StringConverter<Double>() {
                                        override fun toString(value: Double?) = value?.roundToLong()?.toString()
                                                ?: "None"

                                        override fun fromString(string: String?) = throw NotImplementedError()
                                    }

                                    label(yFilterMin.asObject(), converter = stringConverter)
                                    label(" - ")
                                    label(yFilterMax.asObject(), converter = stringConverter)
                                }
                            }
                        }
                        visibleWhen {
                            binding.isNotNull.and(binding.isEqualTo(YFilterTransformer::class))
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

    companion object {
        fun getInput(series: ObservableList<XYChart.Data<Number, Number>>): AbstractTransformer? {
            return TransformerInput(series).getInput()
        }
    }

    fun getInput(): AbstractTransformer? {
        openModal(block = true)
        return when (binding.get()) {
            AbsTransformer::class -> getAbsTransformer()
            GradientTransformer::class -> getGradientTransformer()
            MovingMeanTransformer::class -> getMovingMeanTransformer()
            MovingMedianTransformer::class -> getMovingMedianTransformer()
            DestructionTransformer::class -> getDestructionTransformer()
            XFilterTransformer::class -> getXFilterTransformer()
            YFilterTransformer::class -> getYFilterTransformer()
            else -> null
        }
    }

    private fun getXFilterTransformer(): AbstractTransformer {
        val xMin = if (enableXFilterMin.get()) xFilterMin.get() else null
        val xMax = if (enableXFilterMax.get()) xFilterMax.get() else null
        return XFilterTransformer(xMin, xMax)
    }

    private fun getYFilterTransformer(): AbstractTransformer {
        val yMin = if (enableYFilterMin.get()) yFilterMin.get() else null
        val yMax = if (enableYFilterMax.get()) yFilterMax.get() else null
        return YFilterTransformer(yMin, yMax)
    }

    private fun getDestructionTransformer() = DestructionTransformer(destructionKeepEvery.get())
    private fun getMovingMedianTransformer() = MovingMedianTransformer((movingMedianSecs.get() * 1000).toLong())
    private fun getMovingMeanTransformer() = MovingMeanTransformer((movingMeanSecs.get() * 1000).toLong())
    private fun getGradientTransformer() = GradientTransformer()
    private fun getAbsTransformer() = AbsTransformer()
}