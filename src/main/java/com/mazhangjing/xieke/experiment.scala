package com.mazhangjing.xieke

import java.io.{File, FileReader, FileWriter, PrintWriter, StringWriter}
import java.nio.file.{Path, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util
import java.util.{Properties, List => JList, Map => JMap}

import com.mazhangjing.lab.LabUtils._
import com.mazhangjing.lab.{Experiment, Screen, ScreenAdaptor, Trial}
import com.mazhangjing.xieke.IO._
import com.mazhangjing.xieke.XKExperiment._
import com.mazhangjing.xieke.model.api.{AskCount, Condition, Emotion}
import com.mazhangjing.xieke.model.{Config, Context, Question, Survey}
import javafx.beans.property.{SimpleBooleanProperty, SimpleStringProperty}
import javafx.event.Event
import javafx.geometry.{HPos, Insets, Pos}
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.text.{Font, Text, TextAlignment, TextFlow}
import javafx.scene.{Node, Scene}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import PropertiesUtils.get

object PropertiesUtils {
  private val logger = LoggerFactory.getLogger(getClass)
  def load(from:Path): Properties = {
    try {
      val f = new File(from.toUri)
      val t = new Properties()
      val fr = new FileReader(f)
      t.load(fr)
      fr.close()
      t
    } catch {
      case e: Throwable => logger.warn(s"Can't load Properties from $from: $e"); null
    }
  }
  def get[T](key:String, default: T)(implicit properties: Properties): T = {
    if (properties == null) {
      logger.debug(s"Can't load Properties, Use Default $default")
      default
    } else {
      val res = properties.getProperty(key)
      if (res == null) {
        logger.debug(s"Can't load Resources with Key $key, Use Default $default")
        default
      } else {
        default match {
          case _ : Int => res.toInt.asInstanceOf[T]
          case _ : Long => res.toLong.asInstanceOf[T]
          case _ : Double => res.toDouble.asInstanceOf[T]
          case _ : Boolean => res.toBoolean.asInstanceOf[T]
          case _  => res.asInstanceOf[T]
        }
      }
    }
  }
}

object XKExperiment {
  implicit val default: Properties = PropertiesUtils.load(Paths.get("xieke/default.properties"))


  val version: String =
    """1.0.0 完成实验编写 @2019-09-02
      |1.0.1-DEMO 添加了许可证支持 @2019-09-03
      |2.0.0-DEMO 修改了问卷样式，添加了字体、图片和边距的自定义，约束了图片大小
      |2.0.3-DEMO 解决了情绪逻辑以及一些细节问题
      |2.0.4-DEMO 兼容 Windows 编码
      |3.0.0 添加了一个格式排版的问题，关闭 Auth
      |3.1.1 添加了图片位置细节调整，添加了年龄变量的收集
      |3.1.2 添加了解释图片的上、下左右边距处理
      |3.1.3 添加了伪造图片和解释文字的上下左右边距处理
      |3.1.4 2019年11月21日 使用 CSS 实现大部分样式，交换指导语和前测问卷位置，添加了许多图片指导语，添加了量表表述信息，修改了许多默认值，重新实现了 AgentPane
      |3.1.5 2019年11月21日 添加了量表对齐和换行处理
      |3.1.6 2019年11月22日 最终版本
      |3.1.7 2019年11月25日 添加了默认配置机制和PropertiesUtils
      |""".stripMargin
  var FONT_SIZE: Int = get("FONT_SIZE", 24)
  var IMAGE_WIDTH: Int = get("IMAGE_WIDTH", 1100)
  var EMOTION_IMAGE_WIDTH: Int = get("EMOTION_IMAGE_WIDTH", 300)
  var SU_SHOW_TITLE_NAME: Boolean = get("SU_SHOW_TITLE_NAME", false)
  var SU_VGAP: Int = get("SU_VGAP", 10)
  var SU_HGAP_5: Int = get("SU_HGAP_5", 50)
  var SU_HGAP_9: Int = get("SU_HGAP_9", 5)
  var SU_SHOW_LINE: Boolean = get("SU_SHOW_LINE", true)
  var QU_HGAP: Int = get("QU_HGAP",35)
  var HELP_PADDING_LEFT: Array[Int] = Array(
    get("HELP_PADDING_LEFT_1", 370),
    get("HELP_PADDING_LEFT_2", 200),
    get("HELP_PADDING_LEFT_3", 80))
  var HELP_TEXT_SPACING: Int = get("HELP_TEXT_SPACING", 2)
  var QUESTION_LAYOUT_MARGIN: Int = get("QUESTION_LAYOUT_MARGIN", 200)
  var AGENT_EXPLAIN_IMAGE_WIDTH: Int = get("AGENT_EXPLAIN_IMAGE_WIDTH", 700)

  var DEBUG: Boolean = get("DEBUG", false)
  var CHOOSED_CONDITION = Condition.NORMAL_SUMMARY
  val INFINITY = 100000000
  val EMOTION_SCALE = 0
  val AGENT_SCALE = 1
  val MOV_SCALE = 2
  val FEEDBACK_SCALE = 3
  val FOLDER_PREFIX = "xieke"
  val WELCOME_INTRO = "welcome.png"
  val EXERCISE_INTRO = "exercise_intro.png"
  val NORMAL_INTRO = "normal_intro.png"
  val AFTER_LEARN_INTRO = "after_learn_intro.png"
  val POST_QUIZ_INTRO = "post_quiz_intro.png"
  val ALL_END_INTRO = "all_end_intro.png"
  val NORMAL_IMAGE_NAME = "normal.png"
  val RIGHT_BORDER_IMAGE_NAME = "border_green.png"
  val WRONG_BORDER_IMAGE_NAME = "border_red.png"
  val EX_KIND = "练习"
  val NORMAL_KIND = "学习"
  val POST_KIND = "后测"

  val config: Config = IO.loadConfig()
  val result = new Data()
}

class XKExperiment extends Experiment {

  private[this] val logger = LoggerFactory.getLogger(classOf[XKExperiment])

  override protected def initExperiment(): Unit = {
    trials.add(new BasicTrial().initTrial())
  }

  override def saveData(): Unit = {
    try {
      logger.info("Saving Data now...")
      val resuData = result.output
      logger.info(resuData)
      logger.debug("Saving data to disk now...")
      val writer = new FileWriter(
        (if (result.id != null) result.id else LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)) + ".csv")
      writer.write(resuData)
      writer.close()
      logger.info("Saved to {id}.csv.")
    } catch {
      case e: Exception =>
        val sb = new StringWriter()
        val sw = new PrintWriter(sb)
        e.printStackTrace(sw)
        logger.warn("Save data error.")
        logger.warn(sb.toString)
    }
  }
}

