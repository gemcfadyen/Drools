package gemcfadyen.drools_experimentation;

import java.util.ArrayList;
import java.util.List;

import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.rule.FactHandle;
/**
 * The event listener will keep track of the objects in the session
 * @author Georgina
 *
 */
public class DroolsWorkingMemoryEventListener implements WorkingMemoryEventListener {

	private List<String> objectsInserted = new ArrayList<String>();
    private List<Object> actualObjectsInserted = new ArrayList<Object>();
	private List<String> objectsUpdated = new ArrayList<String>();
	private List<String> objectsRetracted = new ArrayList<String>();
	
	@Override
	public void objectInserted(org.drools.event.rule.ObjectInsertedEvent event) {
		System.out.println("Object inserted " + event.getObject().getClass().getSimpleName());
		objectsInserted.add(event.getObject().getClass().getSimpleName());
		
		Object insertedObject = event.getObject();
		actualObjectsInserted.add(insertedObject);
	}

	@Override
	public void objectUpdated(org.drools.event.rule.ObjectUpdatedEvent event) {
		System.out.println("Object updated " + event.getObject().getClass().getSimpleName());
		objectsUpdated.add(event.getObject().getClass().getSimpleName());

	}

	@Override
	public void objectRetracted(org.drools.event.rule.ObjectRetractedEvent event) {
		System.out.println("Object retracted " + event.getOldObject().getClass().getSimpleName());
		objectsRetracted.add(event.getOldObject().getClass().getSimpleName());

	}
	
	public List<String> getInsertedObjects(){
		return objectsInserted;
	}
	
	public List<String> getUpdatedObjects(){
		return objectsUpdated;
	}
	
	public List<String> getRetractedObjects(){
		return objectsRetracted;
	}

	public List<Object> getActualObjectInserted(){
		return actualObjectsInserted;
	}
}
