package lambdify.apigateway;

import lambdify.aws.events.apigateway.*;
import lambdify.core.RequestHandler;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Defines how a router should behave inside an AWS Lambda application.
 *
 * Created by miere.teixeira on 18/04/2018.
 */
public interface LambdaRouter {

    Entry<Route, Function>[] getRoutes();

    /**
     * Defines a Route.
     */
    @Value @Accessors(fluent = true)
    class Route {

        final String url;
        final Methods method;

        public Entry<Route, Function> with(Supplier target ) {
            return new Entry<>( this, target );
        }

        public Entry<Route, Function> with(Function target ) {
            return new Entry<>( this, target );
        }

        public Entry<Route, Function> withNoContent(Consumer target ) {
            return new Entry<>( this, target );
        }
    }

	/**
	 * Represents a Lambda Function.
	 */
	interface Function extends RequestHandler<ProxyRequestEvent, ProxyResponseEvent> {

		@Override
		default Class<ProxyRequestEvent> getInputClass() {
			return ProxyRequestEvent.class;
		}
	}

	/**
	 * Represents a Lambda Function that does not produce custom response.
	 */
	interface Consumer extends Function {

	    @Override
	    default ProxyResponseEvent handleRequest(ProxyRequestEvent input) {
	        consume(input);
	        return Responses.noContent();
	    }

	    void consume(ProxyRequestEvent input);
	}

	/**
	 * Represents a Lambda Function that only produce custom responses.
	 */
	interface Supplier extends Function {

	    @Override
	    default ProxyResponseEvent handleRequest(ProxyRequestEvent input) {
	        return supply();
	    }

		ProxyResponseEvent supply();
	}

	/**
	 * Represents an Authorizer Function.
	 */
	interface AuthorizerFunction extends RequestHandler<TokenAuthorizerContext, AuthPolicy> {

		@Override
		default Class<TokenAuthorizerContext> getInputClass() {
			return TokenAuthorizerContext.class;
		}
	}

	/**
	 * A simple holder for Key and Value.
	 *
	 * @param <K>
	 * @param <V>
	 */
	@Value @Accessors(fluent = true)
	class Entry<K,V> {
		final K key;
		final V value;
	}
}
