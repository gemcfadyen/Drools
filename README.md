Drools
======

In order to familiarise myself with the Drools language I have spent several weeks working through various examples to understand
* syntax
* functionality
* domain specific languages

This repository contains my full set of working examples.

Using Eclipse
-------------
Eclipse has a good Drools Plugin which can be installed from: 
http://download.jboss.org/drools/release/5.5.0.Final/org.drools.updatesite

In eclipse -> preferences be sure to navigate to 
Drools -> Installed Drools Runtime and navigate to a folder. 
Once you have a project running the plugin will copy the necessary drools jars here.

Using Maven
-----------
I decided to lay out my working environment as a maven project rather than a typical Drools project. This was purely because in the 'real world' we use Drools as part of a larger maven application, so for me it felt more realistic to use maven from the start. use a maven project rather than a typical Drools project. It made loading the files a little harder, as the classpaths had to be taken into account, whereas with a Drools Project the compipler knows exactly where to find the drl files.

    <dependency>
  		<groupId>org.drools</groupId>
			<artifactId>drools-core</artifactId>
			<version>5.5.0.Final</version>
		</dependency>
		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-compiler</artifactId>
			<version>5.5.0.Final</version>
		</dependency>
		<dependency>
			<groupId>org.drools</groupId>
			<artifactId>drools-decisiontables</artifactId>
			<version>5.5.0.Final</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
			<version>1.7.2</version>
			<scope>runtime</scope>

If you use a Drools project you dont need a pom as maven is not used. The drools runtime pulls in all the drools dependencies you require. The downside of this is that if you wish to use hamcrest/junit etc you need to manually add those jars to the classpath - old school ;-)

Summary
-------
Drools seems a straighforward domain to learn. It was quick to get running and make progress. 

Its easy to read the logic, the drl files act as documentation. Rather than compound rules, write seperate ones with appropriate names.

Agenda and activation groups provide a nice way to order rules without relying on salience which could be difficult to maintain over time.

Some of the features, particularly those available within Stateful Sessions are very powerful (such as modifying the working memory during runtime, obtaining facts from the session and manipulating them.
I found it difficult to think of real life usecases for the templated drools files, although I'm sure there are some good ones out there.

In my opinion if you have a problem or area in your code which a rules engine could solve then drools should definitely be considered.


Main Features
-------------

DROOLS Summary

Rules Engine
-------------
* Dynamic – rules are stored, managed and updated as data.
* Delivers knowledge reasoning and representation to the developer.
* 3 components – ontology (model we are using to represent our 'things' ie records/classes), rules (perform the reasoning over) and data.
* The engine takes the rules and they are applied to data to give outcomes. Ie it matches facts and data against rules to infer a conclusion which results in actions.
* Rule engines tell you what to do, not how so it is easier to express solutions.
* Rules engine separate logic (rules) from data (domain) so its easier to maintain future changes as the logic is all in one place.
* Efficient at pattern matching.
* Provide a centralisation of knowledge – all rules live in a knowledge base.  
* Rules are stored in 'production memory' and facts are in the 'working memory' which  means the facts can be modified or retracted.

Rete Algorithm
---------------
* Uses a network to match facts against production rules.
* The rete algorithm builds up a network of nodes, each node corresponding to a pattern on the LHS (conditional part) of the rule.  
* Each node has facts which satisfy that pattern.
* When facts are inserted they evaluate the network and if a node is matched (ie that fact causes the rule to be true), the node is annotated. This means when fireAllRules is called all the prep work has been done upfront so the execution of the rules is fast. Its already determined which rules to run.

Stateful Session
-----------------
* A Stateful session can be modified at  runtime allowing rules to be re-evaluated eg: the RHS of a rule can manipulate the working memory by calling modify(), retract() which can cause rules on the activation list to be cancelled, or new rules to be added to the activation list. 
* Supports inference – that is  the engine being aware of changes and re-reasoning over the rules to take account of the changes.
* Must call dispose() when you have finished with a stateful session so that it is destroyed. This will prevent memory leaks.

Stateless Session
-----------------
* Manipulation of facts does not cause a re-evaluation of the rules.
* Thought of more as a method or function call – you provide input, the rules are evaluated and you get a result back - that is it.
* The command pattern is used (execute() starts the rule execution rather than fireAllRules() which is used in the stateful session).  
* Rather than insert(fact) you pass the facts into the session as parameters in execute() using any iterable java Collection.

Knowledge Builder
------------------
* Creates a package of compiled rules for the Knowledge Base
* Turns the drl file into a package definition of rules
* You need to explicity check for compilation errors – 3 levels of error are reported ERROR, WARNING, INFO.

Knowledge Base
---------------
* Compiled rules and process definitions


LHS (Left Hand Side)
--------------------
* The LHS defines the facts/statements to be reasoned over.
* Can have logic like AND, OR, IN, CONTAINS etc...

RHS (Right Hand Side)
---------------------
* The consequence of the rule
* Can contain java statements.
* Can manipulate the (stateful) session by using retract, modify, insert, new etc..

Activation
----------
* The activation list holds all the rules whose LHS (condtions) are true and are therefore candidates to be run.
* As you add facts to the working memory, the rete algorithm is reasoning over the rules and building up the activation list ready for when fireAllRules() is called.
* When fireAllRules() is called the rules on the activation list are worked through. One rule may cause other rules to be cancelled or added to the activation list.

Agenda
------
* Agendas are used for grouping rules together and can be used to control ordering of activations especially conflicting rules – it can be used to specify what order to execute the conflicting rules in.
* When rules are fully matched and eligible for execution they are placed on an activation list and placed on the agenda for firing. 
* When fireAllRules() is called, focus switches to evaluate the Agenda (Agenda evaluation phase). This selects a rule to fire. If no rules exist, exit.
* Firing a rule may change the working memory, similarly there could be several rules on the agenda. The rules engine needs to know which order to fire the rules. For this agenda groups can be used.
* Agenda-Groups – Used to partition rules on the agenda.  One agenda group will have focus at a time.
* If you have rules which should be grouped together, you can use agenda groups. When you call setFocus(onThatAgendaGroup), that AgendaGroup is pushed onto the stack. Agenda groups can therefore be used to provide a flow between rules.
* MAIN is the default agenda group. Any rule without an explicit Agenda Group will belong to Main.


Globals
-------
* Declaring global variable(s) provides a way of returning results to your calling code.
* Should not be used on LHS for reasoning over.

SetFocus
--------
* If you set the focus to an agenda which does not exist, no compilation error is displayed. 
* The 'bad' group is put on the activation stack it will contain no rules so will simply be skipped over.

Domain Specific Language (DSL)
------------------------------
* seful if you want BA's to define the rules rather than development team.
* An extra layer which provides a mapping from a human-readable (ubiquitous domain) language to drools language.
* The compiler does an in memory translation to produce a drl file (no actual physical file is created).
* Useful if a lot of rules have a similar structure or behaviour.

Template
---------
* You can create templated drl files so you can generate a complete drl on the fly.
* Can have java expand the template and just pass in the fact objects which are substituted and flesh the drl out.
* Can't think of any useful usecases for this, perhaps some sort of book categorisation?

Functions
---------
* Drools allows you to define functions (and queries) which rules can call (and java can call).
* eval(functionName) can only be used with functions returning a boolean. 
* For non-booleans you call the function directly using its name.

Defining types
---------------
* You can declare types within a drools file to store results or create objects that are only needed within the session.
* You can access the defined type from outside the session in the calling code, and manipulate its values which can be powerful – ie you can define a new type in drools, have no java class, yet reference it and change its state from java.
