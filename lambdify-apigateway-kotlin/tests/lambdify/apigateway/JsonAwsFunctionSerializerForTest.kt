package lambdify.apigateway

import com.fasterxml.jackson.jr.ob.JSON
import lambdify.core.AwsFunctionSerializer
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 *
 */
class JsonAwsFunctionSerializerForTest: AwsFunctionSerializer {

    override fun <T : Any?> deserialize( input: ByteArray, type: Class<T> ) =
        JSON.std.beanFrom(type, input)

    override fun serialize(unserializedBody: Any?) =
        ByteArrayOutputStream().apply {
            JSON.std.write(unserializedBody, this)
        }.toByteArray()
}