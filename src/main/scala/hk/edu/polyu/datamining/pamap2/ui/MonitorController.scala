package hk.edu.polyu.datamining.pamap2.ui

import java.io.File
import java.util.concurrent.ConcurrentLinkedQueue
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter

import hk.edu.polyu.datamining.pamap2.actor.ImportActor.ImportFile
import hk.edu.polyu.datamining.pamap2.actor.UIActor
import hk.edu.polyu.datamining.pamap2.utils.Lang.runnable

import scala.io.Source

/**
  * Created by beenotung on 1/30/16.
  */
object MonitorController {
  private var instance: MonitorController = null

  def importingFile(filename: String) = {
    Platform runLater (() => {
      instance.status_left.setText(s"importing $filename")
    })
  }

  def importedFile(filename: String) = {
    Platform runLater (() => {
      instance.status_left.setText(s"imported $filename")
      instance.handleNextFile()
    })
  }
}

class MonitorController extends MonitorControllerSkeleton {
  MonitorController.instance = this
  var pendingFiles = new ConcurrentLinkedQueue[File]
  var handlingFile = false


  def handleNextFile() = {
    val file = pendingFiles.poll()
    if (file != null) {
      UIActor.instance.self ! new ImportFile(file.getName, Source.fromFile(file).getLines().toSeq)
    }
  }

  override def select_import_file(event: ActionEvent) = {
    val fileChooser = new FileChooser()
    fileChooser.setTitle("Import File")
    fileChooser.getExtensionFilters.addAll(
      new ExtensionFilter("Data Files", "*.dat")
    )
    val files = fileChooser.showOpenMultipleDialog(MonitorApplication.getStage)
    pendingFiles.addAll(files)
    if (!handlingFile)
      handleNextFile()
  }
}