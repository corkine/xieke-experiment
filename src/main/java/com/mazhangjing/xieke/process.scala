package com.mazhangjing.xieke

import com.mazhangjing.xieke.model.api.Condition
import com.mazhangjing.xieke.model.{Config, Context, Survey}
import javafx.geometry.{Insets, Pos}
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.{GridPane, HBox}
import org.yaml.snakeyaml.Yaml

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object IO {

  var fromFile = "config.yml"

  def loadConfig(from:String = fromFile): Config = {
    val file = Source.fromFile(fromFile, "utf-8")
    val config = new Yaml().loadAs(file.mkString, classOf[Config])
    file.close()
    config
  }

  def labeledNode(labelText:String = null)(getParent: => Node): HBox = {
    val box = new HBox()
    box.setAlignment(Pos.BASELINE_LEFT)
    box.setSpacing(20)
    box.setPadding(new Insets(0,0,10,0))
    val label = new Label(labelText)
    box.getChildren.add(label)
    box.getChildren.add(getParent)
    box
  }

  def labeledNode(getLabel: => Label)(getParent: => Node): HBox = {
    val box = new HBox()
    box.setAlignment(Pos.BASELINE_LEFT)
    box.setSpacing(20)
    box.setPadding(new Insets(0,0,10,0))
    box.getChildren.add(getLabel)
    box.getChildren.add(getParent)
    box
  }

  def labeledNodeInGrid(labelText:String)(getParent: => Node)
                       (rawIndex: Int)(implicit grid: GridPane): Unit = {
    val box = new HBox()
    box.setAlignment(Pos.BASELINE_LEFT)
    box.setSpacing(20)
    box.setPadding(new Insets(0,0,10,0))
    val label = new Label(labelText)
    box.getChildren.add(label)
    box.getChildren.add(getParent)
    grid.addRow(rawIndex, box)
  }

  def labeledNodeInGrid(getLabel: => Label)(getParent: => Node)
                       (rawIndex: Int)(implicit grid: GridPane): Unit = {
    val box = new HBox()
    box.setAlignment(Pos.BASELINE_LEFT)
    box.setSpacing(20)
    box.setPadding(new Insets(0,0,10,0))
    box.getChildren.add(getLabel)
    box.getChildren.add(getParent)
    grid.addRow(rawIndex, box)
  }
}

class Data {
  var id:String = _
  var gender:String = _
  var age:String = _
  var major:String = _
  var grand:String = _
  var condition: Condition = _

  //前测问卷
  var preEmotion:Survey = _
  //测试成绩
  var contexts: ArrayBuffer[Context] = new ArrayBuffer[Context]()
  //后测问卷
  var postEmotion:Survey = _
  var postAgent:Survey = _
  var postMotivation:Survey = _
  var postCL:Survey = _
  //后测成绩
  var postContexts: ArrayBuffer[Context] = new ArrayBuffer[Context]()

  def output: String = {
    //number, gender, age, major, grade 5
    //preEmotion1-18 1
    //choice1-10, performance1-10, performanceSum, emotion1-10, time1-10, timeAve * 4Condition 24
    //postEmotion1-18,postAgent1-26,postMotivation1-11,postMotivationAve,postCL1-3,
    //postChoice1-10,postPerformance1-10,postPerformanceSum 8
    //4 + 1 + 24 + 8 = 37列
    val sb = new StringBuilder
    //基本信息
    sb.append(id).append(", ")
      .append(gender).append(", ")
      .append(age).append(", ")
      .append(major).append(", ")
      .append(grand).append(", ") //number, gender, major, grade

    //前测信息
    val pre = preEmotion.getChoices.asScala.map(_ + 1).mkString(" ")
    sb.append(pre).append(", ") //preEmotion1-18

    //学习信息 6
    val choices = contexts.map(context => context.getAnswer + 1).mkString(" ")
    sb.append(choices).append(", ") //choice1-10
    val performances = contexts.map(context => {
      if (context.getAnswer == context.getQuestion.getRightChoose) 1 else 0
    })
    sb.append(performances.mkString(" ")).append(", ") //performance1-10
    val performanceSum = performances.sum
    sb.append(performanceSum).append(", ") //performanceSum
    val emotions = contexts.map(context => context.getEmotion.getKind).mkString(" ")
    sb.append(emotions).append(", ") //emotion1-10
    val times = contexts.map(context => context.getUseTime)
    sb.append(times.mkString(" ")).append(", ") //time1-10
    sb.append(times.map(i => i.intValue()).sum * 1.0/times.length).append(", ") //timeAve

    //后测问卷
    //postEmotion
    val pEString = postEmotion.getChoices.asScala.map(_ + 1).mkString(" ")
    sb.append(pEString).append(", ")
    //postAgent
    val pAString = postAgent.getChoices.asScala.map(_ + 1).mkString(" ")
    sb.append(pAString).append(", ")
    //postMotivation
    val pMList = postMotivation.getChoices.asScala.map(_ + 1)
    sb.append(pMList.mkString(" ")).append(", ")
    //postMotivationAve
    sb.append(pMList.sum * 1.0 / pMList.size).append(", ")
    //postCL
    val pCString = postCL.getChoices.asScala.map(_ + 1).mkString(" ")
    sb.append(pCString).append(", ")

    //后测成绩
    val postScoreString = postContexts.map(context => context.getAnswer + 1).mkString(" ")
    sb.append(postScoreString).append(", ") //postScore
    val postPerformance = postContexts.map(context => {
      if (context.getAnswer == context.getQuestion.getRightChoose) 1 else 0
    })
    sb.append(postPerformance.mkString(" ")).append(", ") //postPerformance
    sb.append(postPerformance.sum * 1.0 / postPerformance.size)//postPerformanceAve

    sb.toString()
  }

  override def toString: String = s"[Data] $id - $gender"
}


