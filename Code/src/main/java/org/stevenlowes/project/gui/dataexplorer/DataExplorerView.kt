package org.stevenlowes.project.gui.dataexplorer

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.chart.XYChart
import javafx.scene.control.SelectionMode
import javafx.scene.layout.*
import javafx.scene.text.Font
import org.stevenlowes.project.gui.chart.DataLabel
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.gui.dataexplorer.transformers.TransformerInput
import org.stevenlowes.project.gui.dataexplorer.transformers.AbstractTransformer
import org.stevenlowes.project.gui.datascreenshot.DataScreenshot
import org.stevenlowes.project.gui.util.RearrangableCell
import tornadofx.*
import java.util.*

class DataExplorerView(
        private val rawData: List<Pair<Long, Double>>,
        chartTitle: String? = null,
        chartDescription: String? = null,
        abstractTransformers: List<AbstractTransformer> = listOf(),
        labels: List<DataLabel>
                      ) : View() {

    constructor(chart: GsrChart): this(rawData = chart.series.map { it.xValue.toLong() to it.yValue.toDouble() }, labels = chart.labels)

    private val titleInputProperty = SimpleStringProperty(chartTitle ?: "")
    private val descriptionInputProperty = SimpleStringProperty(chartDescription ?: "")
    private val mutableConverters = FXCollections.observableArrayList<AbstractTransformer>(abstractTransformers)
    private val labels = FXCollections.observableArrayList<DataLabel>(labels)

    private var chartData = applyConverters(rawData, mutableConverters)

    val chart: GsrChart = GsrChart()

    init {
        disableCreate()
        disableRefresh()
        disableDelete()

        whenSaved {
            DataScreenshot.screenshot(rawData, titleInputProperty.get(), descriptionInputProperty.get(), mutableConverters.toList(), labels)
        }

        render()
    }

    @Suppress("UNCHECKED_CAST")
    private fun render() {
        chart.title = titleInputProperty.get()
        chartData = applyConverters(rawData, mutableConverters)

        chart.clear()
        chart.series.addAll(chartData)
        chart.addAllLabels(labels)
    }

    companion object {
        fun applyConverters(rawData: List<Pair<Long, Double>>, converters: List<AbstractTransformer>): ObservableList<XYChart.Data<Number, Number>> {
            converters.forEach { it.clear() }
            var data = rawData
            for (converter in converters) {
                data = data.mapNotNull { converter(it) }
            }
            return FXCollections.observableArrayList(data.map { XYChart.Data(it.first as Number, it.second as Number) })
        }
    }


    override val root = hbox {
        //Controls
        vbox {
            maxHeightProperty().bind(chart.heightProperty())
            form {
                fieldset("Info") {
                    field("Title") {
                        text = "Title:"
                        textfield {
                            bind(titleInputProperty)
                        }
                    }

                    field("Description") {
                        textarea {
                            prefRowCount = 6
                            bind(descriptionInputProperty)
                        }
                    }

                    fieldset("Converters") {
                        field {
                            vbox {
                                val convertersList = listview(mutableConverters) {
                                    vgrow = Priority.ALWAYS

                                    prefHeightProperty().bind(Bindings.size(itemsProperty().get()).multiply(24).add(24))
                                    selectionModel.selectionMode = SelectionMode.SINGLE
                                    placeholder = label("No Converters Added")

                                    setCellFactory { _ -> RearrangableCell { it?.toString() ?: "Null" } }
                                }

                                hbox(4, Pos.CENTER) {
                                    button {
                                        text = "−"
                                        font = Font(font.name, 24.0)
                                        action {
                                            val selected = convertersList.selectedItem
                                            mutableConverters.remove(selected)
                                            render()
                                        }
                                    }

                                    button {
                                        text = "+"
                                        font = Font(font.name, 24.0)
                                        action {
                                            val toAdd = TransformerInput.getInput(chartData)
                                            if (toAdd != null) {
                                                mutableConverters.add(toAdd)
                                                render()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                button {
                    alignment = Pos.CENTER
                    text = "Render"
                    isFillWidth = true
                    action {
                        render()
                    }
                }
            }
        }

        add(chart)
    }
}