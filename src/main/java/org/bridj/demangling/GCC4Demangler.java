package org.bridj.demangling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bridj.CLong;
import org.bridj.NativeLibrary;
import org.bridj.demangling.Demangler.ClassRef;
import org.bridj.demangling.Demangler.DemanglingException;
import org.bridj.demangling.Demangler.Ident;
import org.bridj.demangling.Demangler.MemberRef;
import org.bridj.demangling.Demangler.NamespaceRef;
import org.bridj.demangling.Demangler.TypeRef;
import org.bridj.demangling.Demangler.SpecialName;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bridj.demangling.Demangler.IdentLike;

public class GCC4Demangler extends Demangler {

    public GCC4Demangler(NativeLibrary library, String symbol) {
        super(library, symbol);
    }
    private Map<String, List<IdentLike>> prefixShortcuts = new HashMap<String, List<IdentLike>>() {

        {

            // prefix shortcut: e.g. St is for std::
            put("t", Arrays.asList((IdentLike) new Ident("std")));
            put("a", Arrays.asList((IdentLike) new Ident("std"), new Ident("allocator")));
            put("b", Arrays.asList((IdentLike) new Ident("std"), new Ident("basic_string")));
            TypeRef chartype = classType(Byte.TYPE);
            ClassRef charTraitsOfChar = enclosed("std", new ClassRef(new Ident("char_traits", new TemplateArg[]{chartype})));
            ClassRef allocatorOfChar = enclosed("std", new ClassRef(new Ident("allocator", new TemplateArg[]{chartype})));
            put("d", Arrays.asList((IdentLike) new Ident("std"), new Ident("basic_iostream", new TemplateArg[]{chartype, charTraitsOfChar})));
            put("i", Arrays.asList((IdentLike) new Ident("std"), new Ident("basic_istream", new TemplateArg[]{chartype, charTraitsOfChar})));
            put("o", Arrays.asList((IdentLike) new Ident("std"), new Ident("basic_ostream", new TemplateArg[]{chartype, charTraitsOfChar})));
            // Ss == std::string == std::basic_string<char, std::char_traits<char>, std::allocator<char> >        
            put("s", Arrays.asList((IdentLike) new Ident("std"), new Ident("basic_string", new TemplateArg[]{classType(Byte.TYPE), charTraitsOfChar, allocatorOfChar})));

            // used, as an helper: for i in a b c d e f g h i j k l m o p q r s t u v w x y z; do c++filt _Z1_S$i; done
        }

        private ClassRef enclosed(String ns, ClassRef classRef) {
            classRef.setEnclosingType(new NamespaceRef(new Ident(ns)));
            return classRef;
        }
    };
    private Set<String> shouldContinueAfterPrefix = new HashSet<String>(Arrays.asList("t"));
    private Map<String, TypeRef> typeShortcuts = new HashMap<String, TypeRef>();
    
    private <T> T ensureOfType(Object o, Class<T> type) throws DemanglingException {
        if (type.isInstance(o)) {
            return type.cast(o);
        } else {
            throw new DemanglingException("Internal error in demangler: trying to cast to " + type.getCanonicalName() + " the object '" + o.toString() + "'");
        }
    }
    int nextShortcutId = -1;

    private String nextShortcutId() {
        int n = nextShortcutId++;
        return n == -1 ? "_" : Integer.toString(n, 36).toUpperCase() + "_";
    }

    private TypeRef parsePointerType() throws DemanglingException {
        TypeRef pointed = parseType();
        TypeRef res = pointerType(pointed);
        String id = nextShortcutId();
        typeShortcuts.put(id, res);
        return res;
    }

    public TemplateArg parseTemplateArg() throws DemanglingException {
        if (consumeCharIf('L')) {
            TypeRef tr = parseType();
            StringBuffer b = new StringBuffer();
            char c;
            while (Character.isDigit(c = peekChar())) {
                consumeChar();
                b.append(c);
            }
            expectChars('E');
            // TODO switch on type !
            return new Constant(Integer.parseInt(b.toString()));
        } else {
            return parseType();
        }
    }

