package lambdify.apigateway;

import lambdify.aws.events.apigateway.*;
import lambdify.core.*;
import lombok.*;
import lombok.experimental.*;

import java.io.*;

/**
 * A simple AWS Lambda application that handles API Gateway requests.
 */
@ToString
@NoArgsConstructor
@Accessors(fluent = true)
public class App implements RequestHandler<ProxyRequestEvent, ProxyResponseEvent> {

    /**
     * The internal router.
     */
    private RequestRouter router;

    /**
     * Lazy loader of the internal {@link RequestRouter}.
     *
     * @return
     */
    private RequestRouter getRouter(){
        if ( router == null )
            router = new RequestRouter();
        return router;
    }

    /**
     * Define routes for your application. Due to architecture decisions, once you define
     * a route you can't change how the internal router behaves. If you intent to define your
     * own {@code notFoundHandler} or even a custom set of serializers you should do this
     * before you define your first route.
     *
     * @param routes
     * @return
     */
    @SafeVarargs
    public final App routes(LambdaRouter.Entry<LambdaRouter.Route, LambdaRouter.Function>... routes) {
        for ( val route : routes )
            getRouter().memorizeEndpoint( route );
        return this;
    }

    /**
     * Memorize the routers (and its routes). Due to architecture decisions, once you define
     * a route you can't change how the internal router behaves. If you intent to define your
     * own {@code notFoundHandler} or even a custom set of serializers you should do this
     * before you define your first route.
     *
     * @param routers
     * @return
     */
    public final App routers( LambdaRouter...routers ) {
        for ( val router : routers )
            routes( router.getRoutes() );
        return this;
    }

    /**
     * Handles a Lambda Function request.
     *
     * @param request The Lambda Function input
     * @return The Lambda Function output
     *
     * @see RequestHandler#handleRequest(Object)
     */
    @Override
    public ProxyResponseEvent handleRequest( ProxyRequestEvent request ) {
        try {
            return getRouter().doRouting( request );
        } catch ( Throwable cause ) {
            val error = new StringWriter();
            cause.printStackTrace( new PrintWriter( error ) );
            val errorMsg = error.toString();
            System.err.println( "Failed to handle request: " + cause.getMessage() );
            System.err.println( errorMsg );
            System.err.println( "Global configuration: " + ApiGatewayConfig.INSTANCE );
	        System.err.println( "App configuration: " + this );
            return Responses.internalServerError( errorMsg );
        }
    }
}