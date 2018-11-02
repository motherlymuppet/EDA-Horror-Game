package org.stevenlowes.project.gui.dataexplorer

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Pos
import javafx.scene.chart.XYChart
import javafx.scene.control.SelectionMode
import javafx.scene.layout.*
import javafx.scene.text.Font
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.gui.dataexplorer.converters.ConverterInput
import org.stevenlowes.project.gui.dataexplorer.converters.DataPointConverter
import org.stevenlowes.project.gui.datascreenshot.DataScreenshot
import org.stevenlowes.project.gui.util.RearrangableCell
import tornadofx.*

class DataExplorerView(
        private val rawData: List<Pair<Long, Double>>,
        chartTitle: String? = null,
        chartDescription: String? = null,
        dataPointConverters: List<DataPointConverter> = listOf()
                      ) : View() {

    private val titleInputProperty = SimpleStringProperty(chartTitle ?: "")
    private val descriptionInputProperty = SimpleStringProperty(chartDescription ?: "")
    private val mutableConverters = FXCollections.observableArrayList<DataPointConverter>(dataPointConverters)

    val chart: GsrChart = GsrChart()

    init {
        disableCreate()
        disableRefresh()
        disableDelete()

        whenSaved {
            DataScreenshot.screenshot(rawData, titleInputProperty.get(), descriptionInputProperty.get(), mutableConverters.toList())
        }

        render()
    }

    @Suppress("UNCHECKED_CAST")
    private fun render() {
        chart.title = titleInputProperty.get()
        val data = applyConverters(rawData, mutableConverters)
        chart.series.clear()
        chart.series.addAll(data.map { XYChart.Data(it.first as Number, it.second as Number) })
    }

    companion object {
        fun applyConverters(rawData: List<Pair<Long, Double>>, converters: List<DataPointConverter>): List<Pair<Long, Double>> {
            converters.forEach { it.clear() }
            var data = rawData
            for (converter in converters) {
                data = data.mapNotNull { converter(it) }
            }
            return data
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

                                    prefHeightProperty().bind(Bindings.size(itemsProperty().get()).multiply(24));
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
                                            val toAdd = ConverterInput().getInput()
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