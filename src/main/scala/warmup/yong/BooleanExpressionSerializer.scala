package warmup.yong

import java.util

import com.google.gson.{Gson, JsonArray, JsonParser, JsonSyntaxException}

class BooleanExpressionSerializer {

  def isValidJson(json: String): Boolean = {
    try {
      new JsonParser().parse(json)
      return true
    } catch {
      case j: JsonSyntaxException => return false
    }

    false
  }

  def serialize(jsonArrayStr: String): util.ArrayList[BooleanExpression] = {
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

  def deserialize(list: List[BooleanExpression]): String = {
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

    return ""
  }

  def decode(json: String): BooleanExpression = {
    var s = json.replaceAll("^\\s+", "").trim()
    s match {
      case "true" => return True
      case "false" => return False
      case _ => {
        if (s.contains('&')) {
          val split = s.split('&')
          val e1 = decode(split(0))
          val e2 = decode(split(1))
          if (e1.isInstanceOf[Variable] || e2.isInstanceOf[Variable]) {
            return Variable(json)
          }

          return And(e1, e2)
        }

        if (json.contains('|')) {
          val split = json.split('|')
          val e1 = decode(split(0))
          val e2 = decode(split(1))
          if (e1.isInstanceOf[Variable] || e2.isInstanceOf[Variable]) {
            return Variable(json)
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

      return Variable(json)
    }

    Variable(json)
  }
}
