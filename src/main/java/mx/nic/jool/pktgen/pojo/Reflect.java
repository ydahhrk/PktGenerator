package mx.nic.jool.pktgen.pojo;

import java.lang.reflect.Field;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.annotations.Readable;

public abstract class Reflect {
	
	protected void modifyFieldValues(PacketContent obj, FieldScanner scanner) {
		Object read, fieldValue;
		String fieldToModify;
		int annotationsLength = 0;
		Field[] fields = this.getClass().getDeclaredFields();
		
		
		if (fields.length == 0) {
			System.err.println("Nada que modificar.");
			return;
		}
			
		//Print
		for (Field field : fields) {
			Readable annotation = field.getAnnotation(Readable.class);
			if (annotation == null)
				continue; /* No nos interesa este campo. */
			
			annotationsLength++;
			
			System.out.print(field.getName());
			try {
				fieldValue = field.get(this);
				System.out.println(": " + (fieldValue != null ? fieldValue : "(auto)"));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (annotationsLength == 0) {
			System.err.println("Nada que modificar.");
			return;
		}
			
		
		// Modify
		do {
			fieldToModify = scanner.readLine("Campo a modificar (respetar Mayusculas y minusculas)", "exit");
			if (fieldToModify.equalsIgnoreCase("exit"))
				break;
			
			if (fieldToModify == null || fieldToModify.isEmpty())
				continue;
			
			for (Field field : fields) {
				if (!field.getName().equalsIgnoreCase(fieldToModify))
					continue;
				
				Readable annotation = field.getAnnotation(Readable.class);
				if (annotation == null)
					continue; /* No nos interesa este campo. */
				
				read = scanner.read(field.getName(), annotation.defaultValue(), annotation.type());
				if (read ==  null)
					System.err.println("No se ha asignado valor porque es nulo.");
				try {
					field.set(this, read);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		} while(true);
	}
}
