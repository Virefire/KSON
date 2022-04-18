# KSON - Kotlin JSON library based on GSON

![Open issues](https://img.shields.io/github/issues-raw/Rikonardo/KiORM)

💼 **This readme contains full library documentation/tutorial!**

## Install

Gradle Kotlin:
```kotlin
repositories {
    maven {
        url = uri("https://maven.rikonardo.com/releases")
    }
}

dependencies {
    implementation("dev.virefire.kson:KSON:1.0.0")
}
```

## Documentation

| Content                                |
|----------------------------------------|
| **1. [Serializing](#serializing)**     |
| **2. [Deserializing](#deserializing)** |
| **3. [Exceptions](#exceptions)**       |

### Serializing
KSON has similar API as JavaScript. For example, to serialize a Kotlin map to a JSON object, you can use the following code:
```kotlin
fun main() {
    val json = KSON.stringify(mapOf(
        "name" to "John",
        "age" to 30,
        "isDeveloper" to true,
        "address" to mapOf(
            "city" to "New York",
            "country" to "USA"
        )
    ))
    println(json)
}
```
But there are another way to get JSON. You can serialize whole objects instead of just maps:
```kotlin
fun main() {
    val person = Person(
        name = "John",
        age = 30,
        isDeveloper = true,
        address = Address(
            city = "New York",
            country = "USA"
        )
    )
    val json = KSON.stringify(person)
    println(json)
}
```
This also looks great! But we can serialize even an anonymous object:
```kotlin
fun main() {
    val json = KSON.stringify(object {
        val name = "John"
        val age = 30
        val isDeveloper = true
        val address = Address(
            city = "New York",
            country = "USA"
        )
    })
    println(json)
}
```
If you serialize an object or class instance, you can control the serialization process by using som annotations:
```kotlin
fun main() {
    val json = KSON.stringify(object {
        val name = "John"
        val age = 30
        @JsonName("is_developer")
        val isDeveloper = true
        val address = Address(
            city = "New York",
            country = "USA"
        )
        @Hidden
        val password = "123456"
        @HideIfNull
        val optional = null
    })
    println(json)
}
```
`@JsonName` is used to change the name of the field. `@Hidden` is used to hide the field. `@HideIfNull` is used to hide the field if the value is null (by default null values are serializing to JSON null).

You can also get beautified JSON with indentation:
```kotlin
fun main() {
    val json = KSON.stringify(object {
        val name = "John"
        val age = 30
        val isDeveloper = true
        val address = Address(
            city = "New York",
            country = "USA"
        )
    }, " ".repeat(4)) // Set indentation to 4 spaces
    println(json)
}
```

### Deserializing
KSON also has simple deserialization API. It doesn't completely match the way we doing it in JavaScript, but it's much more convenient, than default GSON API.
```kotlin
fun main() {
    val json = """
        {
            "name": "John",
            "age": 30,
            "isDeveloper": true,
            "address": {
                "city": "New York",
                "country": "USA"
            },
            "hobbies": [
                "Programming",
                "Reading",
                "Coding"
            ],
            "friends": [
                {
                    "name": "Jane",
                    "age": 30,
                    "isDeveloper": true,
                    "address": {
                        "city": "New York",
                        "country": "USA"
                    }
                },
                {
                    "name": "Bob",
                    "age": 30,
                    "isDeveloper": true,
                    "address": {
                        "city": "New York",
                        "country": "USA"
                    }
                }
            ],
            "optional": null
        }
    """.trimIndent()
    val json = KSON.parse(json)
    println(json["name"].string) // John
    println(json["age"].int) // 30
    println(json["isDeveloper"].boolean) // true
    println(json["address"]["city"].string) // New York
    println(json["hobbies"][0].string) // Programming
    for (friend in json["friends"].list) {
        println(friend["name"].string) // Jane, Bob
    }
    println(json["optional"].isNull) // true
    println(json["optional"].string) // null
    println(json["age"].isNumber) // true
    println(json["age"].isString) // false
}
```

### Exceptions
There are two exceptions that can be thrown by KSON:
`JsonElementNotFoundException` and `JsonTypeMismatchException`.

`JsonElementNotFoundException` is thrown when you try to access a field that doesn't exist.
```kotlin
fun main() {
    val json = KSON.parse("""{"name": "John"}""")
    try {
        println(json["age"].int) // throws JsonElementNotFoundException
    } catch (e: JsonElementNotFoundException) {
        println("JsonElementNotFoundException")
    }
}
```
`JsonTypeMismatchException` is thrown when you try to access a field that has wrong type.
```kotlin
fun main() {
    val json = KSON.parse("""{"name": "John"}""")
    try {
        println(json["name"].int) // throws JsonTypeMismatchException
    } catch (e: JsonTypeMismatchException) {
        println("JsonTypeMismatchException")
    }
}
```