package com.mazhangjing.xieke

import java.util

import com.mazhangjing.lab.{ExpRunner, ExperimentHelper, OpenedEvent, SimpleExperimentHelperImpl}
import com.mazhangjing.xieke.model.api.Condition
import javafx.event.ActionEvent
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

  val debugLabel = new Label(text = "Debug 模式")
  val checkDebug = new CheckBox {
    text <== when (selected) choose "Debug 开启" otherwise "Debug 关闭"
    selected = false
  }
  GridPane.setConstraints(debugLabel, 0,0)
  GridPane.setConstraints(checkDebug, 1, 0)

  val folder_prefix = new Label(text = "文件夹目录名")
  val folderInp = new TextField {
    text = "xieke"
    tooltip = "所有的图像必须在此前缀的文件夹目录中才能被搜寻"
  }
  GridPane.setConstraints(folder_prefix, 0,1)
  GridPane.setConstraints(folderInp, 1,1)

  val welcome_png = new Label(text = "欢迎图像名称")
  val welcomePng = new TextField {
    text = "welcome.png"
    tooltip = "在开始之前进行的总的介绍，需要将此文件放入文件夹目录名中"
  }
  GridPane.setConstraints(welcome_png, 0, 2)
  GridPane.setConstraints(welcomePng, 1,2)

  val emotion_png_width_t = new Label("情绪图像宽度")
  val emotion_png_width = new TextField {
    text = "170"
    tooltip = "动图的宽度，单位像素"
  }
  GridPane.setConstraints(emotion_png_width_t, 0, 3)
  GridPane.setConstraints(emotion_png_width, 1, 3)


  val ex_kind_in_config = new Label("练习标签")
  val ex_kind_in_config_t = new TextField {
    text = "练习"
    tooltip = "使用此处设置让程序在配置文件中寻找属于练习的题目"
  }
  GridPane.setConstraints(ex_kind_in_config, 0 ,4)
  GridPane.setConstraints(ex_kind_in_config_t, 1, 4)

  val le_kind_in_config = new Label("学习标签")
  val le_kind_in_config_t = new TextField {
    text = "学习"
    tooltip = "使用此处设置让程序在配置文件中寻找属于学习的题目"
  }
  GridPane.setConstraints(le_kind_in_config, 0, 5)
  GridPane.setConstraints(le_kind_in_config_t, 1,5)

  val post_kind_in_config = new Label("后测标签")
  val post_kind_in_config_t = new TextField {
    text = "后测"
    tooltip = "使用此处设置让程序在配置文件中寻找属于后测的题目"
  }
  GridPane.setConstraints(post_kind_in_config, 0, 6)
  GridPane.setConstraints(post_kind_in_config_t, 1,6)


  val conf_l = new Label("配置文件")
  val conf = new TextField {
    text = "config.yml"
    tooltip = "问卷及答案的配置文件名称，在自定义文件夹下"
  }
  GridPane.setConstraints(conf_l, 0 ,7)
  GridPane.setConstraints(conf, 1, 7)

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
  GridPane.setConstraints(cond_l, 0,8)
  GridPane.setConstraints(cond, 1,8)

  val title_font_size_l = new Label("标题字号")
  val title_font_size = new TextField {
    text = "40"
    tooltip = "请输入标题字体展示的字号大小"
  }
  GridPane.setConstraints(title_font_size_l, 0, 9)
  GridPane.setConstraints(title_font_size, 1, 9)

  val font_size_l = new Label("标准字号")
  val font_size = new TextField {
    text = "24"
    tooltip = "请输入标准字体展示的字号大小"
  }
  GridPane.setConstraints(font_size_l, 0, 10)
  GridPane.setConstraints(font_size, 1, 10)

  val imageWidth_t = new Label("图片宽度")
  val imageWidth = new TextField {
    text = "800"
    tooltip = "使用图片作为提示语的时候，图片的宽度，单位像素"
  }
  GridPane.setConstraints(imageWidth_t, 0, 11)
  GridPane.setConstraints(imageWidth, 1, 11)

  val showTitleName_t = new Label("问卷标题")
  val showTitleName = new CheckBox {
    text = "显示标题"
    tooltip = "是否显示问卷的标题"
    selected = false
  }
  GridPane.setConstraints(showTitleName_t, 0, 12)
  GridPane.setConstraints(showTitleName, 1, 12)

  val showExplainSize_t = new Label("问卷说明字体大小")
  val showExplainSize = new TextField {
    text = "28"
    tooltip = "问卷的说明 - 指导语字体大小"
  }
  GridPane.setConstraints(showExplainSize_t, 0 ,13)
  GridPane.setConstraints(showExplainSize, 1, 13)

  val queryVGap_t = new Label("问卷题目的行间距")
  val queryVGap = new TextField {
    text = "20"
    tooltip = "问卷题目的行间距"
  }
  GridPane.setConstraints(queryVGap_t, 0, 14)
  GridPane.setConstraints(queryVGap, 1, 14)

  val queryHGap_t = new Label("问卷每个选项间距 - 五个题目")
  val queryHGap = new TextField {
    text = "50"
    tooltip = "问卷题目的每个选项的间距 - 五个题目"
  }
  GridPane.setConstraints(queryHGap_t, 0, 15)
  GridPane.setConstraints(queryHGap, 1, 15)

  val queryHGap2_t = new Label("问卷每个选项间距 - 九个题目")
  val queryHGap2 = new TextField {
    text = "5"
    tooltip = "问卷题目的每个选项的间距（九个题目）"
  }
  GridPane.setConstraints(queryHGap2_t, 0, 16)
  GridPane.setConstraints(queryHGap2, 1, 16)

  val queryShowLine_t = new Label("问卷行线")
  val queryShowLine = new CheckBox {
    selected = true
    text <== when(selected) choose "显示行线" otherwise "不显示行线"
  }
  GridPane.setConstraints(queryShowLine_t, 0, 17)
  GridPane.setConstraints(queryShowLine, 1, 17)

  val questionHGap_t = new Label("问题答案间隔")
  val questionHGap = new TextField {
    text = "35"
    tooltip = "需要被试回答问题的四个答案之间的间隔"
  }
  GridPane.setConstraints(questionHGap_t, 0, 18)
  GridPane.setConstraints(questionHGap, 1, 18)

  val question_layout_margin = new Label(text = "问题布局边距像素")
  val pixel = new TextField {
    text = "200"
    tooltip = "当题目过宽，调节此处设置以更改题目两边边距"
  }
  GridPane.setConstraints(question_layout_margin, 0, 19)
  GridPane.setConstraints(pixel, 1,19)

  val help_padding_t = new Label("问卷回答提示向左偏移")
  val help_padding = new TextField {
    text = "100 50 50"
    tooltip = "三种问卷偏移使用空格隔开，依次是情绪问卷，代理问卷，动机问卷"
  }
  GridPane.setConstraints(help_padding_t, 0, 20)
  GridPane.setConstraints(help_padding, 1,20)

  val fullscreen = new CheckBox {
    text = "全屏显示"
    selected <== !checkDebug.selected
    disable = true
    tooltip = "选中后全屏显示程序，按 F11 退出"
  }
  val fullscreen_t = new Label("界面显示")
  GridPane.setConstraints(fullscreen_t, 0, 21)
  GridPane.setConstraints(fullscreen, 1, 21)

  val wrap_t = new Label("回答换行")
  val wrap = new TextField {
    text = "120"
    tooltip = "调节回答的换行程度，数值越小，越难换行"
  }
  GridPane.setConstraints(wrap_t, 0, 22)
  GridPane.setConstraints(wrap, 1, 22)

  val help_spacing_t = new Label("帮助文本间隔")
  val help_spacing = new TextField {
    text = "1"
    tooltip = "数值越大，帮助文本间隔越大"
  }
  GridPane.setConstraints(help_spacing_t, 0, 23)
  GridPane.setConstraints(help_spacing, 1, 23)



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
    WELCOME_INTRO = welcomePng.getText
    EX_KIND = ex_kind_in_config_t.getText
    NORMAL_KIND = le_kind_in_config_t.getText
    POST_KIND = post_kind_in_config_t.getText
    FOLER_PREFIX = folderInp.getText()
    QUESTION_LAYOUT_MARGIN = pixel.getText.toInt
    FONT_SIZE = font_size.getText.toInt
    IO.fromFile = conf.getText

    //新加
    TITLE_FONT_SIZE = title_font_size.getText.toInt
    FONT_SIZE = font_size.getText.toInt
    IMAGE_WIDTH = imageWidth.getText.toInt
    SU_SHOW_TITLE_NAME = showTitleName.isSelected
    SU_SHOW_EXPLAIN_SIZE = showExplainSize.getText.toInt
    SU_VGAP = queryVGap.getText.toInt
    SU_HGAP_5 = queryHGap.getText.toInt
    SU_HGAP_9 = queryHGap2.getText.toInt
    SU_SHOW_LINE = queryShowLine.isSelected
    QU_HGAP = questionHGap.getText.toInt
    EMOTION_IMAGE_WIDTH = emotion_png_width.getText.toInt
    HELP_PADDING_LEFT = help_padding.getText.split(" ").map(_.toInt)
    WRAP = wrap.getText.toInt
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
    stage.setFullScreen(fullscreen.isSelected)
    stage.show()
  }

  val ok = new Button("开始") {
    onAction = (_:ActionEvent) => checkAndLaunch()
  }

  val gridPane: GridPane = new GridPane {
    hgap = 20
    vgap = 10
    padding = Insets(20)
    children = Seq(debugLabel, folder_prefix, welcome_png, question_layout_margin, checkDebug, folderInp, welcomePng, pixel, cond, cond_l, conf, conf_l, fullscreen, fullscreen_t, ok, font_size_l, font_size,
      imageWidth_t, imageWidth, showTitleName_t, showTitleName, showExplainSize_t, showExplainSize, queryVGap_t, queryVGap, queryHGap_t, queryHGap, queryHGap2_t, queryHGap2, queryShowLine_t, queryShowLine, questionHGap_t, questionHGap, title_font_size_l, title_font_size, emotion_png_width, emotion_png_width_t, help_padding_t, help_padding, ex_kind_in_config_t, ex_kind_in_config, le_kind_in_config_t, le_kind_in_config, post_kind_in_config, post_kind_in_config_t, wrap_t, wrap, help_spacing_t, help_spacing)

  }

  val start = 24

  import XKExperiment._
  add("对话框上边距", dialogPaddingTop, start) {
    dialogPaddingTop = _
  }
  add("对话框右边距", dialogPaddingRight, start + 1) {
    dialogPaddingRight = _
  }
  add("对话框下边距", dialogPaddingBottom, start + 2) {
    dialogPaddingBottom = _
  }
  add("对话框左边距", dialogPaddingLeft, start + 3) {
    dialogPaddingLeft = _
  }
  add("文本上边距", textPaddingTop, start + 4) {
    textPaddingTop = _
  }
  add("文本右边距", textPaddingRight, start + 5) {
    textPaddingRight = _
  }
  add("文本下边距", textPaddingBottom, start + 6) {
    textPaddingBottom = _
  }
  add("文本左边距", textPaddingLeft, start + 7) {
    textPaddingLeft = _
  }
  add("解释上边距Image", explainImagePaddingTop, start + 8) {
    explainImagePaddingTop = _
  }
  add("解释右边距Image", explainImagePaddingRight, start + 9) {
    explainImagePaddingRight = _
  }
  add("解释下边距Image", explainImagePaddingBottom, start + 10) {
    explainImagePaddingBottom = _
  }
  add("解释左边距Image", explainImagePaddingLeft, start + 11) {
    explainImagePaddingLeft = _
  }
  add("解释上边距Text", explainTextPaddingTop, start + 12) {
    explainTextPaddingTop = _
  }
  add("解释右边距Text", explainTextPaddingRight, start + 13) {
    explainTextPaddingRight = _
  }
  add("解释下边距Text", explainTextPaddingBottom, start + 14) {
    explainTextPaddingBottom = _
  }
  add("解释左边距Text", explainTextPaddingLeft, start + 15) {
    explainTextPaddingLeft = _
  }
  GridPane.setConstraints(ok, 1, start + 16)

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



  stage = new application.JFXApp.PrimaryStage {
    title = "Configure - " + XKExperiment.version.split("\n").last.split(" ").head
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
