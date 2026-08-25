import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.Transient

@Serializable
class TestClass(
    @Transient var flag: Boolean = true
) {
    @SerialName("testProp")
    val testProp: JsonElement
        get() = JsonPrimitive(flag)
}

fun main() {
    val t = TestClass()
    println(Json.encodeToString(TestClass.serializer(), t))
}
