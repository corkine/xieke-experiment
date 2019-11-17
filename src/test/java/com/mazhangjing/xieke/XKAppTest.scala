package com.mazhangjing.xieke

import org.scalatest.FunSuite

class XKAppTest extends FunSuite {

  test("testIsTimeOk") {
    val value = XKApp.isTimeOk
    println(value)

    val va = XKExperiment.version.split("\n").last.split(" ").head
    println(va)
  }

}
