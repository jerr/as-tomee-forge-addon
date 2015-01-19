/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.tomee;

import org.apache.commons.lang.StringUtils;
import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;

/**
 * The TomEE Configuration
 * 
 * @author Jeremie Lagarde
 */

public class TomEEConfiguration extends AbstractFacet<Project>
{
   private static final String CONFIG_PREFIX = "as.tomee.";
   private static final String CONFIG_VERSION_KEY = CONFIG_PREFIX + "version";
   private static final String CONFIG_DISTRIBUTION_KEY = CONFIG_PREFIX + "dist";
   private static final String CONFIG_CLASSIFIER = CONFIG_PREFIX + "classifier";
   private static final String CONFIG_PATH_KEY = CONFIG_PREFIX + "path";
   private static final String CONFIG_JAVAHOME_KEY = CONFIG_PREFIX + "javahome";
   private static final String CONFIG_JVMARGS_KEY = CONFIG_PREFIX + "jvmargs";
   private static final String CONFIG_SHUTDOWNPORT_KEY = CONFIG_PREFIX + "shutdownport";
   private static final String CONFIG_DEBUG_KEY = CONFIG_PREFIX + "debug";
   private static final String CONFIG_DEBUGPORT_KEY = CONFIG_PREFIX + "debugport";

   /**
    * The default version
    */
   private static final String DEFAULT_VERSION = "1.7.1";
   private static final int    DEFAULT_DEBUG_PORT = 5005;
   private static final int    DEFAULT_SHUTDOWN_PORT = 8005;

   /**
    * The default path
    */
   private static final String DEFAULT_PATH = "target/tomee-dist";

   protected Configuration config;

   @Override
   public boolean install()
   {
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return true;
   }

   void setProject(Project project)
   {
      if (project.hasFacet(ConfigurationFacet.class))
         config = project.getFacet(ConfigurationFacet.class).getConfiguration();
   }

   public void setFaceted(Project project)
   {
      super.setFaceted(project);
      setProject(project);
   }

   public String getVersion()
   {
      return config.getString(CONFIG_VERSION_KEY, DEFAULT_VERSION);
   }

   public void setVersion(String version)
   {
      if (StringUtils.isNotBlank(version))
         config.setProperty(CONFIG_VERSION_KEY, version);
      else
         config.clearProperty(CONFIG_VERSION_KEY);
   }

   public Coordinate getDistibution()
   {
      return CoordinateBuilder.create(config.getString(CONFIG_DISTRIBUTION_KEY));
   }

   public void setDistribution(Coordinate dist)
   {
      setVersion(dist.getVersion());
      config.setProperty(CONFIG_DISTRIBUTION_KEY, dist.toString());
   }
   
   public String getClassifier()
   {
      return config.getString(CONFIG_CLASSIFIER);
   }

   public void setClassifier(String classifier)
   {
      config.addProperty(CONFIG_CLASSIFIER, classifier);
   }

   
   public String getDefaultPath()
   {
      return DEFAULT_PATH;
   }

   public String getPath()
   {
      if (config == null)
         return null;
      return config.getString(CONFIG_PATH_KEY);
   }

   public void setPath(String path)
   {
      if (StringUtils.isNotBlank(path))
         config.setProperty(CONFIG_PATH_KEY, path);
      else
         config.clearProperty(CONFIG_PATH_KEY);
   }

   public String getJavaHome()
   {
      return config.getString(CONFIG_JAVAHOME_KEY);
   }

   public void setJavaHome(String javaHome)
   {
      config.addProperty(CONFIG_JAVAHOME_KEY, javaHome);
   }

   public String[] getJvmArgs()
   {
      return config.getStringArray(CONFIG_JVMARGS_KEY);
   }

   public void setJvmArgs(String[] args)
   {
      config.addProperty(CONFIG_JVMARGS_KEY, args);
   }
   

   public boolean isDebug()
   {
      return config.getBoolean(CONFIG_DEBUG_KEY,false);
   }

   public void setDebug(boolean debug)
   {
      config.addProperty(CONFIG_DEBUG_KEY, debug);
   }


   public int getDebugPort()
   {
      return config.getInt(CONFIG_DEBUGPORT_KEY,DEFAULT_DEBUG_PORT);
   }

   public void setDebugPort(int port)
   {
      config.addProperty(CONFIG_DEBUGPORT_KEY, port);
   }

   public void setShutdownPort(int port)
   {
      config.addProperty(CONFIG_SHUTDOWNPORT_KEY, port);
   }

   public int getShutdownPort()
   {
      return config.getInt(CONFIG_SHUTDOWNPORT_KEY,DEFAULT_SHUTDOWN_PORT);
   }

}
