package com.sportmaster.wc.mc.sourcing;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.part.LCSPartMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSSKU;
import com.lcs.wc.product.ProductDestination;
import com.lcs.wc.sourcing.LCSCostSheetMaster;
import com.lcs.wc.sourcing.LCSCostSheetQuery;
import com.lcs.wc.sourcing.LCSProductCostSheet;
import com.lcs.wc.specification.FlexSpecQuery;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierMaster;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.VersionHelper;
import com.sportmaster.wc.mc.tools.SMFormatHelper;
import org.apache.log4j.Logger;
import wt.fc.ReferenceFactory;
import wt.method.MethodContext;
import wt.part.WTPartMaster;
import wt.util.WTException;
import com.sportmaster.wc.mc.config.SMCostSheetConfig;

import java.util.*;

public class SMCostSheetReader {

    private static final Logger LOGGER = Logger.getLogger(SMCostSheetReader.class);

    public static boolean isBikesProduct_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        LCSProduct lcsProduct = getLCSProduct(lcsProductCostSheet);
        if (lcsProduct != null) {
            FlexType currentType = FlexTypeCache.getFlexType(lcsProduct);
            FlexType type = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.PRODUCT_TYPE_BIKES_SEPD);
            if (type.equals(currentType))
                return true;
        }
        return false;
    }

    public static LCSProduct getLCSProduct(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        LCSPartMaster lcsPartMaster = lcsProductCostSheet.getProductMaster();
        if (lcsPartMaster != null) {
            return VersionHelper.latestIterationOf(lcsPartMaster);
        }
        return null;
    }

    public static boolean isBikesFlexBOMPart_SEPD(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        FlexBOMPart flexBOMPart = getFlexBOMPart(lcsProductCostSheet);
        if (flexBOMPart != null) {
            FlexType currentType = FlexTypeCache.getFlexType(flexBOMPart);
            FlexType type = FlexTypeCache.getFlexTypeFromPath(SMCostSheetConfig.FLEX_BOM_PART_TYPE_BIKES_SEPD);
            if (type.equals(currentType))
                return true;
        }
        return false;
    }

    public static FlexBOMPart getFlexBOMPart(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        FlexSpecification flexSpecification = getFlexSpecification(lcsProductCostSheet);
        if(flexSpecification != null) {
            Collection specBOMs = FlexSpecQuery.getSpecComponents(flexSpecification, "BOM");
            String refBOMName = FormatHelper.format((String) lcsProductCostSheet.getValue(com.sportmaster.wc.mc.sourcing.SMCostSheetConfig.BOM_REF_NAME_ATT));

            for (Object object : specBOMs) {
                FlexBOMPart flexBOMPart = (FlexBOMPart) object;
                if(!"LABOR".equals(flexBOMPart.getBomType()) && refBOMName.split(":")[0].equals(flexBOMPart.getName().split(":")[0]))
                {
                    return flexBOMPart;
                }
            }
        }
        return null;
    }

    public static FlexSpecification getFlexSpecification(LCSProductCostSheet lcsProductCostSheet) throws WTException {

        if(lcsProductCostSheet.getSpecificationMaster() != null)
            return VersionHelper.latestIterationOf(lcsProductCostSheet.getSpecificationMaster());
        return null;
    }

    public static boolean isExcludeRefreshExchangeRatesByStates(LCSProductCostSheet lcsCostSheet) throws WTException {
        String value = (String) lcsCostSheet.getValue(SMCostSheetTypeSelector.getSMCostSheetStatus(lcsCostSheet));
        if (value != null) {
            List<String> smCostSheetStatusFPD_ValuesForExclude = SMCostSheetConfig.getValues(SMCostSheetConfig.SM_COST_SHEET_REFRESH_EXCHANGE_RATES_EXCLUDE_STATES);
            for (String state : smCostSheetStatusFPD_ValuesForExclude) {
                if (state.equalsIgnoreCase(value)) {
                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetReader.isExcludeRefreshExchangeRatesByStates: true, state: " + state);
                    return true;
                }

            }
        }
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetReader.isExcludeRefreshExchangeRatesByStates: false.");
        return false;
    }

    /**
     * Method defines library material or text is specified in bom link (FlexObject)
     * @param bomObject - FlexObject for BOMLink
     * @param bomType - FlexBOMLink type
     * @return
     * @throws WTException
     */
    public static boolean isTextMaterial(FlexObject bomObject, FlexType bomType) throws WTException {
        String materialID = FormatHelper.format(bomObject.getData("LCSMATERIAL.IDA3MASTERREFERENCE"));
        String materialDescription = FormatHelper.format(bomObject.getData(bomType.getAttribute("materialDescription").getSearchResultIndex()));
        if (!materialID.isEmpty() && !materialDescription.isEmpty()) {
            LCSMaterialMaster lcsMaterialMaster = (LCSMaterialMaster) new wt.fc.ReferenceFactory().getReference(
                    "OR:com.lcs.wc.material.LCSMaterialMaster:" + materialID).getObject();
            if (lcsMaterialMaster != null) {
                LCSMaterial lcsMaterial = VersionHelper.latestIterationOf(lcsMaterialMaster);
                //32 -> " "
                //160 -> &nbsp;
                //String materialName = lcsMaterial.getName().replace(String.valueOf((char)32),"").replace(String.valueOf((char)160),"");
                //materialDescription.replace(String.valueOf((char)32),"").replace(String.valueOf((char)160),"")
                String materialNumber1 = materialDescription.trim().split(" ")[0];
                String materialNumber2 = lcsMaterial.getName().trim().split(" ")[0];
                if (!materialNumber1.equalsIgnoreCase(materialNumber2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *
     * @param bomObject
     * @param bomType
     * @return
     * @throws WTException
     */
    public static Object[] getMaterialNameAndBranchId(FlexObject bomObject, FlexType bomType) throws WTException {
        if (!isTextMaterial(bomObject, bomType)) {
            String materialMasterRefID = FormatHelper.format(bomObject.getData("LCSMATERIAL.IDA3MASTERREFERENCE"));
            if (FormatHelper.hasContent(materialMasterRefID)) {
                LCSMaterialMaster lcsMaterialMaster = (LCSMaterialMaster) new wt.fc.ReferenceFactory().getReference(
                        "OR:com.lcs.wc.material.LCSMaterialMaster:" + materialMasterRefID).getObject();
                if (lcsMaterialMaster != null) {
                    LCSMaterial lcsMaterial = VersionHelper.latestIterationOf(lcsMaterialMaster);
                    if (lcsMaterial != null) {
                        String materialName = lcsMaterial.getName();
                        boolean isPlaceholder = SMFormatHelper.isPlaceholder(materialName);
                        return new Object[] {
                                FormatHelper.format(materialName),
                                isPlaceholder ? null : lcsMaterial.getBranchIdentifier()
                        };
                    }
                }
            }
        }

        return new Object[] {
                FormatHelper.format(bomObject.getData(bomType.getAttribute("materialDescription").getSearchResultIndex())),
                null // BranchIdentifier
        };
    }

    public static Object[] getSupplierNameAndBranchId(FlexObject bomObj) throws WTException {
        String supplierMasterID = FormatHelper.format(bomObj.getData("LCSSUPPLIERMASTER.IDA2A2"));
        if (!supplierMasterID.isEmpty()) {
            LCSSupplierMaster lcsSupplierMaster = (LCSSupplierMaster) new wt.fc.ReferenceFactory().getReference(
                    "OR:com.lcs.wc.supplier.LCSSupplierMaster:" + supplierMasterID).getObject();
            if (lcsSupplierMaster != null) {
                LCSSupplier lcsSupplier = VersionHelper.latestIterationOf(lcsSupplierMaster);
                if(lcsSupplier != null) {
                    String supplierName = lcsSupplier.getName();
                    boolean isPlaceholder = SMFormatHelper.isPlaceholder(supplierName);
                    return new Object[] {
                            FormatHelper.format(supplierName),
                            isPlaceholder ? null : lcsSupplier.getBranchIdentifier()
                    };
                }
            }
        }
        return new Object[] { "", null };
    }

    public static Long getColorBranchId(FlexObject bomObj) throws WTException {
        String colorID = FormatHelper.format(bomObj.getData("LCSCOLOR.IDA2A2"));
        if (!colorID.isEmpty()) {
            LCSColor lcsColor = (LCSColor) new ReferenceFactory().getReference(
                    "OR:com.lcs.wc.color.LCSColor:" + colorID).getObject();
            if (lcsColor != null) {
                return lcsColor.getPersistInfo().getObjectIdentifier().getId();
            }
        }
        return null;
    }

    protected static Collection getMOATableRows(LCSProductCostSheet lcsProductCostSheet, String moaTableType, String costSheetMOATableAttName, List<String> attributes) throws WTException {
        return getMOATableRows(lcsProductCostSheet, FlexTypeCache.getFlexTypeFromPath(moaTableType),
                costSheetMOATableAttName, attributes.toArray(new String[attributes.size()]));
    }

    protected static Collection getMOATableRows(LCSProductCostSheet lcsProductCostSheet, FlexType moaTableType, String costSheetMOATableAttName, List<String> attributes) throws WTException {
        return getMOATableRows(lcsProductCostSheet, moaTableType,
                costSheetMOATableAttName, attributes.toArray(new String[attributes.size()]));
    }

    protected static Collection getMOATableRows(LCSProductCostSheet lcsProductCostSheet, String moaTableType, String costSheetMOATableAttName, String ... attributes) throws WTException {
        return getMOATableRows(lcsProductCostSheet, FlexTypeCache.getFlexTypeFromPath(moaTableType),
                costSheetMOATableAttName, attributes);
    }

    protected static Collection getMOATableRows(LCSProductCostSheet lcsProductCostSheet, FlexType moaTableType, String costSheetMOATableAttName, String ... attributes) throws WTException {
        LOGGER.debug("CUSTOM>>>>>> SMCostSheetReader.getMOATableRows: Start.");

        Map<String,String> columns = new HashMap<>();
        for (String attr : attributes) {
            String column;
            if (attr.equalsIgnoreCase("BRANCHID")) column = "LCSMOAOBJECT." + attr;
            else column = moaTableType.getAttribute(attr).getSearchResultIndex().toUpperCase();
            columns.put(attr, column);
        }

        Collection result = new ArrayList();

        String costSheetMasterRef = new ReferenceFactory().getReferenceString( lcsProductCostSheet.getMasterReference() );
        String costSheetMasterID = costSheetMasterRef.substring(costSheetMasterRef.lastIndexOf(':') + 1);
        String costSheetMasterType = costSheetMasterRef.substring(costSheetMasterRef.indexOf(':') + 1, costSheetMasterRef.lastIndexOf(':'));

        PreparedQueryStatement statement = new PreparedQueryStatement();
        statement.appendFromTable("LCSMOAOBJECT");

        for (String attr : attributes) {
            String column = columns.get(attr);
            if( column != null )
                statement.appendSelectColumn("LCSMOAOBJECT", column.replace("LCSMOAOBJECT.", ""));
        }
        statement.appendCriteria(new Criteria("LCSMOAOBJECT", "CLASSNAMEKEYA5", "?", Criteria.EQUALS), costSheetMasterType);
        statement.appendAnd();
        statement.appendCriteria(new Criteria("LCSMOAOBJECT", "IDA3A5", "?", Criteria.EQUALS), costSheetMasterID);
        statement.appendAnd();
        statement.appendCriteria(new Criteria("LCSMOAOBJECT", "ROLE", "?", Criteria.EQUALS), costSheetMOATableAttName);

        LOGGER.trace( "CUSTOM>>>>>> SMCostSheetReader.getMOATableRows: Statement: " + FormatHelper.formatSQL(statement.toString(), false) );

        Enumeration enumeration = LCSQuery.runDirectQuery(statement).getResults().elements();
        while (enumeration.hasMoreElements()) {
            FlexObject flexObject = (FlexObject) enumeration.nextElement();
            FlexObject row = new FlexObject();
            for(String attr : attributes) {
                String column = columns.get(attr);
                if( column != null ) {
                    String columnValue = FormatHelper.format(flexObject.getData(column));
                    row.put(attr, columnValue);
                }
            }
            result.add(row);
        }

        for (Object object : result) {
            LOGGER.debug("CUSTOM>>>>>> SMCostSheetReader.getMOATableRows: FlexObject (" + object.toString() + ")");
        }

        LOGGER.debug("CUSTOM>>>>>> SMCostSheetReader.getMOATableRows: Finish.");

        return result;
    }

    public static String getLCSSKUMasterID(LCSProductCostSheet lcsProductCostSheet) {
        return getRepresentativeLCSSKUMaster(lcsProductCostSheet)[0];
    }

    /*
    public static String[] getRepresentativeLCSSKUMaster(LCSProductCostSheet lcsProductCostSheet) {
        try
        {
            FlexType colorwayType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.product.LCSSKU");
            Collection colorLinks = LCSCostSheetQuery.getRepresentativeColor((LCSCostSheetMaster)lcsProductCostSheet.getMaster());
            LOGGER.trace("CUSTOM>>>>>> SMCostSheetReader.getRepresentativeLCSSKUMaster: colorLinks size = " + colorLinks.size());

            for (Object object : colorLinks) {
                FlexObject fo = (FlexObject) object;
                String masterRefID = FormatHelper.format(fo.getData("LCSSKU.IDA3MASTERREFERENCE"));
                if(FormatHelper.hasContent(masterRefID)) {
                    String colorName = FormatHelper.format(fo.getData(colorwayType.getAttribute("skuName").getSearchResultIndex()));
                    return new String[] { "OR:com.lcs.wc.part.LCSPartMaster:" + masterRefID, colorName };
                }

            }
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetReader.getRepresentativeLCSSKUMaster: No colors links persisted yet.");
        }
        return new String[] { "", "" };
    }
    */

    public static String[] getRepresentativeLCSSKUMaster(LCSProductCostSheet lcsProductCostSheet) {
        try
        {
            String repColorId = "";
            String colorName = "";
            FlexType colorwayType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.product.LCSSKU");
            Collection repColorCol = LCSCostSheetQuery.getRepresentativeColor((LCSCostSheetMaster)lcsProductCostSheet.getMaster());
            for (Object obj : repColorCol) {
                FlexObject fo = (FlexObject) obj;
                repColorId = fo.getString("LCSSKU.BRANCHIDITERATIONINFO");
                colorName = FormatHelper.format(fo.getData(colorwayType.getAttribute("skuName").getSearchResultIndex()));
            }

            if(FormatHelper.hasContent(repColorId)) {
                LCSSKU sku = (LCSSKU) LCSQuery.findObjectById("VR:com.lcs.wc.product.LCSSKU:" + repColorId);
                if(sku != null) {
                    String colorwayMasterRef = FormatHelper.getObjectId((WTPartMaster)sku.getMaster());
                    return new String[] { colorwayMasterRef, colorName };
                }
            }
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetReader.getRepresentativeLCSSKUMaster: No colors links persisted yet.");
        }
        return new String[] { "", "" };
    }

    public static String getProductDestinationID(LCSProductCostSheet lcsProductCostSheet) {
       return getRepresentativeProductDestination(lcsProductCostSheet)[0];
    }

    public static String[] getRepresentativeProductDestination(LCSProductCostSheet lcsProductCostSheet) {
        try
        {
            FlexType productDestinationType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.product.ProductDestination");
            Collection representativeDestCol = LCSCostSheetQuery.getRepresentativeDestination((LCSCostSheetMaster)lcsProductCostSheet.getMaster());
            LOGGER.trace("CUSTOM>>>>>> SMCostSheetReader.getRepresentativeProductDestination: representativeDestCol size = " + representativeDestCol.size());
            for(Object obj : representativeDestCol) {
                FlexObject fo = (FlexObject) obj;
                String refID = fo.getString("PRODUCTDESTINATION.IDA2A2");
                if(FormatHelper.hasContent(refID)) {
                    String productDestinationID = "OR:com.lcs.wc.product.ProductDestination:" + refID;
                    String productDestinationName = FormatHelper.format(fo.getData(productDestinationType.getAttribute("name").getSearchResultIndex()));
                    return new String[] { productDestinationID, productDestinationName };
                }
            }
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetReader.getRepresentativeProductDestination: No destination links persisted yet.");
        }
        return new String[] { "", "" };
    }

    // developed for a fix, is not used at the moment
    public static String[] getRepColorway(LCSProductCostSheet lcscostsheet) throws WTException {
        String[] representativeSKUMaster = new String[]{"", ""};
        Map csDimLinks = (Map) MethodContext.getContext().get("COSTSHEET_DIM_LINKS");
        String repColorId = (String) csDimLinks.get("REPCOLOR");
        if (FormatHelper.hasContent(repColorId)) {
            LCSSKU sku = (LCSSKU) LCSQuery.findObjectById(repColorId);
            if (sku != null) {
                representativeSKUMaster[0] = FormatHelper.getObjectId((WTPartMaster) sku.getMaster());
                representativeSKUMaster[1] = (String) sku.getValue("skuName");
            }
        }
        return representativeSKUMaster;
    }

    // developed for a fix, is not used at the moment
    public static String[] getRepDestination(LCSProductCostSheet lcscostsheet) throws WTException {
        String[] representativeProductDestination = new String[]{"", ""};
        Map csDimLinks = (Map) MethodContext.getContext().get("COSTSHEET_DIM_LINKS");
        String productDestinationID = (String) csDimLinks.get("REPDESTINATION");
        if (FormatHelper.hasContent(productDestinationID)) {
            ProductDestination productDestination = (ProductDestination) LCSQuery.findObjectById(productDestinationID);
            if (productDestination != null) {
                representativeProductDestination[0] = productDestinationID;
                representativeProductDestination[1] = productDestination.getName();
            }
        }
        return representativeProductDestination;
    }

    public static Map<String, String> getColorways(LCSProductCostSheet lcsProductCostSheet, boolean notRepresentativeOnly) {
        Map<String, String> result = new HashMap<>();
        try
        {
            FlexType colorwayType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.product.LCSSKU");
            //LCSSKU.IDA3MASTERREFERENCE
            //COSTSHEETTOCOLORLINK.REPRESENTATIVE - 1 OR 0
            //LCSSKU.PTC_STR_1TYPEINFOLCSSKU - COLOR NAME
            Collection colorLinks = LCSCostSheetQuery.getColorLinks((LCSCostSheetMaster)lcsProductCostSheet.getMaster());
            for (Object object : colorLinks) {
                FlexObject fo = (FlexObject) object;
                boolean representative = false;
                if (notRepresentativeOnly) {
                    representative = FormatHelper.parseBoolean(fo.getData("COSTSHEETTOCOLORLINK.REPRESENTATIVE"));
                }
                if (!representative) {
                    String masterRefID = FormatHelper.format(fo.getData("LCSSKU.IDA3MASTERREFERENCE"));
                    if(FormatHelper.hasContent(masterRefID)) {
                        String colorName = FormatHelper.format(fo.getData(colorwayType.getAttribute("skuName").getSearchResultIndex()));
                        result.put("OR:com.lcs.wc.part.LCSPartMaster:" + masterRefID, colorName);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetReader.getNotRepresentativeColorways: No colors links persisted yet.");
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, String> getProductDestinations(LCSProductCostSheet lcsProductCostSheet, boolean notRepresentativeOnly) {
        Map<String, String> result = new HashMap<>();
        try
        {
            FlexType productDestinationType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.product.ProductDestination");
            /* Example FlexObject:
             PRODUCTDESTINATION.PTC_STR_1TYPEINFOPRODUCTDEST - RU
             COSTSHEETTODESTINATIONLINK.IDA3B5 - 125616409
             COSTSHEETTODESTINATIONLINK.REPRESENTATIVE - 1
             COSTSHEETTODESTINATIONLINK.IDA2A2 - 125616515
             PRODUCTDESTINATION.IDA2A2 - 98659343
             */
            Collection destinationLinks = LCSCostSheetQuery.getDestinationLinks((LCSCostSheetMaster)lcsProductCostSheet.getMaster());
            for (Object object : destinationLinks) {
                FlexObject fo = (FlexObject) object;
                boolean representative = false;
                if (notRepresentativeOnly) {
                    representative = FormatHelper.parseBoolean(fo.getData("COSTSHEETTODESTINATIONLINK.REPRESENTATIVE"));
                }
                if (!representative) {
                    String refID = FormatHelper.format(fo.getData("PRODUCTDESTINATION.IDA2A2"));
                    if(FormatHelper.hasContent( refID )) {
                        String productDestinationName = FormatHelper.format(fo.getData(productDestinationType.getAttribute("name").getSearchResultIndex()));
                        result.put("OR:com.lcs.wc.product.ProductDestination:" + refID, productDestinationName);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("CUSTOM>>>>>> SMCostSheetReader.getNotRepresentativeProductDestinations: No destinations links persisted yet.");
            LOGGER.error(e.getMessage(), e);
            e.printStackTrace();
        }
        return result;
    }

    public static String getDisplayValueForMultiEntry(FlexType type, String attrName, String attrValue) throws WTException {
        String result = "";
        StringTokenizer st = new StringTokenizer(attrValue,"|~*~|");
        while (st.hasMoreTokens()) {
            if (!result.equals("")) result = result + ",";
            String next = st.nextToken();
            next = type.getAttribute(attrName).getAttValueList().getValue(next, null);
            result = result + next;
        }
        return result;
    }

    public static String getDisplayValue(FlexType type, String attrName, String attrValue) throws WTException {
        return type.getAttribute(attrName).getAttValueList().getValue(attrValue, null);
    }

    public static String getDisplayValue(String type, String attrName, String attrValue) throws WTException {
        com.ptc.core.lwc.server.PersistableAdapter obj = new com.ptc.core.lwc.server.PersistableAdapter(
                type,
                wt.session.SessionHelper.getLocale(),
                com.ptc.core.meta.common.OperationIdentifier.newOperationIdentifier(com.ptc.core.meta.common.OperationIdentifierConstants.VIEW));
        obj.load(attrName);

        com.ptc.core.meta.container.common.AttributeTypeSummary ats = obj.getAttributeDescriptor(attrName);
        com.ptc.core.meta.common.DataSet ds = ats.getLegalValueSet();

        if (ds instanceof com.ptc.core.meta.common.DiscreteSet)
        {
            com.ptc.core.meta.common.DiscreteSet discreteSet = (com.ptc.core.meta.common.DiscreteSet) ds;
            for(Object currentObj : discreteSet.getElements()) {
                if (currentObj instanceof com.ptc.core.lwc.common.LWCEnumerationEntryIdentifier) {
                    com.ptc.core.lwc.common.LWCEnumerationEntryIdentifier entryIdentifier = (com.ptc.core.lwc.common.LWCEnumerationEntryIdentifier) currentObj;

                    com.ptc.core.meta.common.EnumerationEntryIdentifier eei = ((com.ptc.core.meta.common.EnumeratedSet)ds).getElementByKey(entryIdentifier.getKey());
                    com.ptc.core.lwc.server.LWCEnumerationEntryValuesFactory eevf = new com.ptc.core.lwc.server.LWCEnumerationEntryValuesFactory();

                    wt.meta.LocalizedValues value = eevf.get(eei, wt.session.SessionHelper.getLocale());

                    LOGGER.debug("CUSTOM>>>>>> SMCostSheetReader.getDisplayValue: key='" + entryIdentifier.getKey() +
                            "', value='" + value.getDisplay() + "', currentKey='" + attrValue + "'");

                    if(entryIdentifier.getKey().equals(attrValue))
                        return value.getDisplay();
                }
            }
        }

        return attrValue;
    }
}
