package warmup.yong

import java.util

import com.google.gson.{Gson, JsonArray, JsonParser, JsonSyntaxException}

class BooleanExpressionSerializer {

  /**
    * Simplifes given BooleanExpression so that it will output a single expression at the end.
    *
    * @param e An expression to simplify
    * @return A resulting expression
    **/
  def simplify(e: BooleanExpression): BooleanExpression = {
    if (e == True || e == False) {
      return e
    }

    e match {
      case r: Not =>
        if (simplify(r.e) == True) return False else return True

      case r: And =>
        if (simplify(r.e1) == simplify(r.e2)) return True else return False

      case r: Or =>
        if (simplify(r.e1) == True || simplify(r.e2) == True) return True else return False

      case _: Variable =>
        return e
    }

    return e
  }

  /**
    * A helper function to determine if the given json format is valid.
    **/
  def isValidJson(json: String): Boolean = {
    try {
      new JsonParser().parse(json)
      return true
    } catch {
      case j: JsonSyntaxException => return false
    }

    false
  }

  /**
    * Deserializes a json array string into a list of BooleanExpression.
    *
    * @param jsonArrayStr A json array string.
    * @return a list of parsed BooleanExpression
    **/
  def deserialize(jsonArrayStr: String): util.ArrayList[BooleanExpression] = {
    val list = new util.ArrayList[BooleanExpression]
    if (!isValidJson(jsonArrayStr)) {
      throw new JsonSyntaxException("Invalid json is given")
    }

    val parser = new JsonParser()
    val jsonArray = parser.parse(jsonArrayStr)
    if (!jsonArray.isJsonArray) {
      return list
    }

    val arr: JsonArray = jsonArray.getAsJsonArray
    for (i <- 0 until arr.size()) {
      val item = arr.get(i)
      if (item.isJsonPrimitive) {
        val b = item.getAsString
        list.add(decode(b))
      }
    }


    list
  }

  /**
    * Transforms a list of BooleanExpressions into a json array string.
    *
    * @param list A list of BooleanExpressions
    * @return json array string
    **/
  def serialize(list: List[BooleanExpression]): String = {
    val arr = new util.ArrayList[String]
    for (i <- list.indices) {
      arr.add(encode(list(i)))
    }

    new Gson().toJson(arr)
  }

  def encode(e: BooleanExpression): String = {
    if (e == null) {
      return ""
    }

    if (e == True) {
      return "true"
    } else if (e == False) {
      return "false"
    }

    e match {
      case r: Variable =>
        return r.symbol
      case r: Not =>
        return "!" + encode(r.e)
      case r: Or =>
        return encode(r.e1) + " | " + encode(r.e2)
      case r: And =>
        return encode(r.e1) + " & " + encode(r.e2)
    }

    ""
  }

  /**
    * Parses the a JSON primitive string and returns a single BooleanExpression if it is in a valid format. A string value having
    * whitespaces in edges is still considered as a valid format. For example, "  true " -> "true" so that this is a
    * valid BooleanExpression. However, "t r u e" is in an invalid format of Boolean so that the resulting BooleanExpression
    * is Variable("t r u e").
    *
    * @param rawstr A candidate BooleanExpression
    * @return A BooleanExpression
    **/
  def decode(rawstr: String): BooleanExpression = {
    // let us consider a string containing extra whitespaces in edges still being in a valid format.
    val s = rawstr.replaceAll("^\\s+", "").trim()
    s match {
      case "true" => return True
      case "false" => return False
      case _ => {
        if (s.contains('&')) {
          val split = s.split('&')
          val e1 = decode(split(0))
          val e2 = decode(split(1))
          if (e1.isInstanceOf[Variable] || e2.isInstanceOf[Variable]) {
            return Variable(rawstr)
          }

          return And(e1, e2)
        }

        if (rawstr.contains('|')) {
          val split = rawstr.split('|')
          val e1 = decode(split(0))
          val e2 = decode(split(1))
          if (e1.isInstanceOf[Variable] || e2.isInstanceOf[Variable]) {
            return Variable(rawstr)
          }

          return Or(e1, e2)
        }
      }
    }

    if (s.startsWith("!")) {
      val b = decode(s.substring(1, s.length))
      if (b == True || b == False) {
        return Not(b)
      }

      return Variable(rawstr)
    }

    Variable(rawstr)
  }
}
