package com.mazhangjing.functional

import java.util.Optional
import java.util.concurrent.TimeUnit

package Chapter1 {

  import java.util.concurrent.TimeUnit

  import scala.util.Random

  class OldCoffeeShop {
    def coffeePay(coffees: Coffee*): Unit = {
      //count
      val sum = coffees.map(coffee => coffee.price).sum
      TimeUnit.SECONDS.sleep(1)
      //pay
      if (Random.nextInt(100) > 2) {
        println(s"Pay Done $sum.")
      } else {
        println("Pay Error")
        throw new RuntimeException("支付失败")
      }
    }
  }
  class NewCoffeeShop {
    def coffeePay(coffee: Coffee*): Unit = {
      //薄的外层，纯的内层
      val (_, payment) = CoffeeShop.getCoffeePayment(coffee:_*)
      payment.payIt()
    }
  }
  object CoffeeShop {
    def getCoffeePayment(coffees:Coffee*): (Array[Coffee], Payment) = {
      val all = coffees.toArray
      val sum = coffees.map(coffee => coffee.price).sum
      (all, new Payment(sum))
    }
  }
  class Coffee(val name:String = "NoName",
               val price:Double = 0.0)
  class Payment(val money:Double) {
    //外部接口，非函数式
    def payIt():Unit = {
      TimeUnit.SECONDS.sleep(1)
      if (Random.nextInt(100) > 2) {
        println(s"Pay Done $money.")
      } else {
        println("Pay Error")
        throw new RuntimeException("支付失败")
      }
    }
  }
}

//函数式编程简而言之，就是无副作用式编程，这里的无副作用并非指的表达什么样的程序，而是指的怎样写程序。即总是存在副作用，
//比如往 IO 流读写、网络读写、GUI 操作等。
//函数式编程更强调的是将带有副作用函数重构分离，撰写多个无副作用函数和较短的副作用脚本，实现一个纯的内核和很薄的外围，
//这样的话，对于大部分的无副作用函数，可以很方便的测试、复用、并行化、泛化和推导，减少 Bug。

//定义：什么是纯函数
/*
纯函数指的是，对于指定的一个函数类型 A => B，传入值 a，得到其结果 b，在既定的 a 的情况下，不论外部环境如何变化，这个函数
永远得到的是结果 b，这样的函数，称之为纯函数。
一个学术的定义：
对于程序 p，如果它包含的表达式 e 满足引用透明，即所有的 e 都可以替换为它的运算结果而不改变程序 p 的含义。假设存在一个函数 f
如果表达式 f(x) 对所有引用透明的表达式 x 也是引用透明的，那么 f 是一个纯函数。
例子：
1 + 1 == 2
"hello".reverse == olleh
val sb = new StringBuilder;
sb.append("Hello"); sb.toString == "Hello"
sb.append("Hello"); sb.toString == "HelloHello"
//和上面的表达式不等，因为 sb 已经发生了变化，虽然表达式还是一样，但是表达式并非在所有环境下得到同样的结果，即并非纯函数/引用透明的表达式

可以看到，函数式编程的一个问题在于，它拆分了函数，重构了程序结构，这是一种复杂化代码的方式，但是提升了程序语意的清晰性
在重构的时候，函数式思想让我们稍稍变通的拆分函数，试图去构建多个纯粹的函数和薄的外围副作用层。这样的优势在于什么呢？

1、纯函数组群形成的替代模型很容易推理程序的行为，而副作用让程序行为的推理变得困难，因为很简单，带有副作用的程序必须在特定外部环境下
才能得到一个预期的输入输出预测。而纯函数，或者说替代模型，很容易推理，因为对于运算的影响是局部的，函数没有状态一说，就像一个无状态的有着确定用途的黑盒子，
无状态性意味着可以很容易的不依赖外部状态进行测试，可以无拘束的模块化组合以复用程序，在并行、泛化程序中也容易 bug free 的使用。
 */

object AAC {
  def isSorted[A](as:Array[A], ordered: (A,A) => Boolean): Boolean = {
    for (i <- as.indices) {
      val now = as(i)
      if (i + 1 < as.length) {
        val next = as(i + 1)
        if (!ordered(now, next)) return false
      }
    }
    true
  }
  def fib(n:Int):Int = {
    def number(currentIndex:Int): Int = {
      if (currentIndex == 0) return 0
      if (currentIndex == 1) return 1
      number(currentIndex - 1) + number(currentIndex - 2)
    }
    number(n)
  }

  def main(args: Array[String]): Unit = {
    println(AAC.isSorted(Array(1,2,34,34,13,4,2), (a:Int, b:Int) => a < b))
    println(AAC.isSorted(Array(1,2,34,36,133), (a:Int, b:Int) => a < b))
    println(fib(4))
    println(P23.length(List(1,2,3)))
    println(P23.foldLeft(List(1,2,3),0)((start:Int, end:Int) => start + end))
    println(P23.map(List(1,2,3))(_ * 3))
    println(P23.flatMap(List(1,2,3,4,5))((a:Int) => List(a * a, a)))
  }
}

object P23 {
  def curry[A,B,C](f:(A,B) => C): A => B => C = {
    a:A => (b:B) => f(a,b)
  }
  def uncurry[A,B,C](f: A => B => C): (A, B) => C = {
    (a:A, b:B) => f(a)(b)
  }
  def compose[A,B,C](f: B => C, g: A => B): A => C = {
    a:A => f(g(a))
  }
  def length[A](as:List[A]):Int = {
    foldLeft(as, 0)((current, _) => 1 + current)
  }
  def foldLeft[A,B](as:List[A], z:B)(f:(B,A) => B):B = {
    as match {
      case Nil => z
      case head :: rest => foldLeft(rest, f(z, head))(f)
    }
  }
  def map[A,B](as:List[A])(f: A => B):List[B] = {
    foldLeft(as.reverse, List[B]())((now, next) => f(next) :: now)
  }
  def flatMap[A,B](as:List[A])(f: A => List[B]): List[B] = {
    foldLeft(as.tail, f(as.head))((current, next) => f(next) ++ current)
  }
}

