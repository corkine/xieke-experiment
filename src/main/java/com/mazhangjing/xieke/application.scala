package com.mazhangjing.xieke

import java.time.{Duration, LocalDate, LocalDateTime}
import java.util

import com.mazhangjing.lab.{ExpRunner, ExperimentHelper, OpenedEvent, SimpleExperimentHelperImpl}
import com.mazhangjing.xieke.model.api.Condition
import javafx.event.ActionEvent
import org.slf4j.{Logger, LoggerFactory}
import scalafx.Includes._
import scalafx.application
import scalafx.application.{JFXApp, Platform}
import scalafx.collections.ObservableBuffer
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.ScrollPane.ScrollBarPolicy
import scalafx.scene.control._
import scalafx.scene.layout.{GridPane, VBox}
import scalafx.scene.text.Font
import scalafx.stage.Stage

object XKApp extends JFXApp {

  val logger: Logger = LoggerFactory.getLogger(getClass)

  val expertTo: LocalDateTime = LocalDate.parse("2023-11-23").atStartOfDay()

  if (Duration.between(LocalDateTime.now(), expertTo).toDays < 0) {
    new Alert(AlertType.Warning) {
      headerText = "错误的软件许可，请联系软件开发和维护人员。"
    }.showAndWait()
    System.exit(-1)
  } else {
    logger.info("Checked Passed")
  }

  val debugLabel = new Label(text = "Debug 模式")
  val checkDebug = new CheckBox {
    text <== when (selected) choose "Debug 开启" otherwise "Debug 关闭"
    selected = XKExperiment.DEBUG
  }
  GridPane.setConstraints(debugLabel, 0,0)
  GridPane.setConstraints(checkDebug, 1, 0)

  val emotion_png_width_t = new Label("情绪图像宽度")
  val emotion_png_width = new TextField {
    text = XKExperiment.EMOTION_IMAGE_WIDTH.toString
    tooltip = "动图的宽度，单位像素"
  }
  GridPane.setConstraints(emotion_png_width_t, 0, 1)
  GridPane.setConstraints(emotion_png_width, 1, 1)

  val cond = new ChoiceBox[Condition] {
    items = ObservableBuffer(
      Condition.NORMAL_SUMMARY,
      Condition.EMOTION_SUMMARY,
      Condition.NORMAL_DETAIL,
      Condition.EMOTION_DETAIL
    )
    tooltip = "NORMAL 为正常图片，EMOTION 为情绪化图片代理\n" +
      "SUMMARY 为带有详细解释，DETAIL 为只提供正确错误判断"
    selectionModel().selectFirst()
  }
  val cond_l = new Label("任务")
  GridPane.setConstraints(cond_l, 0,2)
  GridPane.setConstraints(cond, 1,2)

  val font_size_l = new Label("标准字号")
  val font_size = new TextField {
    text = XKExperiment.FONT_SIZE.toString
    tooltip = "请输入标准字体展示的字号大小"
  }
  GridPane.setConstraints(font_size_l, 0, 3)
  GridPane.setConstraints(font_size, 1, 3)

  val imageWidth_t = new Label("指导语图片宽度")
  val imageWidth = new TextField {
    text = XKExperiment.IMAGE_WIDTH.toString
    tooltip = "使用图片作为提示语的时候，图片的宽度，单位像素"
  }
  GridPane.setConstraints(imageWidth_t, 0, 4)
  GridPane.setConstraints(imageWidth, 1, 4)

  val showTitleName_t = new Label("问卷标题")
  val showTitleName = new CheckBox {
    text = "显示标题"
    tooltip = "是否显示问卷的标题"
    selected = false
  }
  GridPane.setConstraints(showTitleName_t, 0, 5)
  GridPane.setConstraints(showTitleName, 1, 5)

