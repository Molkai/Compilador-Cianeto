/*
Nomes:                                      RA:
Lucca La Fonte Albuquerque Carvalho         726563
Vinícius de Souza Carvalho                  726592
*/

package comp;

import java.util.Hashtable;


public class SymbolTable {
    public SymbolTable() {
        globalTable = new Hashtable<String, Object>();
        localTable  = new Hashtable<String, Object>();
        superClassTable = new Hashtable<String, Object>();
        classTable = new Hashtable<String, Object>();
    }

    public Object putInGlobal( String key, Object value ) {
       return globalTable.put(key, value);
    }

    public Object putInLocal( String key, Object value ) {
       return localTable.put(key, value);
    }

    public Object putInSuperClass( String key, Object value ) {
       return superClassTable.put(key, value);
    }

    public Object putInClass( String key, Object value ) {
       return classTable.put(key, value);
    }

    public Object getInLocal( String key ) {
       return localTable.get(key);
    }

    public Object getInGlobal( String key ) {
       return globalTable.get(key);
    }

    public Object getInSuperClass( String key ) {
       return superClassTable.get(key);
    }

    public Object getInClass( String key ) {
       return classTable.get(key);
    }

    public Object get( String key ) {
        // returns the object corresponding to the key.
        Object result;
        if ( (result = localTable.get(key)) != null ) {
              // found local identifier
            return result;
        }
        else {
              // global identifier, if it is in globalTable
            return globalTable.get(key);
        }
    }

    public void removeLocalIdent() {
           // remove all local identifiers from the table
         localTable.clear();
    }

    public void removeClassIdent() {
           // remove all local identifiers from the table
         superClassTable.clear();
         classTable.clear();
    }


    private Hashtable<String, Object> globalTable, localTable, superClassTable, classTable;
}
