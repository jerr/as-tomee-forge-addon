/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.tomee.server;

import java.security.AccessController;
import java.security.PrivilegedAction;

import org.apache.openejb.config.RemoteServer;

/**
 * Security actions to perform possibly privileged operations.
 * 
 * @author Jeremie Lagarde
 */
public final class SecurityActions
{

   public static void registerShutdown(final RemoteServer server)
   {
      final Thread hook = new Thread(new Runnable()
      {
         @Override
         public void run()
         {
            server.stop();
         }
      });
      hook.setDaemon(true);
      addShutdownHook(hook);
   }

   static void addShutdownHook(final Thread hook)
   {
      if (System.getSecurityManager() == null)
      {
         Runtime.getRuntime().addShutdownHook(hook);
      }
      else
      {
         AccessController.doPrivileged(new PrivilegedAction<Object>()
         {
            public Object run()
            {
               Runtime.getRuntime().addShutdownHook(hook);
               return null;
            }
         });
      }
   }

   static String getEnvironmentVariable(final String key)
   {
      if (System.getSecurityManager() == null)
      {
         return System.getenv(key);
      }
      return AccessController.doPrivileged(new PrivilegedAction<String>()
      {
         @Override
         public String run()
         {
            return System.getenv(key);
         }
      });
   }
}
