package com.hbi.wc.product;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hbi.wc.interfaces.outbound.product.plugins.HBISPAutomationValidationPlugin;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLogic;
import com.lcs.wc.moa.LCSMOACollectionClientModel;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectLogic;
import com.lcs.wc.moa.LCSMOAObjectQuery;
import com.lcs.wc.moa.LCSMOATable;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.season.LCSSeasonProductLink;
import com.lcs.wc.season.LCSSeasonQuery;
import com.lcs.wc.sizing.ProdSizeCategoryToSeason;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.util.FlexObjectUtil;
import com.lcs.wc.util.FormatHelper;

import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MultiObjectHelper;
import com.lcs.wc.util.VersionHelper;

import wt.enterprise.RevisionControlled;
import wt.fc.WTObject;
import wt.folder.FolderEntry;
import wt.org.WTUser;
//import wt.part.WTPartMaster;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 * @author UST
 * Used in Selling Product Automation project- SP to SAP integration, to lock certain attributes once the product is sent to SAP and set to lock.
 * This will avoid update of attributes when 
 * Product Lock Status = Lock
 * Product Lock Status is at product level. This plugin for Selling Product hierarchy only as of now.
 * To update please remove the drop down value of Product Lock Status and then change.
 * 4th Feb 2020 - Added LCSColor check
 */
public class HBILockUponSAPCreation {
	public static final String PUT_UP_CODE_MOA =LCSProperties.get("com.hbi.wc.interfaces.outbound.product.PUT_UP_CODE_MOA","hbiPutUpCode");
	public static final String PLANT_EXTENSION_MOA =LCSProperties.get("com.hbi.wc.interfaces.outbound.product.PLANT_EXTENSION_MOA","hbiErpPlantExtensions");
	public static final String PLANT_NAME_1 = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiPlantName1","hbiPlantName1");
	public static final String PLANT_TYPE = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiPlantType","hbiPlantType");
	public static final String PRIMARY_DELIVERY_PLANT = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiPrimaryDeliverPlant","hbiPrimaryDeliverPlant");
	public static final String MAX_LOT_SIZE = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiMaxLotSize","hbiMaxLotSize");
	public static final String PLANNED_DELIVERY_TIME = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiPlannedDelTime","hbiPlannedDelTime");
	public static final String PROCUREMENT_TYPE = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiProcurementType","hbiProcurementType");
	public static final String SPECIAL_PROCUREMENT = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiSpecialProcurement","hbiSpecialProcurement");
	public static final String TOTAL_REP_LEAD_TIME = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiTotalRepLeadTme","hbiTotalRepLeadTme");
	public static final String PRODUCT_EDIT_LOCK_STATUS = LCSProperties.get("com.hbi.wc.interfaces.outbound.product.hbiProductEditLockStatus","hbiProductEditLockStatus");
	private static String businessObjectName = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.businessObjectName", "SAPAndAPSValidationAttributes");
	private static String businessObjectType = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.businessObjectType", "Business Object\\Integration\\Validation Attributes");
	private static String hbihbiLockEditKey = LCSProperties.get("com.hbi.wc.product.HBISPAutomationValidationPlugin.hbihbiLockEditKey", "hbiLockEdit");
	private static String hbiErpIntegrationLogKey = LCSProperties.get("com.hbi.wc.product.SeasonProductLink.hbiIntegratinLogs", "hbiIntegratinLogs");
	private static final String MOAOBJECTLOCATION = LCSProperties.get("com.lcs.wc.moa.LCSMOAObject.rootFolder", "/MOAObject");
	
