/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.as.tomee.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.as.tomee.TomEEConfiguration;
import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

/**
 * The TomEE Configuration Wisard
 * 
 * @author Jeremie Lagarde
 */
@FacetConstraint({ DependencyFacet.class, ResourcesFacet.class })
public class TomEEConfigurationWizard extends AbstractProjectCommand implements UIWizardStep
{
   private static final List<String> CLASSIFIERS = Collections.unmodifiableList(Arrays.asList("webprofile","jaxrs","plus"));

   private DependencyBuilder tomeeDist = DependencyBuilder.create()
            .setGroupId("org.apache.openejb")
            .setArtifactId("apache-tomee")
            .setVersion("[1.0.0,2.0.0[")
            .setPackaging("zip");
   
   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   @WithAttributes(label = "Version")
   private UISelectOne<Coordinate> version;

   @Inject
   @WithAttributes(label = "Classifier")
   private UISelectOne<String> classifier;

   @Inject
   @WithAttributes(label = "Install directory", description = "The path for installing the application server", required = true)
   private UIInput<DirectoryResource> installDir;

   @Inject
   @WithAttributes(label = "Java home")
   private UIInput<DirectoryResource> javaHome;

   @Inject
   @WithAttributes(label = "jvmargs")
   private UIInputMany<String> jvmargs;

   @Inject
   @WithAttributes(label = "shutdown port")
   private UIInput<Integer> shutdownPort;

   @Inject
   @WithAttributes(label = "debug")
   private UIInput<Boolean> debug;

   @Inject
   @WithAttributes(label = "debug port")
   private UIInput<Integer> debugPort;

   
   @Inject
   private TomEEConfiguration config;

   protected TomEEConfiguration getConfig()
   {
      return config;
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      TomEEConfiguration config = getConfig();


      if (version.getValue() != null)
      {
         config.setDistribution(version.getValue());
      }

      if (classifier.getValue() != null)
      {
         config.setClassifier(classifier.getValue());
      }

      if (installDir.getValue() != null)
      {
         config.setPath(installDir.getValue().getFullyQualifiedName());
      }

      if (javaHome.getValue() != null)
      {
         config.setJavaHome(javaHome.getValue().getFullyQualifiedName());
      }

      if (jvmargs.getValue() != null && jvmargs.getValue().iterator().hasNext())
      {
         List<String> args = new ArrayList<String>();
         for (String arg : jvmargs.getValue())
         {
            args.add(arg);
         }
         config.setJvmArgs(args.toArray(new String[args.size()]));
      }
      else
      {
         config.setJvmArgs(null);
      }
      
      if (shutdownPort.getValue() != null)
      {
         config.setShutdownPort(shutdownPort.getValue());
      }
      return null;
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      TomEEConfiguration config = getConfig();
      
      Project project = getSelectedProject(context);
      config.setFaceted(project);

      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      List<Coordinate> dists = dependencyFacet.resolveAvailableVersions(tomeeDist);

      version.setValueChoices(dists);
      version.setItemLabelConverter(new Converter<Coordinate, String>()
      {
         @Override
         public String convert(Coordinate coordinate)
         {
            return coordinate.getVersion();
         }
      });

      String defaultVersion = config.getVersion();
      for (Coordinate coordinate : dists)
      {
         if (coordinate.getVersion().equals(defaultVersion))
            version.setDefaultValue(coordinate);
      }

      classifier.setValueChoices(CLASSIFIERS);
      classifier.setDefaultValue(CLASSIFIERS.get(0));
      
      String path = config.getPath();
      if (path == null)
      {
         installDir.setDefaultValue((DirectoryResource) project.getRootDirectory().getChildDirectory(config.getDefaultPath()));
      }
      else
      {
         installDir.setDefaultValue(resourceFactory.create(DirectoryResource.class, new File(path)));
      }


      if (config.getJavaHome() != null)
      {
         javaHome.setValue(resourceFactory.create(DirectoryResource.class, new File(config.getJavaHome())));
      }

      String[] args = config.getJvmArgs();
      if (args != null && args.length > 0)
         jvmargs.setDefaultValue(Arrays.asList(args));

      debug.setDefaultValue(config.isDebug());
      
      shutdownPort.setDefaultValue(config.getShutdownPort());
      
      if(config.getDebugPort()>0)
         debugPort.setDefaultValue(config.getDebugPort());
      
      builder.add(version).add(classifier).add(this.installDir).add(javaHome).add(jvmargs).add(shutdownPort).add(debug).add(debugPort);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      // No-op. Do nothing.
      return Results.success();
   }

}