    public TypeRef parseType() throws DemanglingException {
        if (Character.isDigit(peekChar())) {
            Ident name = ensureOfType(parseNonCompoundIdent(), Ident.class);
            String id = nextShortcutId(); // we get the id before parsing the part (might be template parameters and we need to get the ids in the right order)
            TypeRef res = simpleType(name);
            typeShortcuts.put(id, res);
            return res;
        }

        char c = consumeChar();
        switch (c) {
            case 'S': { // here we first check if we have a type shorcut saved, if not we fallback to the (compound) identifier case
                char cc = peekChar();
                int delta = 0;
                if (Character.isDigit(cc) || Character.isUpperCase(cc) || cc == '_') {
                    String id = "";
                    while ((c = peekChar()) != '_' && c != 0) {
                        id += consumeChar();
                        delta++;
                    }
                    if (peekChar() == 0) {
                        throw new DemanglingException("Encountered a unexpected end in gcc mangler shortcut '" + id + "' " + prefixShortcuts.keySet());
                    }
                    id += consumeChar(); // the '_'
                    delta++;
                    if (typeShortcuts.containsKey(id)) {
                        if (peekChar() != 'I') {
                            // just a shortcut
                            return typeShortcuts.get(id);
                        } else {
                            // shortcut but templated
                            List<IdentLike> nsPath = new ArrayList<IdentLike>(prefixShortcuts.get(id));
                            String templatedId = parsePossibleTemplateArguments(nsPath);
                            if (templatedId != null) {
                                return typeShortcuts.get(templatedId);
                            }
                        }
                    }
                    position -= delta;
                }
            }
            // WARNING/INFO/NB: we intentionally continue to the N case
            case 'N':
                position--; // I actually would peek()
            {
                List<IdentLike> ns = new ArrayList<IdentLike>();
                String newShortcutId = parseSimpleOrComplexIdentInto(ns, false);
                ClassRef res = new ClassRef(ensureOfType(ns.remove(ns.size() - 1), Ident.class));
                if (!ns.isEmpty()) {
                    res.setEnclosingType(new NamespaceRef(ns.toArray()));
                }
                if (newShortcutId != null) {
                    typeShortcuts.put(newShortcutId, res);
                }
                return res;
            }
            case 'P':
                return parsePointerType();
            case 'F':
                // TODO parse function type correctly !!!
                while (consumeChar() != 'E') {
                }

                return null;
            case 'K':
                return parseType();
            case 'v': // char
                return classType(Void.TYPE);
            case 'c':
            case 'a':
            case 'h': // unsigned
                return classType(Byte.TYPE);
            case 'b': // bool
                return classType(Boolean.TYPE);
            case 'l':
            case 'm': // unsigned
                return classType(CLong.class);
            //return classType(Platform.is64Bits() ? Long.TYPE : Integer.TYPE);
            case 'x':
            case 'y': // unsigned
                return classType(Long.TYPE);
            case 'i':
            case 'j': // unsigned
                return classType(Integer.TYPE);
            case 's':
            case 't': // unsigned
                return classType(Short.TYPE);
            case 'f':
                return classType(Float.TYPE);
            case 'd':
                return classType(Double.TYPE);
            case 'z': // varargs
                return classType(Object[].class);
            default:
                throw error("Unexpected type char '" + c + "'", -1);
        }
    }

    String parseName() throws DemanglingException { // parses a plain name, e.g. "4plop" (the 4 is the length)
        char c;
        StringBuilder b = new StringBuilder();
        while (Character.isDigit(c = peekChar())) {
            consumeChar();
            b.append(c);
        }
        int len;
        try {
            len = Integer.parseInt(b.toString());
        } catch (NumberFormatException ex) {
            throw error("Expected a number", 0);
        }
        b.setLength(0);
        for (int i = 0; i < len; i++) {
            b.append(consumeChar());
        }
        return b.toString();
    }

