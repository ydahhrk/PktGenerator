package mx.nic.jool.pktgen.auto;

import java.io.IOException;
import java.lang.reflect.Field;

import mx.nic.jool.pktgen.FieldScanner;
import mx.nic.jool.pktgen.annotations.Readable;
import mx.nic.jool.pktgen.pojo.Packet;
import mx.nic.jool.pktgen.pojo.PacketContent;
import mx.nic.jool.pktgen.proto.l3.Ipv4Header;
import mx.nic.jool.pktgen.proto.l3.Ipv6Header;
import mx.nic.jool.pktgen.proto.l3.exthdr.FragmentExt6Header;
import mx.nic.jool.pktgen.proto.l4.UdpHeader;


public class Util {

	public static void writePacket(String fileName, PacketContent... content) throws IOException {
		writePacket(fileName, new Packet(content));
	}
	
	public static void writePacket(String fileName, Packet packet) throws IOException {
		packet.postProcess();
		packet.export(fileName);
	}

	public static Ipv4Header hdr4(boolean sender, boolean df, int id) {
		Ipv4Header result = new Ipv4Header();
		
		result.setIdentification(id);
		result.setDf(df);
		
		if (!sender) {
			result.setTtl(63);
			result.swapAddresses();
		}
		
		return result;
	}
	
	public static Ipv4Header hdr4Inner(boolean sender, boolean df, int id) {
		Ipv4Header result = hdr4(sender, df, id);
		result.setIdentification(id);
		result.setDf(df);
		result.setTtl(63);
		result.swapAddresses();
		return result;
	}
	
	public static Ipv6Header hdr6(boolean sender) {
		Ipv6Header result = new Ipv6Header();
		
		if (!sender) {
			result.setHopLimit(63);
			result.swapAddresses();
		}

		return result;
	}
	
	public static Ipv6Header hdr6Inner(boolean sender) {
		Ipv6Header result = hdr6(sender);
		result.setHopLimit(63);
		result.swapAddresses();
		return result;
	}
	
	public static FragmentExt6Header hdrFrag(long id) {
		FragmentExt6Header result = new FragmentExt6Header();
		result.setIdentification(id);
		return result;
	}
	
	public static UdpHeader hdrUdp(boolean sixToFour) {
		UdpHeader result = new UdpHeader();
		if (!sixToFour) {
			result.swapPorts();
		}
		return result;
	}
	
	public static UdpHeader hdrUdpInner(boolean sixToFour) {
		UdpHeader result = new UdpHeader();
		if (sixToFour) {
			result.swapPorts();
		}
		return result;
	}
	
//	private static Field[] showFieldValues(PacketContent obj) {
//		
//		Field[] fields = obj.getClass().getDeclaredFields();
//		
//		for (Field field : fields) {
//			Readable annotation = field.getAnnotation(Readable.class);
//			if (annotation == null)
//				continue; /* No nos interesa este campo. */
//			System.out.print(field.getName());
//			try {
//				fieldValue = field.get(obj);
//				System.out.println(": " + (fieldValue != null ? fieldValue : "(auto)"));
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		
//		return fields;
//	}
	
	
	private static int showFieldValues(Field[] fields, PacketContent obj) {
		int annotationsLength = 0;
		
		System.out.println("***** Campos de " + obj.getClass().getSimpleName() + " *****");
		for (Field field : fields) {
			field.setAccessible(true);
			Readable annotation = field.getAnnotation(Readable.class);
			if (annotation == null)
				continue; /* No nos interesa este campo. */
			
			annotationsLength++;
			
			System.out.print(field.getName());
			try {
				Object fieldValue = field.get(obj);
				System.out.println(": " + (fieldValue != null ? fieldValue : "(auto)"));
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return annotationsLength;
	}
	
	public static void modifyFieldValues(PacketContent obj, FieldScanner scanner) {
		Object read;
		String fieldToModify;
		int annotationsLength = 0;
		Field[] fields = obj.getClass().getDeclaredFields();
		
		
		if (fields.length == 0) {
			System.err.println("Nada que modificar.");
			return;
		}
			
		do {
			annotationsLength = showFieldValues(fields, obj);
			
			if (annotationsLength == 0) {
				System.err.println("Nada que modificar.");
				return;
			}
			
			fieldToModify = scanner.readLine("Campo a modificar", "exit");
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
				if (read ==  null) {
					System.err.println("No se ha asignado valor porque es nulo.");
//					break;
				}
				try {
					field.set(obj, read);
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
