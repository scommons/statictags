# Static Tags
[![Build Status](https://travis-ci.com/scommons/statictags.svg?branch=master)](https://travis-ci.com/scommons/statictags)
[![scala-index](https://index.scala-lang.org/scommons/statictags/statictags/latest.svg)](https://index.scala-lang.org/scommons/statictags/statictags)
[![Scala.js 0.6](https://www.scala-js.org/assets/badges/scalajs-0.6.29.svg)](https://www.scala-js.org)
[![Scala.js 1.0](https://www.scala-js.org/assets/badges/scalajs-1.1.0.svg)](https://www.scala-js.org)

|Platform|Artifact|Scala Version|Scala JS Version|
|---|---|---|---|
|JVM|```"org.scommons.shogowada" %% "statictags" % "2.6.1"```|2.12, 2.13|NA|
|JS|```"org.scommons.shogowada" %%% "statictags" % "2.6.1"```|2.12, 2.13|0.6.29+, 1.1.0+|

Static Tags makes it easy for you to write HTML in Scala.

- [Examples](#examples)
- [Step by Step](#step-by-step)
- [Notable Features](#notable-features)
- [Extending Static Tags](#extending-static-tags)

## Examples

All Static Tags element can be converted to HTML via ```toString``` method.

```scala
import io.github.shogowada.statictags.StaticTags._

class ToUpperCase(text: String) {
  override def toString: String = text.toUpperCase
}

val example = <.div(^.id := "example")(
  <.p(^.`class` := Seq("main-paragraph", "main-paragraph-bold"))("This is a paragraph."),
  "This is a text.",
  new ToUpperCase("Anything other than Static Tags element is converted to string.")
)

println(example)
```

The above code will output the minified version of the following HTML.

```html
<div id="example">
  <p class="main-paragraph main-paragraph-bold">This is a paragraph.</p>
  This is a text.
  ANYTHING OTHER THAN STATIC TAGS ELEMENT IS CONVERTED TO STRING.
</div>
```

Note that when you use Static Tags, for example, you don't need to worry if the ```class``` attribute value was space delimited or comma delimited. You can just give it a collection of strings, and Static Tags takes care the rest for you. This is one of many advantages of using Static Tags!

## Step by Step

1. Import Static Tags.
    - ```import io.github.shogowada.statictags.StaticTags._```
2. Start with ```<``` to write element.
    - ```<.div```
    - Think of ```<``` as the opening of standard tags (E.g. ```<div>```).
3. Start with ```^``` to write attributes and pass it to the first parameter group.
    - ```<.div(^.id := "foo")```
4. Pass child elements to the second parameter group.
    - ```<.div(^.id := "foo")("bar")```

## Notable Features

### Flattening attributes and elements

```scala
<.div()(
  "When the element is an option,",
  None,
  Some("it will be flattened."),
  Seq(
    "Elements in sequence",
    "will be flattened too."
  )
)
```
is equlvalent of
```scala
<.div()(
  "When the element is an option,",
  "it will be flattened.",
  "Elements in sequence",
  "will be flattened too."
)
```

You can do the same for attributes.

### Dynamically writing elements and attributes

You can dynamically write elements and attributes by using `<(String)` for elements and `^(String)` for attributes.

```scala
<("foo")(
  ^("a") := "A",
  ^("b") := true,
  ^("c") := false
)()
```
```html
<foo a="A" b></foo>
```

However, if it is a custom attribute that's specific to your application, we'd recommend [extending Static Tags](#extending-static-tags) so that you get full benefit of the Scala's strong type system.

If it is a standard element or attribute that's missing in the library, we'd appreciate if you could [create an issue](https://github.com/shogowada/statictags/issues) or PR.

### Static "type" attribute values

All the standard "type" attribute values are defined. You can assess them like this:

```scala
<.input(^.`type`.password)()
```

It will construct the following HTML:

```html
<input type="password">
```

### Static media types

All the standard media types are defined at `MediaTypes` object.

Example:
```scala
import io.github.shogowada.statictags.MediaTypes

MediaTypes.`application/json`
```

## Extending Static Tags

You can add your own elements and attributes, as well as your own deserializer. And it's super easy!

```scala
case class MyElementWrapper(element: Element)

object MyStaticTags extends StaticTags {

  class MyElements extends Elements {
    lazy val myElement = ElementSpec(name = "myElement")
  }

  class MyAttributes extends Attributes {

    case class MyAttributeSpec(name: String) extends AttributeSpec {
      def :=(value: Int) = { // Create an attribute with := operator
        Attribute[Int](name = name, value = value)
      }

      lazy val one = this := 1 // Or have an attribute as constant

      def sumOf(lhs: Int, rhs: Int) = { // Or create an attribute with custom function
        this := (lhs + rhs)
      }
    }

    lazy val myAttribute = MyAttributeSpec("myAttribute")
  }

  override val < = new MyElements
  override val ^ = new MyAttributes

  implicit def asMyElementWrapper(element: Element): MyElementWrapper = {
    // You can implicitly convert it into whatever you want!
    MyElementWrapper(element)
  }
}
```

If you had code like above, you can use it like below.

```scala
import MyStaticTags._ // This imports all of your custom code, including implicit conversion

val element = <.div(
  ^.myAttribute.one
)(
  <.myElement(
    ^.`class` := Seq("my-element"),
    ^.myAttribute := 2
  )(
    <.p(
      ^.myAttribute.sumOf(1, 2)
    )("How easy it is to extend the StaticTags!")
  )
)

println(element) // Use it as HTML string

val myElementWrapper: MyElementWrapper = element // Use it as your custom element
```

If you want to create an add-on to Static Tags instead of building something on top of it, you can create an implicit class of ```Elements``` and ```Attributes``` too.