  val queryVGap_t = new Label("问卷题目的行间距")
  val queryVGap = new TextField {
    text = XKExperiment.SU_VGAP.toString
    tooltip = "问卷题目的行间距"
  }
  GridPane.setConstraints(queryVGap_t, 0, 7)
  GridPane.setConstraints(queryVGap, 1, 7)

  val queryHGap_t = new Label("问卷每个选项间距 - 五个题目")
  val queryHGap = new TextField {
    text = XKExperiment.SU_HGAP_5.toString
    tooltip = "问卷题目的每个选项的间距 - 五个题目"
  }
  GridPane.setConstraints(queryHGap_t, 0, 8)
  GridPane.setConstraints(queryHGap, 1, 8)

  val queryHGap2_t = new Label("问卷每个选项间距 - 九个题目")
  val queryHGap2 = new TextField {
    text = XKExperiment.SU_HGAP_9.toString
    tooltip = "问卷题目的每个选项的间距（九个题目）"
  }
  GridPane.setConstraints(queryHGap2_t, 0, 9)
  GridPane.setConstraints(queryHGap2, 1, 9)

  val queryShowLine_t = new Label("问卷行线")
  val queryShowLine = new CheckBox {
    selected = true
    text <== when(selected) choose "显示行线" otherwise "不显示行线"
  }
  GridPane.setConstraints(queryShowLine_t, 0, 10)
  GridPane.setConstraints(queryShowLine, 1, 10)

  val questionHGap_t = new Label("问题答案间隔")
  val questionHGap = new TextField {
    text = XKExperiment.QU_HGAP.toString
    tooltip = "需要被试回答问题的四个答案之间的间隔"
  }
  GridPane.setConstraints(questionHGap_t, 0, 11)
  GridPane.setConstraints(questionHGap, 1, 11)

  val question_layout_margin = new Label(text = "问题布局边距像素")
  val pixel = new TextField {
    text = XKExperiment.QUESTION_LAYOUT_MARGIN.toString
    tooltip = "当题目过宽，调节此处设置以更改题目两边边距"
  }
  GridPane.setConstraints(question_layout_margin, 0, 12)
  GridPane.setConstraints(pixel, 1,12)

  val help_padding_t = new Label("问卷回答提示向左偏移")
  val help_padding = new TextField {
    text = XKExperiment.HELP_PADDING_LEFT.mkString(" ")
    tooltip = "三种问卷偏移使用空格隔开，依次是情绪问卷，代理问卷，动机问卷"
  }
  GridPane.setConstraints(help_padding_t, 0, 13)
  GridPane.setConstraints(help_padding, 1,13)

  val fullscreen = new CheckBox {
    text = "全屏显示"
    selected <== !checkDebug.selected
    disable = true
    tooltip = "选中后全屏显示程序，按 F11 退出"
  }
  val fullscreen_t = new Label("界面显示")
  GridPane.setConstraints(fullscreen_t, 0, 14)
  GridPane.setConstraints(fullscreen, 1, 14)

  val help_spacing_t = new Label("帮助文本间隔")
  val help_spacing = new TextField {
    text = XKExperiment.HELP_TEXT_SPACING.toString
    tooltip = "数值越大，帮助文本间隔越大"
  }
  GridPane.setConstraints(help_spacing_t, 0, 15)
  GridPane.setConstraints(help_spacing, 1, 15)

  def isTimeOk: Boolean = {
    /*if (LocalDateTime.now().isAfter(LocalDate.of(2019,9,1).atStartOfDay()) &&
    LocalDateTime.now().isBefore(LocalDate.of(2019,9,5).atStartOfDay())) true else false*/
    true
  }

