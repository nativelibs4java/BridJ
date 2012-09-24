/**
 * 
 */
package org.bridj;

import java.io.*;
import java.util.regex.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

import org.bridj.demangling.Demangler.DemanglingException;
import org.bridj.demangling.Demangler.MemberRef;
import org.bridj.demangling.Demangler.Symbol;
import org.bridj.ann.Virtual;
import org.bridj.demangling.GCC4Demangler;
import org.bridj.demangling.VC9Demangler;
import java.lang.reflect.Type;
import static org.bridj.Pointer.*;
import static org.bridj.util.AnnotationUtils.*;

import java.util.Collection;
import org.bridj.Platform.DeleteFiles;
import org.bridj.demangling.Demangler;
import org.bridj.util.ProcessUtils;
import org.bridj.util.StringUtils;

/**
 * Representation of a native shared library, with symbols retrieval / matching facilities.<br>
 * This class is not meant to be used by end users, it's used by pluggable runtimes instead.
 * @author ochafik
 */
public class NativeLibrary {
	volatile long handle, symbols;
	String path;
    final File canonicalFile;
	//Map<Class<?>, long[]> callbacks = new HashMap<Class<?>, long[]>();
	NativeEntities nativeEntities = new NativeEntities();
	
	Map<Long, Symbol> addrToName;
	Map<String, Symbol> nameToSym;
//	Map<String, Long> nameToAddr;
	
    
	protected NativeLibrary(String path, long handle, long symbols) throws IOException {
		this.path = path;
		this.handle = handle;
		this.symbols = symbols;
        this.canonicalFile = path == null ? null : new File(path).getCanonicalFile();
        
        Platform.addNativeLibrary(this);
	}
	
	long getSymbolsHandle() {
		return symbols;
	}
	NativeEntities getNativeEntities() {
		return nativeEntities;
	}
	static String followGNULDScript(String path) {
		try {
			Reader r = new FileReader(path);
			try {
				char c;
				while ((c = (char)r.read()) == ' ' || c == '\t' || c == '\n') {}
				if (c == '/' && r.read() == '*') {
					BufferedReader br = new BufferedReader(r);
					r = br;
					String line;
					StringBuilder b = new StringBuilder("/*");
					while ((line = br.readLine()) != null)
						b.append(line).append('\n');
					String src = b.toString();
					Pattern ldGroupPattern = Pattern.compile("GROUP\\s*\\(\\s*([^\\s)]+)[^)]*\\)");
					Matcher m = ldGroupPattern.matcher(src);
					if (m.find()) {
						String actualPath = m.group(1);
						if (BridJ.verbose)
                            BridJ.info("Parsed LD script '" + path + "', found absolute reference to '" + actualPath + "'");
						return actualPath;
					} else {
						BridJ.error("Failed to parse LD script '" + path + "' !");
					}
				}
			} finally {
				r.close();
			}
		} catch (Throwable th) {
			BridJ.error("Unexpected error: " + th, th);
		}
		return path;
	}
	public static NativeLibrary load(String path) throws IOException {
		long handle = 0;
		File file = new File(path);
		boolean exists = file.exists();
		if (file.isAbsolute() && !exists)
			return null;
		
		if (Platform.isUnix() && exists)
			path = followGNULDScript(path);
		
		handle = JNI.loadLibrary(path);
		if (handle == 0)
			return null;
		long symbols = JNI.loadLibrarySymbols(path);
		return new NativeLibrary(path, handle, symbols);
	}
	
	/*public boolean methodMatchesSymbol(Class<?> declaringClass, Method method, String symbol) {
		return symbol.contains(method.getName()) && symbol.contains(declaringClass.getSimpleName());
	}*/
	
