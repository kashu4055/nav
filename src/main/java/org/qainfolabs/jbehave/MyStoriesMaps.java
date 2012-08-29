package org.qainfolabs.jbehave;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Properties;
import static java.util.Arrays.asList;


import org.jbehave.core.Embeddable;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.junit.JUnitStoryMaps;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.CrossReference;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.DateConverter;
import org.jbehave.core.steps.ParameterConverters.ExamplesTableConverter;
import org.jbehave.core.steps.spring.SpringApplicationContextFactory;
import org.jbehave.core.steps.spring.SpringStepsFactory;
import org.qainfolabs.jbehave.converter.MyConverter;
import org.qainfolabs.jbehave.converter.StudentBean;
import org.qainfolabs.jbehave.converter.StudentConverter;
import org.springframework.context.ApplicationContext;


import static org.jbehave.core.io.CodeLocations.codeLocationFromClass;
import static org.jbehave.core.reporters.Format.CONSOLE;
import static org.jbehave.core.reporters.Format.HTML;
import static org.jbehave.core.reporters.Format.TXT;
import static org.jbehave.core.reporters.Format.XML;

/**
 * <p>
 * {@link Embeddable} class to run multiple textual stories via JUnit using Spring Dependency Injection to compose the steps classes.
 * </p>
 * <p>
 * Stories are specified in classpath and correspondingly the {@link LoadFromClasspath} story loader is configured.
 * </p> 
 */
public class MyStoriesMaps extends JUnitStoryMaps {
    
    public MyStoriesMaps() {
        configuredEmbedder().useMetaFilters(metaFilters());
    }

    @Override
    public Configuration configuration() {
        Class<? extends Embeddable> embeddableClass = this.getClass();
        // Start from default ParameterConverters instance
        ParameterConverters parameterConverters = new ParameterConverters();
        // factory to allow parameter conversion and loading from external resources (used by StoryParser too)
        ExamplesTableFactory examplesTableFactory = new ExamplesTableFactory(new LocalizedKeywords(), new LoadFromClasspath(embeddableClass), parameterConverters);
        // add custom coverters
        parameterConverters.addConverters(new DateConverter(new SimpleDateFormat("yyyy-MM-dd")),
                new ExamplesTableConverter(examplesTableFactory));
        parameterConverters.addConverters(new MyConverter());
        parameterConverters.addConverters(new StudentConverter(new ExamplesTableFactory()));

		CrossReference xref = new CrossReference();
		return new MostUsefulConfiguration()
            .useStoryLoader(new LoadFromClasspath(embeddableClass))
            .useStoryParser(new RegexStoryParser(examplesTableFactory)) 
            .useStoryReporterBuilder(new StoryReporterBuilder()
                .withCodeLocation(CodeLocations.codeLocationFromClass(embeddableClass))
                .withDefaultFormats()
                .withCrossReference(xref )
                .withFormats(CONSOLE, TXT, HTML, XML))
            .useParameterConverters(parameterConverters);
    }

    @Override
    public InjectableStepsFactory stepsFactory() {
        String path = "org.qainfolabs.jbehave".replaceAll("\\.", "/");
        ApplicationContext context = new SpringApplicationContextFactory(path+"/my_steps.xml").createApplicationContext();
        return new SpringStepsFactory(configuration(), context);
    }
    
   


    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(codeLocationFromClass(this.getClass()), "**/new*.story", "**/excluded*.story");
                
    }

	@Override
	protected List<String> metaFilters() {
		// TODO Auto-generated method stub
		return asList("+author pankaj");
	}
    
        
}