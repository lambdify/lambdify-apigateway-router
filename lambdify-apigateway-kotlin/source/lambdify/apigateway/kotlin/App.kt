package lambdify.apigateway.kotlin

import lambdify.apigateway.App
import lambdify.apigateway.LambdaRouter
import lambdify.apigateway.Methods
import lambdify.aws.events.apigateway.ProxyRequestEvent
import lambdify.aws.events.apigateway.ProxyResponseEvent
import lambdify.core.RawRequestHandler
import lambdify.core.RequestHandler

/**
 * Created by miere.teixeira on 06/04/2018.
 */
open class App(val builder: App.() -> Unit)
    : RawRequestHandler by App().apply(builder)

infix fun Methods.and(s:String ): LambdaRouter.Route {
    return and(s)
}

infix fun LambdaRouter.Route.with(t: (ProxyRequestEvent) -> ProxyResponseEvent): LambdaRouter.Entry<LambdaRouter.Route, LambdaRouter.Function> {
    return with( t )
}

infix fun LambdaRouter.Route.with(t: () -> ProxyResponseEvent): LambdaRouter.Entry<LambdaRouter.Route, LambdaRouter.Function> {
    return with( t )
}

infix fun LambdaRouter.Route.withNoContent(t: (ProxyRequestEvent) -> Unit ): LambdaRouter.Entry<LambdaRouter.Route, LambdaRouter.Function> {
    return withNoContent( t )
}