class BasicTrial extends Trial {
  override def initTrial(): Trial = {
    //收集用户信息
    val demo = new DemoScreen().initScreen()
    screens.add(demo)
    //情绪前测
    val preEmotionSurvey = config.getSurveys.get(EMOTION_SCALE)
    val preEmotion = new SurveyScreen(preEmotionSurvey).initScreen()
    result.preEmotion = preEmotionSurvey
    screens.add(preEmotion)
    //欢迎界面
    val welcome = new ImageScreen(image = WELCOME_INTRO).initScreen()
    screens.add(welcome)
    //练习阶段
    val exIntro = new ImageScreen(image = EXERCISE_INTRO).initScreen()
    screens.add(exIntro)
    config.getQuestions.asScala.filter(_.getKind.contains(EX_KIND)).foreach(question => {
      val context = new Context()
      context.setQuestion(question)
      context.setCondition(CHOOSED_CONDITION)
      context.emotionChooses = config.getEmotionChooses
      val exQuest = new QuestionScreen(context = context).initScreen()
      screens.add(exQuest)
    })
    //学习阶段
    val normalIntro = new ImageScreen(image = NORMAL_INTRO).initScreen()
    screens.add(normalIntro)
    config.getQuestions.asScala.filter(_.getKind.contains(NORMAL_KIND)).foreach(question => {
      val context = new Context()
      context.setQuestion(question)
      context.setCondition(CHOOSED_CONDITION)
      context.emotionChooses = config.getEmotionChooses
      val quest = new QuestionScreen(context = context).initScreen()
      screens.add(quest)
      result.contexts.append(context)
    })
    //情绪后测指导语
    val afterLearnIntro = new ImageScreen(image = AFTER_LEARN_INTRO).initScreen()
    screens.add(afterLearnIntro)
    //情绪量表
    val postEmotionSurvey = config.getSurveys.get(EMOTION_SCALE).copy()
    val postEmotion = new SurveyScreen(postEmotionSurvey).initScreen()
    result.postEmotion = postEmotionSurvey
    screens.add(postEmotion)
    //教学代理量表
    val agentSurvey = config.getSurveys.get(AGENT_SCALE)
    val postAgent = new SurveyScreen(agentSurvey).initScreen()
    result.postAgent = agentSurvey
    screens.add(postAgent)
    //动机量表
    val postMotivationSurvey = config.getSurveys.get(MOV_SCALE)
    val moveAgent = new SurveyScreen(postMotivationSurvey).initScreen()
    result.postMotivation = postMotivationSurvey
    screens.add(moveAgent)
    //评价量表
    val clSurvey = config.getSurveys.get(FEEDBACK_SCALE)
    val postCL = new SurveyScreen(clSurvey).initScreen()
    result.postCL = clSurvey
    screens.add(postCL)
    //成绩后测
    val postIntro = new ImageScreen(image = POST_QUIZ_INTRO).initScreen()
    screens.add(postIntro)
    val questions = config.getQuestions.asScala.filter(_.getKind.contains(POST_KIND))
    questions.foreach(question => {
      val context = new Context()
      context.setQuestion(question)
      context.setCondition(CHOOSED_CONDITION)
      context.emotionChooses = config.getEmotionChooses
      val sc = new QuestionScreen(context = context, isPost = true).initScreen()
      screens.add(sc)
      result.postContexts.append(context)
    })
    //所有结束指导语
    val allEnd = new ImageScreen(image = ALL_END_INTRO).initScreen()
    screens.add(allEnd)
    this
  }
}

