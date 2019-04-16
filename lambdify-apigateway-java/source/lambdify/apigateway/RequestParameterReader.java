package lambdify.apigateway;

import lambdify.aws.events.apigateway.ProxyRequestEvent;
import lombok.*;

import java.util.*;

/**
 * The reader of {@link ProxyRequestEvent} parameters.
 */
public interface RequestParameterReader {

	static <T> T getHeaderParam(ProxyRequestEvent request, String key, Class<T> clazz ) {
		val data = request.getHeaders();
		if ( data == null ) return null;
		val value = data.get( key );
		return ApiGatewayConfig.INSTANCE.paramReader().convert( value, clazz );
	}

	static <T> T getQueryParam( ProxyRequestEvent request, String key, Class<T> clazz ) {
		val data = request.getQueryStringParameters();
		if ( data == null ) return null;
		val value = data.get( key );
		return ApiGatewayConfig.INSTANCE.paramReader().convert( value, clazz );
	}

	static<T> T getPathParam( ProxyRequestEvent request, String key, Class<T> clazz ) {
		val data = request.getPathParameters();
		if ( data == null ) return null;
		val value = data.get( key );
		if ( value == null ) return null;
		return ApiGatewayConfig.INSTANCE.paramReader().convert( value, clazz );
	}

	static<T> T getStageParam( ProxyRequestEvent request, String key, Class<T> clazz ) {
		val data = request.getStageVariables();
		if ( data == null ) return null;
		val value = data.get( key );
		return ApiGatewayConfig.INSTANCE.paramReader().convert( value, clazz );
	}

	@SuppressWarnings( "unchecked" )
	static<T> T getBodyAs( ProxyRequestEvent request, Class<T> type ) {
		if ( String.class.equals( type ) )
			return (T) request.getBody();
		else if ( byte[].class.equals( type ) )
			return (T) request.getBody().getBytes();

		val serializer = getSerializer( request );
		if ( serializer == null )
			throw new RuntimeException( "Could not found a valid serializer for this request." );

		val body = request.getIsBase64Encoded()
			 ? new String( Base64.getDecoder().decode( request.getBody() ) )
			 : request.getBody();
		return serializer.toObject( body, type);
	}

	static String getContentType( ProxyRequestEvent request ){
		val headers = request.getHeaders();
		return headers == null ? null
			: removeCharsetIfExists(headers.get( "content-type" ));
	}

	static Serializer getSerializer( ProxyRequestEvent request ) {
		var requestContentType = getContentType( request );
		if ( requestContentType == null ) {
            requestContentType = ApiGatewayConfig.INSTANCE.defaultContentType;
            System.err.println( "No Content-Type header found. Using default content-type as fallback: " + requestContentType );
        }

        val serializer = ApiGatewayConfig.INSTANCE.serializers().get(requestContentType);
        if ( serializer == null )
            System.err.println( "Could not find API Gateway serializer for " + requestContentType );
        return serializer;
	}

	static String removeCharsetIfExists(String requestContentType) {
		val index = requestContentType == null ? 0 : requestContentType.indexOf(";");
		return index > 0 ? requestContentType.substring(0, index) : requestContentType;
	}
}
