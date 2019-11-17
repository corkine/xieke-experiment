package com.mazhangjing.xieke

import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.scalatest.FunSuite

import scala.io.Source

class IOTest extends FunSuite {

  test("testLoadConfig") {
    val config = IO.loadConfig()
    print(config)
    assert(config != null, "Config 应该可以顺利加载")
    assert(config.getEmotionChooses != null, "EmotionChooses 不应该为空")
    assert(config.getQuestions != null, "Questions 不应该为空")
    assert(config.getSurveys != null, "Surveys 不应该为空")
  }

  test("IO") {
    val writer = new FileWriter("result.log")
    writer.write("Hello world")
    writer.close()
  }

  test("Run") {
    val buffer = Source.fromFile("data2vmrk-4.txt").getLines().toBuffer
    print(buffer.size)
  }

  test("format") {
    val str = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
    println(str)
  }

}
