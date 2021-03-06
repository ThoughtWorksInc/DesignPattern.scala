# Type Classes are Plain Old Factory Design Patterns

Type class, the polymorphic type system in Haskell, is not natively available in Java and most of other object-oriented languages.

Some Scala libraries mimic type classes via implicit parameters. This approach is able to represent higher kinded type classes like `Monad`, multi-parameter type classes like `MonadError`, and dependent type classes like operations on heterogenous lists.

However, the implicit parameter approach has some weaknesses in comparison to Haskell's type class.

1. Slow compilation
2. Large byte code generation
3. Bad performance
4. Huge number of lines of boilerplate code
5. Messing up of API documentation
6. Obscure type checking error message
7. Broken Java interoperability

We discovered a new object-oriented type class representation that resolves all the above issues. Our type classes are abstract factories, described in the book *Design Patterns*. Our factories are composable, thus different types of monad transformers can be modeled as different [compositions of factory objects](https://github.com/ThoughtWorksInc/DesignPattern.scala/blob/c8a1c69/benchmark-asyncio-designpattern/src/main/scala/com/thoughtworks/designpattern/benchmark/asyncio/designpattern.scala#L12).

We have implemented some examples of factory pattern monad type classes in both Java and Scala. The capability of both versions are no less than Haskell's type class, though the Java version is not 100% type safe due to lack of the feature of higher kinded type parameter. Fortunately the type safety problem merely affects the users of type classes. There is only one type casting in [our use case](https://github.com/ThoughtWorksInc/DesignPattern.scala/blob/master/lite/src/test/java/com/thoughtworks/designpattern/lite/TaskTest.java), which contains more than one hundrend lines of code.

Apart from the monad type class examples, we had applied the similar approach in the [plugin system](http://deeplearning.thoughtworks.school/plugins) of [DeepLearning.scala](http://deeplearning.thoughtworks.school), where we mix-in factories instead of compositing them. The mix-in-able factories can even resolve problems that are difficult to handle with Haskell's type classes.
