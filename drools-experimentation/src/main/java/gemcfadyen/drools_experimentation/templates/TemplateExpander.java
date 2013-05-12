package gemcfadyen.drools_experimentation.templates;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.template.ObjectDataCompiler;

//Compiles the templated drl and returns a knowledgeBase used in the test
public class TemplateExpander {
	public KnowledgeBase getKnowledgeBaseInitialisedWithTemplatedDrl(KnowledgeBase knowledgeBase, InputStream inputStream,
				Collection<?> templateParameters) throws Exception {
			ObjectDataCompiler compiler = new ObjectDataCompiler();
			String drl = compiler.compile(templateParameters, inputStream);
			System.out.println("DRL Generated : " + drl);

			KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			Reader reader = new StringReader(drl);
			Resource resource = ResourceFactory.newReaderResource(reader);
			knowledgeBuilder.add(resource, ResourceType.DRL);

			if (knowledgeBuilder.hasErrors()) {
				System.out.println("DRL Has errors " + knowledgeBuilder.getErrors());
			}

			knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
			
			return knowledgeBase;
		}

}
