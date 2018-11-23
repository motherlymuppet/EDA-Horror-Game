package org.stevenlowes.project.gui.dataexplorer

import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.chart.XYChart
import javafx.scene.control.SelectionMode
import javafx.scene.layout.Priority
import javafx.scene.text.Font
import org.stevenlowes.project.gui.chart.DataLabel
import org.stevenlowes.project.gui.chart.GsrChart
import org.stevenlowes.project.gui.dataexplorer.transformers.AbstractTransformer
import org.stevenlowes.project.gui.dataexplorer.transformers.TransformerInput
import org.stevenlowes.project.gui.datascreenshot.DataScreenshot
import org.stevenlowes.project.gui.util.RearrangableCell
import tornadofx.*

class DataExplorerView(
        private val rawData: List<Pair<Long, Double>>,
        chartTitle: String? = null,
        chartDescription: String? = null,
        abstractTransformers: List<AbstractTransformer> = listOf(),
        labels: List<DataLabel>
                      ) : View() {

    constructor(chart: GsrChart) : this(rawData = chart.series.map { it.xValue.toLong() to it.yValue.toDouble() },
                                        labels = chart.labels)

    private val titleInputProperty = SimpleStringProperty(chartTitle ?: "")
    private val descriptionInputProperty = SimpleStringProperty(chartDescription ?: "")
    private val mutableConverters = FXCollections.observableArrayList<AbstractTransformer>(abstractTransformers)
    private val labels = FXCollections.observableArrayList<DataLabel>(labels)
    private val shouldAutoRender = SimpleBooleanProperty(true)
    private val showLabels = SimpleBooleanProperty(true)
    private val labelFontSize = SimpleIntegerProperty(24)

    private var chartData = applyConverters(rawData, labels, mutableConverters)

    val chart: GsrChart = GsrChart()

    init {
        disableCreate()
        disableRefresh()
        disableDelete()

        whenSaved {
            DataScreenshot.screenshot(rawData,
                                      titleInputProperty.get(),
                                      descriptionInputProperty.get(),
                                      mutableConverters.toList(),
                                      labels)
        }

        render()
    }

    @Suppress("UNCHECKED_CAST")
    private fun render() {
        chart.title = titleInputProperty.get()

        chart.labelSize = labelFontSize.get()

        val labels = if (showLabels.get()) {
            labels
        }
        else {
            listOf<DataLabel>().observable()
        }

        chartData = applyConverters(rawData, labels, mutableConverters)

        chart.clear()
        chart.series.addAll(chartData)
        chart.replaceAllLabels(labels)
    }

    companion object {
        fun applyConverters(rawData: List<Pair<Long, Double>>,
                            labels: List<DataLabel>,
                            converters: List<AbstractTransformer>): ObservableList<XYChart.Data<Number, Number>> {
            converters.forEach { it.clear() }
            var data = rawData
            for (converter in converters) {
                data = data.mapNotNull { converter(labels, it) }
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
                            titleInputProperty.addListener(InvalidationListener { autoRender() })
                        }
                    }

                    field("Description") {
                        textarea {
                            prefRowCount = 6
                            bind(descriptionInputProperty)
                        }
                    }
                }

                fieldset("Labels") {
                    field("Enable Labels") {
                        checkbox("Show", showLabels) {
                            setOnAction {
                                autoRender()
                            }
                        }
                    }

                    field("Label Size") {
                        hbox(8) {
                            textfield(labelFontSize) {
                                isDisable = true
                                prefWidth = 50.0
                            }

                            slider(1..36) {
                                bind(labelFontSize)
                                setOnMouseReleased {
                                    autoRender()
                                }
                            }
                        }
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

                                onDoubleClick {
                                    val index = selectionModel.selectedIndex
                                    if (index >= 0) {
                                        val replacement = TransformerInput.getInput(chartData)
                                        if (replacement != null) {
                                            mutableConverters[index] = replacement
                                        }
                                    }
                                }
                            }

                            mutableConverters.addListener(InvalidationListener { autoRender() })

                            hbox(4, Pos.CENTER) {
                                button {
                                    text = "−"
                                    font = Font(font.name, 24.0)
                                    action {
                                        val selected = convertersList.selectedItem
                                        mutableConverters.remove(selected)
                                    }
                                }

                                button {
                                    text = "+"
                                    font = Font(font.name, 24.0)
                                    action {
                                        val toAdd = TransformerInput.getInput(chartData)
                                        if (toAdd != null) {
                                            mutableConverters.add(toAdd)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            hbox(16, Pos.CENTER) {
                checkbox("AutoRender", shouldAutoRender) {
                    setOnAction {
                        autoRender()
                    }
                }
                button {
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

    private fun autoRender() {
        if (shouldAutoRender.get()) {
            render()
        }
    }
}