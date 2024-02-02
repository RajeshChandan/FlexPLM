package com.burberry.wc.integration.streamline.bom;



import com.lcs.wc.client.web.TableDataUtil;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.Query;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.LCSFlexBOMLogic.1;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.flextype.FlexTypeUtil;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.flextype.PropertyBasedAttributeValueLogic;
import com.lcs.wc.foundation.LCSPluginManager;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialColorLogic;
import com.lcs.wc.material.LCSMaterialColorQuery;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.material.LCSMaterialQuery;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierMaster;
import com.lcs.wc.material.LCSMaterialSupplierQuery;
import com.lcs.wc.material.PrimaryMaterialUtility;
import com.lcs.wc.part.LCSPartLogic;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.LCSSKUQuery;
import com.lcs.wc.product.ProductDestination;
import com.lcs.wc.season.LCSSeasonMaster;
import com.lcs.wc.sourcing.LCSSourceToSeasonLink;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigLogic;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.sourcing.LCSSourcingConfigQuery;
import com.lcs.wc.sourcing.SourceComponentNumberPlugin;
import com.lcs.wc.specification.FlexSpecDestination;
import com.lcs.wc.specification.FlexSpecLogic;
import com.lcs.wc.specification.FlexSpecMaster;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecToSeasonLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.supplier.LCSSupplierQuery;
import com.lcs.wc.util.FlexObjectUtil;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSException;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.MultiObjectHelper;
import com.lcs.wc.util.VersionHelper;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.WTObject;
import wt.log4j.LogR;
import wt.method.MethodContext;
import wt.part.WTPart;
import wt.session.SessionServerHelper;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;
import wt.vc.Mastered;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;

public class LCSFlexBOMLogic extends LCSPartLogic {
   private static final Logger LOGGER = LogR.getLogger(LCSFlexBOMLogic.class.getName());
   private static final String CLASSNAME = LCSFlexBOMLogic.class.getName();
   private static final String BOMPART_FOLDERLOCATION;
   private static final Class BOMPART_SEQUENCE_CLASS;
   private static final boolean USE_STANDARD_BOM_QUERY_IN_ROLL_UP;
   public static final String INSERT_COPY_MODE = "INSERT_COPY_MODE";
   public static final String REPLACE_COPY_MODE = "REPLACE_COPY_MODE";
   public static final String ASSOCIATE_IN_PROGRESS = "ASSOCIATE_IN_PROGRESS";
   public static final boolean WCPART_ENABLED = LCSProperties.getBoolean("com.lcs.wc.specification.parts.Enabled");
   public static final String PRIMARY_MATERIAL_GROUP;
   public static final boolean USE_PRIMARY_BOM = LCSProperties.getBoolean("com.lcs.wc.specification.usePrimaryBOM");
   public static final boolean USE_PRIMARY_SPEC = LCSProperties.getBoolean("com.lcs.wc.specification.usePrimarySpec");
   public static final String HIDDEN_SYNC_ATTS;

   public Persistable save(Persistable persistable) throws WTException {
      if (persistable instanceof FlexBOMPart) {
         return this.saveBOMPart((FlexBOMPart)persistable);
      } else {
         throw new LCSException("Cannot save object:" + persistable + " using LCSFlexBOMLogic");
      }
   }

   public FlexBOMPart saveBOMPart(FlexBOMPart bomPart) throws WTException {
      return this.saveBOMPart(bomPart, true);
   }

   public FlexBOMPart saveBOMPart(FlexBOMPart bomPart, boolean deriveStrings) throws WTException {
      return this.saveBOMPart(bomPart, true, (String)null);
   }

   public FlexBOMPart saveBOMPart(FlexBOMPart bomPart, boolean deriveStrings, String copyMode) throws WTException {
      LOGGER.debug(".save: start");
      deriveFlexTypeValues(bomPart, deriveStrings);
      if (!PersistenceHelper.isPersistent(bomPart)) {
         bomPart = this.createBOMPart(bomPart);
      } else {
         bomPart = this.updateBOMPart(bomPart);
      }

      return bomPart;
   }

   public FlexBOMPart reviseBOM(FlexBOMPart part) throws WTException {
      FlexBOMPart newPart = null;

      try {
         newPart = (FlexBOMPart)VersionControlHelper.service.newVersion(part);
         part.copyState(newPart);
         assignFolder(BOMPART_FOLDERLOCATION, newPart);
      } catch (WTPropertyVetoException var4) {
         var4.printStackTrace();
         throw new WTException(var4);
      }

      this.copyBOM(part, newPart, (String)null, "REPLACE_COPY_MODE", true, (Map)null);
      newPart = (FlexBOMPart)persist(newPart);
      return newPart;
   }

