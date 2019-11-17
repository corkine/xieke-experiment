package com.mazhangjing.xieke

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.{Label, ScrollPane}
import javafx.scene.text.Font
import javafx.stage.Stage

class ScrollPaneTest extends Application {

  val root: ScrollPane = {
    val rect = new Label("Hello, World")
    rect.setFont(Font.font(300))
    val s1 = new ScrollPane(rect)
    s1.setPrefSize(120, 120)
    s1
  }

  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Test")
    primaryStage.setScene(new Scene(root, 400, 300))
    primaryStage.show()
  }
}