class DemoScreen extends ScreenAdaptor {

  override def initScreen(): Screen = {
    val id = new SimpleStringProperty()
    val gender = new SimpleStringProperty()
    val major = new SimpleStringProperty()
    val grand = new SimpleStringProperty()
    val age = new SimpleStringProperty()
    val pane = new BorderPane()
    implicit val form: GridPane = new GridPane()
    form.setAlignment(Pos.CENTER)

    labeledNodeInGrid("编号") {
      val idt = new TextField()
      id.bind(idt.textProperty())
      idt
    }(0)

    labeledNodeInGrid("性别") {
      val choice = new ChoiceBox[String]()
      choice.getItems.addAll("男","女")
      gender.bind(choice.valueProperty())
      choice
    }(1)

    labeledNodeInGrid("年龄") {
      val aget = new TextField()
      age.bind(aget.textProperty())
      aget
    }(2)

    labeledNodeInGrid("专业") {
      val majort = new TextField()
      major.bind(majort.textProperty())
      majort
    }(3)

    labeledNodeInGrid("年级") {
      val choice = new ChoiceBox[String]()
      choice.getItems.addAll(
        "大一","大二","大三","大四","研一","研二","研三","其他"
      )
      grand.bind(choice.valueProperty())
      choice
    }(4)

    val info = new Label("")
    info.setTextFill(Color.RED)
    form.addRow(5, info)
    GridPane.setMargin(info, new Insets(10,0,0,0))
    GridPane.setHalignment(info, HPos.LEFT)

    val ok = new Button("确定")
    ok.setMinWidth(70)
    GridPane.setMargin(ok, new Insets(20,0,0,0))
    GridPane.setHalignment(ok, HPos.LEFT)
    form.addRow(6, ok)

    ok.setOnAction(_ => {
      if (DEBUG) {
        logger.info(s"Collect Subject Information...")
        result.id = id.get()
        result.gender = gender.get()
        result.major = major.get()
        result.grand = grand.get()
        result.age = age.get()
        logger.debug(result.toString)
        goNextScreenSafe
      } else {
        if (id.get() == null || gender.get() == null ||
          major.get() == null || grand.get() == null)
          info.setText("您必须完成所有选项回答才可继续")
        else {
          logger.info(s"Collect Subject Information...")
          result.id = id.get()
          result.gender = gender.get()
          result.major = major.get()
          result.grand = grand.get()
          result.age = age.get()
          logger.debug(result.toString)
          goNextScreenSafe
        }
      }
    })

    val header = new Label("《心理统计》学习系统")
    header.getStyleClass.add("welcome_intro_text")
    header.setAlignment(Pos.CENTER)
    BorderPane.setAlignment(header, Pos.CENTER)
    BorderPane.setMargin(header, new Insets(150,0,0,0))
    pane.setTop(header)
    pane.setCenter(form)
    layout = pane
    duration = INFINITY
    this
  }

  override def eventHandler(event: Event, experiment: Experiment, scene: Scene): Unit = { }
}

