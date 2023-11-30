package com.hbi.wc.flexbom.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

import wt.fc.WTObject;
//import wt.part.WTPartMaster;
import wt.util.WTException;

public class HBISpecificationPDFGenUtil {
	
	private static String BASIC_CUT_AND_SEW_GARMENT = LCSProperties.get("com.hbi.wc.flexbom.util.HBIPatternSpec.specOwnerType", "Product\\BASIC CUT & SEW - GARMENT");
	private static String BASIC_CUT_AND_SEW_PATTERN = LCSProperties.get("com.hbi.wc.flexbom.util.HBIPatternSpec.parentSpecOwnerType", "Product\\BASIC CUT & SEW - PATTERN");
	private static Boolean PATTERNSPEC_CONSTRUCTION_ENABLED = LCSProperties.getBoolean("com.hbi.wc.flexbom.util.HBIPatternSpec.Construction.Enabled");
	private static Boolean PATTERNSPEC_MEASUREMENT_ENABLED = LCSProperties.getBoolean("com.hbi.wc.flexbom.util.HBIPatternSpec.Measurement.Enabled");
	private static Boolean PATTERNSPEC_IMAGES_ENABLED = LCSProperties.getBoolean("com.hbi.wc.flexbom.util.HBIPatternSpec.Images.Enabled");
	private static String FGM = LCSProperties.get("com.hbi.wc.flexbom.util.HBISpecificationPDFGenUtil.fgm", "FGM");
	private static String GM = LCSProperties.get("com.hbi.wc.flexbom.util.HBISpecificationPDFGenUtil.gm", "GM");
	public static String TYPE_COMP_DELIM = "-:-";
	
	public static String getParentSpecId(FlexSpecification spec) throws WTException{
		
		String parentSpecId = null;
		if(spec != null) {
		Collection<?> parents = FlexSpecQuery.findSpecToSpecLinks(spec, null);
	        if(parents != null && parents.size() > 0){
	            FlexObject link = (FlexObject)parents.iterator().next();
	            parentSpecId = "VR:com.lcs.wc.specification.FlexSpecification:" + link.getString("PARENTSPECIFICATION.BRANCHIDITERATIONINFO");
			}
		}
		return parentSpecId;
	}
	
	public static ArrayList<FlexObject> getParentSpecComponents(String parentSpecId) throws WTException {
		ArrayList<FlexObject> parentSpecComponents = new ArrayList<FlexObject>();
		Collection<?> parentComponents = null;
		FlexObject compFO = null;

		if(FormatHelper.hasContent(parentSpecId)) {
			FlexSpecification parentFlexSpec = (FlexSpecification)FlexSpecQuery.findObjectById(parentSpecId);
			if(isParentSpecOwnerPattern(parentFlexSpec)) {

				parentComponents = FlexSpecQuery.getSpecToComponentObjectsData(parentFlexSpec);

				Iterator<?> compIterator = parentComponents.iterator();
				  while(compIterator.hasNext()){
					  compFO = (FlexObject)compIterator.next();
					  if(PATTERNSPEC_CONSTRUCTION_ENABLED && compFO.getData("COMPONENT_TYPE").equals("Construction")) {
						  parentSpecComponents.add(compFO);
					  }
					  if(PATTERNSPEC_MEASUREMENT_ENABLED && compFO.getData("COMPONENT_TYPE").equals("Measurements")) {
						  parentSpecComponents.add(compFO);
					  }	
					  if(PATTERNSPEC_IMAGES_ENABLED && compFO.getData("COMPONENT_TYPE").equals("Images Page")) {
						  parentSpecComponents.add(compFO);
					  }						  
				  }
				  
			}
					
		}
		return parentSpecComponents;		
	}	
	
