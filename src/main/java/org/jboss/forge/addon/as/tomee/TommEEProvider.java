/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.tomee;

import java.util.List;

import org.jboss.forge.addon.as.spi.ApplicationServerProvider;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.result.Result;

/**
 * The TomEE application server provider
 * 
 * @author Jeremie Lagarde
 */
@FacetConstraint({ ProjectFacet.class, ConfigurationFacet.class, ResourcesFacet.class })
public class TommEEProvider implements ApplicationServerProvider
{

   @Override
   public Project getFaceted()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean install()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean isInstalled()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public boolean uninstall()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void validate(UIValidationContext arg0)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public String getName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getDescription()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public List<Class<? extends UICommand>> getSetupFlow()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setup(UIContext context)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public DirectoryResource install(UIContext context)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isASInstalled(UIContext context)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Result start(UIContext context)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Result shutdown(UIContext context)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Result deploy(UIContext uiContext)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Result undeploy(UIContext uiContext)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
