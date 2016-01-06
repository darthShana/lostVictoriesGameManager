package com.lostVictories.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class LostVictoriesServletContextListener extends GuiceServletContextListener{

	@Override
	protected Injector getInjector() {
		Injector createInjector = Guice.createInjector(new RestAPIServletModule());
		return createInjector;
	}

	
	
}
