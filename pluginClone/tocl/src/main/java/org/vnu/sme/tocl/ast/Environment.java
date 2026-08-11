package org.vnu.sme.tocl.ast;

import java.util.ArrayList;
import java.util.HashMap;

public class Environment {
    public Environment(Environment p) {
        this.parent = p;
    }

    public static Environment EMPTY_ENV = new Environment(null);

    public Object lookupLocal(String name) {
        return this.elems.get(name);
    }

    public Object lookup(String name) {
        Object defined = this.elems.get(name);
        if (defined != null) return defined;
        else if (this.parent != null) {
            return this.parent.lookup(name);
        }
        else {
            return null;
        }
    }

    public Object lookupPathName(ArrayList<String> path) {
        Object namespaceInEnv = lookupLocal(path.get(0));
        if (namespaceInEnv != null) {
            return null; //?
            /*return nestedEnvironment().addNamespace(namespaceInEnv).lookupPathName()
                .lookupPathName(path.subList(1, path.size()));*/
        }
        else if (this.parent != null) {
            return this.parent.lookupPathName(path);
        }
        else {
            return null;
        }
    }

    public Environment addElement(String name, Object elem) {
        if (elems.get(name) != null) {
            //Throw error
        }
        elems.put(name, elem);
        return this;
    }

    public Environment addEnvironment(Environment env) {
        env.elems.forEach((n,e)-> { this.addElement(n,e); });
        return this;
    }

    public Environment nestedEnvironment() {
        return new Environment(this);
    }

    HashMap<String, Object> elems = new HashMap<String, Object>();
    Environment parent;
}