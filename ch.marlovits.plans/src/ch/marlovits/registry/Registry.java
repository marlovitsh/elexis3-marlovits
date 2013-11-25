package ch.marlovits.registry;

import java.lang.reflect.Method;
import java.util.prefs.Preferences;

import ch.rgw.io.SysSettings;

public class Registry {
	
	private static final int HKEY_CURRENT_USER = 0x80000001;
	private static final int KEY_QUERY_VALUE = 1;
	private static final int KEY_SET_VALUE = 2;
	private static final int KEY_READ = 0x20019;
	
	public static void testit(){
		String[] rootNodes = {
			"HKEY_CLASSES_ROOT", "HKEY_CURRENT_USER", "HKEY_LOCAL_MACHINE"
		};
		
		final Preferences userRoot = Preferences.userRoot();
		// userRoot = HKEY_CURRENT_USER
		
		final Preferences systemRoot = Preferences.systemRoot();
		final Class clz = userRoot.getClass();
		try {
			// *** get the methods needed
			final Method openKey =
				clz.getDeclaredMethod("openKey", byte[].class, int.class, int.class);
			openKey.setAccessible(true);
			final Method closeKey = clz.getDeclaredMethod("closeKey", int.class);
			closeKey.setAccessible(true);
			final Method winRegQueryValue =
				clz.getDeclaredMethod("WindowsRegQueryValueEx", int.class, byte[].class);
			winRegQueryValue.setAccessible(true);
			final Method winRegEnumValue =
				clz.getDeclaredMethod("WindowsRegEnumValue1", int.class, int.class, int.class);
			winRegEnumValue.setAccessible(true);
			final Method winRegQueryInfo =
				clz.getDeclaredMethod("WindowsRegQueryInfoKey1", int.class);
			winRegQueryInfo.setAccessible(true);
			
			final Method windowsRegEnumKeyEx =
				clz.getDeclaredMethod("WindowsRegEnumKeyEx", int.class, int.class, int.class);
			windowsRegEnumKeyEx.setAccessible(true);
			
			// Query Internet Settings for Proxy
// String key1 = "HKEY_CLASSES_ROOT";
// handle = (Integer) openKey.invoke(userRoot, toCstr(key1), KEY_READ, KEY_READ);
// valb =
// (byte[]) winRegQueryValue
// .invoke(userRoot, handle.intValue(), toCstr("ProxyServer"));
// vals = (valb != null ? new String(valb).trim() : null);
// System.out.println("Proxy Server = " + vals);
// closeKey.invoke(Preferences.userRoot(), handle);
			
			byte[] valb = null;
			String vals = null;
			String key = null;
			Integer handle = -1;
			
			// Query Internet Settings for Proxy
			key = "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
			handle = (Integer) openKey.invoke(userRoot, toCstr(key), KEY_READ, KEY_READ);
			
			int someValue = 0;
			int someValue2 = 50;
			byte[] valb1 = null;
			String vals1 = null;
			for (int i = 0; i < 20; i++) {
				valb1 = (byte[]) windowsRegEnumKeyEx.invoke(handle, handle, i, someValue2);
				vals1 = (valb1 != null ? new String(valb1).trim() : null);
				System.out.println(vals1);
				
			}
			
			valb =
				(byte[]) winRegQueryValue
					.invoke(userRoot, handle.intValue(), toCstr("ProxyServer"));
			vals = (valb != null ? new String(valb).trim() : null);
			System.out.println("Proxy Server = " + vals);
			closeKey.invoke(Preferences.userRoot(), handle);
			
			// Query for IE version
			key = "SOFTWARE\\Microsoft\\Internet Explorer";
			handle = (Integer) openKey.invoke(systemRoot, toCstr(key), KEY_READ, KEY_READ);
			valb = (byte[]) winRegQueryValue.invoke(systemRoot, handle, toCstr("Version"));
			vals = (valb != null ? new String(valb).trim() : null);
			System.out.println("Internet Explorer Version = " + vals);
			closeKey.invoke(Preferences.systemRoot(), handle);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] toCstr(String str){
		byte[] result = new byte[str.length() + 1];
		for (int i = 0; i < str.length(); i++) {
			result[i] = (byte) str.charAt(i);
		}
		result[str.length()] = 0;
		return result;
	}
}