	long getHandle() {
		if (path != null && handle == 0)
			throw new RuntimeException("Library was released and cannot be used anymore");
		return handle;
	}
	@Override
	protected void finalize() throws Throwable {
		release();
	}
	public synchronized void release() {
		if (handle == 0)
			return;
		
		if (BridJ.verbose)
			BridJ.info("Releasing library '" + path + "'");
		
		nativeEntities.release();
		
		JNI.freeLibrarySymbols(symbols);
		JNI.freeLibrary(handle);
		handle = 0;
        
        if (canonicalFile != null && Platform.temporaryExtractedLibraryCanonicalFiles.remove(canonicalFile)) {
            if (canonicalFile.delete()) {
                if (BridJ.verbose)
                    BridJ.info("Deleted temporary library file '" + canonicalFile + "'");
            } else
                BridJ.error("Failed to delete temporary library file '" + canonicalFile + "'");
        }
            
	}
    public Pointer<?> getSymbolPointer(String name) {
        return pointerToAddress(getSymbolAddress(name));
    }
	public long getSymbolAddress(String name) {
		if (nameToSym != null) {
			Symbol addr = nameToSym.get(name);
//			long addr = nameToAddr.get(name);
//			if (addr != 0)
			if (addr != null)
				return addr.getAddress();
		}
		long address = JNI.findSymbolInLibrary(getHandle(), name);
		if (address == 0)
			address = JNI.findSymbolInLibrary(getHandle(), "_" + name);
		return address;
	}

    public synchronized Symbol getSymbol(AnnotatedElement member) throws FileNotFoundException {
    	org.bridj.ann.Symbol mg = getAnnotation(org.bridj.ann.Symbol.class, member);
    	String name = null;
    	if (member instanceof Member)
            name = ((Member)member).getName();
        
        List<String> names = new ArrayList<String>();
        if (mg != null)
        		names.addAll(Arrays.asList(mg.value()));
        	if (name != null)
        		names.add(name);
        	
		for (String n : names)
		{
			Symbol handle = getSymbol(n);
			if (handle == null)
				handle = getSymbol("_" + n);
			if (handle == null)
				handle = getSymbol(n + (Platform.useUnicodeVersionOfWindowsAPIs ? "W" : "A"));
			if (handle != null)
				return handle;
		}
		
        if (member instanceof Method) {
            Method method = (Method)member;
            for (Demangler.Symbol symbol : getSymbols()) {
                if (symbol.matches(method))
                    return symbol;
            }
        }
        return null;
    }
	
	public boolean isMSVC() {
		return Platform.isWindows();
	}
    /** Filter for symbols */
	public interface SymbolAccepter {
        boolean accept(Symbol symbol);
    }
    public Symbol getFirstMatchingSymbol(SymbolAccepter accepter) {
        for (Symbol symbol : getSymbols())
            if (accepter.accept(symbol))
                return symbol;
        return null;
    }
	public Collection<Demangler.Symbol> getSymbols() {
        try {
            scanSymbols();
        } catch (Exception ex) {
            assert BridJ.error("Failed to scan symbols of library '" + path + "'", ex);
        }
		return nameToSym == null ? Collections.EMPTY_LIST : Collections.unmodifiableCollection(nameToSym.values());
	}
	public String getSymbolName(long address) {
		if (addrToName == null && getSymbolsHandle() != 0)//Platform.isUnix())
			return JNI.findSymbolName(getHandle(), getSymbolsHandle(), address);
	
		Demangler.Symbol symbol = getSymbol(address);
		return symbol == null ? null : symbol.getSymbol();
	}
	
