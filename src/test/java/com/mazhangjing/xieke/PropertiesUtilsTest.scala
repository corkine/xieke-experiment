package com.mazhangjing.xieke

import java.nio.file.Paths

import org.scalatest.FunSuite

class PropertiesUtilsTest extends FunSuite {

  implicit val properties = PropertiesUtils.load(Paths.get(getClass.getClassLoader.getResource("test/test.properties").toURI))

  test("testLoad") {
    val properties = PropertiesUtils.load(Paths.get(getClass.getClassLoader.getResource("test/test.properties").toURI))
    println(properties)
  }

  test("testGet") {
    val str = PropertiesUtils.get("name", "Marvin")
    println(str)
  }

  test("testGet 2") {
    val str = PropertiesUtils.get("name2", "Marvin")
    println(str)
    assert(str === "Marvin")
  }

  test("testGet 3") {
    val str = PropertiesUtils.get("age4", 43)
    println(str)
    assert(str === 43)
  }

  test("testGet 4") {
    val str = PropertiesUtils.get("age5", 43.2)
    println(str)
    assert(str === 43.2)
  }

  test("testGet 5") {
    val str = PropertiesUtils.get("age8", true)
    println(str)
    assert(str === true)
  }

}
