/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.tomee.server;

import org.apache.openejb.config.RemoteServer;
import org.jboss.forge.addon.as.tomee.TomEEConfiguration;

/**
 * The TomEE Remote Server Controller
 * 
 * @author Jeremie Lagarde
 */
public class ServerController
{
   private RemoteServer server;

   public void start(TomEEConfiguration config)
   {
      run("start", true, config);
   }

   public void stop(TomEEConfiguration config)
   {
      run("stop", false, config);
   }

   protected void run(String cmd, boolean checkStarted, TomEEConfiguration config)
   {

      System.setProperty("openejb.home", config.getPath());
      if (config.isDebug())
      {
         System.setProperty("openejb.server.debug", "true");
         System.setProperty("server.debug.port", Integer.toString(config.getDebugPort()));
      }

      System.setProperty("server.shutdown.port", Integer.toString(config.getShutdownPort()));
      System.setProperty("server.shutdown.command", "SHUTDOWN" );

      server = new RemoteServer();
      SecurityActions.registerShutdown(server);

      server.start(null, cmd, checkStarted);
   }
}
