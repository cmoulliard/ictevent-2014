package cool;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.*;
import org.apache.camel.model.language.SimpleExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CamelMainAppDemo {

    private static Logger logger = LoggerFactory.getLogger(CamelMainAppDemo.class);
    private static String message = "ICT Expo";

    public static void main(String[] args) throws Exception {

        // CamelContext = container where we will register the routes
        DefaultCamelContext camelContext = new DefaultCamelContext();

        // Registry of Beans
        SimpleRegistry myRegistry = new SimpleRegistry();
        myRegistry.put("myBean", new MyBean());

        // 1. Using RouteDefinition
        RouteDefinition rd = new RouteDefinition();
        rd.setExchangePattern(ExchangePattern.InOnly);
        rd.setAutoStartup("true");
        rd.setErrorHandlerBuilder(new DefaultErrorHandlerBuilder());
        rd.setTrace("true");

        // From(s) list
        FromDefinition from = new FromDefinition("timer://demo1?delay=1000");
        List<FromDefinition> fromDefinitionList = new ArrayList<FromDefinition>();
        fromDefinitionList.add(from);

        // Processor(s) list
        List<ProcessorDefinition<?>> processorDefinitions = new ArrayList<ProcessorDefinition<?>>();

        // Add processor to the lists

        // A Body
        SetBodyDefinition myBody = new SetBodyDefinition(new SimpleExpression(message));

        // A Bean
        BeanDefinition myBean = new BeanDefinition("myBean");

        // A Log EIP
        LogDefinition logDefinition = new LogDefinition(">> Message received : ${body}");
        logDefinition.setLoggingLevel(LoggingLevel.INFO );
        logDefinition.setLogName("notcool");

        processorDefinitions.add(myBody);
        processorDefinitions.add(myBean);
        processorDefinitions.add(logDefinition);

        // Complete Route definition
        rd.setInputs(fromDefinitionList);
        rd.setOutputs(processorDefinitions);

        // 2. Using RouteBuilder
        RouteBuilder routeBuilder = new RouteBuilder() {

            // A Route sending every x seconds a message
            // to the logger processor

            @Override
            public void configure() throws Exception {
                from("timer://demo2?delay=1000")
                        .setBody().simple(message)
/*                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                logger.info(">> Invoked timer at " + new Date() + " for RouteBuilder");
                            }
                        })*/
                        .beanRef("myBean", "sayHello")
                        .log(LoggingLevel.INFO, "cool",">> Message received : ${body}");
            }
        };

        // Register RouteDefinition & RouteBuilder
        camelContext.addRouteDefinition(rd);
        camelContext.addRoutes(routeBuilder);
        camelContext.setRegistry(myRegistry);

        // Start the container
        camelContext.start();

        // give it time to realize it has work to do
        Thread.sleep(20000);

    }

    public static class MyBean {
        public String sayHello(Exchange exchange) {
            String body = (String)exchange.getIn().getBody();
            // Enrich the message
            body = "Hello " + body + ", Welcome to Camel World !";
            return body;
        }
    }
}
