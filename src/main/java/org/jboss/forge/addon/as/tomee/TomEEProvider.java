/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.tomee;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.as.spi.ApplicationServerProvider;
import org.jboss.forge.addon.as.tomee.server.ServerController;
import org.jboss.forge.addon.as.tomee.ui.TomEEConfigurationWizard;
import org.jboss.forge.addon.configuration.facets.ConfigurationFacet;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;

/**
 * The TomEE application server provider
 * 
 * @author Jeremie Lagarde
 */
@FacetConstraint({ ProjectFacet.class, ConfigurationFacet.class, ResourcesFacet.class })
public class TomEEProvider extends AbstractFacet<Project> implements ApplicationServerProvider
{

   @Inject
   private ServerController serverController;

   @Inject
   private TomEEConfiguration config;

   @Inject
   private DependencyResolver resolver;

   @Inject
   private Converter<File, DirectoryResource> directoryResourceConverter;

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

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public String getName()
   {
      return "tomee";
   }

   @Override
   public String getDescription()
   {
      return "TomEE 1.x";
   }

   @Override
   public void setFaceted(Project project)
   {
      super.setFaceted(project);
      if (project.hasFacet(ConfigurationFacet.class))
         config.setProject(project);
   }

   @Override
   public List<Class<? extends UICommand>> getSetupFlow()
   {
      List<Class<? extends UICommand>> setupCommands = new ArrayList<Class<? extends UICommand>>();
      setupCommands.add(TomEEConfigurationWizard.class);
      return setupCommands;
   }

   @Override
   public void setup(UIContext context)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public DirectoryResource install(UIContext context)
   {
      String path = config.getPath();

      CoordinateBuilder coordinate = CoordinateBuilder.create(config.getDistibution());
      coordinate.setClassifier(config.getClassifier());
      DependencyQueryBuilder query = DependencyQueryBuilder.create(coordinate);
      Dependency dist = resolver.resolveArtifact(query);

      File target = new File(path);

      try
      {
         if (target.exists())
         {
            if (!Files.isEmptyDirectory(target))
            {
               Files.deleteRecursively(target);
            }
         }
         Files.extractAppServer(dist.getArtifact().getFullyQualifiedName(), target);
         return directoryResourceConverter.convert(target);
      }
      catch (IOException e)
      {
         Files.deleteRecursively(target);
      }

      return null;
   }

   @Override
   public boolean isASInstalled(UIContext context)
   {
      return config.getPath() != null && (new File(config.getPath())).exists();
   }

   @Override
   public Result start(UIContext context)
   {
      if (serverController.isStarted())
      {
         return Results.fail("A TomEE Server is already running.");
      }
      serverController.start(config);
      return Results.success("TomEE " + config.getVersion() + "  successfully started.");
   }

   @Override
   public Result shutdown(UIContext context)
   {
      serverController.stop(config);
      return Results.success("TomEE shutdown successfully.");
   }

   @Override
   public Result deploy(UIContext uiContext)
   {
      final File content = getContentFile(uiContext);
      serverController.deploy(config, content.getAbsolutePath());
      return Results.success("The deployment operation (DEPLOY) was successful.");
   }

   @Override
   public Result undeploy(UIContext uiContext)
   {
      final File content = getContentFile(uiContext);
      serverController.undeploy(config, content.getAbsolutePath());
      return Results.success("The deployment operation (UNDEPLOY) was successful.");
   }

   private File getContentFile(UIContext uiContext) throws Error
   {
      final PackagingFacet packagingFacet = getFaceted().getFacet(
               PackagingFacet.class);
      String path = (String) uiContext.getAttributeMap().get("path");

      // Can't deploy/undeploy what doesn't exist
      if (!packagingFacet.getFinalArtifact().exists())
         throw new Error();

      final File content;
      if (path == null)
      {
         content = new File(packagingFacet.getFinalArtifact()
                  .getFullyQualifiedName());
      }
      else if (path.startsWith("/"))
      {
         content = new File(path);
      }
      else
      {
         // TODO this might not work for EAR deployments
         content = new File(packagingFacet.getFinalArtifact().getParent()
                  .getFullyQualifiedName(), path);
      }
      return content;
   }

}
