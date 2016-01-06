package com.lostVictories.bootstrap;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;



public class RestAPIServletModule extends JerseyServletModule {

	@Override
	protected void configureServlets() {
	    ResourceConfig rc = new PackagesResourceConfig( "com.lostVictories.resources" );
	    
		for ( Class<?> resource : rc.getClasses() ) {
			bind( resource );
		}
		
		bind(MessageBodyReader.class).to(JacksonJsonProvider.class);
        bind(MessageBodyWriter.class).to(JacksonJsonProvider.class);
        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("com.sun.jersey.config.feature.Trace", "true");
        initParams.put("com.sun.jersey.api.json.POJOMappingFeature", "true");
        serve( "/*" ).with(
                GuiceContainer.class,
                initParams);
		
	 }

}
