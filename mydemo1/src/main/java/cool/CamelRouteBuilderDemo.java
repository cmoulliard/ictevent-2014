package cool;

import org.apache.camel.builder.RouteBuilder;

public class CamelRouteBuilderDemo extends RouteBuilder{

    public void configure() throws Exception {
/*        from("timer://demo?delay=5s")
          .setBody().constant("Hello ICT participants")
          .setHeader("SessionType").simple("Developer")
          .log(">> Session Type : {header.SessionType} - ${body}");*/
    }
}
