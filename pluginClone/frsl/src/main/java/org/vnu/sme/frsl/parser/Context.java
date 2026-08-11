package org.vnu.sme.frsl.parser;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.antlr.runtime.Token;
import org.tzi.use.parser.SemanticException;
import org.tzi.use.parser.SrcPos;
import org.tzi.use.parser.Symtable;
import org.tzi.use.uml.ocl.type.Type;
import org.tzi.use.uml.ocl.value.VarBindings;
import org.vnu.sme.frsl.mm.FRSLmodel.FrslModel;
import org.vnu.sme.frsl.mm.FRSLmodel.Usecase;

public class Context extends org.tzi.use.parser.Context {

	/*
	 * Context Properties.
	 */
	private FrslModel frslModel;
	private FrslModelFactory frslModelFactory;

	/*
	 * ----------------------------------------
	 * 
	 * Context Getters.
	 * 
	 * ----------------------------------------
	 */
	public FrslModel getFrslModel() {
		return frslModel;
	}

	public FrslModelFactory getFrslModelFactory() {
		return frslModelFactory;
	}

	public FrslModelFactory modelFactory() {
		return getFrslModelFactory();
	}

	/*
	 * ----------------------------------------
	 * 
	 * Context Setters.
	 * 
	 * ----------------------------------------
	 */
	public void setFrslModel(FrslModel frslModel) {
		this.frslModel = frslModel;
	}

	public void setFrslModelFactory(FrslModelFactory frslModelFactory) {
		this.frslModelFactory = frslModelFactory;
	}

	/*
	 * ----------------------------------------
	 * 
	 * Context Constructors.
	 * 
	 * ----------------------------------------
	 */
	public Context(String filename, PrintWriter err, VarBindings globalBindings, FrslModelFactory factory) {
		super(filename, err, globalBindings, factory);
		this.frslModelFactory = factory;
	}

	/*
	 * ----------------------------------------
	 * 
	 * Other Methods.
	 * 
	 * ----------------------------------------
	 */
	/**
	 * A typeTable().lookup wrapper function that handles null values.
	 * <p>
	 * Casts the found type and return it.
	 * 
	 * @param <T>
	 * @param typeName
	 * @param targetType
	 * @return
	 */
	public <T extends Type> T typeTableLookup(String typeName, Class<T> targetType) {
		// Get the type table.
		Symtable typeTable = this.typeTable();

		// Lookup the type.
		Type foundType = typeTable.lookup(typeName);
		// return null;
		// Handle the result.
		if (foundType != null) {
			return targetType.cast(foundType);
		} else {
			throw new IllegalArgumentException(String.format("Type '%s' not found in type table.", typeName));
		}
	}

	/**
	 * A typeTable().lookup wrapper function that handles null values.
	 * <p>
	 * Returns the found type as "Type".
	 * 
	 * @param typeName
	 * @param couldReturnNull
	 * @return
	 */
	public Type typeTableLookup(String typeName, boolean couldReturnNull) {
		// Get the type table.
		Symtable typeTable = this.typeTable();

		// Lookup the type.
		Type foundType = typeTable.lookup(typeName);

		// Handle the result.
		if (foundType != null) {
			return foundType;
		} else {
			if (couldReturnNull) {
				return null;
			}

			throw new IllegalArgumentException(String.format("Type '%s' not found in type table.", typeName));
		}
	}

	/**
	 * A typeTable().add wrapper function that handles null values.
	 * 
	 * @param typeName
	 * @param targetType
	 * @return
	 */
	public void typeTableAdd(Token fName, Type type) {
		// Get the type table.
		Symtable typeTable = this.typeTable();

		// Try to add the type into the type table.
		// TODO: Refactor if needed.
		try {
			typeTable.add(fName, type);
		} catch (SemanticException e) {
			e.printStackTrace();
		}
	}

	public void typeTableAdd(Token fName, Type type, String useName) {
		// Get the type table.
		Symtable typeTable = this.typeTable();

		// Try to add the type into the type table.
		// TODO: Refactor if needed.
		try {
			typeTable.add(fName.getText() + useName, type, new SrcPos(fName));
		} catch (SemanticException e) {
			e.printStackTrace();
		}
	}
}
