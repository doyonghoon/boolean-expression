# Boolean Expressions

> encodes boolean expressions and also simplifies a given boolean expression algebraically.

This program takes a json string and returns a list of `BooleanExpression`s. For a bonus assignment, it provides a function to simplify a `BooleanExpression` so that it would ultimately output a `True` or `False` depending on the boolean expression given.

### Usage Example

1. How to decode (deserialization)

If the json is simply a primitive type itself, then you can decode the expression by following:

```scala
new BooleanExpressionSerializer().decode("true | true") === Or(True, True)
```

If the json is an array of expressions, then you would want to call `serialize(jsonArrayStr: String)`:

```scala
val json = "[\"true\",\"false\",\"true | false\",\"!false\"]"
new BooleanExpressionSerializer().deserialize(sample) === [True, False, Or(True, False), Not(False)]
```

2. How to encode (serialization)

To transform `BooleanExpression` to json `String`, you can call `encode(e: BooleanExpression)` as following:

```scala
new BooleanExpressionSerializer().encode(And(True, False)) === "true & false"
```

Or, you could also transform a list of `BooleanExpression`s into a json array string by calling as the following:

```scala
val json = "[\"true\",\"false\",\"true | false\",\"!false\"]"
new BooleanExpressionSerializer().serialize(sample) === [True, False, Or(True, False), Not(False)]
```

### Bonus Assignment

The following function simplifies the given boolean expression so that it will potentially produce a single boolean expression only if the given expression is recursively breakable.
Otherwise, it will just output `Variable(symbol: String)` as failing to parse the expression.

```scala
def simplify(e: BooleanExpression)
```

For example, the following will simply output `False`.
```scala
new BooleanExpressionSerializer().simplify(And(True, False) === False
```

# Author

Yong Hoon Do, yhdo@ucsd.edu