	public static void lockAttributesUponSAPCreation(WTObject wtObj) {
		try {
			//LCSLog.debug("HBILockUponSAPCreation wtObj toString " + wtObj.toString());		
			Map<String, Map<String, String>> validationAttributesMap = new HashMap<String, Map<String, String>>();
			HBISPAutomationValidationPlugin erpInterfaceValidationPluginObj = new HBISPAutomationValidationPlugin();
			LCSLifecycleManaged businessObject = erpInterfaceValidationPluginObj.findValidationBOByNameAndType(businessObjectName, businessObjectType);
			validationAttributesMap = erpInterfaceValidationPluginObj.getValidationAttributesMap(businessObject, hbihbiLockEditKey);
			//Product Locking

			if(wtObj instanceof LCSProduct) {
				LCSProduct prod = (LCSProduct) wtObj;

				boolean productEditLockStatus = getLockStatus(prod);
				
				if(productEditLockStatus) {
					lockAttributes(wtObj,prod, validationAttributesMap.get("Product"));
					LCSLogic.deriveFlexTypeValues(prod);
				}
			}
			//ProdSizeCategoryToSeason locking
			if(wtObj instanceof ProdSizeCategoryToSeason) {
				ProdSizeCategoryToSeason pscts = (ProdSizeCategoryToSeason) wtObj;
				//LCSLog.debug("HBILockUponSAPCreation pscts getOwnership " + pscts.getOwnership());
				//LCSLog.debug("HBILockUponSAPCreation getSizeCategoryMaster PM " + pscts.getSizeCategoryMaster().getProductMaster());
				LCSProduct prod=(LCSProduct)VersionHelper.latestIterationOf( pscts.getSizeCategoryMaster().getProductMaster());
				prod=(LCSProduct)VersionHelper.getVersion(prod, "A")  ;

				//boolean productEditLockStatus = new HBISPAutomationFlexObjectLock().getProductObjectLockStatus(prod);
				boolean productEditLockStatus = getLockStatus(prod);
				//LCSLog.debug("HBILockUponSAPCreation productEditLockStatus "+productEditLockStatus);
				if(productEditLockStatus) {
					String sizeValues ="";
					String prevSizeValues="";

					//LCSLog.debug("HBILockUponSAPCreation pscts " + pscts);
					//LCSLog.debug("HBILockUponSAPCreation getSizeValues " + pscts.getSizeValues());
					sizeValues = pscts.getSizeValues();

					ProdSizeCategoryToSeason prevPscts = (ProdSizeCategoryToSeason) VersionHelper.predecessorOf(pscts);
					prevSizeValues = prevPscts.getSizeValues();
					if(FormatHelper.hasContent(prevSizeValues) && !sizeValues.contains(prevSizeValues)) {
						pscts.setSizeValues(prevSizeValues);

					}
				}
			}


			if(wtObj instanceof LCSMOAObject) {
				LCSMOAObject moaObj = (LCSMOAObject) wtObj;
				//LCSLog.debug("HBILockUponSAPCreation moaObj.getOwner() "+moaObj.getOwner());
				//WTPartMaster productMaster =(WTPartMaster) moaObj.getOwner();
				LCSPartMaster productMaster =(LCSPartMaster) moaObj.getOwner();
				LCSProduct prod=(LCSProduct)VersionHelper.latestIterationOf( productMaster);
				prod=(LCSProduct)VersionHelper.getVersion(prod, "A")  ;
				//boolean productEditLockStatus = new HBISPAutomationFlexObjectLock().getProductObjectLockStatus(prod);
				boolean productEditLockStatus = getLockStatus(prod);
				//LCSLog.debug("HBILockUponSAPCreation MOA productEditLockStatus "+productEditLockStatus);
				if(productEditLockStatus) {
	//				FlexTypeAttribute att=(FlexTypeAttribute)moaObj.getOwnerAttribute();
					FlexTypeAttribute att=(FlexTypeAttribute)moaObj.getOwningAttribute();
					//LCSLog.debug("HBILockUponSAPCreation MOA PUT_UP_CODE_MOA "+att);
					//LCSLog.debug("HBILockUponSAPCreation MOA att.getAttKey() "+att.getAttKey());
					if(PUT_UP_CODE_MOA.equals(att.getAttKey())) {
					//	LCSLog.debug("HBILockUponSAPCreation 2 MOA PUT_UP_CODE_MOA "+PUT_UP_CODE_MOA);
						String materialNumber ="";
						if(moaObj.getValue("hbiMaterialNumber")!=null) {
							materialNumber = (String) moaObj.getValue("hbiMaterialNumber");
						}
						if(FormatHelper.hasContent(materialNumber)) {
							lockAttributes(wtObj,prod, validationAttributesMap.get("PutUpMOAObject"));
						}

					}else if(PLANT_EXTENSION_MOA.equals(att.getAttKey())) {
						//String synchStatus = null;
						Boolean synchStatus = null;
						if(moaObj.getValue("hbiSynchedStatus")!=null) {
							synchStatus = (Boolean) moaObj.getValue("hbiSynchedStatus");
						}
						//if("true".equals(synchStatus)) {
						if(synchStatus) {
							lockAttributes(wtObj,prod, validationAttributesMap.get("PlantExtMOAObject"));
						}

					}
				}
			}
			if(wtObj instanceof LCSSKU) {
				LCSSKU sku = (LCSSKU) wtObj;

				LCSProduct prod=(LCSProduct)VersionHelper.latestIterationOf( sku.getProductMaster());
				prod=(LCSProduct)VersionHelper.getVersion(prod, "A")  ;
				//boolean productEditLockStatus = new HBISPAutomationFlexObjectLock().getProductObjectLockStatus(prod);
				boolean productEditLockStatus = getLockStatus(prod);
				//LCSLog.debug("HBILockUponSAPCreation SKU productEditLockStatus "+productEditLockStatus);
				if(productEditLockStatus) {
					lockAttributes(wtObj,prod, validationAttributesMap.get("Colorway"));
					LCSLogic.deriveFlexTypeValues(sku);
				}
			}

		} catch (WTException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}


	}
	/**
	 * @param prod
	 * @return
	 */
	public static boolean getLockStatus(LCSProduct prod)  {
		boolean productEditLockStatus = false;
		try {
			prod=(LCSProduct)VersionHelper.getVersion (prod,"A");

			if(prod!=null) {
				String lockStatus=(String)prod.getValue("hbiProductEditLockStatus");

				if(FormatHelper.hasContent(lockStatus) && "hbiLock".equalsIgnoreCase(lockStatus)) {
					productEditLockStatus=true;
				}
			}
		} catch (WTException e) {
			e.printStackTrace();
		} 
		return productEditLockStatus;
	}
	//This reverts back the attribute values
	/**
	 * @param wtObj
	 * @param validationAttributes
	 */
	private static void lockAttributes(WTObject wtObj,LCSProduct prod, Map<String, String> validationAttributes) {
		try {
			Object prevObjAttValue = null;
			Object objAttValue = null;
			boolean attributeChanged = false;
			//LCSLog.debug("HBILockUponSAPCreation lockAttributes wtObj "+wtObj);
			Method getValueMethod = wtObj.getClass().getMethod("getValue", new Class[] { String.class });
			Method setValueMethod = wtObj.getClass().getMethod("setValue", new Class[] { String.class,Object.class });
			//String[] attributeNameDataType = null;
			WTObject prevWtobj = (WTObject) VersionHelper.getPreSavePersistable(wtObj);
			if(wtObj instanceof LCSSeasonProductLink) {
				LCSSeasonProductLink splink = (LCSSeasonProductLink) wtObj;
				LCSSeasonProductLink prevSplLink = LCSSeasonQuery.getPriorSeasonProductLink(splink);
				prevWtobj = prevSplLink;
			}

			//LCSLog.debug("HBILockUponSAPCreation prevWtobj "+prevWtobj);
			if(prevWtobj!=null) {
				//LCSLog.debug("validationAttributes "+validationAttributes);
				for(String attributeKey : validationAttributes.keySet()){
					//LCSLog.debug("HBILockUponSAPCreation attributeKey "+attributeKey);
					prevObjAttValue = getValueMethod.invoke(prevWtobj, attributeKey);
					objAttValue = getValueMethod.invoke(wtObj, attributeKey);
					//LCSLog.debug("HBILockUponSAPCreation prevObjAttValue "+prevObjAttValue);
					//LCSLog.debug("HBILockUponSAPCreation objAttValue "+objAttValue);
					
					if(prevObjAttValue!=null && objAttValue!=null) {
						if(prevObjAttValue instanceof String && objAttValue instanceof String) {
							String prevValue = (String) prevObjAttValue;
							String currentValue = (String) objAttValue;
							
							//Remove special character before comparing
							if(FormatHelper.hasContent(currentValue)) {
								currentValue = currentValue.replaceAll( "[^a-zA-Z0-9]" , "" ); 
							}
							if(FormatHelper.hasContent(prevValue)) {
								prevValue = prevValue.replaceAll( "[^a-zA-Z0-9]" , "" ); 
							}
							//LCSLog.debug("HBILockUponSAPCreation 2prevValue "+prevValue);
							//LCSLog.debug("HBILockUponSAPCreation 2currentValue "+currentValue);
							if(!prevValue.matches(currentValue)) {
								attributeChanged=true;
								//LCSLog.debug("HBILockUponSAPCreation String attributeChanged "+attributeChanged);
								setValueMethod.invoke(wtObj, attributeKey,prevValue);
							}
						}else if(prevObjAttValue instanceof Double && objAttValue instanceof Double) {
							Double prevValue = (Double) prevObjAttValue;
							Double currentValue = (Double) objAttValue; 
							if(prevValue!=currentValue) {
								attributeChanged=true;
								//LCSLog.debug("HBILockUponSAPCreation Double attributeChanged "+attributeChanged);
								setValueMethod.invoke(wtObj, attributeKey,prevObjAttValue);
							}
						}
						else if(prevObjAttValue instanceof LCSLifecycleManaged && objAttValue instanceof LCSLifecycleManaged) {
							LCSLifecycleManaged prevValue = (LCSLifecycleManaged) prevObjAttValue;
							LCSLifecycleManaged currentValue = (LCSLifecycleManaged) objAttValue; 
							String prevId = FormatHelper.getNumericObjectIdFromObject(prevValue);
							String currentId = FormatHelper.getNumericObjectIdFromObject(currentValue);
							if(!prevId.matches(currentId)) {
								attributeChanged=true;
								//LCSLog.debug("HBILockUponSAPCreation LCSLifecycleManaged attributeChanged "+attributeChanged);
								setValueMethod.invoke(wtObj, attributeKey,prevObjAttValue);
							}
						}else if(prevObjAttValue instanceof LCSProduct && objAttValue instanceof LCSProduct) {
							LCSProduct prevValue = (LCSProduct) prevObjAttValue;
							LCSProduct currentValue = (LCSProduct) objAttValue; 
							String prevId = FormatHelper.getNumericObjectIdFromObject(prevValue);
							String currentId = FormatHelper.getNumericObjectIdFromObject(currentValue);
							if(!prevId.matches(currentId)) {
								attributeChanged=true;
								//LCSLog.debug("HBILockUponSAPCreation LCSProduct attributeChanged "+attributeChanged);
								setValueMethod.invoke(wtObj, attributeKey,prevObjAttValue);
							}
						}else if(prevObjAttValue instanceof FlexSpecification && objAttValue instanceof FlexSpecification) {
							FlexSpecification prevValue = (FlexSpecification) prevObjAttValue;
							FlexSpecification currentValue = (FlexSpecification) objAttValue; 
							String prevId = FormatHelper.getNumericObjectIdFromObject(prevValue);
							String currentId = FormatHelper.getNumericObjectIdFromObject(currentValue);
							if(!prevId.matches(currentId)) {
								attributeChanged=true;
								//LCSLog.debug("HBILockUponSAPCreation FlexSpecification attributeChanged "+attributeChanged);
								setValueMethod.invoke(wtObj, attributeKey,prevObjAttValue);
							}
						}else if(prevObjAttValue instanceof LCSSupplier && objAttValue instanceof LCSSupplier) {
							LCSSupplier prevValue = (LCSSupplier) prevObjAttValue;
							LCSSupplier currentValue = (LCSSupplier) objAttValue; 
							String prevId = FormatHelper.getNumericObjectIdFromObject(prevValue);
							String currentId = FormatHelper.getNumericObjectIdFromObject(currentValue);
							if(!prevId.matches(currentId)) {
								attributeChanged=true;
							//	LCSLog.debug("HBILockUponSAPCreation LCSSupplier attributeChanged "+attributeChanged);
								setValueMethod.invoke(wtObj, attributeKey,prevObjAttValue);
							}
						}else if(prevObjAttValue instanceof LCSColor && objAttValue instanceof LCSColor) {
							LCSColor prevValue = (LCSColor) prevObjAttValue;
							LCSColor currentValue = (LCSColor) objAttValue; 
							String prevId = FormatHelper.getNumericObjectIdFromObject(prevValue);
							String currentId = FormatHelper.getNumericObjectIdFromObject(currentValue);
							if(!prevId.matches(currentId)) {
								attributeChanged=true;
								//LCSLog.debug("HBILockUponSAPCreation LCSColor attributeChanged "+attributeChanged);
								setValueMethod.invoke(wtObj, attributeKey,prevObjAttValue);
							}
						}
					}
					
				}
			}	
			if(attributeChanged) {
				if(!VersionHelper.isFirstIterationOfAll(prod)) {
					populateIntegrationLogComments(prod,"key Attributes updation is locked since this SP is already transfered to SAP, Please refer the Business Object\\Integration\\Validation Attributes for list of key fields ");
				}
			}
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (WTException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (WTPropertyVetoException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param productObj
	 * @param missingAttributes
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 */
	public static void populateIntegrationLogComments(LCSProduct productObj, String missingAttributes) throws WTException, WTPropertyVetoException
	{
		//LCSLog.debug("### START lockAttributesUponSAPCreation.populateIntegrationLogComments");
		if(productObj!=null) {
			//LCSLog.debug("HBILockUponSAPCreation productObj not null for printing integration log ");
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			String sortingNumber = "1";

			SearchResults moaResults = LCSMOAObjectQuery.findMOACollectionData((LCSPartMaster)productObj.getMaster(),  productObj.getFlexType().getAttribute(hbiErpIntegrationLogKey), "LCSMOAObject.createStampA2", true);
			//LCSLog.debug("HBILockUponSAPCreation  moaResults    " +moaResults.getResults().size());
			if(moaResults != null && moaResults.getResultsFound() > 0)
			{
				Collection moaData = moaResults.getResults();
				String maxSortingNumber = FlexObjectUtil.maxValueForFlexObjects(moaData, "LCSMOAOBJECT.SORTINGNUMBER", "int");
				sortingNumber = Integer.toString((Integer.parseInt(maxSortingNumber) + 1));
			}
			
			
	//		LCSSeasonProductLink spl = new LCSSeasonProductLink();
			//getOwnerVersion
			//LCSMOAObject moaObject = LCSMOAObject.newLCSMOAObject();
			//Code Upgrade by Wipro Team
			LCSMOACollectionClientModel moaModel = new LCSMOACollectionClientModel();
			StringBuffer dataBuffer = new StringBuffer();
			dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "ID", sortingNumber );
			dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "sortingnumber", sortingNumber );
            dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "user", FormatHelper.getNumericObjectIdFromObject(user) );
            dataBuffer = MultiObjectHelper.addAttribute(dataBuffer, "comments", missingAttributes );
            dataBuffer.append(MultiObjectHelper.ROW_DELIMITER);
			moaModel.load(FormatHelper.getObjectId(productObj),hbiErpIntegrationLogKey);
			moaModel.updateMOACollection(dataBuffer.toString());
		//Commented the below code Wipro Upgrade Team
			//System.out.println("moaObject.getFolderingInfo()>>>>>>>>>>>>>>>>>>"+moaObject1.getFolderingInfo());
//			moaObject.setFlexType(productObj.getFlexType().getAttribute(hbiErpIntegrationLogKey).getRefType());
//			
//			moaObject.setOwnerReference(((RevisionControlled)productObj).getMasterReference());
//			moaObject.setOwnerVersion(productObj.getVersionIdentifier().getValue());
//			//moaObject.setOwnerAttribute(productObj.getFlexType().getAttribute(hbiErpIntegrationLogKey));
//			moaObject.setOwner(productObj.getFlexType().getAttribute(hbiErpIntegrationLogKey));
//			moaObject.setBranchId(Integer.parseInt(sortingNumber));
//			moaObject.setDropped(false);
//			moaObject.setSortingNumber(Integer.parseInt(sortingNumber));
//			moaObject.getFlexType().getAttribute("comments").setValue(moaObject, missingAttributes);
//			moaObject.getFlexType().getAttribute("user").setValue(moaObject, user);
			//LCSMOAObjectLogic.persist(moaObject);
			//LCSLog.debug("HBILockUponSAPCreation productObj productObj  " +productObj.getVersionIdentifier().getValue());
			LCSMOATable.clearTableFromMethodContextCache((FlexTyped)productObj, productObj.getFlexType().getAttribute(hbiErpIntegrationLogKey));	
		}


		//LCSLog.debug("### END lockAttributesUponSAPCreation IntegrationLog comment ");
	}
}