	public Symbol getSymbol(long address) {
		try {
			scanSymbols();
			Symbol symbol = addrToName.get(address);
			return symbol;
		} catch (Exception ex) {
			throw new RuntimeException("Failed to get name of address " + address, ex);
		}
	}
	public Symbol getSymbol(String name) {
		try {
			Symbol symbol;
			long addr;
			
			if (nameToSym == null) {// symbols not scanned yet, try without them !
				addr = JNI.findSymbolInLibrary(getHandle(), name);
				if (addr != 0) {
					symbol = new Symbol(name, this);
					symbol.setAddress(addr);
					return symbol;		
				}
			}
            scanSymbols();
            if (nameToSym == null)
                return null;
            
			symbol = nameToSym.get(name);
			if (addrToName == null) {
				if (symbol == null) {
					addr = JNI.findSymbolInLibrary(getHandle(), name);
					if (addr != 0) {
						symbol = new Symbol(name, this);
						symbol.setAddress(addr);
						nameToSym.put(name, symbol);
					}
				}
			}
			return symbol;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
//			throw new RuntimeException("Failed to get symbol " + name, ex);
		}
	}
	void scanSymbols() throws Exception {
		if (addrToName != null)
			return;
		
		nameToSym = new HashMap<String, Symbol>();
//		nameToAddr = new HashMap<String, Long>();
		
		String[] symbs = null;
		if (false) // TODO turn to false !!!
		try {
			if (Platform.isMacOSX()) {
				Process process = java.lang.Runtime.getRuntime().exec(new String[] {"nm", "-gj", path});
				BufferedReader rin = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				List<String> symbsList = new ArrayList<String>();
				while ((line = rin.readLine()) != null) {
					symbsList.add(line);
				}
				symbs = symbsList.toArray(new String[symbsList.size()]);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (symbs == null) {
			//System.out.println("Calling getLibrarySymbols");
			symbs = JNI.getLibrarySymbols(getHandle(), getSymbolsHandle());
			//System.out.println("Got " + symbs + " (" + (symbs == null ? "null" : symbs.length + "") + ")");
		}
		
		if (symbs == null)
			return;
		
		addrToName = new HashMap<Long, Demangler.Symbol>();
		
		boolean is32 = !Platform.is64Bits();
		for (String name : symbs) {
			if (name == null)
				continue;
				
			long addr = JNI.findSymbolInLibrary(getHandle(), name);
			if (addr == 0 && name.startsWith("_")) {
				String n2 = name.substring(1);
				addr = JNI.findSymbolInLibrary(getHandle(), n2);
                if (addr == 0) {
                    n2 = "_" + name;
                    addr = JNI.findSymbolInLibrary(getHandle(), n2);
                }
                if (addr != 0)
                    name = n2;

			}
			if (addr == 0) {
				if (BridJ.verbose)
					BridJ.warning("Symbol '" + name + "' not found.");
				continue;
			}
			//if (is32)
			//	addr = addr & 0xffffffffL;
			//System.out.println("Symbol " + Long.toHexString(addr) + " = '" + name + "'");
			
			Symbol sym = new Demangler.Symbol(name, this);
			sym.setAddress(addr);
			addrToName.put(addr, sym);
			nameToSym.put(name, sym);
			//nameToAddr.put(name, addr);
			//System.out.println("'" + name + "' = \t" + TestCPP.hex(addr) + "\n\t" + sym.getParsedRef());
		}
		if (BridJ.debug) {
			BridJ.info("Found " + nameToSym.size() + " symbols in '" + path + "' :");
			
			for (Symbol sym : nameToSym.values())
				BridJ.info("DEBUG(BridJ): library=\"" + path + "\", symbol=\"" + sym.getSymbol() + "\", address=" + Long.toHexString(sym.getAddress()) + ", demangled=\"" + sym.getParsedRef() + "\""); 
			
			//for (Symbol sym : nameToSym.values())
			//	System.out.println("Symbol '" + sym + "' = " + sym.getParsedRef());
		}
	}

	public MemberRef parseSymbol(String symbol) throws DemanglingException {
        if ("__cxa_pure_virtual".equals(symbol))
            return null;
        
		Demangler demangler;
		if (Platform.isWindows())
			demangler = new VC9Demangler(this, symbol);
		else
			demangler = new GCC4Demangler(this, symbol);
		return demangler.parseSymbol();
	}
}
