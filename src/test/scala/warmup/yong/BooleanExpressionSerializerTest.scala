package warmup.yong

import org.scalatest.FunSuite

class BooleanExpressionSerializerTest extends FunSuite {

  test("Decodes valid formats in various combinations of the expression") {
    assert(new BooleanExpressionSerializer().decode("true | true") === Or(True, True))
    assert(new BooleanExpressionSerializer().decode("true | true") === Or(True, True))
    assert(new BooleanExpressionSerializer().decode("false | true") === Or(False, True))
    assert(new BooleanExpressionSerializer().decode("true | false") === Or(True, False))
    assert(new BooleanExpressionSerializer().decode("false | false") === Or(False, False))

    assert(new BooleanExpressionSerializer().decode("true & true") === And(True, True))
    assert(new BooleanExpressionSerializer().decode("false & true") === And(False, True))
    assert(new BooleanExpressionSerializer().decode("true & false") === And(True, False))
    assert(new BooleanExpressionSerializer().decode("false & false") === And(False, False))

    assert(new BooleanExpressionSerializer().decode("!true & false") === And(Not(True), False))
    assert(new BooleanExpressionSerializer().decode("!false & false") === And(Not(False), False))
    assert(new BooleanExpressionSerializer().decode("!true & !false") === And(Not(True), Not(False)))
    assert(new BooleanExpressionSerializer().decode("!true & !true") === And(Not(True), Not(True)))
    assert(new BooleanExpressionSerializer().decode("!false & !false") === And(Not(False), Not(False)))
  }

  test("Encodes BooleanExpressions") {
    assert(new BooleanExpressionSerializer().encode(True) === "true")
    assert(new BooleanExpressionSerializer().encode(False) === "false")
    assert(new BooleanExpressionSerializer().encode(And(True, False)) === "true & false")
    assert(new BooleanExpressionSerializer().encode(Or(True, Not(False))) === "true | !false")


  }

  test("Handles extra whitespaces") {
    assert(new BooleanExpressionSerializer().decode(" false  ") === False)
    assert(new BooleanExpressionSerializer().decode(" trUe  ") === Variable(" trUe  "))
    assert(new BooleanExpressionSerializer().decode(" False\t") === Variable(" False\t"))
    assert(new BooleanExpressionSerializer().decode("!false|!true") === Or(Not(False), Not(True)))
  }

  test("Deserializes json string into BooleanExpression") {
    val json = "[\"true\", \"false\", \"true|false\", !TRUE&true]"
    val expectedOutput = List(True, False, Or(True, False), Variable("!TRUE&true"))
    val actualOutput = new BooleanExpressionSerializer().deserialize(json)
    assert(expectedOutput.size === actualOutput.size)
    for (i <- 0 until expectedOutput.size - 1) {
      assert(expectedOutput( i ) === actualOutput.get( i ))
    }

  }

  test("Serializes BooleanExpressions into json string") {
    val json = "[\"true\",\"false\",\"true | false\",\"!false\"]"
    val sample = List(True, False, Or(True, False), Not(False))
    val output = new BooleanExpressionSerializer().serialize(sample)
    assert(json === output)
  }

  test("Simplifies BooleanExpressions") {
    assert(new BooleanExpressionSerializer().simplify(Or(Not(True), False)) === False)
    assert(new BooleanExpressionSerializer().simplify(And(True, False)) === False)
    assert(new BooleanExpressionSerializer().simplify(Or(Not(True), True)) === True)
    assert(new BooleanExpressionSerializer().simplify(Or(Not(And(True, False)), False)) === True)
    assert(new BooleanExpressionSerializer().simplify(Or(Not(And(True, False)), Or(And(False, Not(False)), Not(False)))) === True)
    assert(new BooleanExpressionSerializer().simplify(Variable("!hello")) === Variable("!hello"))
  }
}