  def checkAndLaunch(): Unit = {
    if (!isTimeOk) {
      new Alert(AlertType.Warning) {
        headerText = "您的许可证已过期"
        contentText = "您的许可证已过期，请联系技术支持人员。"
        buttonTypes = Seq(ButtonType.OK)
      } .showAndWait() match {
        case _ => Platform.exit()
      }
    }
    import XKExperiment._
    DEBUG = checkDebug.selected.value
    CHOOSED_CONDITION = cond.selectionModel().getSelectedItem
    QUESTION_LAYOUT_MARGIN = pixel.getText.toInt
    FONT_SIZE = font_size.getText.toInt
    IMAGE_WIDTH = imageWidth.getText.toInt
    SU_SHOW_TITLE_NAME = showTitleName.isSelected
    SU_VGAP = queryVGap.getText.toInt
    SU_HGAP_5 = queryHGap.getText.toInt
    SU_HGAP_9 = queryHGap2.getText.toInt
    SU_SHOW_LINE = queryShowLine.isSelected
    QU_HGAP = questionHGap.getText.toInt
    EMOTION_IMAGE_WIDTH = emotion_png_width.getText.toInt
    HELP_PADDING_LEFT = help_padding.getText.split(" ").map(_.toInt)
    HELP_TEXT_SPACING = help_spacing.getText.toInt
    runExperiment(stage)
  }
  def runExperiment(stage: Stage): Unit = {
    val helper: ExperimentHelper = new SimpleExperimentHelperImpl(new ExpRunner {
      override def initExpRunner(): Unit = {
        setEventMakerSet(null)
        val set = new util.HashSet[OpenedEvent]()
        set.add(OpenedEvent.KEY_PRESSED)
        setOpenedEventSet(set)
        setExperimentClassName("com.mazhangjing.xieke.XKExperiment")
        setTitle("Experiment")
        setVersion("0.0.1")
        setFullScreen(false)
      }
    })
    helper.initStage(stage)
    stage.setTitle("XKExperiment")
    stage.getScene.getStylesheets.add("file:xieke/custom.css")
    stage.setFullScreen(fullscreen.isSelected)
    stage.show()
  }

  val ok: Button = new Button("开始") {
    onAction = (_:ActionEvent) => checkAndLaunch()
  }

  val gridPane: GridPane = new GridPane {
    hgap = 20
    vgap = 10
    padding = Insets(20)
    children = Seq(debugLabel, question_layout_margin, checkDebug, pixel, cond, cond_l, fullscreen, fullscreen_t, ok, font_size_l, font_size,
      imageWidth_t, imageWidth, showTitleName_t, showTitleName, queryVGap_t,
      queryVGap, queryHGap_t, queryHGap, queryHGap2_t, queryHGap2, queryShowLine_t, queryShowLine, questionHGap_t,
      questionHGap, emotion_png_width, emotion_png_width_t, help_padding_t, help_padding, help_spacing_t, help_spacing)
  }

  GridPane.setConstraints(ok, 0, 18)

  def add(lab:String, variable:Int, line: Int)(op: Int => Unit): Unit = {
    val label = new Label(lab)
    val textField = new TextField {
      text = variable.toString
      promptText = lab
      text.addListener(_ => {
        op(text().toInt)
      })
    }
    GridPane.setConstraints(label, 0, line)
    GridPane.setConstraints(textField, 1, line)
    gridPane.children.addAll(label, textField)
  }

  add("Agent 解释图片宽度", XKExperiment.AGENT_EXPLAIN_IMAGE_WIDTH, 16) {
    XKExperiment.AGENT_EXPLAIN_IMAGE_WIDTH = _
  }

  stage = new application.JFXApp.PrimaryStage {
    title = "Configure - " + XKExperiment.version.split("\n").map(_.trim).last
    scene = new Scene {
      root = new ScrollPane {
        content = new VBox {
          hbarPolicy = ScrollBarPolicy.AsNeeded
          vbarPolicy = ScrollBarPolicy.AsNeeded
          children = List(
            new Label {
              text = "配置中心"
              padding = Insets(20,0,0,20)
              font = new Font(size = 20)
            },
            gridPane
          )
        }
      }
    }
  }
}