sealed trait Maybe[+A] {

  def map[B](f: A => B): Maybe[B]
  def flatMap[B](f: A => Maybe[B]): Maybe[B]
  def getOrElse[B >: A](default: => B):B
  def orElse[B >: A](ob: => Maybe[B]): Maybe[B]
  def filter(f: A => Boolean): Maybe[A]

}
case class Have[+A](get:A) extends Maybe[A] {

  override def map[B](f: A => B): Maybe[B] = Have(f(get))

  override def flatMap[B](f: A => Maybe[B]): Maybe[B] = f(get) match {
    case Nope => Nope
    case res: Have[_] => res
  }

  override def getOrElse[B >: A](default: => B): B = get

  override def orElse[B >: A](ob: => Maybe[B]): Maybe[B] = this

  override def filter(f: A => Boolean): Maybe[A] = if (f(get)) this else Nope

}
case object Nope extends Maybe[Nothing] {

  override def map[B](f: Nothing => B): Maybe[B] = Nope

  override def flatMap[B](f: Nothing => Maybe[B]): Maybe[B] = Nope

  override def getOrElse[B >: Nothing](default: => B): B = default

  override def orElse[B >: Nothing](ob: => Maybe[B]): Maybe[B] = ob

  override def filter(f: Nothing => Boolean): Maybe[Nothing] = Nope

}

object P31 {
  def mean(xs:Seq[Double]): Maybe[Double] =
    if (xs.isEmpty) Nope
    else Have(xs.sum / xs.length)

  def mean2(xs:Seq[Double]): Both[String, Double] =
    if (xs.isEmpty) Left("Mean of empty list!")
    else Right(xs.sum / xs.length)

  def main(args: Array[String]): Unit = {
    mean(List(1,2,3)) match {
      case Nope =>
      case a: Have[_] => println(a.get) // or use Have(a) => print(a)
    }
    Try {
      1/0
    } match {
      case Nope => println("Nothing")
      case Have(a) => println(a)
    }

    println(mean2(List(1,2,3)))

    Try2 {
      1/0
    } match {
      case Left(i) => println(s"Error ${i.getMessage}")
      case Right(res) => println(s"Res is $res")
    }
  }

  def Try[A](a: => A): Maybe[A] = try Have(a) catch { case _: Exception => Nope}

  def Try2[A](a: => A): Both[Exception, A] = try Right(a) catch { case e: Exception => Left(e) }
}

sealed trait Both[+E, +A] {
  def map[B](f: A => B): Both[E,B]
  def flatMap[EE >: E, B](f: A => Both[EE, B]): Both[EE, B]
  def orElse[EE >: E, B >: A](b: Both[EE, B]): Both[EE, B]
}
case class Left[+E](value:E) extends Both[E, Nothing] {

  override def map[B](f: Nothing => B): Both[E, B] = Left[E](value)

  override def flatMap[EE >: E, B](f: Nothing => Both[EE, B]): Both[EE, B] = Left[E](value)

  override def orElse[EE >: E, B >: Nothing](b: Both[EE, B]): Both[EE, B] = b
}
case class Right[+A](value:A) extends Both[Nothing, A] {

  override def map[B](f: A => B): Both[Nothing, B] = Right(f(value))

  override def flatMap[EE >: Nothing, B](f: A => Both[EE, B]): Both[EE, B] = f(value)

  override def orElse[EE >: Nothing, B >: A](b: Both[EE, B]): Both[EE, B] = Right[A](value)
}

trait Rand {
  def nextInt: (Int, Rand)
}
case class RandImpl(seed:Long) extends Rand {
  override def nextInt: (Int, Rand) = {
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    val nextRand = RandImpl(newSeed)
    val n = (newSeed >> 16).toInt
    (n, nextRand)
  }
}
object Rander {
  var lastRand: Rand = RandImpl(42)
  def nextInt:Int = {
    val nowStatus = lastRand.nextInt
    lastRand = nowStatus._2
    nowStatus._1
  }
}

object Test {
  def main(args: Array[String]): Unit = {
    val r = RandImpl(233)
    val (int, rand) = r.nextInt
    println(int)
    println(rand.nextInt._1)
    1 to 10 foreach(_ => println(Rander.nextInt))
  }
}

package A {
  trait RNG {
    def nextInt: (Int, RNG)
  }
  class RNGImpl(val seed:Long) extends RNG {
    override def nextInt: (Int, RNG) = {
      val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
      val nextRand = new RNGImpl(newSeed)
      val n = (newSeed >> 16).toInt
      (n, nextRand)
    }
  }
  object RNGFactory {
    type Rand[+A] = RNG => (A, RNG)
    def nextInt(RNG: RNG): (Int, RNG) = RNG.nextInt
    def nextDouble: Rand[Double] = {
      rng: RNG => {
        val tup = rng.nextInt
        (tup._1.toDouble, tup._2)
      }
    }
  }
  object Test {
    def main(args: Array[String]): Unit = {
      1 to 10 foreach(_ => println(RNGFactory.nextInt(new RNGImpl(233))))
      1 to 10 foreach(_ => println(new RNGImpl(233).nextInt))
    }
  }
}


