package ch.marlovits.unsupportedClassesSniffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * this is a subclass of ObjectInputStream. It is used just to find unresolved
 * classes saved to binary data in the db using
 * ObjectOutputStream.writeObject(). This is used by ExtInfoSniffer
 * 
 * @author marlovitsh
 * 
 */
public class ObjectInputStreamSniffer extends ObjectInputStream {
	/**
	 * the list of unresolved classes
	 */
	public static final HashSet<String> UNRESOLVEDCLASSES = new HashSet<String>();

	public ObjectInputStreamSniffer(InputStream arg0) throws IOException {
		super(arg0);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,
			ClassNotFoundException {

		// *** I just try to regularily resolve a class calling super. If it
		// can't be resolved, then it is a class not defined anywhere. This
		// produces an exception catched by the catch clause where I save this
		// class to the field UNRESOLVEDCLASSES
		try {
			Class<?> result = super.resolveClass(desc);
		} catch (Exception ex1) {
			UNRESOLVEDCLASSES.add(desc.getName());
		}

		return super.resolveClass(desc);
	}
}