	public static ArrayList<FlexObject> getParentSpecComponentsForChooser(String parentSpecId) throws WTException {
		ArrayList<FlexObject> parentSpecComponents = new ArrayList<FlexObject>();
		Collection<?> parentComponents = null;
		FlexObject compFO = null;

		if(FormatHelper.hasContent(parentSpecId)) {
			FlexSpecification parentFlexSpec = (FlexSpecification)FlexSpecQuery.findObjectById(parentSpecId);
			if(isParentSpecOwnerPattern(parentFlexSpec)) {

				parentComponents = FlexSpecQuery.getSpecToComponentObjectsData(parentFlexSpec);

				Iterator<?> compIterator = parentComponents.iterator();
				  while(compIterator.hasNext()){
					  compFO = (FlexObject)compIterator.next();
					  if(PATTERNSPEC_CONSTRUCTION_ENABLED && compFO.getData("COMPONENT_TYPE").equals("Construction")) {
						  compFO.setData("COMPONENT_TYPE", "Pattern Construction");
						  parentSpecComponents.add(compFO);
					  }
					  if(PATTERNSPEC_MEASUREMENT_ENABLED && compFO.getData("COMPONENT_TYPE").equals("Measurements")) {
						  compFO.setData("COMPONENT_TYPE", "Pattern Measurements");
						  parentSpecComponents.add(compFO);
					  }	
					  if(PATTERNSPEC_IMAGES_ENABLED && compFO.getData("COMPONENT_TYPE").equals("Images Page")) {
						  compFO.setData("COMPONENT_TYPE", "Pattern Images Page");
						  parentSpecComponents.add(compFO);
					  }						  
				  }
				  
			}
					
		}
		return parentSpecComponents;		
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<FlexObject> getSpecComponents(String specId) throws WTException {
		ArrayList<FlexObject> specComponents = new ArrayList<FlexObject>();
		Collection<FlexObject> components = null;
		FlexObject compFO = null;

		if(FormatHelper.hasContent(specId)) {
			FlexSpecification flexSpec = (FlexSpecification)FlexSpecQuery.findObjectById(specId);

			if(flexSpec != null && isSpecOwnerGarment(flexSpec)) {

				components = FlexSpecQuery.getSpecToComponentObjectsData(flexSpec);

				if(components != null) {
					Iterator<?> compIterator = components.iterator();
				  while(compIterator.hasNext()){
					  compFO = (FlexObject)compIterator.next();
					  specComponents.add(compFO);
				  }
				}
				  
			}
					
		}
		return (ArrayList<FlexObject>) specComponents;		
	}	

	@SuppressWarnings("unchecked")
	public static ArrayList<String> getPatternSpecs(FlexSpecification spec) throws WTException{
		ArrayList<String> patternSpecKeys = new ArrayList<String>();
			if(spec != null && isSpecOwnerGarment(spec)) {
					Collection<?> patternSpecs = getParentSpecComponents(getParentSpecId(spec));
					Collection<?> components = getSpecComponents(FormatHelper.getVersionId(spec));
					if(patternSpecs != null) {
						FlexObject compFO = null;
						if(components != null) {
							patternSpecs = cleanUpDuplicateComponents((ArrayList<FlexObject>) patternSpecs,(ArrayList<FlexObject>) components);
						}
						patternSpecs = sortPatternComponents((ArrayList<FlexObject>) patternSpecs);
						Iterator<?> compIterator = patternSpecs.iterator();
						  while(compIterator.hasNext()){
							  compFO = (FlexObject)compIterator.next();
							  patternSpecKeys.add(compFO.getData("COMPONENT_TYPE") + TYPE_COMP_DELIM + compFO.getData("OID"));
						  }
						System.out.println("patternSpecKeys " + patternSpecKeys);
					}				
			} 
		return patternSpecKeys;
	}
	
	public static ArrayList<FlexObject> sortPatternComponents(ArrayList<FlexObject> patternSpecs) {
		ArrayList<FlexObject> orderedPatternSpecs = new ArrayList<FlexObject>();
		ArrayList<FlexObject> measurementsFGMList = new ArrayList<FlexObject>();
		ArrayList<FlexObject> measurementsList = new ArrayList<FlexObject>();
		ArrayList<FlexObject> imagesMeasurementsList = new ArrayList<FlexObject>();
		ArrayList<FlexObject> constructionList = new ArrayList<FlexObject>();
		ArrayList<FlexObject> imagesConstructionList = new ArrayList<FlexObject>();
		ArrayList<FlexObject> imagesPlacementList = new ArrayList<FlexObject>();
		ArrayList<FlexObject> imagesMarkerList = new ArrayList<FlexObject>();
		for(FlexObject patternFO : patternSpecs) {
			if(patternFO.getData("COMPONENT_TYPE").equals("Measurements") && patternFO.getData("NAME").indexOf(FGM) != -1) {
				measurementsFGMList.add(patternFO);			
			} 
			else if(patternFO.getData("COMPONENT_TYPE").equals("Measurements") && patternFO.getData("NAME").indexOf(GM) != -1) {
				measurementsFGMList.add(patternFO);		
			}
			else if(patternFO.getData("COMPONENT_TYPE").equals("Measurements")) {
				measurementsList.add(patternFO);	
			} else if(patternFO.getData("COMPONENT_TYPE").equals("Images Page") &&
					patternFO.getData("IMAGES_PAGE_TYPE").equals("measurements")) {
				imagesMeasurementsList.add(patternFO);
			} else if(patternFO.getData("COMPONENT_TYPE").equals("Construction")) {
				constructionList.add(patternFO);
			} else if(patternFO.getData("COMPONENT_TYPE").equals("Images Page") && 
					patternFO.getData("IMAGES_PAGE_TYPE").equals("construction")) {
				imagesConstructionList.add(patternFO);
			} else if(patternFO.getData("COMPONENT_TYPE").equals("Images Page") && 
					patternFO.getData("IMAGES_PAGE_TYPE").equals("placementDetails")) {
				imagesPlacementList.add(patternFO);
			} else if(patternFO.getData("COMPONENT_TYPE").equals("Images Page") && 
					patternFO.getData("IMAGES_PAGE_TYPE").equals("markerLayout")) {
				imagesMarkerList.add(patternFO);
			} else if(patternFO.getData("COMPONENT_TYPE").equals("Images Page")) {
				imagesConstructionList.add(patternFO);
			}
		}
		orderedPatternSpecs.addAll(measurementsFGMList);
		orderedPatternSpecs.addAll(measurementsList);
		orderedPatternSpecs.addAll(imagesMeasurementsList);
		orderedPatternSpecs.addAll(constructionList);
		orderedPatternSpecs.addAll(imagesConstructionList);
		orderedPatternSpecs.addAll(imagesPlacementList);
		orderedPatternSpecs.addAll(imagesMarkerList);		
		return orderedPatternSpecs;
	}
	
	/*public static String getPatternSpecOptions(String specId) throws WTException{
		System.out.println("Inside Gen Util " + specId);
		StringBuffer strPatternSpecOptions = new StringBuffer();
		if(FormatHelper.hasContent(specId)) {
			FlexSpecification spec = (FlexSpecification)FlexSpecQuery.findObjectById(specId);
			if(spec != null && isSpecOwnerGarment(spec)) {
					Collection<?> patternSpecs = getParentSpecComponents(getParentSpecId(spec));
					Collection<?> components = getSpecComponents(specId);
					if(patternSpecs != null) {
						FlexObject compFO = null;
						System.out.println("<<PatternSpecs >> " + patternSpecs.size());
						System.out.println("<<components>> " + components.size());
						if(components != null) {
							patternSpecs = cleanUpDuplicateComponents((ArrayList<FlexObject>) patternSpecs,(ArrayList<FlexObject>) components);
						}
						System.out.println("<<PatternSpecs >> " + patternSpecs.size());
						Iterator<?> compIterator = patternSpecs.iterator();
						  while(compIterator.hasNext()){
							  compFO = (FlexObject)compIterator.next();
							  strPatternSpecOptions.append(MOAHelper.DELIM + compFO.getData("COMPONENT_TYPE") + TYPE_COMP_DELIM + compFO.getData("OID"));
						  }
						System.out.println("strPatternSpecOptions " + strPatternSpecOptions.toString());
					}				
			} }
		return strPatternSpecOptions.toString();
	}	*/
	
	public static ArrayList<FlexObject> cleanUpDuplicateComponents(ArrayList<FlexObject> parentSpecComponents, ArrayList<FlexObject> allSpecComponents) {
		Iterator<?> compIterator = parentSpecComponents.iterator();
		ArrayList<FlexObject> parentDistinctSpecs = new ArrayList<FlexObject>();
		boolean removed = false;
		while(compIterator.hasNext()) {
			FlexObject parentFO = (FlexObject) compIterator.next();
			for(FlexObject compFO : allSpecComponents) {
				if(parentFO.getData("OID").equals(compFO.getData("OID"))) {
					System.out.println("parentFO to be removed" + parentFO);
					removed = true;
				}				
			}
			if(!removed) {
				parentDistinctSpecs.add(parentFO);
			} else {
				removed = false;
			}
		}
		return parentDistinctSpecs;
	}
	
	public static Boolean isParentSpecOwnerPattern(FlexSpecification parentSpec) throws WTException {
		if(parentSpec != null) {
		    LCSPartMaster ownerMaster = parentSpec.getSpecOwner();
		    WTObject owner = (WTObject)VersionHelper.latestIterationOf(ownerMaster);
		    if(!(owner instanceof LCSProduct)) {
		    	throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + owner);
		    }
		    LCSProduct product = (LCSProduct)owner;

		    if((BASIC_CUT_AND_SEW_PATTERN).indexOf(product.getFlexType().getFullName()) != -1) {
		    	return true;
		    }
		}
		return false;
	}
	
	public static Boolean isSpecOwnerGarment(FlexSpecification spec) throws WTException {
		if(spec != null) {
		    LCSPartMaster ownerMaster = spec.getSpecOwner();
		    WTObject owner = (WTObject)VersionHelper.latestIterationOf(ownerMaster);
		    if(!(owner instanceof LCSProduct)) {
		    	throw new WTException("Can not use PDFProductSpecification on a non-LCSProduct - " + owner);
		    }
		    LCSProduct product = (LCSProduct)owner;
		    if((BASIC_CUT_AND_SEW_GARMENT).indexOf(product.getFlexType().getFullName()) != -1) {
		    	return true;
		    }
		}
		return false;
	}	

}
