package gemcfadyen.drools_experimentation.templates;

import static junit.framework.Assert.assertTrue;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;

import junit.framework.Assert;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.template.ObjectDataCompiler;
import org.junit.Test;

public class BookTemplateTest {

	private String expectedDrl = "package gemcfadyen.drools_experimentation.templates; "
			+ "import gemcfadyen.drools_experimentation.templates.Book;"
			+

			"rule \"should print out restricted books\""
			+ "when"
			+ "	Book(title == Encyclopedia)"
			+ "then "
			+ "   System.out.println(\"The book with the title \" + Encyclopedia + \" is restricted\";"
			+ "end";

	@Test
	public void shouldCreateDrlWhichPrintsOutRestrictedBookTitles() {
		Book restrictedTitle = new Book("Encyclopedia");
		Collection<Book> restrictedBooks = new ArrayList<Book>();
		restrictedBooks.add(restrictedTitle);
		KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
		KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		InputStream inputStream = this.getClass().getResourceAsStream("../template-restrictedbooks.drl");
		ObjectDataCompiler compiler = new ObjectDataCompiler();
		String drl = compiler.compile(restrictedBooks, inputStream);
		System.out.println("DRL " + drl);

		knowledgeBuilder.add(
				ResourceFactory.newReaderResource(new StringReader(drl)),
				ResourceType.DRL);

		if (knowledgeBuilder.hasErrors()) {
			System.out.println("errors in drl" + knowledgeBuilder.getErrors());
		}
		knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());

		assertTrue(drl.contains("Book(title == \"Encyclopedia\")"));
	}

}