    private String parseSimpleOrComplexIdentInto(List<IdentLike> res, boolean isParsingNonShortcutableElement) throws DemanglingException {
        String newlyAddedShortcutForThisType = null;
        boolean shouldContinue = false;
        boolean expectEInTheEnd = false;
        if (consumeCharIf('N')) { // complex (NB: they don't recursively nest (they actually can within a template parameter but not elsewhere))
            if (consumeCharIf('S')) { // it uses some shortcut prefix or type
                parseShortcutInto(res);
            }
            shouldContinue = true;
            expectEInTheEnd = true;
        } else { // simple
            if (consumeCharIf('S')) { // it uses some shortcut prefix or type
                shouldContinue = parseShortcutInto(res);
            } else {
                res.add(parseNonCompoundIdent());
            }
        }
        if (shouldContinue) {
            do {
                String id = nextShortcutId(); // we get the id before parsing the part (might be template parameters and we need to get the ids in the right order)
                newlyAddedShortcutForThisType = id;
                IdentLike part = parseNonCompoundIdent();
                res.add(part);
                prefixShortcuts.put(id, new ArrayList<IdentLike>(res)); // the current compound name is saved by gcc as a shortcut (we do the same)
                parsePossibleTemplateArguments(res);
            } while (Character.isDigit(peekChar()) || peekChar() == 'C' || peekChar() == 'D');
            if (isParsingNonShortcutableElement) {
                //prefixShortcuts.remove(previousShortcutId()); // correct the fact that we parsed one too much
                nextShortcutId--;
            }
        }
        parsePossibleTemplateArguments(res);
        if (expectEInTheEnd) {
            expectAnyChar('E');
        }
        return newlyAddedShortcutForThisType;
    }

    /**
     * 
     * @param res a list of identlikes with the namespace elements and finished with an Ident which will be replaced by a new one enriched with template info
     * @return null if res was untouched, or the new id created because of the presence of template arguments
     */
    private String parsePossibleTemplateArguments(List<IdentLike> res) throws DemanglingException {
        if (consumeCharIf('I')) {
            List<TemplateArg> args = new ArrayList<TemplateArg>();
            while (!consumeCharIf('E')) {
                args.add(parseTemplateArg());
            }
            String id = nextShortcutId(); // we get the id after parsing the template parameters
            // It is very important that we create a new Ident as the other one has most probably been added as a shortcut and should be immutable from then
            Ident templatedIdent = new Ident(ensureOfType(res.remove(res.size() - 1), Ident.class).toString(), args.toArray(new TemplateArg[args.size()]));
            res.add(templatedIdent);
            prefixShortcuts.put(id, new ArrayList<IdentLike>(res));
            {
                List<IdentLike> ns = new ArrayList<IdentLike>(res);
                ClassRef clss = new ClassRef(ensureOfType(ns.remove(ns.size() - 1), Ident.class));
                if (!ns.isEmpty()) {
                    clss.setEnclosingType(new NamespaceRef(ns.toArray()));
                }
                typeShortcuts.put(id, clss);
            }
            return id;
        }
        return null;
    }

    /**
     * @return whether we should expect more parsing after this shortcut (e.g. std::vector<...> is actually not NSt6vectorI...EE but St6vectorI...E (without trailing N)
     */
    private boolean parseShortcutInto(List<IdentLike> res) throws DemanglingException {
        char c = peekChar();
        // GCC builds shortcuts for each encountered type, they appear in the mangling as: S_, S0_, S1_, ..., SA_, SB_, ..., SZ_, S10_
        if (c == '_') { // we encounter S_
            List<IdentLike> toAdd = prefixShortcuts.get(Character.toString(consumeChar()));
            if (toAdd == null) {
                throw new DemanglingException("Encountered a yet undefined gcc mangler shortcut S_ (first one), i.e. '_' " + prefixShortcuts.keySet());
            }
            res.addAll(toAdd);
            return false;
        } else if (Character.isDigit(c) || Character.isUpperCase(c)) { // memory shorcut S[0-9A-Z]+_
            String id = "";
            while ((c = peekChar()) != '_' && c != 0) {
                id += consumeChar();
            }
            if (peekChar() == 0) {
                throw new DemanglingException("Encountered a unexpected end in gcc mangler shortcut '" + id + "' " + prefixShortcuts.keySet());
            }
            id += consumeChar(); // the '_'
            List<IdentLike> toAdd = prefixShortcuts.get(id);
            if (toAdd == null) {
                throw new DemanglingException("Encountered a unexpected gcc mangler shortcut '" + id + "' " + prefixShortcuts.keySet());
            }
            res.addAll(toAdd);
            return false;
        } else if (Character.isLowerCase(c)) { // other, single character built-in shorcuts. We suppose for now that all shortcuts are lower case (e.g. Ss, St, ...)
            String id = Character.toString(consumeChar());
            List<IdentLike> toAdd = prefixShortcuts.get(id);
            if (toAdd == null) {
                throw new DemanglingException("Encountered a unexpected gcc mangler built-in shortcut '" + id + "' " + prefixShortcuts.keySet());
            }
            res.addAll(toAdd);
            return shouldContinueAfterPrefix.contains(id);
        } else {
            throw new DemanglingException("Encountered a unexpected gcc unknown shortcut '" + c + "' " + prefixShortcuts.keySet());
        }
    }