class ImageScreen(val prefix:String = FOLDER_PREFIX,
                  val image:String) extends ScreenAdaptor {

  override def initScreen(): Screen = {
    val pane = new BorderPane()
    val view = new ImageView()
    view.setImage(new Image("file:" + Paths.get(prefix, image).toString))
    view.setFitWidth(IMAGE_WIDTH)
    view.setPreserveRatio(true)
    pane.setCenter(view)
    val ok = new Button("确定")
    ok.setMinSize(80,30)
    pane.setBottom(ok)
    BorderPane.setMargin(ok, new Insets(0,0,30,0))
    BorderPane.setAlignment(ok, Pos.CENTER)
    ok.setOnAction(_ => goNextScreenSafe)
    layout = pane
    duration = INFINITY
    this
  }

  override def eventHandler(event: Event, experiment: Experiment, scene: Scene): Unit = { }
}

class TextScreen(val textNode:Text, op: Text => Unit = null)
  extends ScreenAdaptor {

  override def initScreen(): Screen = {
    val pane = new BorderPane()
    pane.setCenter(textNode)
    if (op == null) {
      textNode.setLineSpacing(10)
      textNode.setTextAlignment(TextAlignment.CENTER)
    } else {
      op(textNode)
    }
    val ok = new Button("确定")
    ok.setMinSize(80,30)
    pane.setBottom(ok)
    BorderPane.setMargin(ok, new Insets(0,0,30,0))
    BorderPane.setAlignment(ok, Pos.CENTER)
    ok.setOnAction(_ => goNextScreenSafe)
    layout = pane
    duration = INFINITY
    this
  }

  override def eventHandler(event: Event, experiment: Experiment, scene: Scene): Unit = { }
}

class SurveyScreen(val survey:Survey) extends ScreenAdaptor {

  val surveyAnswer = new Array[Int](survey.getQuestions.size())

  val form = new GridPane()
  val outsideBox = new VBox()