   private FlexBOMPart updateBOMPart(FlexBOMPart bomPart) throws WTException {
      LOGGER.debug(".update: start");
      FlexBOMPart newIteration = null;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".update:" + bomPart.getName());
      }

      newIteration = (FlexBOMPart)persist(bomPart);
      return newIteration;
   }

   public FlexBOMPart initiateBOMPart(RevisionControlled owner) throws WTException {
      return this.initiateBOMPart(owner, (FlexType)null, "MAIN", (Collection)null);
   }

   public FlexBOMPart initiateBOMPart(RevisionControlled owner, FlexType flexType, String bomType) throws WTException {
      return this.initiateBOMPart(owner, flexType, bomType, (Collection)null);
   }

   public FlexBOMPart initiateBOMPart(RevisionControlled owner, FlexType flexType, String bomType, Collection specificationIds) throws WTException {
      return this.initiateBOMPart(owner, flexType, bomType, specificationIds, (FlexBOMPart)null);
   }

   public FlexBOMPart initiateBOMPart(RevisionControlled owner, FlexType flexType, String bomType, Collection specificationIds, FlexBOMPart copyStatePart) throws WTException {
      return this.initiateBOMPart(owner, flexType, bomType, specificationIds, copyStatePart, (String)null);
   }

   public FlexBOMPart initiateBOMPart(RevisionControlled owner, FlexType flexType, String bomType, Collection specificationIds, FlexBOMPart copyStatePart, String copyMode) throws WTException {
      return this.initiateBOMPart(owner, flexType, bomType, specificationIds, copyStatePart, copyMode, true);
   }

   public FlexBOMPart initiateBOMPart(RevisionControlled owner, FlexType flexType, String bomType, Collection specificationIds, FlexBOMPart copyStatePart, String copyMode, boolean applyAttributeRules) throws WTException {
      return this.initiateBOMPart(owner, flexType, bomType, specificationIds, copyStatePart, copyMode, applyAttributeRules, false);
   }

   public FlexBOMPart initiateBOMPart(RevisionControlled owner, FlexType flexType, String bomType, Collection specificationIds, FlexBOMPart copyStatePart, String copyMode, boolean applyAttributeRules, boolean setPrimary) throws WTException {
      if (bomType == null) {
         throw new WTException("A bomType must be specified");
      } else {
         FlexBOMPart bomPart = FlexBOMPart.newFlexBOMPart();

         try {
            if (copyStatePart != null) {
               copyStatePart.copyState(bomPart);
            }

            if (flexType != null) {
               bomPart.setFlexType(flexType);
            }

            FlexType type;
            if ("MAIN".equals(bomType) && flexType == null) {
               type = FlexTypeCache.getFlexTypeFromPath("BOM\\Materials");
               if (type == null) {
                  throw new WTException("Required FlexType 'BOM\\Materials' not found.");
               }

               bomPart.setFlexType(type);
            }

            if ("LABOR".equals(bomType) && flexType == null) {
               type = FlexTypeCache.getFlexTypeFromPath("BOM\\Labor");
               if (type == null) {
                  throw new WTException("Required FlexType 'BOM\\Labor' not found.");
               }

               bomPart.setFlexType(type);
            }

            if (applyAttributeRules) {
               PropertyBasedAttributeValueLogic.setAttributes(bomPart, "com.lcs.wc.flexbom.FlexBOMPart", "", copyMode);
               setFlexTypedDefaults(bomPart, "BOM_SCOPE", (String)null, false);
            }

            bomPart.setCopiedFrom(copyStatePart);
            String name = (String)bomPart.getValue("name");
            if (!FormatHelper.hasContent(name)) {
               name = "BOM Part For: " + owner.getName();
            }

            if (name.length() > 60) {
               name = name.substring(0, 60);
            }

            bomPart.setValue("name", name);
            bomPart.setOwnerMaster((BOMOwner)owner.getMaster());
            bomPart.setBomType(bomType);
            resetBOMNameNumber(bomPart);
         } catch (WTPropertyVetoException var26) {
            var26.printStackTrace();
            throw new WTException(var26);
         }

         bomPart = this.saveBOMPart(bomPart, true);
         if (specificationIds != null) {
            boolean old_enforced = SessionServerHelper.manager.setAccessEnforced(false);

            try {
               Iterator ids = specificationIds.iterator();
               FlexSpecification spec = null;

               for(FlexSpecLogic logic = new FlexSpecLogic(); ids.hasNext(); logic.addBOMToSpec(spec, bomPart, (FlexSpecDestination)null, setPrimary)) {
                  String id = (String)ids.next();
                  Object obj = LCSQuery.findObjectById(id);
                  if (obj instanceof FlexSpecToSeasonLink) {
                     FlexSpecMaster specMaster = ((FlexSpecToSeasonLink)obj).getSpecificationMaster();
                     spec = (FlexSpecification)VersionHelper.latestIterationOf(specMaster);
                  } else {
                     spec = (FlexSpecification)LCSQuery.findObjectById(id);
                  }

                  String specMaseterId = FormatHelper.getNumericFromOid(spec.getMaster().toString());
                  Collection components = FlexSpecQuery.getSpecComponents(spec, "BOM");
                  int exist_components_size = 0;
                  Iterator iterator = components.iterator();

                  while(iterator.hasNext()) {
                     Object objBOMPart = iterator.next();
                     if (objBOMPart != null && objBOMPart instanceof FlexBOMPart) {
                        FlexBOMPart flexBOMPart = (FlexBOMPart)objBOMPart;
                        if (!"LABOR".equals(flexBOMPart.getBomType())) {
                           ++exist_components_size;
                           if (exist_components_size > 0) {
                              setPrimary = false;
                              break;
                           }
                        }
                     }
                  }
               }
            } finally {
               SessionServerHelper.manager.setAccessEnforced(old_enforced);
            }
         }

         return bomPart;
      }
   }

   public void updateAllDimensionBOM(FlexBOMPart part, Collection bomChanges, String colorDimId, String sourceDimId, String size1, String size2, String destDimId, String activeDimensionName) throws WTException {
      BOMOwner ownerMaster = part.getOwnerMaster();
      boolean productBOM = false;
      if (ownerMaster instanceof LCSPartMaster) {
         productBOM = true;
      } else {
         productBOM = false;
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".updateAllDimensionBOM: activeDimensionName = " + activeDimensionName);
      }

      Collection allApplicableLinks = null;
      String dimensionColumnIndex = "";
      if (activeDimensionName == null) {
         activeDimensionName = "SKU";
      }

      if ("SKU".equals(activeDimensionName)) {
         allApplicableLinks = LCSFlexBOMQuery.findFlexBOMData(part, sourceDimId, (String)null, size1, size2, destDimId, "WIP_ONLY", (Date)null, false, true, "ALL_APPLICABLE_TO_DIMENSION", "ALL_SKUS", (String)null, (String)null).getResults();
         dimensionColumnIndex = "FLEXBOMLINK.IDA3E5";
      } else if ("SIZE1".equals(activeDimensionName)) {
         allApplicableLinks = LCSFlexBOMQuery.findFlexBOMData(part, sourceDimId, colorDimId, (String)null, size2, destDimId, "WIP_ONLY", (Date)null, false, true, "ALL_APPLICABLE_TO_DIMENSION", (String)null, (String)null, "ALL_SIZE1").getResults();
         dimensionColumnIndex = "FLEXBOMLINK.SIZE1";
      } else if ("SIZE2".equals(activeDimensionName)) {
         allApplicableLinks = LCSFlexBOMQuery.findFlexBOMData(part, sourceDimId, colorDimId, size1, (String)null, destDimId, "WIP_ONLY", (Date)null, false, true, "ALL_APPLICABLE_TO_DIMENSION", (String)null, (String)null, "ALL_SIZE2").getResults();
         dimensionColumnIndex = "FLEXBOMLINK.SIZE2";
      } else if ("DESTINATION".equals(activeDimensionName)) {
         allApplicableLinks = LCSFlexBOMQuery.findFlexBOMData(part, sourceDimId, colorDimId, size1, size2, (String)null, "WIP_ONLY", (Date)null, false, true, "ALL_APPLICABLE_TO_DIMENSION", (String)null, (String)null, "ALL_SIZE2").getResults();
         dimensionColumnIndex = "FLEXBOMLINK.IDA3H5";
      } else if ("SIZE1_SIZE2".equals(activeDimensionName)) {
         allApplicableLinks = LCSFlexBOMQuery.findFlexBOMData(part, sourceDimId, colorDimId, (String)null, (String)null, destDimId, "WIP_ONLY", (Date)null, false, true, "ALL_APPLICABLE_TO_DIMENSION", (String)null, (String)null, "ALL_SIZE1_AND_2").getResults();
         TableDataUtil.appendConstantString(allApplicableLinks, "FLEXBOMLINK.SIZE1", "_", "SIZE1_SIZE2");
         TableDataUtil.concatIndexes(allApplicableLinks, "SIZE1_SIZE2", "FLEXBOMLINK.SIZE2", "SIZE1_SIZE2");
         dimensionColumnIndex = "SIZE1_SIZE2";
      } else if ("SOURCE".equals(activeDimensionName)) {
         allApplicableLinks = LCSFlexBOMQuery.findFlexBOMData(part, (String)null, colorDimId, size1, size2, destDimId, "WIP_ONLY", (Date)null, false, true, "ALL_APPLICABLE_TO_DIMENSION", (String)null, "ALL_SOURCES", (String)null).getResults();
         dimensionColumnIndex = "FLEXBOMLINK.IDA3F5";
      }

      Map dimensionRecordMap = FlexObjectUtil.groupIntoCollections(allApplicableLinks, dimensionColumnIndex);
      Iterator changeIter = bomChanges.iterator();
      FlexObject changes = null;
      Iterator keyIter = null;
      String changeKey = null;
      Map dimensionChangesMap = new HashMap();
      Map branchChangesMap = null;
      String dimensionId = null;
      String branchId = null;
      FlexObject branchChange = null;
      HashSet dimensionIds = new HashSet();
      String attKeyName = "";

      while(changeIter.hasNext()) {
         changes = (FlexObject)changeIter.next();
         keyIter = changes.keySet().iterator();
         branchId = changes.getString("ID");

         while(keyIter.hasNext()) {
            changeKey = (String)keyIter.next();
            if (changeKey.indexOf("$" + activeDimensionName + "$") > 1) {
               dimensionId = changeKey.substring(changeKey.lastIndexOf("$") + 1);
               attKeyName = changeKey.substring(0, changeKey.indexOf("$"));
               dimensionIds.add(dimensionId);
               branchChangesMap = (Map)dimensionChangesMap.get(dimensionId);
               if (branchChangesMap == null) {
                  branchChangesMap = new HashMap();
                  dimensionChangesMap.put(dimensionId, branchChangesMap);
               }

               branchChange = (FlexObject)((Map)branchChangesMap).get(branchId);
               if (branchChange == null) {
                  branchChange = new FlexObject();
                  branchChange.put("ID", branchId);
                  ((Map)branchChangesMap).put(branchId, branchChange);
               }

               if (changeKey.startsWith("COLORDESCRIPTION")) {
                  branchChange.put("colorDescription", changes.getString(changeKey));
               } else if (changeKey.startsWith("COLORID")) {
                  branchChange.put("colorId", changes.getString(changeKey));
               } else if (changeKey.startsWith("MATERIALCOLORID")) {
                  branchChange.put("materialColorId", changes.getString(changeKey));
               } else if (changeKey.startsWith("CHILDID")) {
                  branchChange.put("childId", changes.getString(changeKey));
               } else if (changeKey.startsWith("MATERIALDESCRIPTION")) {
                  branchChange.put("materialDescription", changes.getString(changeKey));
               } else if (changeKey.startsWith("QUANTITY")) {
                  branchChange.put("quantity", changes.getString(changeKey));
               } else if (part.getFlexType().getAttributeKeyList().contains(attKeyName.toUpperCase())) {
                  branchChange.put(attKeyName, changes.getString(changeKey));
               }
            }
         }
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("dimensionChangesMap = " + dimensionChangesMap);
      }

      WTObject skuMaster = null;
      WTObject scMaster = null;
      FlexSpecDestination destination = null;
      if (FormatHelper.hasContent(sourceDimId)) {
         scMaster = (WTObject)LCSQuery.findObjectById(sourceDimId);
      }

      if (FormatHelper.hasContent(colorDimId)) {
         skuMaster = (WTObject)LCSQuery.findObjectById(colorDimId);
      }

      if (FormatHelper.hasContent(destDimId)) {
         destination = (FlexSpecDestination)LCSQuery.findObjectById(destDimId);
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("dimensionRecordMap = " + dimensionRecordMap);
      }

      Vector tempRecords = new Vector();
      Collection productRecords = (Collection)dimensionRecordMap.get("0");
      if (productRecords == null) {
         productRecords = (Collection)dimensionRecordMap.get("");
      }

      if (productRecords == null) {
         productRecords = (Collection)dimensionRecordMap.get("_");
      }

      if (productRecords != null) {
         tempRecords.addAll(productRecords);
      }

      SearchResults tempResults = new SearchResults();
      tempResults.setResults(tempRecords);
      Collection branches = LCSQuery.getObjectsFromResults(tempResults, "OR:com.lcs.wc.flexbom.FlexBOMLink:", "FLEXBOMLINK.IDA2A2");
      this.makeDimensionChanges(part, bomChanges, scMaster, skuMaster, size1, size2, destination, this.hashLink(branches));
      Iterator dimensionIdIter = dimensionIds.iterator();

      while(dimensionIdIter.hasNext()) {
         dimensionId = (String)dimensionIdIter.next();
         if ("SKU".equals(activeDimensionName)) {
            if (productBOM) {
               skuMaster = (WTObject)LCSQuery.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:" + dimensionId);
            } else {
               skuMaster = (WTObject)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:" + dimensionId);
            }
         } else if ("SOURCE".equals(activeDimensionName)) {
            if (productBOM) {
               scMaster = (WTObject)LCSQuery.findObjectById("OR:com.lcs.wc.sourcing.LCSSourcingConfigMaster:" + dimensionId);
            } else {
               scMaster = (WTObject)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialSupplierMaster:" + dimensionId);
            }
         } else if ("DESTINATION".equals(activeDimensionName)) {
            destination = (FlexSpecDestination)LCSQuery.findObjectById("OR:com.lcs.wc.product.ProductDestination:" + dimensionId);
         } else if ("SIZE1".equals(activeDimensionName)) {
            size1 = dimensionId;
         } else if ("SIZE2".equals(activeDimensionName)) {
            size2 = dimensionId;
         } else if ("SIZE1_SIZE2".equals(activeDimensionName)) {
            size1 = dimensionId.substring(0, dimensionId.indexOf("_"));
            size2 = dimensionId.substring(dimensionId.indexOf("_") + 1);
         }

         tempRecords = new Vector();
         if (dimensionRecordMap.get(dimensionId) != null) {
            tempRecords.addAll((Collection)dimensionRecordMap.get(dimensionId));
            tempResults = new SearchResults();
            tempResults.setResults(tempRecords);
         } else {
            tempResults.setResults(new Vector());
         }

         branches = LCSQuery.getObjectsFromResults(tempResults, "OR:com.lcs.wc.flexbom.FlexBOMLink:", "FLEXBOMLINK.IDA2A2");
         Map branchChangesMap = (Map)dimensionChangesMap.get(dimensionId);
         this.makeDimensionChanges(part, branchChangesMap.values(), scMaster, skuMaster, size1, size2, destination, this.hashLink(branches));
      }

   }

   public void updateBOM(FlexBOMPart part, Collection bomChanges, String sourceDimId, String colorDimId, String size1, String size2, String destDimId) throws WTException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".updateBOM: PART = " + part + " bomChanges = " + bomChanges);
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".updateBOM: updating BOM for : " + part + " - Rev " + part.getVersionIdentifier().getValue() + " scMaster = " + sourceDimId + ", SKU = " + colorDimId + ", size1 = " + size1 + ", size2 " + size2 + ", destDimId = " + destDimId);
      }

      Map currentBOM = this.hashLink(LCSFlexBOMQuery.findFlexBOMLinks(part, sourceDimId, colorDimId, size1, size2, destDimId, "WIP_ONLY", (Date)null, false, "DIMENSION_OVERRIDES_ONLY", (String)null, (String)null, (String)null));
      WTObject skuMaster = null;
      WTObject scMaster = null;
      FlexSpecDestination destination = null;
      if (FormatHelper.hasContent(sourceDimId)) {
         scMaster = (WTObject)LCSQuery.findObjectById(sourceDimId);
      }

      if (FormatHelper.hasContent(colorDimId)) {
         skuMaster = (WTObject)LCSQuery.findObjectById(colorDimId);
      }

      if (FormatHelper.hasContent(destDimId)) {
         destination = (FlexSpecDestination)LCSQuery.findObjectById(destDimId);
      }

      this.makeDimensionChanges(part, bomChanges, scMaster, skuMaster, size1, size2, destination, currentBOM);
      this.rollUpBOM(part);
   }

   protected void makeDimensionChanges(FlexBOMPart part, Collection bomChanges, WTObject scMaster, WTObject skuMaster, String size1, String size2, FlexSpecDestination destination, Map currentLinks) throws WTException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".makeDimensionChanges: " + part + " - Rev " + part.getVersionIdentifier().getValue() + " scMaster = " + scMaster + ", SKU = " + skuMaster + ", size1 = " + size1 + ", size2 " + size2 + ", destination = " + destination);
      }

      boolean dimensionOverride = scMaster != null || skuMaster != null || FormatHelper.hasContentAllowZero(size1) || FormatHelper.hasContentAllowZero(size2) || destination != null;
      Iterator changesIter = bomChanges.iterator();
      Map changeInfo = null;
      String branchId = "";
      int var14 = this.getMaxBranchId(part);

      while(changesIter.hasNext()) {
         changeInfo = (Map)changesIter.next();
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(".updateBOM: makeDimensionChanges = " + changeInfo);
         }

         branchId = (String)changeInfo.get("ID");
         FlexBOMLink workingBranch = (FlexBOMLink)currentLinks.get(branchId);
         if (workingBranch == null) {
            workingBranch = FlexBOMLink.newFlexBOMLink();

            try {
               workingBranch.setInDate((Timestamp)null);
               workingBranch.setOutDate((Timestamp)null);
               workingBranch.setSequence(-1);
               workingBranch.setDropped(false);
               workingBranch.setFlexType(part.getFlexType());
               if (!dimensionOverride) {
                  workingBranch.setBranchId(var14++);
                  if (LOGGER.isDebugEnabled()) {
                     LOGGER.debug("Product Level BOM: Creating new Branch : " + workingBranch.getBranchId());
                  }
               } else {
                  workingBranch.setBranchId(Integer.parseInt(branchId));
                  if (LOGGER.isDebugEnabled()) {
                     LOGGER.debug("Dimension Override BOM: Creating new override record : " + workingBranch.getBranchId());
                  }
               }

               workingBranch.setParent(part.getMaster());
               workingBranch.setParentRev(part.getVersionIdentifier().getValue());
               if (skuMaster != null) {
                  workingBranch.setColorDimension(skuMaster);
               }

               if (scMaster != null) {
                  workingBranch.setSourceDimension(scMaster);
               }

               if (destination != null) {
                  workingBranch.setDestinationDimension(destination);
               }

               if (FormatHelper.hasContentAllowZero(size1)) {
                  workingBranch.setSize1(size1);
               }

               if (FormatHelper.hasContentAllowZero(size2)) {
                  workingBranch.setSize2(size2);
               }

               workingBranch.setWip(true);
            } catch (WTPropertyVetoException var18) {
               throw new WTException(var18);
            }
         }

         if (!workingBranch.isWip()) {
            FlexBOMLink wipBranch = FlexBOMLink.newFlexBOMLink();

            try {
               wipBranch = (FlexBOMLink)workingBranch.copyState(wipBranch);
               wipBranch.setWip(true);
               wipBranch.setFlexType(part.getFlexType());
               wipBranch.setInDate((Timestamp)null);
               wipBranch.setOutDate((Timestamp)null);
               workingBranch = wipBranch;
            } catch (WTPropertyVetoException var17) {
               throw new WTException(var17);
            }
         }

         if (FormatHelper.parseBoolean((String)changeInfo.get("DROPPED"))) {
            if (!dimensionOverride) {
               this.dropBranchFromBOM(part, new Integer(branchId));
            }
         } else {
            this.loadAndSaveChanges(workingBranch, changeInfo, false);
         }
      }

   }

   public boolean skuLevelOverrideExists(FlexBOMPart bomPart, String currentBranchId, Collection bdmData) throws WTException {
      String nameStub = "-PARENT:" + FormatHelper.getObjectId(bomPart.getMaster()).substring(3) + "-REV:" + bomPart.getVersionIdentifier().getValue() + "-";
      Map currentLinkData = this.hashMapsByDimensionId(nameStub, bdmData);
      Iterator links = bdmData.iterator();
      Vector oids = new Vector();

      Map currentLinks;
      while(links.hasNext()) {
         currentLinks = (Map)links.next();
         oids.add("OR:com.lcs.wc.flexbom.FlexBOMLink:" + currentLinks.get("linkId"));
      }

      currentLinks = this.hashLinksByDimensionId(LCSQuery.getObjectsFromCollection(oids));
      String dimensionId = "";
      Iterator iter = currentLinks.keySet().iterator();

      while(iter.hasNext()) {
         dimensionId = (String)iter.next();
         if (dimensionId.contains("-SKU:") && dimensionId.contains("-BRANCH:" + currentBranchId)) {
            FlexBOMLink curLink = (FlexBOMLink)currentLinks.get(dimensionId);
            Map changeInfo = (HashMap)currentLinkData.get(dimensionId);
            if (!this.isBlankSKUOverrideBranch(curLink, changeInfo)) {
               return true;
            }
         }
      }

      return false;
   }

   public FlexBOMPart updateBOMForAllDimensions(FlexBOMPart bomPart, String changesString) throws WTException {
      Collection changes = MultiObjectHelper.parseData(changesString).values();
      return this.updateBOMForAllDimensions(bomPart, changes);
   }

   public FlexBOMPart updateBOMForAllDimensions(FlexBOMPart bomPart, Collection changes) throws WTException {
      return this.updateBOMForAllDimensions(bomPart, changes, false);
   }

   public FlexBOMPart updateBOMForAllDimensions(FlexBOMPart bomPart, Collection changes, boolean massChange) throws WTException {
      LOGGER.debug(".updateBOMForAllDimensions  START");
      changes = this.orderChanges(changes);
      LOGGER.debug("Get old BomPart before change");
      FlexBOMPart oldBomPart = (FlexBOMPart)VersionHelper.latestIterationOf(bomPart.getMaster());
      bomPart = this.saveBOMPart(bomPart);
      BOMOwner ownerMaster = bomPart.getOwnerMaster();
      boolean productBOM = false;
      if (ownerMaster instanceof LCSPartMaster) {
         productBOM = true;
      }

      FlexObject changeInfo = null;
      String branchId = "";
      String dimensionId = "";
      boolean dimensionOverride = false;
      Map newBranchIdMap = new HashMap();
      Map currentLinks = this.hashLinksByDimensionId(LCSFlexBOMQuery.findFlexBOMLinks(bomPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, "ALL_DIMENSIONS", (String)null, (String)null, (String)null));
      Iterator changesIter = changes.iterator();

      while(true) {
         FlexBOMLink workingBranch;
         boolean isRemovedOverride;
         while(true) {
            if (!changesIter.hasNext()) {
               if (productBOM && FormatHelper.hasContent(PRIMARY_MATERIAL_GROUP) && bomPart.getFlexType().getAttributeGroup(PRIMARY_MATERIAL_GROUP, "BOM_SCOPE", (String)null).size() > 0) {
                  LCSPluginManager.handleEvent(bomPart, "PRIMARY_MATERIAL_ROLL_UP");
                  if (USE_PRIMARY_SPEC && USE_PRIMARY_BOM && this.checkChange(oldBomPart, bomPart)) {
                     this.syncPrimaryMaterialAtts(bomPart);
                  }
               }

               LOGGER.debug(".updateBOMForAllDimensions  END");
               return bomPart;
            }

            changeInfo = (FlexObject)changesIter.next();
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(".updateBOM: makeDimensionChanges = " + changeInfo);
            }

            dimensionId = (String)changeInfo.get("ID");
            branchId = (String)changeInfo.get("branchId");
            workingBranch = (FlexBOMLink)currentLinks.get(dimensionId);
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(".updateBOMForAllDimensions: processing change. dimensionId = " + dimensionId);
            }

            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(".updateBOMForAllDimensions: processing change. branchId = " + branchId);
            }

            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(".updateBOMForAllDimensions: processing change. workingBranch = " + workingBranch);
            }

            if (FormatHelper.hasContent(changeInfo.getString("dimensionName"))) {
               dimensionOverride = true;
            } else {
               dimensionOverride = false;
            }

            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(".updateBOMForAllDimensions: dimensionOverride = " + dimensionOverride);
            }

            if (workingBranch == null) {
               LOGGER.debug(".updateBOMForAllDimensions: working branch == null");
               workingBranch = FlexBOMLink.newFlexBOMLink();

               try {
                  workingBranch.setInDate((Timestamp)null);
                  workingBranch.setOutDate((Timestamp)null);
                  workingBranch.setSequence(-1);
                  workingBranch.setDropped(false);
                  workingBranch.setFlexType(bomPart.getFlexType());
                  if (!dimensionOverride) {
                     int newBranchId = Integer.parseInt(branchId);
                     newBranchIdMap.put(branchId, "" + branchId);
                     if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(".updateBOMForAllDimensions: creating new branch... branchId = " + newBranchId);
                     }

                     workingBranch.setBranchId(newBranchId);
                     if (FormatHelper.hasContent(changeInfo.getString("masterBranchId"))) {
                        String masterBranchId = changeInfo.getString("masterBranchId");
                        String newMasterBranchId = (String)newBranchIdMap.get(masterBranchId);
                        if (FormatHelper.hasContent(newMasterBranchId)) {
                           changeInfo.setData("masterBranchId", newMasterBranchId);
                        }
                     }
                  } else {
                     if (FormatHelper.hasContent("" + newBranchIdMap.get(branchId))) {
                        String newBranchId = "" + newBranchIdMap.get(branchId);
                        workingBranch.setBranchId(Integer.parseInt(newBranchId));
                     } else {
                        workingBranch.setBranchId(Integer.parseInt(branchId));
                     }

                     if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(".updateBomForAllChanges Dimension Override BOM: Creating new override record : " + workingBranch.getBranchId());
                     }

                     WTObject sourceDimObject;
                     if (FormatHelper.hasContent(changeInfo.getString("colorDimensionId"))) {
                        if (productBOM) {
                           sourceDimObject = (WTObject)LCSQuery.findObjectById("OR:com.lcs.wc.part.LCSPartMaster:" + changeInfo.getString("colorDimensionId"));
                        } else {
                           sourceDimObject = (WTObject)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:" + changeInfo.getString("colorDimensionId"));
                        }

                        workingBranch.setColorDimension(sourceDimObject);
                     }

                     if (FormatHelper.hasContent(changeInfo.getString("sourceDimensionId"))) {
                        if (productBOM) {
                           sourceDimObject = (WTObject)LCSQuery.findObjectById("OR:com.lcs.wc.sourcing.LCSSourcingConfigMaster:" + changeInfo.getString("sourceDimensionId"));
                        } else {
                           sourceDimObject = (WTObject)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialSupplierMaster:" + changeInfo.getString("sourceDimensionId"));
                        }

                        workingBranch.setSourceDimension(sourceDimObject);
                     }

                     if (FormatHelper.hasContent(changeInfo.getString("destinationDimensionId"))) {
                        FlexSpecDestination destinationDimObject = (FlexSpecDestination)LCSQuery.findObjectById("OR:com.lcs.wc.product.ProductDestination:" + changeInfo.getString("destinationDimensionId"));
                        workingBranch.setDestinationDimension(destinationDimObject);
                     }

                     if (FormatHelper.hasContentAllowZero(changeInfo.getString("size1"))) {
                        workingBranch.setSize1(changeInfo.getString("size1"));
                     }

                     if (FormatHelper.hasContentAllowZero(changeInfo.getString("size2"))) {
                        workingBranch.setSize2(changeInfo.getString("size2"));
                     }
                  }

                  workingBranch.setParent(bomPart.getMaster());
                  workingBranch.setParentRev(bomPart.getVersionIdentifier().getValue());
                  workingBranch.setWip(true);
               } catch (WTPropertyVetoException var20) {
                  throw new WTException(var20);
               }
            }

            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug("changeInfo: " + changeInfo);
            }

            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug("is Blank: " + this.isBlankOverrideBranch(workingBranch, changeInfo));
            }

            isRemovedOverride = false;
            if (!dimensionOverride || !this.isBlankOverrideBranch(workingBranch, changeInfo)) {
               break;
            }

            if (PersistenceHelper.isPersistent(workingBranch) && workingBranch.isWip()) {
               try {
                  workingBranch.setDropped(true);
                  break;
               } catch (WTPropertyVetoException var19) {
                  throw new WTException(var19);
               }
            }

            if (PersistenceHelper.isPersistent(workingBranch) && !workingBranch.isWip()) {
               isRemovedOverride = true;
               break;
            }
         }

         if (!workingBranch.isWip()) {
            LOGGER.debug(".updateBOMForAllDimensions: working branch is not WIP");
            FlexBOMLink wipBranch = FlexBOMLink.newFlexBOMLink();

            try {
               wipBranch = (FlexBOMLink)workingBranch.copyState(wipBranch);
               wipBranch.setWip(true);
               wipBranch.setFlexType(bomPart.getFlexType());
               wipBranch.setInDate((Timestamp)null);
               wipBranch.setOutDate((Timestamp)null);
               if (isRemovedOverride) {
                  wipBranch.setDropped(true);
               }

               workingBranch = wipBranch;
            } catch (WTPropertyVetoException var18) {
               throw new WTException(var18);
            }
         }

         if (FormatHelper.parseBoolean((String)changeInfo.get("DROPPED"))) {
            if (!dimensionOverride) {
               this.dropBranchFromBOM(bomPart, new Integer(branchId));
            }
         } else {
            this.loadAndSaveChanges(workingBranch, changeInfo, false, massChange);
         }
      }
   }

   public boolean checkChange(FlexBOMPart oldBomPart, FlexBOMPart bomPart) throws WTException {
      LOGGER.debug(".checkChange START");
      Collection attributes = bomPart.getFlexType().getAttributeGroup(PRIMARY_MATERIAL_GROUP, "BOM_SCOPE", (String)null);
      Iterator att = attributes.iterator();

      String attKey;
      do {
         if (!att.hasNext()) {
            LOGGER.debug(".checkChange END");
            return false;
         }

         FlexTypeAttribute fta = (FlexTypeAttribute)att.next();
         attKey = fta.getAttKey();
      } while(!PrimaryMaterialUtility.compareValueChanged(bomPart, attKey, oldBomPart.getValue(attKey), bomPart.getValue(attKey)));

      LOGGER.debug(".checkChange END");
      return true;
   }

   public void syncPrimaryMaterialAtts(FlexBOMPart bomPart) throws WTException {
      LOGGER.debug(".syncPrimaryMaterialAtts(): Starting...");
      Collection links = FlexSpecQuery.getSpecToComponentLinks(bomPart, false, true);
      HashSet<String> copiedSourceToSeasonLink = new HashSet();
      Iterator i = links.iterator();
      FlexSpecToComponentLink link = null;

      while(i.hasNext()) {
         link = (FlexSpecToComponentLink)i.next();
         FlexSpecification spec = (FlexSpecification)VersionHelper.latestIterationOf(link.getSpecificationMaster());
         this.syncPrimaryMaterialAtts(spec, bomPart, copiedSourceToSeasonLink);
      }

      LOGGER.debug(".syncPrimaryMaterialAtts(): End");
   }

   public void syncPrimaryMaterialAtts(FlexSpecification flexSpec, FlexBOMPart bomPart) throws WTException {
      this.syncPrimaryMaterialAtts(flexSpec, bomPart, new HashSet());
   }

   public void syncPrimaryMaterialAtts(FlexSpecification flexSpec, FlexBOMPart bomPart, HashSet<String> copiedSourceToSeasonLink) throws WTException {
      this.syncPrimaryMaterialAtts(flexSpec, bomPart, copiedSourceToSeasonLink, false);
   }

   public void syncPrimaryMaterialAtts(FlexSpecification flexSpec, FlexBOMPart bomPart, HashSet<String> copiedSourceToSeasonLink, boolean clearOut) throws WTException {
      if ("MAIN".equals(bomPart.bomType)) {
         LCSSourcingConfigQuery sourceConfigQuery = new LCSSourcingConfigQuery();
         SearchResults searchResults = FlexSpecQuery.findPrimarySpecToSeasonLinks((LCSSeasonMaster)null, (WTObject)null, flexSpec.getMaster());
         if (searchResults.getResults().size() > 0) {
            LCSSourcingConfig sConfig = (LCSSourcingConfig)VersionHelper.latestIterationOf((LCSSourcingConfigMaster)flexSpec.getSpecSource());
            Collection<FlexSpecToSeasonLink> specToSeasonlinks = LCSQuery.getObjectsFromResults(searchResults.getResults(), "OR:com.lcs.wc.specification.FlexSpecToSeasonLink:", "FLEXSPECTOSEASONLINK.IDA2A2");
            Iterator var9 = specToSeasonlinks.iterator();

            while(var9.hasNext()) {
               FlexSpecToSeasonLink specToSeasonlink = (FlexSpecToSeasonLink)var9.next();
               LCSSourceToSeasonLink sourceToSeasonLink = sourceConfigQuery.getSourceToSeasonLink(sConfig.getMaster(), specToSeasonlink.getSeasonMaster());
               sourceToSeasonLink = (LCSSourceToSeasonLink)VersionHelper.latestIterationOf(sourceToSeasonLink);
               if (clearOut) {
                  this.syncAtts(bomPart, sourceToSeasonLink, true);
               } else if (sourceToSeasonLink != null && !copiedSourceToSeasonLink.contains(FormatHelper.getObjectId(sourceToSeasonLink))) {
                  this.syncAtts(bomPart, sourceToSeasonLink, false);
                  copiedSourceToSeasonLink.add(FormatHelper.getObjectId(sourceToSeasonLink));
               }
            }
         }

      }
   }

   public void removePrimaryMaterialAttGroupFromSourceToSeason(FlexSpecification flexSpec) throws WTException {
      FlexSpecToComponentLink link = FlexSpecQuery.getPrimaryComponentLink(flexSpec, "BOM");
      if (link != null) {
         WTObject component = VersionHelper.latestIterationOf((Mastered)link.getComponent());
         if (component instanceof FlexBOMPart) {
            this.syncPrimaryMaterialAtts(flexSpec, (FlexBOMPart)component, (HashSet)null, true);
         }
      }

   }

   public void syncAtts(FlexTyped source, FlexTyped target, boolean clearOut) throws WTException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(CLASSNAME + ": syncAtts()");
      }

      if (null != source && null != target) {
         if (source instanceof FlexBOMPart) {
            if (!"MAIN".equals(((FlexBOMPart)source).bomType)) {
               return;
            }

            Collection fromAtts = source.getFlexType().getAttributeGroup(PRIMARY_MATERIAL_GROUP, "BOM_SCOPE", (String)null);
            Collection<String> hiddenAttKeys = FormatHelper.commaSeparatedListToCollection(HIDDEN_SYNC_ATTS);
            Iterator i = hiddenAttKeys.iterator();

            while(i.hasNext()) {
               fromAtts.add(source.getFlexType().getAttribute((String)i.next()));
            }

            Map sourceMap = new HashMap();
            Iterator fromAttsIt = fromAtts.iterator();

            while(fromAttsIt.hasNext()) {
               FlexTypeAttribute fromAttribute = (FlexTypeAttribute)fromAttsIt.next();
               sourceMap.put(fromAttribute.getAttKey(), fromAttribute);
            }

            Collection toAtts = target.getFlexType().getAttributeGroup(PRIMARY_MATERIAL_GROUP, "SOURCE_TO_SEASON_SCOPE", "PRODUCT");
            Iterator toAttsIt = hiddenAttKeys.iterator();

            while(toAttsIt.hasNext()) {
               toAtts.add(target.getFlexType().getAttribute((String)toAttsIt.next()));
            }

            toAttsIt = toAtts.iterator();

            while(toAttsIt.hasNext()) {
               FlexTypeAttribute toAttribute = (FlexTypeAttribute)toAttsIt.next();
               if (null != sourceMap.get(toAttribute.getAttKey())) {
                  FlexTypeAttribute fromAttribute = (FlexTypeAttribute)sourceMap.get(toAttribute.getAttKey());
                  if (toAttribute.getAttVariableType().equals(fromAttribute.getAttVariableType())) {
                     try {
                        if (!clearOut) {
                           toAttribute.setValue(target, fromAttribute.getValue(source));
                        } else {
                           toAttribute.setValue(target, (Object)null);
                        }
                     } catch (WTPropertyVetoException var16) {
                        if (LOGGER.isDebugEnabled()) {
                           LOGGER.debug(CLASSNAME + ": throw exception when synchronize primary group attributes...");
                        }

                        throw new WTException();
                     }
                  }
               }
            }
         }

         if (target instanceof LCSSourceToSeasonLink) {
            boolean originalAccessEnforced = SessionServerHelper.manager.setAccessEnforced(false);

            try {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug(CLASSNAME + ": save target object when synchronize primary group attributes");
               }

               LCSSourceToSeasonLink var10000 = (LCSSourceToSeasonLink)(new LCSSourcingConfigLogic()).save((LCSSourceToSeasonLink)target);
            } finally {
               SessionServerHelper.manager.setAccessEnforced(originalAccessEnforced);
            }
         }
      }

   }

   private boolean isBlankSKUOverrideBranch(FlexBOMLink link, Map changeInfo) throws WTException {
      return this.isBlankOverrideBranch(link, changeInfo, true);
   }

   private boolean isBlankOverrideBranch(FlexBOMLink link, Map changeInfo) throws WTException {
      return this.isBlankOverrideBranch(link, changeInfo, false);
   }

   private boolean isBlankOverrideBranch(FlexBOMLink link, Map changeInfo, boolean ignoreSKUColor) throws WTException {
      FlexBOMLink topLevel = LCSFlexBOMQuery.findTopLevelBranch(link);
      if (topLevel == null) {
         return true;
      } else {
         Collection attributes = topLevel.getFlexType().getAllAttributes("LINK_SCOPE", (String)null, false);
         Iterator i = attributes.iterator();
         FlexTypeAttribute att = null;
         Object orVal = null;
         String colorDescAttKey = topLevel.getFlexType().getAttKeyForAttribute("colorDescription");

         while(i.hasNext()) {
            att = (FlexTypeAttribute)i.next();
            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug("Att " + att.getAttKey());
            }

            if (!ignoreSKUColor || !colorDescAttKey.equals(att.getAttKey())) {
               orVal = changeInfo.get(att.getAttKey().toUpperCase());
               if (orVal == null) {
                  orVal = att.getValue(link);
               }

               if (!att.getAttVariableType().equals("integer") && !att.getAttVariableType().equals("float") && !att.getAttVariableType().equals("currency") && !att.getAttVariableType().equals("sequence")) {
                  if (!this.isMatched(att.getValue(topLevel), orVal)) {
                     return false;
                  }
               } else if (!this.isMatched(att.getValue(topLevel), orVal, att.getAttDecimalFigures())) {
                  return false;
               }
            }
         }

         LOGGER.debug("Material");
         orVal = changeInfo.get("CHILDID");
         String id;
         LCSMaterialSupplierMaster msmaster;
         if (orVal == null) {
            orVal = link.getChild();
         } else if (orVal.equals(FormatHelper.getNumericFromOid(LCSMaterialSupplierQuery.PLACEHOLDERID))) {
            orVal = "";
         } else if (FormatHelper.hasContent((String)orVal)) {
            id = "OR:com.lcs.wc.material.LCSMaterialSupplierMaster:" + (String)orVal;
            msmaster = (LCSMaterialSupplierMaster)LCSQuery.findObjectById(id);
            orVal = FormatHelper.getNumericFromReference(msmaster.getMaterialMasterReference());
         }

         if (orVal != null && (orVal.equals(LCSMaterialQuery.PLACEHOLDER) || orVal.equals(FormatHelper.getNumericFromOid(LCSMaterialQuery.PLACEHOLDERID)))) {
            orVal = "";
         }

         if (orVal != null && (orVal.equals(LCSMaterialSupplierQuery.PLACEHOLDER) || orVal.equals(FormatHelper.getNumericFromOid(LCSMaterialSupplierQuery.PLACEHOLDERID)))) {
            orVal = "";
         }

         if (!this.isMatched(topLevel.getChild(), orVal)) {
            return false;
         } else {
            LOGGER.debug("Supplier");
            orVal = changeInfo.get("MATERIALSUPPLIERMASTERID");
            if (orVal == null) {
               orVal = link.getSupplier();
            } else if (orVal.equals(FormatHelper.getNumericFromOid(LCSMaterialSupplierQuery.PLACEHOLDERID))) {
               orVal = "";
            } else if (FormatHelper.hasContent((String)orVal)) {
               id = "OR:com.lcs.wc.material.LCSMaterialSupplierMaster:" + (String)orVal;
               msmaster = (LCSMaterialSupplierMaster)LCSQuery.findObjectById(id);
               orVal = FormatHelper.getNumericFromReference(msmaster.getSupplierMasterReference());
            }

            if (orVal != null && (orVal.equals(LCSSupplierQuery.PLACEHOLDER) || orVal.equals(FormatHelper.getNumericFromOid(LCSSupplierQuery.PLACEHOLDERID)))) {
               orVal = "";
            }

            if (!this.isMatched(topLevel.getSupplier(), orVal)) {
               return false;
            } else {
               if (!ignoreSKUColor) {
                  LOGGER.debug("Color");
                  orVal = changeInfo.get("COLORID");
                  if (orVal == null) {
                     orVal = link.getColor();
                  }

                  if (!this.isMatched(topLevel.getColor(), orVal)) {
                     return false;
                  }

                  LOGGER.debug("Material Color");
                  orVal = changeInfo.get("MATERIALCOLORID");
                  boolean blankColor = "0".equals(changeInfo.get("COLORID"));
                  if (!blankColor && orVal == null) {
                     orVal = link.getMaterialColor();
                  }

                  if (!this.isMatched(topLevel.getMaterialColor(), orVal)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   private boolean isMatched(Object val1, Object val2) {
      return this.isMatched(val1, val2, -1);
   }

   private boolean isMatched(Object val1, Object val2, int decimalFigures) {
      if (val1 == null) {
         val1 = "";
      }

      if (val2 == null) {
         val2 = "";
      }

      if (val1 instanceof LCSMaterialMaster && LCSMaterialQuery.PLACEHOLDERID.equals(FormatHelper.getObjectId((LCSMaterialMaster)val1))) {
         val1 = "";
      }

      if (val2 instanceof LCSMaterialMaster && LCSMaterialQuery.PLACEHOLDERID.equals(FormatHelper.getObjectId((LCSMaterialMaster)val2))) {
         val2 = "";
      }

      if (val1 instanceof LCSMaterialSupplierMaster && LCSMaterialSupplierQuery.PLACEHOLDERID.equals(FormatHelper.getObjectId((LCSMaterialSupplierMaster)val1))) {
         val1 = "";
      }

      if (val2 instanceof LCSMaterialSupplierMaster && LCSMaterialSupplierQuery.PLACEHOLDERID.equals(FormatHelper.getObjectId((LCSMaterialSupplierMaster)val2))) {
         val2 = "";
      }

      if (val1 instanceof LCSSupplierMaster && LCSSupplierQuery.PLACEHOLDERID.equals(FormatHelper.getObjectId((LCSSupplierMaster)val1))) {
         val1 = "";
      }

      if (val2 instanceof LCSSupplierMaster && LCSSupplierQuery.PLACEHOLDERID.equals(FormatHelper.getObjectId((LCSSupplierMaster)val2))) {
         val2 = "";
      }

      if (val1 instanceof WTObject) {
         val1 = FormatHelper.getNumericObjectIdFromObject((WTObject)val1);
      }

      if (val2 instanceof WTObject) {
         val2 = FormatHelper.getNumericObjectIdFromObject((WTObject)val2);
      }

      Object val1 = FormatHelper.format(val1.toString());
      Object val2 = FormatHelper.format(val2.toString());
      if (decimalFigures > 0) {
         if (FormatHelper.hasContent((String)val1)) {
            val1 = FormatHelper.formatWithPrecision(new Double(val1.toString()), decimalFigures);
         }

         if (FormatHelper.hasContent((String)val2)) {
            val2 = FormatHelper.formatWithPrecision(new Double(val2.toString()), decimalFigures);
         }
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("\tVal1: " + val1);
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("\tVal2: " + val2);
      }

      if (FormatHelper.hasContent((String)val1)) {
         if (FormatHelper.hasContent((String)val2) && !val2.equals(val1)) {
            return false;
         }
      } else if (FormatHelper.hasContent((String)val2)) {
         return false;
      }

      return true;
   }

   private Collection<FlexObject> orderChanges(Collection<FlexObject> changes) {
      Collection<FlexObject> ordered = new ArrayList();
      Collection<FlexObject> topLevel = new ArrayList();
      Collection<FlexObject> subAssembly = new ArrayList();
      List<FlexObject> ors = new ArrayList();
      Iterator var6 = changes.iterator();

      while(true) {
         FlexObject obj;
         while(var6.hasNext()) {
            obj = (FlexObject)var6.next();
            if (!FormatHelper.hasContent(obj.getString("masterBranchId")) && !FormatHelper.hasContent(obj.getString("dimensionName"))) {
               topLevel.add(obj);
            } else if (FormatHelper.hasContent(obj.getString("masterBranchId")) && !FormatHelper.hasContent(obj.getString("dimensionName"))) {
               subAssembly.add(obj);
            } else {
               ors.add(obj);
            }
         }

         var6 = topLevel.iterator();

         while(var6.hasNext()) {
            obj = (FlexObject)var6.next();
            ordered.add(obj);
         }

         var6 = subAssembly.iterator();

         while(var6.hasNext()) {
            obj = (FlexObject)var6.next();
            ordered.add(obj);
         }

         Collections.sort(ors, new com.lcs.wc.flexbom.LCSFlexBOMLogic.DimensionNameComparatorForFlexObject(this, (1)null));
         var6 = ors.iterator();

         while(var6.hasNext()) {
            obj = (FlexObject)var6.next();
            ordered.add(obj);
         }

         return ordered;
      }
   }

   private Collection orderChangesLinks(Collection<FlexBOMLink> changes) {
      Collection<FlexBOMLink> ordered = new ArrayList();
      Collection<FlexBOMLink> topLevel = new ArrayList();
      Collection<FlexBOMLink> subAssembly = new ArrayList();
      List<FlexBOMLink> ors = new ArrayList();
      Iterator var6 = changes.iterator();

      while(true) {
         FlexBOMLink obj;
         while(var6.hasNext()) {
            obj = (FlexBOMLink)var6.next();
            if (!FormatHelper.hasContent("" + obj.getMasterBranchId()) && !FormatHelper.hasContent("" + obj.getDimensionName())) {
               topLevel.add(obj);
            } else if (FormatHelper.hasContent("" + obj.getMasterBranchId()) && !FormatHelper.hasContent("" + obj.getDimensionName())) {
               subAssembly.add(obj);
            } else {
               ors.add(obj);
            }
         }

         var6 = topLevel.iterator();

         while(var6.hasNext()) {
            obj = (FlexBOMLink)var6.next();
            ordered.add(obj);
         }

         var6 = subAssembly.iterator();

         while(var6.hasNext()) {
            obj = (FlexBOMLink)var6.next();
            ordered.add(obj);
         }

         Collections.sort(ors, new com.lcs.wc.flexbom.LCSFlexBOMLogic.DimensionNameComparatorForFlexBOMLink(this, (1)null));
         var6 = ors.iterator();

         while(var6.hasNext()) {
            obj = (FlexBOMLink)var6.next();
            ordered.add(obj);
         }

         return ordered;
      }
   }

   public Map hashLink(Collection links) {
      Iterator it = links.iterator();
      Map table = new HashMap();
      FlexBOMLink link = null;

      while(it.hasNext()) {
         link = (FlexBOMLink)it.next();
         table.put("" + link.getBranchId(), link);
      }

      return table;
   }

   public Map hashLinksByDimensionId(Collection links) {
      Iterator it = links.iterator();
      Map table = new HashMap();
      FlexBOMLink link = null;

      while(it.hasNext()) {
         link = (FlexBOMLink)it.next();
         table.put("" + link.getDimensionId(), link);
      }

      return table;
   }

   public Map hashMapsByDimensionId(String nameStub, Collection maps) {
      Iterator it = maps.iterator();
      Map table = new HashMap();
      HashMap map = null;

      while(it.hasNext()) {
         map = (HashMap)it.next();
         String key = nameStub + (String)map.get("dimensionId");
         table.put(key, map);
      }

      return table;
   }

   protected void loadAndSaveChanges(FlexBOMLink link, Map changes, boolean allowNoWipPersist) throws WTException {
      this.loadAndSaveChanges(link, changes, allowNoWipPersist, false);
   }

   protected void loadAndSaveChanges(FlexBOMLink link, Map<String, Object> changes, boolean allowNoWipPersist, boolean massChange) throws WTException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".loadAndSaveChanges  \nMake Changes:\n " + changes);
      }

      link.calculateDimensionId();
      if (!link.isWip() && !allowNoWipPersist) {
         throw new WTException("Attempt to loadChanges into a non wip record.");
      } else {
         int colorMode;
         LCSMaterialColor matColor;
         try {
            if (FormatHelper.hasContent("" + changes.get("sortingNumber"))) {
               link.setSortingNumber(Integer.parseInt("" + changes.get("sortingNumber")));
            }

            if (FormatHelper.hasContent("" + changes.get("masterBranchId"))) {
               link.setMasterBranchId(Integer.parseInt("" + changes.get("masterBranchId")));
            }

            if (FormatHelper.hasContent("" + changes.get("masterBranch"))) {
               link.setMasterBranch(Boolean.valueOf("" + changes.get("masterBranch")));
            }

            if (FormatHelper.parseBoolean((String)changes.get("DROPPED"))) {
               if (link.getSequence() < 0) {
                  LOGGER.debug(".loadAndSaveChanges Dropped WIP Link is new.... so deleting");
                  this.delete(link);
                  return;
               }

               link.setDropped(true);
               link = (FlexBOMLink)persist(link);
               return;
            }

            LCSSupplierMaster supplierMaster;
            if (changes.get("childId") != null) {
               boolean materialIsPlaceHolder = false;
               if (FormatHelper.hasContent("" + changes.get("childId"))) {
                  LCSMaterialSupplierMaster materialSupplierMaster = (LCSMaterialSupplierMaster)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialSupplierMaster:" + changes.get("childId"));
                  link.setChildReference(materialSupplierMaster.getMaterialMasterReference());
                  link.setSupplierReference(materialSupplierMaster.getSupplierMasterReference());
                  supplierMaster = materialSupplierMaster.getSupplierMaster();
                  if (null != supplierMaster) {
                     link.setValue("supplierDescription", supplierMaster.getSupplierName());
                  }
               } else {
                  link.setChild(LCSMaterialQuery.PLACEHOLDER);
                  link.setSupplier(LCSSupplierQuery.PLACEHOLDER);
                  materialIsPlaceHolder = true;
               }

               if (!materialIsPlaceHolder && !FormatHelper.hasContentAllowZero((String)changes.get("colorId"))) {
                  LCSColor color = this.getColorForRecord(link);
                  if (color != null) {
                     new LCSMaterialColorQuery();
                     LCSMaterialColor matColor = LCSMaterialColorQuery.findMaterialColorsForMaterialSupplierAndColor(FormatHelper.getObjectId(link.getChild()), FormatHelper.getObjectId(link.getSupplier()), FormatHelper.getObjectId(color));
                     colorMode = this.getColorMode(link.getChild());
                     this.setMaterialColorId(link, changes, link.getChild(), link.getSupplier(), colorMode, matColor);
                  }
               }
            }

            if (link.getChildReference() == null) {
               link.setChild(LCSMaterialQuery.PLACEHOLDER);
            }

            if (link.getSupplierReference() == null) {
               link.setSupplier(LCSSupplierQuery.PLACEHOLDER);
            }

            if (FormatHelper.hasContentAllowZero((String)changes.get("colorId")) && !FormatHelper.hasContent((String)changes.get("materialColorId"))) {
               LOGGER.debug(".loadAndSaveChanges Setting Color!!");
               if ("0".equals(changes.get("colorId"))) {
                  link.setColor((LCSColor)null);
                  link.setMaterialColor((LCSMaterialColor)null);
                  link.setValue("colorDescription", "");
               } else {
                  LCSColor color = (LCSColor)LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:" + changes.get("colorId"));
                  link.setColor(color);
                  link.setValue("colorDescription", color.getValue("name"));
                  if (LOGGER.isDebugEnabled()) {
                     LOGGER.debug("loadAndSaveChanges Assigning Color: color id = " + link.getColor().getColorName());
                  }

                  if (!massChange) {
                     LCSMaterialMaster childMaster = link.getChild();
                     supplierMaster = link.getSupplier();
                     FlexBOMLink productLink = null;
                     if (FormatHelper.hasContent(link.getDimensionName()) && FormatHelper.areWTObjectsEqual(childMaster, LCSMaterialQuery.PLACEHOLDER) && !FormatHelper.hasContent((String)link.getValue("materialDescription")) && !FormatHelper.hasContent((String)changes.get("materialDescription"))) {
                        LOGGER.debug("This is an override record with placeholder for material and does not have a value for material description");
                        if (FormatHelper.hasContent(link.getDimensionName())) {
                           LOGGER.debug(".loadAndSaveChanges Assigning Color: Looking up product link");
                           productLink = LCSFlexBOMQuery.findNextApplicableLink(link);
                           if (productLink != null) {
                              childMaster = productLink.getChild();
                           }

                           if (LOGGER.isDebugEnabled()) {
                              LOGGER.debug(".loadAndSaveChanges Assigning Color: productLink " + productLink);
                           }

                           if (LOGGER.isDebugEnabled()) {
                              LOGGER.debug(".loadAndSaveChanges Assigning Color: childMaster " + childMaster);
                           }
                        }
                     }

                     if (FormatHelper.areWTObjectsEqual(link.getSupplier(), LCSSupplierQuery.PLACEHOLDER) && productLink != null) {
                        supplierMaster = productLink.getSupplier();
                        if (LOGGER.isDebugEnabled()) {
                           LOGGER.debug(".loadAndSaveChanges Assigning Color: supplierMaster " + supplierMaster);
                        }
                     }

                     int colorMode = this.getColorMode(childMaster);
                     matColor = null;
                     if (!FormatHelper.areWTObjectsEqual(childMaster, LCSMaterialQuery.PLACEHOLDER) && childMaster != null && supplierMaster != null && link.getColor() != null) {
                        LOGGER.debug(".loadAndSaveChanges Looking up materialColor, material not placeholder, valid child/supplier/color ");
                        new LCSMaterialColorQuery();
                        matColor = LCSMaterialColorQuery.findMaterialColorsForMaterialSupplierAndColor(FormatHelper.getObjectId(childMaster), FormatHelper.getObjectId(supplierMaster), FormatHelper.getObjectId(link.getColor()));
                        this.setMaterialColorId(link, changes, childMaster, supplierMaster, colorMode, matColor);
                     }
                  }
               }
            }

            if (FormatHelper.hasContentAllowZero((String)changes.get("materialColorId"))) {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug(".loadAndSaveChanges Setting Material Color: " + changes.get("materialColorId"));
               }

               if ("0".equals(changes.get("materialColorId"))) {
                  link.setMaterialColor((LCSMaterialColor)null);
                  if (link.getColor() != null) {
                     link.setValue("colorDescription", link.getColor().getColorName());
                  } else {
                     link.setValue("colorDescription", "");
                     link.setColor((LCSColor)null);
                  }
               } else {
                  link.setMaterialColor((LCSMaterialColor)LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterialColor:" + changes.get("materialColorId")));
                  link.setColor(link.getMaterialColor().getColor());
                  link.setValue("colorDescription", link.getMaterialColor().getColor().getValue("name"));
               }
            }

            if (WCPART_ENABLED && FormatHelper.hasContentAllowZero((String)changes.get("wcPartId"))) {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug(".loadAndSaveChanges Setting WCPart: " + changes.get("wcPartId"));
               }

               if ("0".equals(changes.get("wcPartId"))) {
                  link.setWcPart((WTPart)null);
               } else {
                  link.setWcPart((WTPart)LCSQuery.findObjectById("VR:wt.part.WTPart:" + changes.get("wcPartId")));
               }
            }

            if (changes.containsKey("QUANTITY")) {
               if (FormatHelper.hasContentAllowZero("" + changes.get("QUANTITY"))) {
                  link.setYield(FormatHelper.parseDouble((String)changes.get("QUANTITY")));
               } else {
                  link.setYield(0.0D);
               }
            }

            Collection<FlexTypeAttribute> attSet = link.getFlexType().getAllAttributes("LINK_SCOPE", (String)null);
            Map<String, FlexTypeAttribute> attMap = FlexTypeUtil.getAttributeMap(attSet);
            Map<String, FlexTypeAttribute> attMapUpperCase = new HashMap();
            Iterator var30 = attMap.entrySet().iterator();

            label278:
            while(true) {
               if (!var30.hasNext()) {
                  var30 = changes.keySet().iterator();

                  while(true) {
                     if (!var30.hasNext()) {
                        break label278;
                     }

                     String key = (String)var30.next();
                     if (attMapUpperCase.containsKey(key)) {
                        link.setValue(key, changes.get(key));
                     }
                  }
               }

               Entry<String, FlexTypeAttribute> entry = (Entry)var30.next();
               attMapUpperCase.put(((String)entry.getKey()).toUpperCase(), entry.getValue());
            }
         } catch (WTPropertyVetoException var18) {
            throw new WTException(var18);
         }

         if (link.getValue("section") == null && link.getDimensionName() == null) {
            throw new WTException("Attempt made to create a flexbomlink with an empty section value and no dimension name");
         } else {
            try {
               if (!FormatHelper.hasContent(link.getDimensionName())) {
                  LOGGER.debug("******************\nCheck for top level material color modes where material color made need adjusting");
                  LCSMaterialMaster childMaster = link.getChild();
                  LCSSupplierMaster supplierMaster = link.getSupplier();
                  LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(childMaster);
                  colorMode = 0;
                  if (material.getFlexType().getAttributeKeyList().contains("TYPECOLORCONTROLLED")) {
                     colorMode = ((Float)material.getValue("typeColorControlled")).intValue();
                     if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(".loadAndSaveChanges typeColorControlled mode=" + colorMode);
                     }
                  }

                  if (material.getFlexType().getAttributeKeyList().contains("MATERIALCOLORCONTROLLED") && FormatHelper.hasContentAllowZero((String)material.getValue("materialColorControlled"))) {
                     colorMode = (new Float((String)material.getValue("materialColorControlled"))).intValue();
                     if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(".loadAndSaveChanges materialColorControlled mode=" + colorMode + " overriding type level setting");
                     }
                  }

                  if (LOGGER.isDebugEnabled()) {
                     LOGGER.debug("Color mode: " + colorMode);
                  }

                  Iterator orLink;
                  LCSColor mcolor;
                  FlexBOMLink orLink;
                  Collection orLinks;
                  if (colorMode == 2) {
                     orLinks = LCSFlexBOMQuery.getMissingColorsForOverridesForBranch(link, childMaster, supplierMaster);
                     matColor = null;
                     if (!FormatHelper.hasContentAllowZero((String)changes.get("colorId")) && link.getColor() != null) {
                        new LCSMaterialColorQuery();
                        matColor = LCSMaterialColorQuery.findMaterialColorsForMaterialSupplierAndColor(FormatHelper.getObjectId(childMaster), FormatHelper.getObjectId(supplierMaster), FormatHelper.getObjectId(link.getColor()));
                        if (matColor == null) {
                           LOGGER.debug(".loadAndSaveChanges palette mode material verification...materialColor NOT found");
                           matColor = this.createMaterialColor(link.getColor(), childMaster, supplierMaster);
                           link.setMaterialColor(matColor);
                           LOGGER.debug(".loadAndSaveChanges materialColor create: Material Color Created & Assigned");
                        }
                     }

                     LOGGER.debug("missing colors #: " + orLinks.size());
                     orLink = orLinks.iterator();
                     mcolor = null;
                     orLink = null;
                     FlexObject fobj = null;
                     Collection<String> processed = new ArrayList();
                     String mcolorid = "";

                     while(orLink.hasNext()) {
                        fobj = (FlexObject)orLink.next();
                        mcolorid = fobj.getData("FLEXBOMLINK.IDA3D5");
                        if (!processed.contains(mcolorid)) {
                           orLink = (FlexBOMLink)LCSFlexBOMQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMLink:" + fobj.getData("FLEXBOMLINK.IDA2A2"));
                           mcolor = (LCSColor)LCSFlexBOMQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:" + mcolorid);
                           if (LOGGER.isDebugEnabled()) {
                              LOGGER.debug("color: " + mcolor.getValue("name"));
                           }

                           matColor = this.createMaterialColor(mcolor, childMaster, supplierMaster);
                           orLink.setMaterialColor(matColor);
                           persist(orLink);
                           processed.add(mcolorid);
                           LOGGER.debug(".loadAndSaveChanges materialColor create: Material Color Created & Assigned");
                        }
                     }
                  }

                  if (colorMode == 0) {
                     LOGGER.debug("uncontrolled mode");
                     orLinks = LCSFlexBOMQuery.getAllLinksForBranch(link);
                     if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("links found: " + orLinks.size());
                     }

                     Iterator<FlexBOMLink> allLinks = orLinks.iterator();
                     orLink = null;
                     mcolor = null;
                     orLink = null;

                     while(allLinks.hasNext()) {
                        FlexBOMLink orLink = (FlexBOMLink)allLinks.next();
                        if (LOGGER.isDebugEnabled()) {
                           LOGGER.debug("dimensionName: " + orLink.getDimensionName());
                        }

                        if (FormatHelper.hasContent(orLink.getDimensionName())) {
                           LCSMaterialMaster orChildMaster = orLink.getChild();
                           if (LOGGER.isDebugEnabled()) {
                              LOGGER.debug("placeholder  : " + LCSMaterialQuery.PLACEHOLDER);
                           }

                           if (LOGGER.isDebugEnabled()) {
                              LOGGER.debug("orChildMaster: " + orChildMaster);
                           }

                           if (FormatHelper.areWTObjectsEqual(orChildMaster, LCSMaterialQuery.PLACEHOLDER) && !FormatHelper.hasContent((String)orLink.getValue("materialDescription")) && orLink.getColor() != null) {
                              LOGGER.debug("childmaster = placeholder...do materialColor check");
                              new LCSMaterialColorQuery();
                              LCSMaterialColor matColor = LCSMaterialColorQuery.findMaterialColorsForMaterialSupplierAndColor(FormatHelper.getObjectId(childMaster), FormatHelper.getObjectId(supplierMaster), FormatHelper.getObjectId(orLink.getColor()));
                              if (LOGGER.isDebugEnabled()) {
                                 LOGGER.debug("materialColor found: " + matColor);
                              }

                              if (LOGGER.isDebugEnabled()) {
                                 LOGGER.debug("materialColor set on orLink: " + orLink.getMaterialColor());
                              }

                              if (matColor != null) {
                                 orLink.setMaterialColor(matColor);
                                 persist(orLink);
                              } else if (orLink.getMaterialColor() != null) {
                                 orLink.setMaterialColor((LCSMaterialColor)null);
                                 persist(orLink);
                              }
                           }
                        }
                     }

                     LOGGER.debug("end color mode check\n******************");
                  }
               }
            } catch (WTPropertyVetoException var17) {
               throw new WTException(var17);
            }

            deriveFlexTypeValues(link, true);
            link = (FlexBOMLink)persist(link);
            LOGGER.debug(".loadAndSaveChanges  END");
         }
      }
   }

   private LCSMaterialColor createMaterialColor(LCSColor color, LCSMaterialMaster childMaster, LCSSupplierMaster supplierMaster) throws WTException, WTPropertyVetoException {
      LOGGER.debug("inside createMaterialColor");
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("color: " + color.getValue("name"));
      }

      LCSMaterialColor matColor = LCSMaterialColor.newLCSMaterialColor();
      matColor.setFlexType(FlexTypeCache.getFlexTypeFromPath("Material Color"));
      matColor.setColor(color);
      matColor.setMaterialMaster(childMaster);
      matColor.setSupplierMaster(supplierMaster);
      setFlexTypedDefaults(matColor, (String)null, (String)null);
      matColor = (new LCSMaterialColorLogic()).saveMaterialColor(matColor);
      return matColor;
   }

   private LCSColor getColorForRecord(FlexBOMLink link) throws WTException {
      LCSColor color = link.getColor();
      if (color == null && FormatHelper.hasContent(link.getDimensionName())) {
         FlexBOMLink topLink = LCSFlexBOMQuery.getToplevelLinkForBranch(link);
         if (topLink != null && FormatHelper.hasContent((String)link.getValue("colorDescription")) && !((String)link.getValue("colorDescription")).equals((String)link.getValue("topLink"))) {
            color = topLink.getColor();
         }
      }

      return color;
   }

   private int getColorMode(Mastered linkChildMaster) throws WTException {
      LCSMaterial material = (LCSMaterial)VersionHelper.latestIterationOf(linkChildMaster);
      int colorMode = 0;
      if (material.getFlexType().getAttributeKeyList().contains("TYPECOLORCONTROLLED")) {
         colorMode = ((Float)material.getValue("typeColorControlled")).intValue();
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(".loadAndSaveChanges typeColorControlled mode=" + colorMode);
         }
      }

      if (material.getFlexType().getAttributeKeyList().contains("MATERIALCOLORCONTROLLED") && FormatHelper.hasContentAllowZero((String)material.getValue("materialColorControlled"))) {
         colorMode = (new Float((String)material.getValue("materialColorControlled"))).intValue();
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(".loadAndSaveChanges materialColorControlled mode=" + colorMode + " overriding type level setting");
         }
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".loadAndSaveChanges Assigning Color: material " + material.getFlexType().getFullName(true) + " : " + material.getName());
      }

      return colorMode;
   }

   private void setMaterialColorId(FlexBOMLink link, Map changes, LCSMaterialMaster childMaster, LCSSupplierMaster supplierMaster, int colorMode, LCSMaterialColor matColor) throws WTPropertyVetoException, WTException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("childMaster: " + childMaster);
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("supplierMaster: " + supplierMaster);
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("link.getColor(): " + link.getColor());
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("colorMode: " + colorMode);
      }

      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("matColor: " + matColor);
      }

      if (matColor != null) {
         LOGGER.debug(".loadAndSaveChanges materialColor found, using it ");
         this.doSetMaterialColorId(link, changes, matColor);
      } else if (colorMode == 2 && link.getColor() != null) {
         LOGGER.debug(".loadAndSaveChanges materialColor NOT found");
         LOGGER.debug(".loadAndSaveChanges materialColor create: colorMode=2 and material is not the placeholder");
         matColor = this.createMaterialColor(link.getColor(), childMaster, supplierMaster);
         LOGGER.debug(".loadAndSaveChanges materialColor create: Material Color Created & Assigned");
         this.doSetMaterialColorId(link, changes, matColor);
      } else {
         this.doSetMaterialColorId(link, changes, (LCSMaterialColor)null);
      }

   }

   private void doSetMaterialColorId(FlexBOMLink link, Map changes, LCSMaterialColor matColor) throws WTPropertyVetoException, WTException {
      String matColorId = "0";
      if (matColor != null) {
         link.setMaterialColor(matColor);
         matColorId = FormatHelper.getNumericObjectIdFromObject(matColor);
      }

      if (changes instanceof FlexObject) {
         ((FlexObject)changes).setData("materialColorId", matColorId);
      } else {
         changes.put("materialColorId", matColorId);
      }

   }

   public FlexBOMPart checkInBOM(FlexBOMPart part) throws WTException {
      LCSPluginManager.handleEvent(part, "PRE_CHECK_IN");
      Collection links = LCSFlexBOMQuery.findFlexBOMLinks(part, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_AND_EFFECTIVE", (Date)null, true, "ALL_DIMENSIONS", (String)null, (String)null, (String)null);
      Date now = new Date();
      FlexBOMLink link = null;
      Map wipTable = new HashMap();
      Map effectiveTable = new HashMap();
      Iterator iter = links.iterator();

      while(iter.hasNext()) {
         link = (FlexBOMLink)iter.next();
         if (link.isWip()) {
            wipTable.put("" + link.getDimensionId(), link);
         } else {
            effectiveTable.put("" + link.getDimensionId(), link);
         }
      }

      FlexBOMLink wipLink = null;
      FlexBOMLink effectiveLink = null;
      iter = wipTable.values().iterator();

      while(iter.hasNext()) {
         wipLink = (FlexBOMLink)iter.next();
         effectiveLink = (FlexBOMLink)effectiveTable.get("" + wipLink.getDimensionId());
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(".checkInBOM: dimension: " + wipLink.getDimensionId() + " wipLink = " + wipLink + " effectiveLink = " + effectiveLink);
         }

         try {
            if (effectiveLink == null) {
               wipLink.setSequence(0);
            } else {
               wipLink.setSequence(effectiveLink.getSequence() + 1);
               effectiveLink.setOutDate(new Timestamp(now.getTime()));
               persist(effectiveLink);
            }

            if (wipLink.isWip() && wipLink.isDropped()) {
               this.delete(wipLink);
            } else {
               wipLink.setInDate(new Timestamp(now.getTime()));
               wipLink.setOutDate((Timestamp)null);
               wipLink.setWip(false);
               wipLink.calculateDimensionId();
               persist(wipLink);
            }
         } catch (WTPropertyVetoException var12) {
            throw new WTException(var12);
         }
      }

      try {
         part = (FlexBOMPart)WorkInProgressHelper.service.checkin(part, "");
      } catch (WTPropertyVetoException var11) {
         throw new WTException(var11);
      }

      LCSPluginManager.handleEvent(part, "POST_CHECK_IN");
      this.rollUpBOM(part);
      return part;
   }

   public void delete(Persistable p) throws WTException {
      if (p instanceof FlexBOMPart) {
         this.deleteFlexBOMPart((FlexBOMPart)p);
      } else {
         deleteObject((WTObject)p);
      }

   }

   public String createDimensionId(FlexBOMPart parent, String branchId, String sourceDimId, String colorDimId, String size1, String size2, String destDimId) {
      StringBuffer buffer = new StringBuffer();
      if (parent != null) {
         buffer.append("-PARENT:" + parent.getMasterReference().toString());
      }

      if (parent != null) {
         buffer.append("-REV:" + parent.getVersionIdentifier().getValue());
      }

      buffer.append("-BRANCH:" + branchId);
      if (FormatHelper.hasContent(sourceDimId)) {
         buffer.append("-SC:" + sourceDimId.substring(2));
      }

      if (FormatHelper.hasContent(colorDimId)) {
         buffer.append("-SKU:" + colorDimId.substring(2));
      }

      if (FormatHelper.hasContentAllowZero(size1)) {
         buffer.append("-SIZE1:" + size1);
      }

      if (FormatHelper.hasContentAllowZero(size2)) {
         buffer.append("-SIZE2:" + size2);
      }

      if (FormatHelper.hasContent(destDimId)) {
         buffer.append("-DESTINATION:" + destDimId.substring(2));
      }

      return buffer.toString();
   }

   public String createDimensionName(FlexBOMPart parent, String branchId, String sourceDimId, String colorDimId, String size1, String size2, String destDimId) {
      String dimensionName = "";
      if (FormatHelper.hasContent(sourceDimId)) {
         dimensionName = dimensionName + ":SOURCE";
      }

      if (FormatHelper.hasContent(colorDimId)) {
         dimensionName = dimensionName + ":SKU";
      }

      if (FormatHelper.hasContentAllowZero(size1)) {
         dimensionName = dimensionName + ":SIZE1";
      }

      if (FormatHelper.hasContentAllowZero(size2)) {
         dimensionName = dimensionName + ":SIZE2";
      }

      if (FormatHelper.hasContent(destDimId)) {
         dimensionName = dimensionName + ":DESTINATION";
      }

      return dimensionName;
   }

   public FlexBOMPart copyMaterialBOM(LCSMaterial source, LCSMaterial destination, String sourceVersion) throws WTException {
      return this.copyMaterialBOM(source, destination, sourceVersion, (Map)null);
   }

   public FlexBOMPart copyMaterialBOM(LCSMaterial source, LCSMaterial destination, String sourceVersion, Map overRideDimMap) throws WTException {
      LOGGER.debug(".copyMaterialBOM(LCSMaterial source, LCSMaterial destination, String sourceVersion)");
      Collection materialBOMs = (new LCSFlexBOMQuery()).findBOMPartsForOwner(source, sourceVersion, "MAIN");
      if (materialBOMs.size() < 1) {
         return null;
      } else {
         FlexBOMPart sourceBOMPart = (FlexBOMPart)materialBOMs.iterator().next();
         if (!FormatHelper.hasContent(sourceVersion)) {
            sourceBOMPart = (FlexBOMPart)VersionHelper.latestIterationOf(sourceBOMPart.getMaster());
         }

         FlexBOMPart destinationBOMPart = (new LCSFlexBOMLogic()).initiateBOMPart(destination, sourceBOMPart.getFlexType(), "MAIN");
         return this.copyBOM(sourceBOMPart, destinationBOMPart, (String)null, "REPLACE_COPY_MODE", true, overRideDimMap);
      }
   }

   public FlexBOMPart copyMaterialBOL(LCSMaterial source, LCSMaterial destination, String sourceVersion) throws WTException {
      LOGGER.debug(".copyMaterialBOL(LCSMaterial source, LCSMaterial destination, String sourceVersion)");
      Collection materialBOLs = (new LCSFlexBOMQuery()).findBOMPartsForOwner(source, sourceVersion, "LABOR");
      if (materialBOLs.size() < 1) {
         return null;
      } else {
         FlexBOMPart sourceBOLPart = (FlexBOMPart)materialBOLs.iterator().next();
         if (!FormatHelper.hasContent(sourceVersion)) {
            sourceBOLPart = (FlexBOMPart)VersionHelper.latestIterationOf(sourceBOLPart.getMaster());
         }

         FlexBOMPart destinationBOLPart = (new LCSFlexBOMLogic()).initiateBOMPart(destination, sourceBOLPart.getFlexType(), "LABOR");
         return this.copyBOM(sourceBOLPart, destinationBOLPart, (String)null, "REPLACE_COPY_MODE", true, (Map)null);
      }
   }

   public FlexBOMPart copyProductBOM(LCSProduct source, LCSProduct destination, boolean skuCopy) throws WTException {
      return this.copyProductBOM(source, destination, skuCopy, (String)null);
   }

   public FlexBOMPart copyProductBOM(LCSProduct source, LCSProduct destination, boolean skuCopy, String sourceVersion) throws WTException {
      Collection productBOMs = (new LCSFlexBOMQuery()).findBOMPartsForOwner(source, sourceVersion, "MAIN");
      if (productBOMs.size() < 1) {
         return null;
      } else {
         FlexBOMPart sourceBOMPart = (FlexBOMPart)productBOMs.iterator().next();
         if (!FormatHelper.hasContent(sourceVersion)) {
            sourceBOMPart = (FlexBOMPart)VersionHelper.latestIterationOf(sourceBOMPart.getMaster());
         }

         FlexBOMPart destinationBOMPart = (new LCSFlexBOMLogic()).initiateBOMPart(destination, sourceBOMPart.getFlexType(), "MAIN");
         Map skuMap = new HashMap();
         if (skuCopy) {
            Collection destinationSKUs = LCSSKUQuery.findAllSKUs(destination, false);
            Iterator skuIter = destinationSKUs.iterator();
            LCSSKU sku = null;

            while(skuIter.hasNext()) {
               sku = (LCSSKU)skuIter.next();
               if (sku.getCopiedFrom() != null) {
                  skuMap.put(FormatHelper.getNumericFromReference(sku.getCopiedFrom().getMasterReference()), sku.getMaster());
               }
            }

            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(".copyProductBOM: skuMap = " + skuMap);
            }
         }

         return this.copyBOM(sourceBOMPart, destinationBOMPart, (String)null, "REPLACE_COPY_MODE", true, skuMap);
      }
   }

   public FlexBOMPart copyBOM(FlexBOMPart source, FlexBOMPart destination, String section, String copyMode) throws WTException {
      return this.copyBOM(source, destination, section, copyMode, false, new HashMap());
   }

   public FlexBOMPart copyBOM(FlexBOMPart source, FlexBOMPart destination, String section, String copyMode, boolean deepCopy, Map overRideDimMap) throws WTException {
      return this.copyBOM(source, destination, section, copyMode, deepCopy, overRideDimMap, (Collection)null);
   }

   public FlexBOMPart copyBOM(FlexBOMPart source, FlexBOMPart destination, String section, String copyMode, boolean deepCopy, Map overRideDimMap, Collection copyLinkIds) throws WTException {
      LOGGER.debug("copyBOM....");
      FlexBOMPart sourceBOMPart = null;
      FlexBOMPart destinationBOMPart = null;
      if (source instanceof FlexBOMPart) {
         sourceBOMPart = source;
      }

      if (destination instanceof FlexBOMPart) {
         destinationBOMPart = destination;
      }

      BOMOwner sourceOwner = sourceBOMPart.getOwnerMaster();
      BOMOwner destinationOwner = destinationBOMPart.getOwnerMaster();
      if (FormatHelper.areWTObjectsEqual((WTObject)sourceOwner, (WTObject)destinationOwner)) {
         LOGGER.debug("Copy BOM: owners are equal");
         if ("REPLACE_COPY_MODE".equals(copyMode) && section == null) {
            return this.copyBOMForSameOwner(source, destination);
         }
      }

      boolean isCheckedOut = VersionHelper.isCheckedOut(destinationBOMPart);
      boolean isCopyInProgress = "true".equals(MethodContext.getContext().get("COPY_IN_PROGRESS")) || "true".equals(MethodContext.getContext().get("ASSOCIATE_IN_PROGRESS"));
      String copyProductMode = (String)MethodContext.getContext().get("copyMode");
      if (!isCheckedOut && !isCopyInProgress) {
         destinationBOMPart = (FlexBOMPart)VersionHelper.checkout(destinationBOMPart);
         isCheckedOut = true;
      }

      Collection currentSourceBOM = LCSFlexBOMQuery.findFlexBOMLinks(sourceBOMPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, "ALL_DIMENSIONS", (String)null, (String)null, (String)null);
      Collection currentDestBOM = LCSFlexBOMQuery.findFlexBOMLinks(destinationBOMPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, "ALL_DIMENSIONS", (String)null, (String)null, (String)null);
      int nextBranchId = this.getMaxBranchId(destinationBOMPart) + 1;
      Map dimensionMap;
      if (!"INSERT_COPY_MODE".equals(copyMode) && "REPLACE_COPY_MODE".equals(copyMode) && !"rowSelect".equals(section) && currentDestBOM != null && !currentDestBOM.isEmpty()) {
         if (FormatHelper.hasContent(section)) {
            dimensionMap = this.groupBySection(currentDestBOM);
            this.effectOutLinks((Collection)dimensionMap.get(section));
         } else {
            this.effectOutLinks(currentDestBOM);
         }

         currentDestBOM = LCSFlexBOMQuery.findFlexBOMLinks(destinationBOMPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, "ALL_DIMENSIONS", (String)null, (String)null, (String)null);
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("After delete: destination bom.size = " + currentDestBOM.size());
         }
      }

      dimensionMap = this.groupByDimensionName(currentSourceBOM);
      currentSourceBOM = (Collection)dimensionMap.get("");
      if (currentSourceBOM == null) {
         return destinationBOMPart;
      } else {
         Map sectionMap = this.groupBySection(currentSourceBOM);
         int destMaxRowNumber = false;
         Collection rowsToCopy = currentSourceBOM;
         if (FormatHelper.hasContent(section) && !"rowSelect".equals(section)) {
            rowsToCopy = (Collection)sectionMap.get(section);
         }

         if (rowsToCopy == null) {
            return destinationBOMPart;
         } else {
            Map destSectionMap = this.groupBySection(currentDestBOM);
            Collection sourceLink = (Collection)destSectionMap.get(section);
            int destMaxRowNumber = sourceLink == null ? 0 : sourceLink.size();
            rowsToCopy = this.orderChangesLinks(rowsToCopy);
            Iterator newLinkLoop = rowsToCopy.iterator();
            sourceLink = null;
            FlexBOMLink newLink = null;
            Map branchIdMap = new HashMap();
            Date now = new Date();

            while(true) {
               String colorMasterId;
               FlexBOMLink sourceLink;
               while(newLinkLoop.hasNext()) {
                  sourceLink = (FlexBOMLink)newLinkLoop.next();
                  if (sourceLink.isDropped()) {
                     LOGGER.debug("Found a dropped link....");
                  } else if (copyLinkIds == null || copyLinkIds.contains(FormatHelper.getObjectId(sourceLink))) {
                     newLink = FlexBOMLink.newFlexBOMLink();

                     try {
                        if (LOGGER.isDebugEnabled()) {
                           LOGGER.debug("About to copy link: " + sourceLink.getBranchId() + " part = " + sourceLink.getValue("partName") + " dimensionName = " + sourceLink.getDimensionName() + " dimensionId = " + sourceLink.getDimensionId());
                        }

                        if (LOGGER.isDebugEnabled()) {
                           LOGGER.debug("nextBranchId = " + nextBranchId);
                        }

                        sourceLink.copyState(newLink);
                        LOGGER.debug("should be applying copy rules here");
                        if (FormatHelper.hasContent(copyProductMode)) {
                           if (LOGGER.isDebugEnabled()) {
                              LOGGER.debug("copyMode: " + copyProductMode);
                           }

                           PropertyBasedAttributeValueLogic.setAttributes(newLink, "com.lcs.wc.flexbom.FlexBOMLink", "", copyProductMode);
                        } else {
                           LOGGER.debug("COPY copy mode");
                           PropertyBasedAttributeValueLogic.setAttributes(newLink, "com.lcs.wc.flexbom.FlexBOMLink", "", "COPY");
                        }

                        newLink.setParentReference(destinationBOMPart.getMasterReference());
                        newLink.setParentRev(destinationBOMPart.getVersionIdentifier().getValue());
                        newLink.setBranchId(nextBranchId++);
                        newLink.setInDate(new Timestamp(now.getTime()));
                        newLink.setOutDate((Timestamp)null);
                        newLink.setWip(false);
                        newLink.setSequence(0);
                        newLink.calculateDimensionId();
                        newLink.setSortingNumber(newLink.getSortingNumber() + destMaxRowNumber);

                        try {
                           if (!"null".equals(sourceLink.getMasterBranchId() + "") && FormatHelper.hasContent("" + sourceLink.getMasterBranchId())) {
                              colorMasterId = (String)branchIdMap.get("" + sourceLink.getMasterBranchId());
                              if (colorMasterId != null && !"null".equals(colorMasterId)) {
                                 newLink.setMasterBranchId(Integer.parseInt(colorMasterId));
                              }
                           }

                           branchIdMap.put("" + sourceLink.getBranchId(), "" + newLink.getBranchId());
                        } catch (NumberFormatException var37) {
                           System.out.println("Error setting sourceLink's MasterBranchId " + sourceLink.getMasterBranchId() + " --\n" + var37.getLocalizedMessage());
                        }
                     } catch (WTPropertyVetoException var40) {
                        var40.printStackTrace();
                        throw new WTException(var40.getLocalizedMessage());
                     }

                     deriveFlexTypeValues(newLink, true);
                     persist(newLink);
                  }
               }

               if (deepCopy) {
                  String size1Val = "";
                  String size2Val = "";
                  currentSourceBOM = this.obtainAllOverRideRows(dimensionMap);
                  this.groupBySection(currentSourceBOM);
                  newLinkLoop = currentSourceBOM.iterator();

                  label245:
                  while(true) {
                     while(true) {
                        if (!newLinkLoop.hasNext()) {
                           break label245;
                        }

                        sourceLink = (FlexBOMLink)newLinkLoop.next();
                        newLink = FlexBOMLink.newFlexBOMLink();

                        try {
                           sourceLink.copyState(newLink);
                           LOGGER.debug("Deep copy section...");
                           if (FormatHelper.hasContent(copyProductMode)) {
                              if (LOGGER.isDebugEnabled()) {
                                 LOGGER.debug("copyMode:" + copyProductMode);
                              }

                              PropertyBasedAttributeValueLogic.setAttributes(newLink, "com.lcs.wc.flexbom.FlexBOMLink", "", copyProductMode);
                           } else {
                              LOGGER.debug("COPY section");
                              PropertyBasedAttributeValueLogic.setAttributes(newLink, "com.lcs.wc.flexbom.FlexBOMLink", "", "COPY");
                           }

                           newLink.setParentReference(destinationBOMPart.getMasterReference());
                           newLink.setParentRev(destinationBOMPart.getVersionIdentifier().getValue());

                           try {
                              newLink.setBranchId(Integer.parseInt("" + branchIdMap.get("" + sourceLink.getBranchId())));
                           } catch (NumberFormatException var38) {
                              System.out.println("In the while loop - Error setting branchIdMap's MasterBranchId " + branchIdMap.get("" + sourceLink.getBranchId()) + " --\n" + var38.getLocalizedMessage());
                              continue;
                           }

                           newLink.setInDate(new Timestamp(now.getTime()));
                           newLink.setOutDate((Timestamp)null);
                           newLink.setWip(false);
                           newLink.setSequence(0);
                           if (overRideDimMap != null) {
                              if (sourceLink.getSourceDimension() != null) {
                                 String sourceMasterId = FormatHelper.getNumericFromReference(sourceLink.getSourceDimensionReference());
                                 WTObject sourceDimObject = (WTObject)overRideDimMap.get(sourceMasterId);
                                 if (sourceDimObject == null) {
                                    continue;
                                 }

                                 newLink.setSourceDimension(sourceDimObject);
                              } else if (FormatHelper.hasContent(sourceLink.getDimensionName()) && sourceLink.getDimensionName().indexOf("SOURCE") > -1) {
                                 continue;
                              }

                              if (sourceLink.getDestinationDimension() != null) {
                                 ProductDestination oldDestinationDim = (ProductDestination)sourceLink.getDestinationDimension();
                                 WTObject destinationDimObject = (WTObject)overRideDimMap.get(FormatHelper.getNumericObjectIdFromObject(oldDestinationDim));
                                 if (destinationDimObject == null) {
                                    continue;
                                 }

                                 newLink.setDestinationDimension((ProductDestination)destinationDimObject);
                              } else if (FormatHelper.hasContent(sourceLink.getDimensionName()) && sourceLink.getDimensionName().indexOf("DESTINATIION") > -1) {
                                 continue;
                              }

                              if (sourceLink.getColorDimension() != null) {
                                 colorMasterId = FormatHelper.getNumericFromReference(sourceLink.getColorDimensionReference());
                                 WTObject colorDimObject = (WTObject)overRideDimMap.get(colorMasterId);
                                 if (colorDimObject == null) {
                                    continue;
                                 }

                                 newLink.setColorDimension(colorDimObject);
                              } else if (FormatHelper.hasContent(sourceLink.getDimensionName()) && sourceLink.getDimensionName().indexOf("SKU") > -1) {
                                 continue;
                              }

                              if (FormatHelper.hasContentAllowZero(sourceLink.getSize1())) {
                                 size1Val = (String)overRideDimMap.get(sourceLink.getSize1());
                                 if (!FormatHelper.hasContent(size1Val)) {
                                    continue;
                                 }

                                 newLink.setSize1(size1Val);
                              } else if (FormatHelper.hasContent(sourceLink.getDimensionName()) && sourceLink.getDimensionName().indexOf("SIZE1") > -1) {
                                 continue;
                              }

                              if (FormatHelper.hasContentAllowZero(sourceLink.getSize2())) {
                                 size2Val = (String)overRideDimMap.get(sourceLink.getSize2());
                                 if (!FormatHelper.hasContent(size2Val)) {
                                    continue;
                                 }

                                 newLink.setSize2(size2Val);
                              } else if (FormatHelper.hasContent(sourceLink.getDimensionName()) && sourceLink.getDimensionName().indexOf("SIZE2") > -1) {
                                 continue;
                              }

                              newLink.calculateDimensionId();
                           }

                           try {
                              if (!"null".equals(sourceLink.getMasterBranchId() + "") && FormatHelper.hasContent("" + sourceLink.getMasterBranchId())) {
                                 String stMasterBranchId = (String)branchIdMap.get("" + sourceLink.getMasterBranchId());
                                 if (stMasterBranchId != null && !"null".equals(stMasterBranchId)) {
                                    newLink.setMasterBranchId(Integer.parseInt(stMasterBranchId));
                                 }
                              }

                              branchIdMap.put("" + sourceLink.getBranchId(), "" + newLink.getBranchId());
                           } catch (NumberFormatException var36) {
                              System.out.println("Error setting sourceLink's MasterBranchId " + sourceLink.getMasterBranchId() + " --\n" + var36.getLocalizedMessage());
                           }
                           break;
                        } catch (WTPropertyVetoException var39) {
                           var39.printStackTrace();
                           throw new WTException(var39.getLocalizedMessage());
                        }
                     }

                     deriveFlexTypeValues(newLink, true);
                     persist(newLink);
                  }
               }

               if (isCheckedOut && !isCopyInProgress) {
                  destinationBOMPart = this.checkInBOM(destinationBOMPart);
               }

               return destinationBOMPart;
            }
         }
      }
   }

   public void effectOutLinks(Collection links) throws WTException {
      if (links != null && !links.isEmpty()) {
         try {
            Iterator i = links.iterator();
            FlexBOMLink link = null;
            FlexBOMLink newLink = null;
            Date now = new Date();

            while(i.hasNext()) {
               link = (FlexBOMLink)i.next();
               link.setWip(false);
               link.setOutDate(new Timestamp(now.getTime()));
               newLink = FlexBOMLink.newFlexBOMLink();
               link.copyState(newLink);
               newLink.setSequence(newLink.getSequence() + 1);
               newLink.setOutDate((Timestamp)null);
               newLink.setInDate(link.getOutDate());
               newLink.setDropped(true);
               persist(link);
               persist(newLink);
            }

         } catch (WTException var6) {
            throw var6;
         } catch (Exception var7) {
            throw new WTException(var7);
         }
      }
   }

   public FlexBOMPart copyBOMForSameOwner(FlexBOMPart source, FlexBOMPart destination) throws WTException {
      LOGGER.debug(".copyBOMForSameOwner: start");
      FlexBOMPart sourceBOMPart = null;
      FlexBOMPart destinationBOMPart = null;
      if (source instanceof FlexBOMPart) {
         sourceBOMPart = source;
      }

      if (destination instanceof FlexBOMPart) {
         destinationBOMPart = destination;
      }

      boolean isCheckedOut = VersionHelper.isCheckedOut(destinationBOMPart);
      boolean isCopyInProgress = "true".equals(MethodContext.getContext().get("COPY_IN_PROGRESS")) || "true".equals(MethodContext.getContext().get("ASSOCIATE_IN_PROGRESS"));
      if (!isCheckedOut && !isCopyInProgress) {
         destinationBOMPart = (FlexBOMPart)VersionHelper.checkout(destinationBOMPart);
      }

      Collection currentSourceBOM = LCSFlexBOMQuery.findFlexBOMLinks(sourceBOMPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, "ALL_DIMENSIONS", (String)null, (String)null, (String)null);
      Collection currentDestBOM = LCSFlexBOMQuery.findFlexBOMLinks(destinationBOMPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, "ALL_DIMENSIONS", (String)null, (String)null, (String)null);
      if (currentSourceBOM != null && currentSourceBOM.size() != 0) {
         Iterator newLinkLoop = currentSourceBOM.iterator();
         FlexBOMLink sourceLink = null;
         FlexBOMLink newLink = null;
         Date now = new Date();

         while(newLinkLoop.hasNext()) {
            sourceLink = (FlexBOMLink)newLinkLoop.next();
            newLink = FlexBOMLink.newFlexBOMLink();

            try {
               if (LOGGER.isDebugEnabled()) {
                  LOGGER.debug("About to copy link: " + sourceLink.getBranchId() + " part = " + sourceLink.getValue("partName") + " dimensionName = " + sourceLink.getDimensionName() + " dimensionId = " + sourceLink.getDimensionId());
               }

               sourceLink.copyState(newLink);
               LOGGER.debug("COPY BOM for same owner -  section");
               PropertyBasedAttributeValueLogic.setAttributes(newLink, "com.lcs.wc.flexbom.FlexBOMLink", "", "COPY");
               newLink.setParentReference(destinationBOMPart.getMasterReference());
               newLink.setParentRev(destinationBOMPart.getVersionIdentifier().getValue());
               newLink.setInDate(new Timestamp(now.getTime()));
               newLink.setOutDate((Timestamp)null);
               newLink.setWip(false);
               newLink.setSequence(0);
               newLink.calculateDimensionId();
            } catch (WTPropertyVetoException var15) {
               var15.printStackTrace();
               throw new WTException(var15.getLocalizedMessage());
            }

            deriveFlexTypeValues(newLink, true);
            persist(newLink);
         }

         if (isCheckedOut && !isCopyInProgress) {
            destinationBOMPart = this.checkInBOM(destinationBOMPart);
         }

         return destinationBOMPart;
      } else {
         return destinationBOMPart;
      }
   }

   public int getMaxBranchId(FlexBOMPart part) throws WTException {
      boolean var2 = false;

      try {
         Query query = new Query();
         query.prepareForQuery();
         ResultSet results = query.runQuery("SELECT MAX(branchId) FROM FlexBOMLink WHERE idA3A5 = " + FormatHelper.getNumericFromReference(part.getMasterReference()));
         results.next();
         int max = results.getInt(1);
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("max = " + max);
         }

         results.close();
         query.cleanUpQuery();
         return max;
      } catch (Exception var5) {
         throw new WTException(var5);
      }
   }

   public int getMaxSortingNumber(FlexBOMPart bomPart, FlexBOMLink bomLink) throws WTException {
      boolean var3 = false;

      try {
         Query query = new Query();
         query.prepareForQuery();
         String sectionName = bomLink.getFlexType().getAttribute("section").getColumnName();
         String sql = "SELECT MAX(sortingNumber) FROM FlexBOMLink WHERE idA3A5 = " + FormatHelper.getNumericFromReference(bomPart.getMasterReference()) + " and " + sectionName + " = '" + (String)bomLink.getValue("section") + "' and dropped <> 1";
         ResultSet results = query.runQuery(sql);
         results.next();
         int max = results.getInt(1);
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("max = " + max);
         }

         results.close();
         query.cleanUpQuery();
         return max;
      } catch (Exception var8) {
         throw new WTException(var8);
      }
   }

   /** @deprecated */
   @Deprecated
   public int getMaxBranchId(Collection links) {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("getMaxBranchId: OLD METHOD!!!!" + FormatHelper.getInvocationPath(5));
      }

      int maxBranchId = 0;
      FlexBOMLink branch = null;
      Iterator loop = links.iterator();

      while(loop.hasNext()) {
         branch = (FlexBOMLink)loop.next();
         if (branch.getBranchId() > maxBranchId) {
            maxBranchId = branch.getBranchId();
         }
      }

      return maxBranchId;
   }

   public Map groupBySection(Collection links) throws WTException {
      Map map = new HashMap();
      Iterator loop = links.iterator();
      FlexBOMLink link = null;

      while(loop.hasNext()) {
         link = (FlexBOMLink)loop.next();
         String section = FormatHelper.format("" + link.getValue("section"));
         Collection collection = (Collection)map.get(section);
         if (collection == null) {
            collection = new ArrayList();
         }

         ((Collection)collection).add(link);
         map.put(section, collection);
      }

      return map;
   }

   public Map groupByDimensionName(Collection links) throws WTException {
      Map map = new HashMap();
      Iterator loop = links.iterator();
      FlexBOMLink link = null;

      while(loop.hasNext()) {
         link = (FlexBOMLink)loop.next();
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Creating Dimension Map: branch = " + link.getBranchId() + " partName = " + link.getValue("partName") + " dimensionName = " + link.getDimensionName());
         }

         String dimension = FormatHelper.format(link.getDimensionName());
         Collection collection = (Collection)map.get(dimension);
         if (collection == null) {
            collection = new ArrayList();
         }

         ((Collection)collection).add(link);
         map.put(dimension, collection);
      }

      return map;
   }

   public void deleteLinks(Collection links) throws WTException {
      if (links != null) {
         FlexBOMLink link = null;
         Iterator loop = links.iterator();

         while(loop.hasNext()) {
            link = (FlexBOMLink)loop.next();
            remove(link);
         }

      }
   }

   public void deleteFlexBOMPart(FlexBOMPart bomPart) throws WTException {
      Collection v = VersionHelper.allVersionsOf(bomPart.getMaster());
      Iterator i = v.iterator();

      while(i.hasNext()) {
         this.deleteBOMVersion((FlexBOMPart)i.next());
      }

   }

   public void deleteBOMVersion(FlexBOMPart bomPart) throws WTException {
      if (!"A".equals(bomPart.getVersionIdentifier().getValue()) && VersionHelper.isCheckedOut(bomPart)) {
         bomPart = (FlexBOMPart)VersionHelper.checkin(bomPart);
      }

      Collection links = FlexSpecQuery.getSpecToComponentLinks(bomPart, true);
      Iterator i = links.iterator();
      FlexSpecToComponentLink link = null;
      FlexSpecLogic fslogic = new FlexSpecLogic();

      while(i.hasNext()) {
         link = (FlexSpecToComponentLink)i.next();
         FlexSpecMaster linkSpecMaster = link.getSpecificationMaster();
         FlexSpecification linkSpec = (FlexSpecification)VersionHelper.latestIterationOf(linkSpecMaster);
         boolean isCurrentOwner = linkSpec.getSpecOwner().equals(bomPart.getOwnerMaster());
         if (!isCurrentOwner) {
            Object[] obj = new Object[]{bomPart.getName()};
            throw new LCSException("com.lcs.wc.resource.ExceptionRB", "cannostDeleteLinkedBOM_MSG", obj);
         }

         fslogic.delete(link);
      }

      Collection currentBOM = LCSFlexBOMQuery.getAllFlexBOMLinks(bomPart, (WTObject)null, (WTObject)null, (WTObject)null, (String)null, (String)null);
      this.deleteLinks(currentBOM);
      deleteObject(bomPart);
   }

   public void addBranchToBOM(FlexBOMPart bomPart, FlexBOMLink link) throws WTException {
      Collection currentBOM = LCSFlexBOMQuery.findFlexBOMLinks(bomPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, (String)null, (String)null, (String)null, (String)null);
      this.addBranchToBOM(bomPart, link, currentBOM);
   }

   public void addBranchToBOM(FlexBOMPart bomPart, FlexBOMLink link, Collection currentBOM) throws WTException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".addBranchToBOM: bomPart = " + bomPart + " link = " + link);
      }

      int maxBranchId = this.getMaxBranchId(bomPart) + 1;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".addBranchToBOM: bomPart = [" + bomPart.getName() + "] new branchId = " + maxBranchId);
      }

      try {
         link.setBranchId(maxBranchId);
         link.setFlexType(bomPart.getFlexType());
         if (link.getChildReference() == null) {
            link.setChild(LCSMaterialQuery.PLACEHOLDER);
         }

         if (link.getSupplierReference() == null) {
            link.setSupplier(LCSSupplierQuery.PLACEHOLDER);
         }

         link.setParentRev(bomPart.getVersionIdentifier().getValue());
         link.setParentReference(bomPart.getMasterReference());
         link.setInDate(new Timestamp((new Date()).getTime()));
         link.setOutDate((Timestamp)null);
         link.setSequence(0);
         int sortingNumber = this.getMaxSortingNumber(bomPart, link) + 1;
         link.setSortingNumber(sortingNumber);
         link.calculateDimensionId();
      } catch (WTPropertyVetoException var14) {
         var14.printStackTrace();
         throw new WTException(var14);
      }

      deriveFlexTypeValues(link, true);
      persist(link);
      BOMOwner ownerMaster = bomPart.getOwnerMaster();
      boolean productBOM = false;
      if (ownerMaster instanceof LCSPartMaster) {
         productBOM = true;
      } else {
         productBOM = false;
      }

      LCSMaterialSupplier primaryMaterialSupplier = null;
      if (bomPart.getFlexType().attributeExist("primaryMaterial")) {
         primaryMaterialSupplier = (LCSMaterialSupplier)bomPart.getValue("primaryMaterial");
      }

      String textPrimaryMaterial = null;
      if (bomPart.getFlexType().attributeExist("pmDescription")) {
         textPrimaryMaterial = (String)bomPart.getValue("pmDescription");
      }

      String materialMasterId = "";
      String supplierMasterId = "";
      boolean isLibPrimary = false;
      boolean isTextPrimary = false;
      if (primaryMaterialSupplier != null) {
         materialMasterId = FormatHelper.getNumericObjectIdFromObject(primaryMaterialSupplier.getMaterialMaster());
         supplierMasterId = FormatHelper.getNumericObjectIdFromObject(primaryMaterialSupplier.getSupplierMaster());
         if (materialMasterId.equals(FormatHelper.getNumericObjectIdFromObject(link.getChild())) && supplierMasterId.equals(FormatHelper.getNumericObjectIdFromObject(link.getSupplier()))) {
            isLibPrimary = true;
         }
      }

      isTextPrimary = FormatHelper.hasContent(textPrimaryMaterial) && textPrimaryMaterial.equals(link.getValue("materialDescription"));
      FlexBOMPart oldBomPart = (FlexBOMPart)LCSQuery.findObjectById(FormatHelper.getObjectId(bomPart));
      if (productBOM && FormatHelper.hasContent(PRIMARY_MATERIAL_GROUP) && bomPart.getFlexType().getAttributeGroup(PRIMARY_MATERIAL_GROUP, "BOM_SCOPE", (String)null).size() > 0 && (isLibPrimary || isTextPrimary)) {
         LCSPluginManager.handleEvent(bomPart, "PRIMARY_MATERIAL_ROLL_UP");
         if (USE_PRIMARY_SPEC && USE_PRIMARY_BOM && this.checkChange(oldBomPart, bomPart)) {
            this.syncPrimaryMaterialAtts(bomPart);
         }
      }

   }

   public void addBranchToBOMAsWIP(FlexBOMPart bomPart, FlexBOMLink link) throws WTException {
      Collection currentBOM = LCSFlexBOMQuery.findFlexBOMLinks(bomPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, (String)null, (String)null, (String)null, (String)null);
      this.addBranchToBOM(bomPart, link, currentBOM);
   }

   public void addBranchToBOMAsWIP(FlexBOMPart bomPart, FlexBOMLink link, Collection currentBOM) throws WTException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".addBranchToBOM: bomPart = " + bomPart + " link = " + link);
      }

      int maxBranchId = this.getMaxBranchId(bomPart) + 1;
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".addBranchToBOM: bomPart = [" + bomPart.getName() + "] new branchId = " + maxBranchId);
      }

      try {
         link.setBranchId(maxBranchId);
         link.setFlexType(bomPart.getFlexType());
         if (link.getChildReference() == null) {
            link.setChild(LCSMaterialQuery.PLACEHOLDER);
         }

         if (link.getSupplierReference() == null) {
            link.setSupplier(LCSSupplierQuery.PLACEHOLDER);
         }

         link.setParentRev(bomPart.getVersionIdentifier().getValue());
         link.setParentReference(bomPart.getMasterReference());
         link.setInDate((Timestamp)null);
         link.setOutDate((Timestamp)null);
         link.setSequence(-1);
         link.setDropped(false);
         link.calculateDimensionId();
         link.setWip(true);
      } catch (WTPropertyVetoException var6) {
         var6.printStackTrace();
         throw new WTException(var6);
      }

      FlexObject changeInfo = new FlexObject();
      deriveFlexTypeValues(link, true);
      this.loadAndSaveChanges(link, changeInfo, false);
   }

   public void dropBranchFromBOM(FlexBOMPart bomPart, int branchId) throws WTException {
      Collection currentBOM = LCSFlexBOMQuery.findFlexBOMLinks(bomPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, "ALL_DIMENSIONS", (String)null, (String)null, (String)null);
      this.dropBranchFromBOM(bomPart, branchId, currentBOM);
   }

   protected void dropBranchFromBOM(FlexBOMPart bomPart, int branchId, Collection currentBOM) throws WTException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(".dropBranchFromBOM: bomPart [" + bomPart.getName() + "] branch [" + branchId + "]");
      }

      if (VersionHelper.isCheckedOut(bomPart) && !VersionHelper.isCheckedOutByUser(bomPart)) {
         throw new WTException("BOM [" + bomPart.getName() + "] can not be modified because it is checked out by another user");
      } else {
         Iterator linkIter = currentBOM.iterator();

         while(linkIter.hasNext()) {
            FlexBOMLink link = (FlexBOMLink)linkIter.next();
            if (link.getBranchId() == branchId) {
               try {
                  if (link.isWip()) {
                     link.setDropped(true);
                  } else {
                     Timestamp now = new Timestamp((new Date()).getTime());
                     link.setOutDate(now);
                     FlexBOMLink newBranch = FlexBOMLink.newFlexBOMLink();
                     newBranch = (FlexBOMLink)link.copyState(newBranch);
                     newBranch.setFlexType(bomPart.getFlexType());
                     newBranch.setInDate(now);
                     newBranch.setOutDate((Timestamp)null);
                     newBranch.setSequence(link.getSequence() + 1);
                     newBranch.setParent(bomPart.getMaster());
                     newBranch.setParentRev(bomPart.getVersionIdentifier().getValue());
                     newBranch.setDropped(true);
                     persist(newBranch);
                  }
               } catch (WTPropertyVetoException var8) {
                  throw new WTException(var8);
               }

               persist(link);
            }
         }

      }
   }

   private static final boolean isUSE_STANDARD_BOM_QUERY_IN_ROLL_UP() {
      return USE_STANDARD_BOM_QUERY_IN_ROLL_UP;
   }

   public void rollUpBOM(FlexBOMPart bomPart) throws WTException {
      Collection currentBOM = null;
      if (isUSE_STANDARD_BOM_QUERY_IN_ROLL_UP() && LCSPluginManager.hasPluginMethods(bomPart, "BOM_ROLL_UP")) {
         currentBOM = LCSFlexBOMQuery.findFlexBOMData(bomPart, (String)null, (String)null, (String)null, (String)null, (String)null, "WIP_ONLY", (Date)null, false, false, "ALL_DIMENSIONS", (String)null, (String)null, (String)null).getResults();
      }

      this.rollUpBOM(bomPart, currentBOM);
   }

   public void rollUpBOM(FlexBOMPart bomPart, Collection currentBOM) throws WTException {
      if (currentBOM != null) {
         MethodContext.getContext().put("CURRENT_BOM", currentBOM);
      }

      LCSPluginManager.handleEvent(bomPart, "BOM_ROLL_UP");
   }

   public FlexBOMPart associateToProduct(FlexBOMPart bomPart, String productId) throws WTException {
      LCSProduct product = (LCSProduct)LCSQuery.findObjectById(productId);
      return this.associateToProduct(bomPart, product, false);
   }

   public FlexBOMPart associateToProduct(FlexBOMPart bomPart, LCSProduct destination, boolean skuCopy) throws WTException {
      if (bomPart == null) {
         return null;
      } else {
         Map skuMap = new HashMap();
         if (skuCopy) {
            Collection destinationSKUs = LCSSKUQuery.findAllSKUs(destination, false);
            Iterator skuIter = destinationSKUs.iterator();
            LCSSKU sku = null;

            while(skuIter.hasNext()) {
               sku = (LCSSKU)skuIter.next();
               if (sku.getCopiedFrom() != null) {
                  skuMap.put(FormatHelper.getNumericFromReference(sku.getCopiedFrom().getMasterReference()), sku.getMaster());
               }
            }

            if (LOGGER.isDebugEnabled()) {
               LOGGER.debug(".copyProductBOM: skuMap = " + skuMap);
            }
         }

         return this.associateToProduct(bomPart, destination, true, skuMap);
      }
   }

   public FlexBOMPart associateToProduct(FlexBOMPart bomPart, LCSProduct destination, boolean copyOverRides, Map overRideMap) throws WTException {
      return this.associateToProduct(bomPart, destination, copyOverRides, overRideMap, false);
   }

   public FlexBOMPart associateToProduct(FlexBOMPart bomPart, LCSProduct destination, boolean copyOverRides, Map overRideMap, boolean applyAttributeRules) throws WTException {
      return this.associateToProduct(bomPart, destination, copyOverRides, overRideMap, applyAttributeRules, (String)MethodContext.getContext().get("copyMode"));
   }

   public FlexBOMPart associateToProduct(FlexBOMPart bomPart, LCSProduct destination, boolean copyOverRides, Map overRideMap, boolean applyAttributeRules, String copyMode) throws WTException {
      if (bomPart == null) {
         return null;
      } else {
         MethodContext.getContext().put("ASSOCIATE_IN_PROGRESS", "true");
         if (!FormatHelper.hasContent(copyMode) && "true".equals(MethodContext.getContext().get("COPY_IN_PROGRESS"))) {
            copyMode = "COPY";
         }

         FlexBOMPart destinationBOMPart = (new LCSFlexBOMLogic()).initiateBOMPart(destination, bomPart.getFlexType(), bomPart.getBomType(), (Collection)null, bomPart, copyMode, applyAttributeRules);
         if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(".copyProductBOM: overRideMap = " + overRideMap);
         }

         return this.copyBOM(bomPart, destinationBOMPart, (String)null, "REPLACE_COPY_MODE", copyOverRides, overRideMap);
      }
   }

   private FlexBOMPart createBOMPart(FlexBOMPart bomPart) throws WTException {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(CLASSNAME + ".create: start");
      }

      String name = "" + bomPart.getValue("name");
      if (name.length() > 60) {
         name = name.substring(0, 60);
      }

      assignFolder(BOMPART_FOLDERLOCATION, bomPart);
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("pre persist: bomPart.getOwnerMaster() = " + bomPart.getOwnerMaster());
      }

      try {
         resetBOMPartNameNumber(bomPart);
      } catch (WTPropertyVetoException var4) {
         throw new WTException(var4, "LOGIC ERROR in create while setting part number: " + var4.getLocalizedMessage());
      }

      bomPart = (FlexBOMPart)persist(bomPart);
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug("post persist: bomPart.getOwnerMaster() = " + bomPart.getOwnerMaster());
      }

      return bomPart;
   }

   public static void resetBOMPartNameNumber(FlexBOMPart bomPart) throws WTException, WTPropertyVetoException {
      try {
         String name = (String)bomPart.getValue("name");
         String number = (String)bomPart.getValue(LCSFlexBOMQuery.BOM_NUM_KEY);
         if (FormatHelper.hasContent(number)) {
            if (name.startsWith(number)) {
               int index = name.indexOf(SourceComponentNumberPlugin.NUM_NAME_DELIM) + SourceComponentNumberPlugin.NUM_NAME_DELIM.length();
               name = name.substring(index);
               bomPart.setValue("name", name);
            }

            bomPart.setValue(LCSFlexBOMQuery.BOM_NUM_KEY, "");
         }

      } catch (WTException var4) {
         throw new WTException(var4);
      }
   }

   private Collection obtainAllOverRideRows(Map dimensionMap) {
      Collection allOverRidesCollection = new Vector();
      Set keySet = dimensionMap.keySet();
      Iterator keySetItr = keySet.iterator();

      while(keySetItr.hasNext()) {
         Object key = keySetItr.next();
         if (!"".equals(key)) {
            allOverRidesCollection.addAll((Collection)dimensionMap.get(key));
         }
      }

      return allOverRidesCollection;
   }

   private Collection obtainSourceOverRideRows(Map dimensionMap) {
      Collection sourceCollection = new Vector();
      if (dimensionMap.get(":SOURCE") != null) {
         sourceCollection.addAll((Collection)dimensionMap.get(":SOURCE"));
      }

      if (dimensionMap.get(":SOURCE:SKU") != null) {
         sourceCollection.addAll((Collection)dimensionMap.get(":SOURCE:SKU"));
      }

      if (dimensionMap.get(":SOURCE:SIZE1") != null) {
         sourceCollection.addAll((Collection)dimensionMap.get(":SOURCE:SIZE1"));
      }

      if (dimensionMap.get(":SOURCE:SIZE2") != null) {
         sourceCollection.addAll((Collection)dimensionMap.get(":SOURCE:SIZE2"));
      }

      if (dimensionMap.get(":SOURCE:SIZE1:SIZE2") != null) {
         sourceCollection.addAll((Collection)dimensionMap.get(":SOURCE:SIZE1:SIZE2"));
      }

      if (dimensionMap.get(":SOURCE:SKU:SIZE1") != null) {
         sourceCollection.addAll((Collection)dimensionMap.get(":SOURCE:SKU:SIZE1"));
      }

      if (dimensionMap.get(":SOURCE:SIZE1:SIZE2") != null) {
         sourceCollection.addAll((Collection)dimensionMap.get(":SOURCE:SIZE1:SIZE2"));
      }

      if (dimensionMap.get(":SOURCE:DESTINATIION") != null) {
         sourceCollection.addAll((Collection)dimensionMap.get(":SOURCE:DESTINATION"));
      }

      return sourceCollection;
   }

   Collection obtainSKUOverRideRows(Map dimensionMap) {
      Collection skuCollection = new Vector();
      if (dimensionMap.get(":SKU") != null) {
         skuCollection.addAll((Collection)dimensionMap.get(":SKU"));
      }

      if (dimensionMap.get(":SKU:SIZE1") != null) {
         skuCollection.addAll((Collection)dimensionMap.get(":SKU:SIZE1"));
      }

      if (dimensionMap.get(":SOURCE:SKU:SIZE1") != null) {
         skuCollection.addAll((Collection)dimensionMap.get(":SOURCE:SKU:SIZE1"));
      }

      if (dimensionMap.get(":SKU:DESTINATION") != null) {
         skuCollection.addAll((Collection)dimensionMap.get(":SKU:DESTINATION"));
      }

      return skuCollection;
   }

   private Collection obtainDestinationOverRideRows(Map dimensionMap) {
      Collection destinationCollection = new Vector();
      if (dimensionMap.get(":DESTINATION") != null) {
         destinationCollection.addAll((Collection)dimensionMap.get(":DESTINATION"));
      }

      if (dimensionMap.get(":SOURCE:DESTINATION") != null) {
         destinationCollection.addAll((Collection)dimensionMap.get(":SOURCE:DESTINATION"));
      }

      if (dimensionMap.get(":SKU:DESTINATION") != null) {
         destinationCollection.addAll((Collection)dimensionMap.get(":SKU:DESTINATION"));
      }

      return destinationCollection;
   }

   private Collection obtainSizeOverRideRows(Map dimensionMap) {
      Collection sizeCollection = new Vector();
      if (dimensionMap.get(":SIZE1") != null) {
         sizeCollection.addAll((Collection)dimensionMap.get(":SIZE1"));
      }

      if (dimensionMap.get(":SIZE2") != null) {
         sizeCollection.addAll((Collection)dimensionMap.get(":SIZE2"));
      }

      if (dimensionMap.get(":SIZE1:SIZE2") != null) {
         sizeCollection.addAll((Collection)dimensionMap.get(":SIZE1:SIZE2"));
      }

      if (dimensionMap.get(":SOURCE:SIZE1") != null) {
         sizeCollection.addAll((Collection)dimensionMap.get(":SOURCE:SIZE1"));
      }

      if (dimensionMap.get(":SOURCE:SIZE2") != null) {
         sizeCollection.addAll((Collection)dimensionMap.get(":SOURCE:SIZE2"));
      }

      if (dimensionMap.get(":SOURCE:SIZE1:SIZE2") != null) {
         sizeCollection.addAll((Collection)dimensionMap.get(":SOURCE:SIZE1:SIZE2"));
      }

      return sizeCollection;
   }

   public static void resetBOMNameNumber(FlexBOMPart part) throws WTException, WTPropertyVetoException {
      try {
         String name = (String)part.getValue("name");
         String number = (String)part.getValue(LCSFlexBOMQuery.BOM_NUM_KEY);
         if (FormatHelper.hasContent(number)) {
            if (name.startsWith(number)) {
               int index = name.indexOf(SourceComponentNumberPlugin.NUM_NAME_DELIM) + SourceComponentNumberPlugin.NUM_NAME_DELIM.length();
               name = name.substring(index);
               part.setValue("name", name);
            }

            part.setValue(LCSFlexBOMQuery.BOM_NUM_KEY, "");
         }

      } catch (WTException var4) {
         throw new WTException(var4);
      }
   }

   /** @deprecated */
   @Deprecated
   public void debug(String msg) {
      if (LOGGER.isDebugEnabled()) {
         LOGGER.debug(CLASSNAME + " " + msg);
      }

   }

   static {
      try {
         BOMPART_FOLDERLOCATION = LCSProperties.get("com.lcs.wc.flexbom.FlexBOMPart.rootFolder", "/FlexBOMPart");
         HIDDEN_SYNC_ATTS = LCSProperties.get("com.lcs.wc.flexbom.FlexBOMLogic.hiddenSynchedAttributes", "primaryMaterial");
         BOMPART_SEQUENCE_CLASS = Class.forName(LCSProperties.get("com.lcs.wc.flexbom.FlexBOMPart.sequenceName", "com.lcs.wc.flexbom.FlexBOMPartNumberSeq"));
         USE_STANDARD_BOM_QUERY_IN_ROLL_UP = LCSProperties.getBoolean("com.lcs.wc.flexbom.useStandardBomQueryInRollUp");
         PRIMARY_MATERIAL_GROUP = LCSProperties.get("com.lcs.wc.flexbom.PrimaryMaterialGroup", "Primary Material");
      } catch (Throwable var1) {
         System.err.println("LCSFlexBOMLogic: Error reading com.lcs.wc.flexbom.* properties");
         var1.printStackTrace(System.err);
         throw new ExceptionInInitializerError(var1);
      }
   }
}
    Download file