    IdentLike parseNonCompoundIdent() throws DemanglingException { // This is a plain name  with possible template parameters (or special like constructor C1, C2, ...)
        if (consumeCharIf('C')) {
            if (consumeCharIf('1')) {
                return SpecialName.Constructor;
            } else if (consumeCharIf('2')) {
                return SpecialName.SpecialConstructor;
            } else {
                throw error("Unknown constructor type 'C" + peekChar() + "'");
            }
        } else if (consumeCharIf('D')) {
            // see http://zedcode.blogspot.com/2007/02/gcc-c-link-problems-on-small-embedded.html
            if (consumeCharIf('0')) {
                return SpecialName.DeletingDestructor;
            } else if (consumeCharIf('1')) {
                return SpecialName.Destructor;
            } else if (consumeCharIf('2')) {
                return SpecialName.SelfishDestructor;
            } else {
                throw error("Unknown destructor type 'D" + peekChar() + "'");
            }
        } else {
            String n = parseName();
            return new Ident(n);
        }
    }

    @Override
    public MemberRef parseSymbol() throws DemanglingException {
        MemberRef mr = new MemberRef();
        if (!consumeCharIf('_')) {
            mr.setMemberName(new Ident(str));
            return mr;
        }
        consumeCharIf('_');
        expectChars('Z');

        if (consumeCharIf('T')) {
            if (consumeCharIf('V')) {
                mr.setEnclosingType(ensureOfType(parseType(), ClassRef.class));
                mr.setMemberName(SpecialName.VFTable);
                return mr;
            }
            return null; // can be a type info, a virtual table or strange things like that
        }
        /*
        Reverse engineering of C++ operators :
        delete[] = __ZdaPv
        delete  = __ZdlPv
        new[] = __Znam
        new = __Znwm
         */
        if (consumeCharsIf('d', 'l', 'P', 'v')) {
            mr.setMemberName(SpecialName.Delete);
            return mr;
        }
        if (consumeCharsIf('d', 'a', 'P', 'v')) {
            mr.setMemberName(SpecialName.DeleteArray);
            return mr;
        }
        if (consumeCharsIf('n', 'w', 'm')) {
            mr.setMemberName(SpecialName.New);
            return mr;
        }
        if (consumeCharsIf('n', 'a', 'm')) {
            mr.setMemberName(SpecialName.NewArray);
            return mr;
        }

        {
            List<IdentLike> ns = new ArrayList<IdentLike>();
            parseSimpleOrComplexIdentInto(ns, true);
            mr.setMemberName(ns.remove(ns.size() - 1));
            if (!ns.isEmpty()) {
                ClassRef parent = new ClassRef(ensureOfType(ns.remove(ns.size() - 1), Ident.class));
                if (mr.getMemberName() == SpecialName.Constructor || mr.getMemberName() == SpecialName.SpecialConstructor)
                    typeShortcuts.put(nextShortcutId(), parent);
                if (!ns.isEmpty()) {
                    parent.setEnclosingType(new NamespaceRef(ns.toArray()));
                }
                mr.setEnclosingType(parent);
            }
        }

        //System.out.println("mr = " + mr + ", peekChar = " + peekChar());

        //mr.isStatic =
        //boolean isMethod = consumeCharIf('E');

        if (consumeCharIf('v')) {
            if (position < length) {
                error("Expected end of symbol", 0);
            }
            mr.paramTypes = new TypeRef[0];
        } else {
            List<TypeRef> paramTypes = new ArrayList<TypeRef>();
            while (position < length) {// && !consumeCharIf('E')) {
                paramTypes.add(parseType());
            }
            mr.paramTypes = paramTypes.toArray(new TypeRef[paramTypes.size()]);
        }
        return mr;
    }
}