  override def initScreen(): Screen = {
    val pane = new BorderPane()
    val sp = new ScrollPane()
    sp.getStyleClass.add("noborder")
    val titBox = new VBox()
    val help = new Label()
    val hbox = new HBox(help)
    hbox.setAlignment(Pos.BOTTOM_RIGHT)
    val dest = survey.getName match {
      case i if i.contains("情绪") => HELP_PADDING_LEFT(0)
      case p if p.contains("代理") => HELP_PADDING_LEFT(1)
      case q if q.contains("动机") => HELP_PADDING_LEFT(2)
      case _ => 0
    }
    hbox.setPadding(new Insets(0, dest, 0, 0))
    outsideBox.getChildren.addAll(titBox, hbox, sp)
    outsideBox.setAlignment(Pos.CENTER)

    sp.setStyle("-fx-focus-color: transparent;")
    sp.setFitToHeight(true)
    sp.setFitToWidth(true)
    sp.setVbarPolicy(ScrollBarPolicy.AS_NEEDED)
    sp.setHbarPolicy(ScrollBarPolicy.AS_NEEDED)

    //根据 survey.name 获取标题
    form.setStyle("-fx-focus-color: #0093ff;")
    form.setPadding(new Insets(10,0,30,0))
    form.setAlignment(Pos.CENTER)
    form.setVgap(SU_VGAP)
    form.setHgap(20)
    form.prefWidthProperty().addListener((_,_,n) => {
      println("Here", getScene.getWidth, n)
      val width = (getScene.getWidth - n.doubleValue())/2
      println(width)
      outsideBox.setPadding(new Insets(0,0,0,width))
    })

    val title = new Label(survey.getName)
    title.getStyleClass.add("title_text")
    title.setVisible(SU_SHOW_TITLE_NAME)
    //根据 survey.intro 获取介绍
    val intro = new TextFlow(new Text(survey.getIntro))
    intro.getStyleClass.add("survey_intro_text")
    titBox.getChildren.addAll(title, intro)
    titBox.setAlignment(Pos.CENTER)
    titBox.setSpacing(10)

    var MAXNUM_SPACING = (4, 15)
    val info = if (survey.getKind == AskCount.NINE) {
      MAXNUM_SPACING = (9, SU_HGAP_9)
      ""
    } else if (survey.getKind == AskCount.FIVE) {
      MAXNUM_SPACING = (5, SU_HGAP_5)
      survey.getQuestions.get(0).getChoose.asScala.mkString(" " * HELP_TEXT_SPACING)
    } else ""
    help.setFont(Font.font(FONT_SIZE))
    help.setText(info)

    //根据 survey.questions 获取题目
    var index = 2
    var questionIndex = 0
    survey.getQuestions.asScala.foreach(question => {
      val titLabel = new Label(question.getBody)
      titLabel.setFont(Font.font(FONT_SIZE))
      form.add(titLabel, 0, index)
      val tg = new ToggleGroup()
      tg.setUserData(questionIndex)
      val box = new HBox()
      box.setSpacing(MAXNUM_SPACING._2)

      //根据 survey.askCount 判断决定布局
      1 to MAXNUM_SPACING._1 foreach(i => {
        if (survey.getKind == AskCount.NINE) {
          if (i == 1) {
            //添加开头标签
            val t = new Label(question.getChoose.asScala.head)
            t.setFont(Font.font(FONT_SIZE))
            box.getChildren.add(t)
            addNormal(i)
          } else if (i == MAXNUM_SPACING._1) {
            //添加结尾标签
            addNormal(i)
            val t = new Label(question.getChoose.asScala.last)
            t.setFont(Font.font(FONT_SIZE))
            box.getChildren.add(t)
          } else {
            addNormal(i)
          }
        } else {
          addNormal(i)
        }
      })

      def addNormal(i:Int): Unit = {
        val rb = new RadioButton(i.toString)
        rb.setFont(Font.font(FONT_SIZE))
        rb.setUserData(i)
        tg.selectedToggleProperty().addListener((_,_,n) => {
          surveyAnswer(tg.getUserData.asInstanceOf[Int]) = n.getUserData.asInstanceOf[Int]
        })
        rb.setToggleGroup(tg)
        box.getChildren.add(rb)
      }

      GridPane.setFillWidth(box, true)
      box.setAlignment(Pos.CENTER)
      form.add(box, 1, index)
      val sep = new Separator()
      sep.setVisible(SU_SHOW_LINE)
      form.add(sep, 0, index + 1)
      GridPane.setConstraints(sep, 0, index + 1, 2, 1)
      index += 2
      questionIndex += 1
    })

    sp.setContent(form)

    val ok = new Button("确定")
    ok.setMinSize(80,30)
    BorderPane.setAlignment(ok, Pos.CENTER)
    BorderPane.setMargin(ok, new Insets(10,0,10,0))
    pane.setCenter(outsideBox)
    pane.setBottom(ok)
    ok.setOnAction(_ => checkAndGo())
    layout = pane
    duration = INFINITY
    this
  }

  def checkAndGo(): Unit = {
    if (DEBUG) goNextScreenSafe else {
      if (surveyAnswer.contains(0)) {
        logger.warn("没有完成问卷")
        val alert = new Alert(AlertType.WARNING)
        alert.setHeaderText(s"请完成小题 ${surveyAnswer.indexOf(0) + 1}")
        alert.setContentText("没有完成所有问卷选项，请完成后再提交")
        alert.show()
      } else {
        val res = new util.ArrayList[Integer]()
        surveyAnswer.foreach(i => res.add(i))
        survey.setChoices(res)
        logger.debug("Survey Data: " + surveyAnswer.mkString(", "))
        goNextScreenSafe
      }
    }
  }

  override def eventHandler(event: Event, experiment: Experiment, scene: Scene): Unit = { }
}

