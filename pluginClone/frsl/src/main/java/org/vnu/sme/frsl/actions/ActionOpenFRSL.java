package org.vnu.sme.frsl.actions;

import java.util.Collection;

import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseModelApi;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.tzi.use.uml.mm.MAssociationClass;
import org.tzi.use.uml.mm.MClass;
import org.tzi.use.uml.mm.MModel;
import org.vnu.sme.frsl.gui.FrslForm;

public class ActionOpenFRSL implements IPluginActionDelegate {

	private boolean isLoad = false;


	@Override
	public void performAction(IPluginAction pluginAction) {
		Session fSession = pluginAction.getSession();
		MainWindow fMainWindow = pluginAction.getParent();
		if (!isLoad) {
			// Add _DomainClass and _Tracks.
				MModel model = fSession.system().model();
				UseModelApi useModelApi = new UseModelApi(model);
				add_DomainClass(useModelApi, model);
				add_Tracks(useModelApi, model);
				isLoad = true;
		}

		FrslForm fParamForm = new FrslForm(fSession, fMainWindow);
		fParamForm.setResizable(true);
        fParamForm.setVisible(true);

	}

	public static void add_DomainClass(UseModelApi useModelApi, MModel domainModel) {
		// Add classes: _DomainClass and _Tracks.
		// Interact directly to USE model (refer to RTL for more information).
		MClass _DomainClass = null;

		if (useModelApi.getClass("_DomainClass") == null)
		try {
			_DomainClass = useModelApi.createClass("_DomainClass", false);
			useModelApi.createAttribute("_DomainClass", "isProblDom", "Boolean");
		} catch (UseApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Collection<MClass> domainModelClasses = domainModel.classes();
		for (MClass mc : domainModelClasses) {
			try {
				// Inherit every mc <- _DomainClass (except _DomainClass itself).
				// TODO: Check if multiple inheritance is allowed.
				// For example: mc1 <- _DomainClass, mc2 <- mc1;
				// but this algorithm will also make mc2 <- _DomainClass.
				if (mc != _DomainClass && !(mc instanceof MAssociationClass)) {
					useModelApi.createGeneralizationEx(mc, _DomainClass);
				}
			} catch (UseApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void add_Tracks(UseModelApi useModelApi, MModel domainModel) {
		/*
		 * This is the method signature of createAssociation in UseModelApi class:
		 * 
		 * public MAssociation createAssociation(
		 * String associationName,
		 * String end1ClassName, String end1RoleName,
		 * String end1Multiplicity, int end1Aggregation,
		 * String end2ClassName, String end2RoleName,
		 * String end2Multiplicity, int end2Aggregation
		 * ) throws UseApiException { ... }
		 * 
		 */
		try {
			useModelApi.createAssociation("_Tracks", "_DomainClass", "problObj", "0..1", 0, "_DomainClass", "swObj",
					"0..1", 0);
		} catch (UseApiException e) {
			e.printStackTrace();
		}
	}
	

}
