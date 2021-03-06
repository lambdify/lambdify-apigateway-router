package lambdify.apigateway;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.val;

@Accessors(fluent = true)
public class PlainTextSerializer implements Serializer {

    @Getter final String contentType = "text/plain";
    @Getter final boolean candidateToBeDefaultSerializer = false;

    @Override
    public Stringified toString(Object unserializedBody) {
        val txt = unserializedBody != null ? unserializedBody.toString() : "null";
        return Stringified.plainText( txt );
    }

    @Override @SuppressWarnings("unchecked")
    public <T> T toObject(String content, Class<T> type) {
        if ( String.class.equals(type) )
            return (T) content;
        throw new UnsupportedOperationException( "Can't convert to " + type );
    }
}