class QuestionScreen(val prefix:String = FOLDER_PREFIX,
                     val context:Context,
                     val isPost:Boolean = false) extends ScreenAdaptor {

  val question: Question = context.getQuestion
  var choosedAnswer: Int = -1
  var choosedEmotion: Emotion = _
  val disableQuestionToggleGroup = new SimpleBooleanProperty(false)

  val form = new GridPane()
  def getQuestionPane(question: Question): GridPane = {
    form.setVgap(20)
    form.setHgap(QU_HGAP)
    form.setAlignment(Pos.CENTER)

    //设置题目
    val title = new Label(question.getBody)
    title.setFont(Font.font(FONT_SIZE))
    title.setWrapText(true)
    form.add(title, 0,0,4,1)
    //设置答案
    val wrap = if (question.getChoose.get(0).length > 6) true else false
    val tg = new ToggleGroup()
    tg.selectedToggleProperty().addListener((_,_,n) => {
      val i = tg.getToggles.toArray().indexOf(n)
      choosedAnswer = i
      logger.debug(s"Index $i selected, record it.")
    })
    var index = 0
    question.getChoose.forEach(choose => {
      val t = new RadioButton(choose)
      t.disableProperty().bind(disableQuestionToggleGroup)
      t.setFont(Font.font(FONT_SIZE))
      t.setToggleGroup(tg)
      if (index > 1 && wrap) {
        form.add(t, index - 2, 2)
      } else {
        form.add(t, index, 1)
      }
      if (index == 3) index = 0 else index += 1
    })
    //form.setStyle("-fx-border-color: red")
    form
  }

  override def callWhenShowScreen(): Unit = {
    form.setMaxWidth(getScene.getWidth - QUESTION_LAYOUT_MARGIN)
  }

  def getEmotionPane(emotionChooses: JList[JMap[String,String]]): GridPane = {
    val form = new GridPane()
    form.setVgap(20)
    form.setHgap(20)
    form.setAlignment(Pos.CENTER)

    //设置题目
    val title = new Label("请选择最符合你此时此刻状态的情绪")
    title.setFont(Font.font(FONT_SIZE))
    title.setAlignment(Pos.CENTER)
    form.add(title, 0,0)
    //设置选项
    val tg = new ToggleGroup()
    tg.selectedToggleProperty().addListener((_,_,n) => {
      val emo = n.getUserData.asInstanceOf[String]
      choosedEmotion = new Emotion(emo)
    })
    val hb = new HBox()
    emotionChooses.forEach(choose => {
      val item = choose.asScala
      val rb = new RadioButton(item.head._1)
      rb.setUserData(item.head._1)
      rb.setFont(Font.font(FONT_SIZE))
      rb.setToggleGroup(tg)
      hb.getChildren.add(rb)
    })
    hb.setSpacing(QU_HGAP)
    hb.setAlignment(Pos.CENTER)
    form.add(hb, 0,1)
    //form.setStyle("-fx-border-color: black")
    form
  }

  def getAgentPane(condition: Condition, question: Question): Node = {

    val isRight = choosedAnswer == question.getRightChoose

    //当 Condition 类型为基本时，根据选择显示红或者绿的边框和标题
    //当 Condition 类型为详细时，根据选择显示红或者绿的边框、标题和解释
    val (text, borderImage, color) =
      if (isRight) ("回答正确",RIGHT_BORDER_IMAGE_NAME, Color.GREEN)
      else ("回答错误",WRONG_BORDER_IMAGE_NAME, Color.RED)

      val list = context.emotionChooses.asScala.find(map => map.containsKey(context.getEmotion.getKind)).get.get(context.getEmotion.getKind).split("---").map(_.trim)

    val (emotionImage, emotionTextGod, emotionTextBad) = (list(2), list(0), list(1))

    logger.debug(s"Use Emotion for User choosed ${context.getEmotion} and Picture: $emotionImage")
    val normalImage = NORMAL_IMAGE_NAME

    val (imageName, showExplain) =
      if (condition == Condition.NORMAL_SUMMARY) {
        (normalImage, false)
      } else if (condition == Condition.EMOTION_SUMMARY) {
        (emotionImage, false)
      } else if (condition == Condition.NORMAL_DETAIL) {
        (normalImage, true)
      } else if (condition == Condition.EMOTION_DETAIL) {
        (emotionImage, true)
      } else (emotionImage, true)

    val agentBox = new VBox()
    agentBox.getStyleClass.add("agentAndExplainBox")

    //绘制人像
    val agent = new ImageView(new Image("file:" + Paths.get(prefix, imageName).toString))
    agent.setFitWidth(EMOTION_IMAGE_WIDTH)
    agent.getStyleClass.add("agentImage")
    agent.setPreserveRatio(true)
    agentBox.getChildren.add(agent)

    //绘制对话框
    val explainBox = new VBox()
    explainBox.setMaxWidth(AGENT_EXPLAIN_IMAGE_WIDTH + 100)
    explainBox.getStyleClass.add("explainBox")
    if (isRight) explainBox.getStyleClass.add("explainBoxIfRight")
    else explainBox.getStyleClass.add("explainBoxIfWrong")
    agentBox.getChildren.add(explainBox)

    //绘制 Title
    val showTxt = if (CHOOSED_CONDITION == Condition.NORMAL_DETAIL || CHOOSED_CONDITION == Condition.NORMAL_SUMMARY) {
      text
    } else {
      text + "。" + (if (isRight) emotionTextGod else emotionTextBad)
    }
    val title = new Text(showTxt)
    title.setFill(color)
    val titleFlow = new TextFlow(title)
    titleFlow.getStyleClass.add("explainBoxHeader")
    explainBox.getChildren.add(titleFlow)

    if (showExplain) {
      if (question.getExplain.endsWith(".png") || question.getExplain.endsWith(".jpg")
        || question.getExplain.endsWith(".jpeg")) {
        //绘制图像
        val img = new Image("file:" + Paths.get(prefix, question.getExplain))
        val iv = new ImageView(img)
        iv.setFitWidth(AGENT_EXPLAIN_IMAGE_WIDTH)
        iv.setPreserveRatio(true)
        val ivBox = new HBox()
        ivBox.getChildren.add(iv)
        ivBox.getStyleClass.add("explainBoxContentIfIsImage")
        explainBox.getChildren.add(ivBox)
      } else {
        //绘制文本
        val la = new Text(question.getExplain)
        val laFlow = new TextFlow(la)
        laFlow.getStyleClass.add("explainBoxContentIfIsText")
        explainBox.getChildren.add(laFlow)
      }
    } else {
      //绘制 假图像
      val img = new Image("file:" + Paths.get(prefix, "fake_image.png"))
      val iv = new ImageView(img)
      iv.setFitWidth(AGENT_EXPLAIN_IMAGE_WIDTH)
      iv.setPreserveRatio(true)
      val ivBox = new HBox()
      ivBox.getChildren.add(iv)
      ivBox.getStyleClass.add("explainBoxContentIfIsImage")
      explainBox.getChildren.add(ivBox)
    }

    agentBox
  }

  var stageChanged = new SimpleStringProperty()
  stageChanged.addListener(_ => {
    if (stageChanged.get() == "0") {
      box.getChildren.clear()
      box.getChildren.add(questPane)
    } else if (stageChanged.get() == "1") {
      disableQuestionToggleGroup.set(true)
      box.getChildren.clear()
      box.getChildren.addAll(questPane, emotionQueryPane)
    } else if (stageChanged.get() == "2") {
      logger.debug("Timer Start now...")
      currentTime = System.currentTimeMillis()
      box.getChildren.clear()
      box.setAlignment(Pos.CENTER)
      box.setSpacing(0)
      box.getChildren.addAll(questPane, feedbackPane)
    }
  })

  val box = new VBox()
  val questPane: GridPane = getQuestionPane(question)
  val emotionQueryPane: GridPane = getEmotionPane(context.emotionChooses)
  lazy val feedbackPane: Node = getAgentPane(context.getCondition, question)

  var currentTime: Long = System.currentTimeMillis()

  override def initScreen(): Screen = {
    val pane = new BorderPane()
    box.setAlignment(Pos.CENTER)
    box.setSpacing(100)
    pane.setCenter(box)
    stageChanged.set("0")
    //翻页
    val ok = new Button("确定")
    ok.setMinSize(80,30)
    pane.setBottom(ok)
    BorderPane.setMargin(ok, new Insets(0,0,30,0))
    BorderPane.setAlignment(ok, Pos.CENTER)
    ok.setOnAction(_ => {
      chooseGo()
    })
    layout = pane
    duration = INFINITY
    this
  }

  def chooseGo(): Unit = {
    if (stageChanged.get() == "0") {
      if (choosedAnswer == -1) {
        val a = new Alert(AlertType.ERROR)
        a.setHeaderText("请选择答案！")
        a.show()
      } else {
        logger.info(s"Choosed answer $choosedAnswer")
        context.setAnswer(choosedAnswer)
        if (isPost) goNextScreenSafe
        else stageChanged.set("1")
      }
    } else if (stageChanged.get() == "1") {
      if (choosedEmotion == null) {
        val a = new Alert(AlertType.ERROR)
        a.setHeaderText("请选择情绪！")
        a.show()
      } else {
        logger.info(s"Choosed emotion $choosedEmotion")
        context.setEmotion(choosedEmotion)
        stageChanged.set("2")
      }
    } else if (stageChanged.get() == "2") {
      logger.info("Compute and record Time now...")
      context.setUseTime((System.currentTimeMillis() - currentTime).toInt)
      goNextScreenSafe
    }
  }

  override def eventHandler(event: Event, experiment: Experiment, scene: Scene): Unit = { }
}
