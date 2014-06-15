/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.tomee.server;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.assembler.Deployer;
import org.apache.openejb.assembler.classic.AppInfo;
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
      System.setProperty("server.shutdown.command", "SHUTDOWN");

      server = new RemoteServer();
      SecurityActions.registerShutdown(server);

      server.start(null, cmd, checkStarted);
   }

   public void deploy(TomEEConfiguration config, String path)
   {
      final Deployer deployer = (Deployer) lookup("openejb/DeployerBusinessRemote", config);
      try
      {
         deployer.deploy(path);
      }
      catch (OpenEJBException e)
      {
         throw new Error(e.getMessage(), e);
      }
   }

   public void undeploy(TomEEConfiguration config, String path)
   {
      final Deployer deployer = (Deployer) lookup("openejb/DeployerBusinessRemote", config);
      try
      {
         Collection<AppInfo>  infos = deployer.getDeployedApps();
         for (Iterator iterator = infos.iterator(); iterator.hasNext();)
         {
            AppInfo appInfo = (AppInfo) iterator.next();
            if(path.startsWith(appInfo.path)) {
               deployer.undeploy(appInfo.path);
            }
         }
      }
      catch (OpenEJBException e)
      {
         throw new Error(e.getMessage(), e);
      }
   }
   
   protected Object lookup(String name, TomEEConfiguration config)
   {
      String tomeeHost = "localhost";
      String tomeeHttpPort = "8080";
      final Properties props = new Properties();
      props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
      props.put(Context.PROVIDER_URL, "http://" + tomeeHost + ":" + tomeeHttpPort + "/tomee/ejb");
      try
      {
         return new InitialContext(props).lookup(name);
      }
      catch (NamingException e)
      {
         throw new Error(e.getMessage(), e);
      }
   }

}
