/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hbi.etl.transformer;

import com.hbi.etl.dao.HbiBusinessObject;
import com.hbi.etl.dao.HbiColor;
import com.hbi.etl.dao.HbiCountry;
import com.hbi.etl.dao.HbiMaterial;
import com.hbi.etl.dao.HbiMaterialSupplier;
import com.hbi.etl.dao.HbiMoaObject;
import com.hbi.etl.dao.HbiPalette;
import com.hbi.etl.dao.HbiPaletteToColorLink;
import com.hbi.etl.dao.HbiSupplier;
import com.hbi.etl.logger.PLMETLLogger;
import com.hbi.etl.util.PLMETLException;
import com.hbi.etl.util.TransformUtil;
import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.color.LCSPaletteToColorLink;
import com.lcs.wc.country.LCSCountry;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTyped;
import com.lcs.wc.foundation.LCSLifecycleManaged;
import com.lcs.wc.foundation.LCSLifecycleManagedTypeInfo;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialSupplier;
import com.lcs.wc.material.LCSMaterialSupplierTypeInfo;
import com.lcs.wc.material.LCSMaterialTypeInfo;
import com.lcs.wc.moa.LCSMOAObject;
import com.lcs.wc.moa.LCSMOAObjectTypeInfo;
import com.lcs.wc.supplier.LCSSupplier;
import com.lcs.wc.supplier.LCSSupplierTypeInfo;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.dozer.MappingException;
import wt.util.WTException;
import wt.util.WTPropertyVetoException;

/**
 *
 * @author UST
 */
public class PLMETLTransformer {

    public static final String logLevel = LCSProperties.get("com.hbi.etl.logLevel");
    static Logger etlLogger = PLMETLLogger.createInstance(PLMETLTransformer.class, logLevel);
    //static Logger log = PLMETLLogger.getLogger(PLMETLTransformer.class, "D:\\ETL.log", "INFO");
	

    Vector transList = new Vector();

    public Vector tranform(Vector expObjects, String trEntity) throws WTException {
		etlLogger.info("Inside ETL Transformer function");
    	//int count = 1;
        for (Object obj : expObjects) {

            FlexObject flexObj = (FlexObject) obj;
			//etlLogger.info("got first flex object");
			
			//if (count==1){
            if ("LCSSupplier".equals(trEntity)) {
				//etlLogger.info("inside if clause of transform function");
				//count = 0;
                LCSSupplier suppObj = (LCSSupplier) LCSQuery.findObjectById("OR:com.lcs.wc.supplier.LCSSupplier:" + flexObj.getString("LCSSUPPLIER.IDA2A2"));
                FlexTyped flexTypedObj = (FlexTyped) suppObj;
                String flexTypeName = suppObj.getFlexType().getFullName();
                LCSSupplierTypeInfo typeInfoLCSSupplier = suppObj.getTypeInfoLCSSupplier();
                
//                if (count<=2) {
//					count++;
//					System.out.println("flexObj>>>>>>>>>>>>>>>>>>"+flexObj);
//					Collection<FlexTypeAttribute> allAttributes = suppObj.getFlexType().getAllAttributes();
//					System.out.println("suppObj.getTypeInfoLCSSupplier().PTC_STR_1>>>>>>>>>>>>>>>>>>"+suppObj.getTypeInfoLCSSupplier().getPtc_str_1());
//					for (FlexTypeAttribute flexTypeAttribute : allAttributes) {
//						System.out.println("Column Name>>>>>>>>>>>>>>>>"+flexTypeAttribute.getColumnName()+"  Att Value ="+suppObj.getValue(flexTypeAttribute.getAttKey()));
//						System.out.print(" Column getColumnDescriptorName="+flexTypeAttribute.getColumnDescriptorName());
//					}
//				}
                TransformUtil trUtil;
                trUtil = new TransformUtil();
                try {
                    if (trUtil.transformFlexTyped(flexTypedObj)) {
						//etlLogger.info("inside if transform utility");
                        // Transformed object is saved to Hbisupplier bean using Dozer bean copy util
                        HbiSupplier transObj = new HbiSupplier();
                        Mapper mapper = new DozerBeanMapper();
                        //transObj = mapper.map(suppObj, HbiSupplier.class);
                        typeInfoLCSSupplier = suppObj.getTypeInfoLCSSupplier();
                      
                        transObj = mapper.map(typeInfoLCSSupplier, HbiSupplier.class);
                        transObj.setStatestate(suppObj.getState().toString());
                        transObj.setCreatestampa2(suppObj.getCreateTimestamp());
                        transObj.setUpdatestampa2(suppObj.getModifyTimestamp());
                        transObj.setIda2a2(new BigDecimal(flexObj.getString("LCSSUPPLIER.IDA2A2")));
                        transObj.setMarkfordeletea2(new BigDecimal(flexObj.getString("LCSSUPPLIER.MARKFORDELETEA2")));
                        transObj.setBranchiditerationinfo(new BigDecimal(flexObj.getString("LCSSUPPLIER.BRANCHIDITERATIONINFO")));
                        transObj.setLatestiterationinfo(new BigDecimal(1));
                        transObj.setDatasourcesystem("PLM");
                        transObj.setSecuritylabels(suppObj.getSecurityLabels().toString());
                        transObj.setPrimaryimageurl(suppObj.getPrimaryImageURL());
                        transObj.setFlextypeidpath(suppObj.getFlexTypeIdPath());
                        transObj.setPtc_dbl_1(new BigDecimal(typeInfoLCSSupplier.getPtc_lng_1()));
                       // transObj.setPtc_str_1typeinfolcssupplier("ABC");
                   //    System.out.println("transObj>>>>>>>>>>>>>>>>>>"+transObj);
                        if (flexTypeName.contains("Factory")) {
                        	//hbiCfc
                        	transObj.setPtc_str_9(String.valueOf(typeInfoLCSSupplier.getPtc_bln_1()));
                        	//hbiProvideSupplies
                        	transObj.setPtc_str_46(String.valueOf(typeInfoLCSSupplier.getPtc_bln_2()));
                        	//hbiOffshoreBroker
                        	transObj.setPtc_str_36(String.valueOf(typeInfoLCSSupplier.getPtc_bln_3()));
						}
                        if (flexTypeName.contains("Supplier")&& !suppObj.getFlexType().isTypeRoot()) {
                        	//hbiEhWhVam
                        	transObj.setPtc_str_8(String.valueOf(typeInfoLCSSupplier.getPtc_bln_1()));
                        	//hbiEdiCapable
                        	transObj.setPtc_str_6(String.valueOf(typeInfoLCSSupplier.getPtc_bln_2()));
						}
                        transList.addElement(transObj);
                        //remove this break after testing
                    }
                } catch (Exception ex) {
                    etlLogger.debug("Exception in PLMETLTransformer for supplier:  "+suppObj);
                    etlLogger.debug(ex);
                    continue;
                }

            }
			//}
            if ("LCSBusinessObject".equals(trEntity)) {
            	
                etlLogger.debug("LCSBusinessObject Transformation");
                LCSLifecycleManaged busObj = (LCSLifecycleManaged) LCSQuery.findObjectById("OR:com.lcs.wc.foundation.LCSLifecycleManaged:" + flexObj.getString("LCSLIFECYCLEMANAGED.IDA2A2"));
                FlexTyped flexTypedObj = (FlexTyped) busObj;
                LCSLifecycleManagedTypeInfo typeInfoLCSLifecycleManaged = busObj.getTypeInfoLCSLifecycleManaged();
                TransformUtil trUtil;
                String flexTypeName = flexTypedObj.getFlexType().getFullName();
                trUtil = new TransformUtil();
               // System.out.println("busObj.getFlexType().getTypeDisplayName()>>>>>>>>>>>>>>>>>>>"+busObj.getFlexType().getTypeDisplayName());
                try {
                    if (trUtil.transformFlexTyped(flexTypedObj)) {

                        HbiBusinessObject transObj = new HbiBusinessObject();
                        Mapper mapper = new DozerBeanMapper();
                        transObj = mapper.map(typeInfoLCSLifecycleManaged, HbiBusinessObject.class);
                        transObj.setStatestate(busObj.getState().toString());
                        transObj.setCreatestampa2(busObj.getCreateTimestamp());
                        transObj.setUpdatestampa2(busObj.getModifyTimestamp());
                        transObj.setIda2a2(new BigDecimal(flexObj.getString("LCSLIFECYCLEMANAGED.IDA2A2")));
                        transObj.setMarkfordeletea2(new BigDecimal(flexObj.getString("LCSLIFECYCLEMANAGED.MARKFORDELETEA2")));
                        transObj.setDatasourcesystem("PLM");
                        transObj.setFlextypeidpath(busObj.getFlexTypeIdPath());
                        transObj.setTypedisplay(busObj.getFlexType().getTypeDisplayName());
                 
                        if (flexTypeName.contains("Care Codes")||flexTypeName.contains("Routing Source")) {
                        	transObj.setPtc_str_3(String.valueOf(typeInfoLCSLifecycleManaged.getPtc_bln_1()));
						}
                        if (flexTypeName.contains("PLM Job Administration")) {
                        	transObj.setPtc_dbl_1(new BigDecimal(typeInfoLCSLifecycleManaged.getPtc_lng_1()));
						}
                        if (flexTypeName.contains("Transaction BO")) {
                        	//setting value of attribute hbiEventTriggeredBy
                        	transObj.setPtc_dbl_1(new BigDecimal(typeInfoLCSLifecycleManaged.getPtc_lng_3()));
                        	//setting value of attribute hbiFlexBranchId
                        	transObj.setPtc_dbl_2(new BigDecimal(typeInfoLCSLifecycleManaged.getPtc_lng_4()));
                        	//setting value of attribute hbiFlexObjectId
                        	transObj.setPtc_dbl_3(new BigDecimal(typeInfoLCSLifecycleManaged.getPtc_lng_1()));
                        	//setting value of attribute hbiTransactionId
                        	transObj.setPtc_dbl_4(new BigDecimal(typeInfoLCSLifecycleManaged.getPtc_lng_2()));
						}
                        if (flexTypeName.contains("Defaults")) {
                        	transObj.setPtc_dbl_2(new BigDecimal(typeInfoLCSLifecycleManaged.getPtc_lng_1()));
						}
                        transList.addElement(transObj);
                        break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
    
                	etlLogger.debug("Exception in PLMETLTransformer for LCSBusinessObject:  "+busObj);
                    etlLogger.debug(ex);
                    continue;
                }
                etlLogger.debug("Transformed object saved to HbiBusinessObject bean using Dozer bean copy util");

            }
            if ("LCSColor".equals(trEntity)) {

                LCSColor colorObj = (LCSColor) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSColor:" + flexObj.getString("LCSCOLOR.IDA2A2"));
                FlexTyped flexTypedObj = (FlexTyped) colorObj;
                String flexTypeName = colorObj.getFlexType().getFullName(false);

                TransformUtil trUtil;
                trUtil = new TransformUtil();
                try {
                    if (trUtil.transformFlexTyped(flexTypedObj)) {
                    	
                    	// Transformed object is saved to Hbimaterial bean using Dozer bean copy util
                        HbiColor transObj = new HbiColor();
                        Mapper mapper = new DozerBeanMapper();
                        transObj = mapper.map(colorObj.getTypeInfoLCSColor(), HbiColor.class);
                        transObj.setStatestate(colorObj.getState().toString());
                        transObj.setCreatestampa2(colorObj.getCreateTimestamp());
                        transObj.setUpdatestampa2(colorObj.getModifyTimestamp());
                        transObj.setIda2a2(new BigDecimal(flexObj.getString("LCSCOLOR.IDA2A2")));
                        transObj.setMarkfordeletea2(new BigDecimal(flexObj.getString("LCSCOLOR.MARKFORDELETEA2")));
                        transObj.setClassnamea2a2(flexObj.getString("LCSCOLOR.classnameida2a2"));
                        transObj.setDatasourcesystem("PLM");
                        transObj.setFlextypeidpath(colorObj.getFlexTypeIdPath());
                        transObj.setThumbnail(colorObj.getThumbnail());
                        //transObj.setTypedisplay(colorObj.getFlexType().getTypeDisplayName());
                        transObj.setColorhexidecimalvalue(colorObj.getColorHexidecimalValue());
                        transObj.setColorname(colorObj.getColorName());
                        transObj.setPtc_dbl_1(new BigDecimal(colorObj.getTypeInfoLCSColor().getPtc_lng_1()));
                      if (flexTypeName.contains("Colorway")||flexTypeName.contains("Non-Dipped")) {
						transObj.setPtc_str_11(String.valueOf(colorObj.getTypeInfoLCSColor().getPtc_bln_1()));
					}
                      if (flexTypeName.equalsIgnoreCase("S")) {
                    	  transObj.setPtc_dbl_1(new BigDecimal(colorObj.getTypeInfoLCSColor().getPtc_lng_1()));
 					}
                        transList.addElement(transObj);
                    //    break;
	                    }
	                } catch (Exception ex) {
	                    etlLogger.debug("Exception in PLMETLTransformer for LCSColor:  "+colorObj);
	                    etlLogger.debug(ex);
	                    continue;
	                }
	
	            }
            if ("LCSPalette".equals(trEntity)) {

                LCSPalette paletteObj = (LCSPalette) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSPalette:" + flexObj.getString("LCSPALETTE.IDA2A2"));
                FlexTyped flexTypedObj = (FlexTyped) paletteObj;
              //  System.out.println("flexObj>>>>>>>>>>>>>>>"+flexObj);
              //  System.out.println("paletteObj.getFlexType().getTypeDisplayName()>>>>>>>>>>>>>>>>"+paletteObj.getFlexType().getTypeDisplayName());
                TransformUtil trUtil;
                trUtil = new TransformUtil();
                try {
                    if (trUtil.transformFlexTyped(flexTypedObj)) {

                        // Transformed object is saved to HbiPalette bean using Dozer bean copy util
                        HbiPalette transObj = new HbiPalette();
                        Mapper mapper = new DozerBeanMapper();
                        transObj = mapper.map(paletteObj.getTypeInfoLCSPalette(), HbiPalette.class);
                        transObj.setStatestate(paletteObj.getState().toString());
                        transObj.setCreatestampa2(paletteObj.getCreateTimestamp());
                        transObj.setUpdatetampa2(paletteObj.getModifyTimestamp());
                        transObj.setIda2a2(new BigDecimal(flexObj.getString("LCSPALETTE.IDA2A2")));
                        transObj.setIda3a9(new BigDecimal(flexObj.getString("LCSPALETTE.IDA3A9")));
                        //transObj.setClassnamea2a2(flexObj.getString("LCSPALETTE.classnameida2a2"));
                        transObj.setMarkfordeletea2(new BigDecimal(flexObj.getString("LCSPALETTE.MARKFORDELETEA2")));
                        transObj.setDatasourcesystem("PLM");                   
                        transObj.setFlextypeidpath(paletteObj.getFlexTypeIdPath());
                        transObj.setPalettename(paletteObj.getPaletteName());
                        //transObj.setTypedisplay(paletteObj.getFlexType().getTypeDisplayName());
                        
                        transList.addElement(transObj);
                      //  break;
                    }
                } catch (Exception ex) {
                    etlLogger.debug("Exception in PLMETLTransformer for LCSPalette:  "+paletteObj);
                    etlLogger.debug(ex);
                    continue;
                }

            }
            if ("LCSColorToPalette".equals(trEntity)) {

                LCSPaletteToColorLink paletteToColorLinkObj = (LCSPaletteToColorLink) LCSQuery.findObjectById("OR:com.lcs.wc.color.LCSPaletteToColorLink:" + flexObj.getString("LCSPALETTETOCOLORLINK.IDA2A2"));
                FlexTyped flexTypedObj = (FlexTyped) paletteToColorLinkObj;
               // System.out.println("flexObj LCSColorToPalette >>>>>>>>>>>>>>>"+flexObj);
               //  System.out.println("paletteToColorLinkObj.getTypeInfoLCSPaletteToColorLink()>>>>>>>>>"+paletteToColorLinkObj.getTypeInfoLCSPaletteToColorLink());
               // System.out.println("paletteToColorLinkObj.>>>>>>>>>>>>"+paletteToColorLinkObj.getValue("hbiMktgDesc"));
                //paletteToColorLinkObj.setValue("hbiMktgDesc","ABCD");
                TransformUtil trUtil;
                trUtil = new TransformUtil();
                try {
                    if (trUtil.transformFlexTyped(flexTypedObj)) {

                        // Transformed object is saved to HbiPaletteToColorLink bean using Dozer bean copy util
                        HbiPaletteToColorLink transObj = new HbiPaletteToColorLink();
                        Mapper mapper = new DozerBeanMapper();
                        transObj = mapper.map(paletteToColorLinkObj.getTypeInfoLCSPaletteToColorLink(), HbiPaletteToColorLink.class);
                        transObj.setCreatestampa2(paletteToColorLinkObj.getCreateTimestamp());
                        transObj.setUpdatestampa2(paletteToColorLinkObj.getModifyTimestamp());
                        transObj.setIda2a2(new BigDecimal(flexObj.getString("LCSPALETTETOCOLORLINK.IDA2A2")));
                        transObj.setMarkfordeletea2(new BigDecimal(flexObj.getString("LCSPALETTETOCOLORLINK.MARKFORDELETEA2")));
                        transObj.setIda3a5(new BigDecimal(flexObj.getString("LCSPALETTETOCOLORLINK.IDA3A5")));
                       // transObj.setIda3a6(new BigDecimal(flexObj.getString("LCSPALETTETOCOLORLINK.IDA3A6")));
                        transObj.setIda3b5(new BigDecimal(flexObj.getString("LCSPALETTETOCOLORLINK.IDA3B5")));
                       // transObj.setIda3b6(new BigDecimal(flexObj.getString("LCSPALETTETOCOLORLINK.IDA3B6")));
                       // transObj.setIda3c6(new BigDecimal(flexObj.getString("LCSPALETTETOCOLORLINK.IDA3C6")));
                       // transObj.setClassnamekeyroleaobjectref(flexObj.getString("LCSPALETTETOCOLORLINK.CLASSNAMEKEYROLEAOBJECTREF"));
                        //transObj.setClassnamekeyrolebobjectref(flexObj.getString("LCSPALETTETOCOLORLINK.CLASSNAMEKEYROLEBOBJECTREF"));
                        transObj.setFlextypeidpath(paletteToColorLinkObj.getFlexTypeIdPath());
                        transList.addElement(transObj);
                       // System.out.println("transObj>>>>>>>>>>>>>>>>>>>>>>>>>>>"+transObj);
                       // break;
                    }
                } catch (Exception ex) {
                    etlLogger.debug("Exception in PLMETLTransformer for LCSColorToPalette:  "+paletteToColorLinkObj);
                    etlLogger.debug(ex);
                    continue;
                }

            }
            if ("LCSCountry".equals(trEntity)) {

                LCSCountry cObj = (LCSCountry) LCSQuery.findObjectById("OR:com.lcs.wc.country.LCSCountry:" + flexObj.getString("LCSCOUNTRY.IDA2A2"));
                FlexTyped flexTypedObj = (FlexTyped) cObj;
               // System.out.println("flexObj>>>>>>>>>>>>>>"+flexObj);
                TransformUtil trUtil;
                trUtil = new TransformUtil();
                try {
                    if (trUtil.transformFlexTyped(flexTypedObj)) {

                        // Transformed object is saved to HBICOUNTRY bean using Dozer bean copy util
                        HbiCountry transObj = new HbiCountry();
                        Mapper mapper = new DozerBeanMapper();
                        transObj = mapper.map(cObj.getTypeInfoLCSCountry(), HbiCountry.class);
                        transObj.setIda2a2(new BigDecimal(flexObj.getString("LCSCOUNTRY.IDA2A2")));
                        transObj.setMarkfordeletea2(new BigDecimal(flexObj.getString("LCSCOUNTRY.MARKFORDELETEA2")));
                        transObj.setBranchiditerationinfo(new BigDecimal(flexObj.getString("LCSCOUNTRY.BRANCHIDITERATIONINFO")));
                        transObj.setCreatestampa2(cObj.getCreateTimestamp());
                        transObj.setUpdatestampa2(cObj.getModifyTimestamp());
                        transObj.setDatasourcesystem("PLM");
                        transObj.setLatestiterationinfo(new BigDecimal(1));
                        transObj.setFlextypeidpath(cObj.getFlexTypeIdPath());
                        transList.addElement(transObj);
                     //   break;
                    }
                } catch (Exception ex) {
                    etlLogger.debug("Exception in PLMETLTransformer for LCSCountry:  "+cObj);
                    etlLogger.debug(ex);
                    continue;
                }

            }
            if ("LCSMaterial".equals(trEntity)) {
				
				etlLogger.info("inside if material transform utility");

                LCSMaterial matObj = (LCSMaterial) LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterial:" + flexObj.getString("LCSMATERIAL.IDA2A2"));
                FlexTyped flexTypedObj = (FlexTyped) matObj;
                String flexTypeName = matObj.getFlexType().getFullName();
				etlLogger.info("flex type name"+ flexTypeName);
                System.out.println("flexTypeName>>>>>>>>>>>>>>>"+flexTypeName);
                //System.out.println("matObj.getModifyTimestamp()>>>>>>>>>>>>>>>"+matObj.getModifyTimestamp());
                
               // System.out.println(" Material flexObj>>>>>>>>>>>>>>"+flexObj);
                TransformUtil trUtil;
                LCSMaterialTypeInfo typeInfoLCSMaterial = matObj.getTypeInfoLCSMaterial();
                trUtil = new TransformUtil();
                try {
                    if (trUtil.transformFlexTyped(flexTypedObj)) {

                        // Transformed object is saved to Hbimaterial bean using Dozer bean copy util
                        HbiMaterial transObj = new HbiMaterial();
                        Mapper mapper = new DozerBeanMapper();
                        transObj = mapper.map(typeInfoLCSMaterial, HbiMaterial.class);
                        transObj.setStatestate(matObj.getState().toString());
                        transObj.setCreatestampa2(matObj.getCreateTimestamp());
                        transObj.setUpdatestampa2(matObj.getModifyTimestamp());
                        transObj.setIda2a2(new BigDecimal(flexObj.getString("LCSMATERIAL.IDA2A2")));
                        //transObj.setIda3a11(new BigDecimal(flexObj.getString("LCSMATERIAL.IDA3A11")));
                        transObj.setIda3a11(new BigDecimal(flexObj.getString("LCSMATERIAL.IDA2TYPEDEFINITIONREFERENCE")));
                        transObj.setMarkfordeletea2(new BigDecimal(flexObj.getString("LCSMATERIAL.MARKFORDELETEA2")));
                        transObj.setBranchiditerationinfo(new BigDecimal(flexObj.getString("LCSMATERIAL.BRANCHIDITERATIONINFO")));
                        transObj.setFlextypeidpath(flexObj.getString("LCSMATERIAL.flextypeidpath"));
                        transObj.setLatestiterationinfo(new BigDecimal(1));
                        transObj.setVersionida2versioninfo(flexObj.getString("LCSMATERIAL.versionida2versioninfo"));
                        transObj.setDatasourcesystem("PLM");
                        //sequence
                        transObj.setPtc_dbl_6(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_22()));
                        //hbiBuyOrNotBuy
                        transObj.setPtc_str_68(String.valueOf(typeInfoLCSMaterial.getPtc_bln_11()));
                        //hbiImageAttached
                        transObj.setPtc_str_24(String.valueOf(typeInfoLCSMaterial.getPtc_bln_13()));
                        //hbiMasterMaterial
                        transObj.setPtc_str_81(String.valueOf(typeInfoLCSMaterial.getPtc_bln_15()));
                        //hbiNewWear
                        transObj.setPtc_str_35(String.valueOf(typeInfoLCSMaterial.getPtc_bln_1()));
                        //hbiOpticalBrightener
                        transObj.setPtc_str_76(String.valueOf(typeInfoLCSMaterial.getPtc_bln_10()));
                        //hbiBars
                        transObj.setPtc_dbl_32(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_18()));
                        //hbiHook
                        transObj.setPtc_dbl_34(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_23()));
                        //hbiPlyNew
                        transObj.setPtc_dbl_20(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_24()));
                        //hbiScreens
                        transObj.setPtc_dbl_35(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_19()));
                        
                        if (flexTypeName.contains("Casing")) {
                        	//hbiAPSBOMMaterial
                        	transObj.setPtc_str_48(String.valueOf(typeInfoLCSMaterial.getPtc_bln_17()));
                        	//masterMaterial
                        	transObj.setPtc_str_26(String.valueOf(typeInfoLCSMaterial.getPtc_bln_16()));
						}
                        if (flexTypeName.contains("Fabric") && !flexTypeName.contains("Fabrics")) {
                        	//hbiCompact
                        	transObj.setPtc_str_59(String.valueOf(typeInfoLCSMaterial.getPtc_bln_16()));
                        	//hbiTurn
                        	transObj.setPtc_str_60(String.valueOf(typeInfoLCSMaterial.getPtc_bln_17()));
						}
                        if (flexTypeName.contains("Fabric Buy")) {
                        	//hbiCoreFabric
                        	transObj.setPtc_str_88(String.valueOf(typeInfoLCSMaterial.getPtc_bln_18()));
                        	//hbiMoldedItem
                        	transObj.setPtc_str_87(String.valueOf(typeInfoLCSMaterial.getPtc_bln_19()));
						}
                        if (flexTypeName.contains("Yarn")) {
                        	//hbiConditioned
                        	transObj.setPtc_str_50(String.valueOf(typeInfoLCSMaterial.getPtc_bln_16()));
                        	//hbiHeathered
                        	transObj.setPtc_str_55(String.valueOf(typeInfoLCSMaterial.getPtc_bln_18()));
                        	//hbiWaxed
                        	transObj.setPtc_str_63(String.valueOf(typeInfoLCSMaterial.getPtc_bln_17()));
						}
                        if (flexTypeName.contains("Material SKU\\Accessories\\MoldedCup")) {
                        	//hbiCupSetFlange
                        	transObj.setPtc_str_48(String.valueOf(typeInfoLCSMaterial.getPtc_bln_18()));
                        	//hbiSloper
                        	transObj.setPtc_str_55(String.valueOf(typeInfoLCSMaterial.getPtc_bln_17()));
                        	//hbiStrapPlatform
                        	transObj.setPtc_str_56(String.valueOf(typeInfoLCSMaterial.getPtc_bln_16()));
						}
                        if (flexTypeName.contains("HookEye")) {
                        	//hbiHasInserts
                        	transObj.setPtc_str_50(String.valueOf(typeInfoLCSMaterial.getPtc_bln_16()));
                        	//hbiNumAdjustments
                        	transObj.setPtc_dbl_14(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_3()));
                        	//hbiNumRows
                        	transObj.setPtc_dbl_15(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_29()));
						}
                        if (flexTypeName.contains("Material SKU\\Elastics")) {
                        	//hbiPrinted
                        	transObj.setPtc_str_51(String.valueOf(typeInfoLCSMaterial.getPtc_bln_17()));
                        	//hbiStretch
                        	transObj.setPtc_str_52(String.valueOf(typeInfoLCSMaterial.getPtc_bln_16()));
						}
                        if (flexTypeName.contains("Zippers")) {
                        	//hbiSeparating
                        	transObj.setPtc_str_49(String.valueOf(typeInfoLCSMaterial.getPtc_bln_16()));
						}
                        if (flexTypeName.contains("Cut BOM Material")) {
                        	//hbiGarments
                        	transObj.setPtc_dbl_30(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_34()));
						}
                        if (flexTypeName.contains("Fabrics\\Fabrics")) {
                        	//hbiMachineGuage
                        	transObj.setPtc_dbl_1(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_34()));
                        	//hbiTest
                        	transObj.setPtc_dbl_21(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_26()));
                        	//test
                        	transObj.setPtc_dbl_15(new BigDecimal(typeInfoLCSMaterial.getPtc_lng_33()));
						}
                        
                        transList.addElement(transObj);
                        /*if (transList.size()>22) {
                        	break;
						}*/
                        
                    }
                } catch (Exception ex) {
                    etlLogger.debug("Exception in PLMETLTransformer for LCSMaterial:  "+matObj);
                    etlLogger.debug(ex);
                    continue;
                }

            }
            if ("LCSMaterialSupplier".equals(trEntity)) {

                //LCSMaterial matObj = (LCSMaterial) LCSQuery.findObjectById("OR:com.lcs.wc.material.LCSMaterial:" + flexObj.getString("LCSMATERIAL.IDA2A2"));
                String matSupOid = "VR:com.lcs.wc.material.LCSMaterialSupplier:" + flexObj.getData("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO");
                LCSMaterialSupplier matSupObj = (LCSMaterialSupplier) LCSQuery.findObjectById(matSupOid);
                FlexTyped flexTypedObj = (FlexTyped) matSupObj;
                //System.out.println("Material Supplier flexObj>>>>>>>>>>>>>>"+flexObj);
                LCSMaterialSupplierTypeInfo typeInfoLCSMaterialSupplier = matSupObj.getTypeInfoLCSMaterialSupplier();
                TransformUtil trUtil;
                trUtil = new TransformUtil();
                try {
                    if (trUtil.transformFlexTyped(flexTypedObj)) {

                        // Transformed object is saved to Hbimaterial bean using Dozer bean copy util
                        HbiMaterialSupplier transObj = new HbiMaterialSupplier();
                        Mapper mapper = new DozerBeanMapper();
                        transObj = mapper.map(typeInfoLCSMaterialSupplier, HbiMaterialSupplier.class);
                        transObj.setStatestate(matSupObj.getState().toString());
                        transObj.setCreatestampa2(matSupObj.getCreateTimestamp());
                        transObj.setUpdatestampa2(matSupObj.getModifyTimestamp());
                        transObj.setIda2a2(new BigDecimal(flexObj.getString("LCSMATERIALSUPPLIER.IDA2A2")));
                        transObj.setMarkfordeletea2(new BigDecimal(flexObj.getString("LCSMATERIALSUPPLIER.MARKFORDELETEA2")));
                        transObj.setBranchiditerationinfo(new BigDecimal(flexObj.getString("LCSMATERIALSUPPLIER.BRANCHIDITERATIONINFO")));
                        transObj.setClassnamea2a2(flexObj.getString("LCSMATERIALSUPPLIER.classnameida2a2"));
                        transObj.setMaterialref(new BigDecimal(flexObj.getString("LCSMATERIAL.BRANCHIDITERATIONINFO")));
                        transObj.setSupplierref(new BigDecimal(flexObj.getString("LCSSUPPLIER.BRANCHIDITERATIONINFO")));
                        transObj.setLatestiterationinfo(new BigDecimal(1));
                        transObj.setDatasourcesystem("PLM");
                        transObj.setFlextypeidpath(flexObj.getString("LCSMATERIALSUPPLIER.flexTypeIdPath"));
                        transObj.setPrimaryimageurl(matSupObj.getPrimaryImageURL());
                        transObj.setActive(matSupObj.isActive());
                        //Setting value of IntimatesMatSuppLoaded
                        transObj.setPtc_str_24(String.valueOf(typeInfoLCSMaterialSupplier.getPtc_bln_1()));
                        transList.addElement(transObj);
                        
                      /*if (transList.size()>22) {
                      	break;
						}*/
                    }
                } catch (Exception ex) {
                    etlLogger.debug("Exception in PLMETLTransformer for LCSMaterialSupplier:  "+matSupObj);
                    etlLogger.debug(ex);
                    continue;
                }

            }
            if ("LCSMOAObject".equals(trEntity)) {

                LCSMOAObject moaObj = (LCSMOAObject) LCSQuery.findObjectById("OR:com.lcs.wc.moa.LCSMOAObject:" + flexObj.getString("LCSMOAOBJECT.IDA2A2"));
                FlexTyped flexTypedObj = (FlexTyped) moaObj;
                String flexTypeName = moaObj.getFlexType().getFullName();
                System.out.println("LCSMOAObject flexTypeName>>>>>>>>>>>>>>>>"+flexTypeName);
                LCSMOAObjectTypeInfo typeInfoLCSMOAObject = moaObj.getTypeInfoLCSMOAObject();
                TransformUtil trUtil;
                trUtil = new TransformUtil();
                try {
                    if (trUtil.transformFlexTyped(flexTypedObj)) {

                        // Transformed object is saved to HbiMoaObject bean using Dozer bean copy util
                        HbiMoaObject transObj = new HbiMoaObject();
                        Mapper mapper = new DozerBeanMapper();
                        transObj = mapper.map(typeInfoLCSMOAObject, HbiMoaObject.class);
                        //System.out.println("transObj mapp>>>>>>>>>>>>>>>>>>"+transObj);
                        transObj.setCreatestampa2(moaObj.getCreateTimestamp());
                        transObj.setUpdatestampa2(moaObj.getModifyTimestamp());
                        transObj.setIda2a2(new BigDecimal(flexObj.getString("LCSMOAOBJECT.IDA2A2")));
                        transObj.setMarkfordeletea2(new BigDecimal(flexObj.getString("LCSMOAOBJECT.MARKFORDELETEA2")));
                        //transObj.setClassnamekeya4(flexObj.getString("LCSMOAOBJECT.CLASSNAMEKEYA4"));
                        //transObj.setIda3a4(new BigDecimal(flexObj.getString("LCSMOAOBJECT.IDA3A4")));
                        transObj.setIda3a4(new BigDecimal(flexObj.getString("LCSMOAOBJECT.IDA2TYPEDEFINITIONREFERENCE")));
                        if (flexObj.getString("LCSMOAOBJECT.IDA3B5") != null) {
                            transObj.setIda3b5(new BigDecimal(flexObj.getString("LCSMOAOBJECT.IDA3B5")));
                        }
                        if (flexObj.getString("LCSMOAOBJECT.CLASSNAMEKEYB5") != null) {
                            transObj.setClassnamekeyb5(flexObj.getString("LCSMOAOBJECT.CLASSNAMEKEYB5"));
                        }

                        String ownerRef = moaObj.getOwner().toString();
                        if (!ownerRef.isEmpty()) {

                            TransformUtil tUtil = new TransformUtil();
                            transObj.setOwnerref(tUtil.getTableName(ownerRef));
                            transObj.setOwnerkey(new BigDecimal(tUtil.getOwnerKey(ownerRef)));
                            //Changed for 12 upgrade(afsyed) - start
                            transObj.setAttributekey(moaObj.getOwningAttribute().getAttKey());
                            transObj.setAttributevalue(moaObj.getOwningAttribute().getAttDisplay());
                            //Changed for 12 upgrade(afsyed) - end
                            transObj.setDatasourcesystem("PLM");
                            transObj.setFlextypeidpath(moaObj.getFlexTypeIdPath());
                            transObj.setOwnerversion(flexObj.getString("LCSMOAOBJECT.OWNERVERSION"));
                            transObj.setDropped(moaObj.isDropped());
                            transObj.setEffectindate(moaObj.getEffectInDate());
                            transObj.setEffectoutdate(moaObj.getEffectOutDate());
                            transObj.setEffectlatest(moaObj.isEffectLatest());
                            transObj.setEffectsequence(new BigDecimal(moaObj.getEffectSequence()));
                            transObj.setSortingnumber(new BigDecimal(moaObj.getSortingNumber()));
                            transObj.setBranchid(new BigDecimal(moaObj.getBranchId()));
                            if (flexTypeName.contains("Revision Attributes")||flexTypeName.contains("Discussion")) {
								transObj.setPtc_dbl_1(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_1()));
							}
              
                            if (flexTypeName.contains("APS Routings")) {
                            	//hbiAPSCapacityCheckInd
								transObj.setPtc_str_1(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
								//hbiAPSOperReporting
								transObj.setPtc_str_2(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_2()));
								//hbiAPSStdUnits
								transObj.setPtc_dbl_3(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_1()));
							}
                            if (flexTypeName.contains("Sizing Table")) {
                            	//hbiActiveSizeMOATable
								transObj.setPtc_str_5(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
								//hbiXSize
								transObj.setPtc_dbl_2(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_1()));
							}
                            
                            if (flexTypeName.contains("Put Up Code")) {
                            	//hbiEPC
								transObj.setPtc_str_1(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
		
							}
                            if (flexTypeName.contains("SAP And APS Validation Attributes")) {
                            	//hbiFinalCompleteSetup
								transObj.setPtc_str_4(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_2()));
								//hbiLockEdit
								transObj.setPtc_str_5(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_3()));
								//hbiRequiredAPS
								transObj.setPtc_str_6(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_5()));
								//hbiRequiredSAP
								transObj.setPtc_str_7(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_4()));
								//hbiSAPKeyFieldsLock
								transObj.setPtc_str_8(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
		
							}
                            if (flexTypeName.contains("SAP Team Template")) {
                            	//hbiIsExclusive
								transObj.setPtc_str_3(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
		
							}
                            if (flexTypeName.contains("SAP Division Derivation Table")) {
                            	//hbiOmniSelection
								transObj.setPtc_str_5(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
		
							}
                            if (flexTypeName.contains("Cut Spec Fabric Table")) {
                            	//hbiPrimary
								transObj.setPtc_str_5(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
		
							}
                            if (flexTypeName.contains("Plant Extensions")) {
                            	//hbiPrimaryDeliverPlant
								transObj.setPtc_str_3(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_2()));
								//hbiSynchedStatus
								transObj.setPtc_str_5(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
								//hbiPlannedDelTime
								transObj.setPtc_dbl_1(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_1()));
								//hbiTotalRepLeadTme
								transObj.setPtc_dbl_2(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_10()));
							}
                            if (flexTypeName.contains("Routing MOA")) {
                            	//hbiPrimaryDistribution
								transObj.setPtc_str_1(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
		
							}
                            if (flexTypeName.contains("Dye Formula Components")) {
                            	//hbiPullDesc
								transObj.setPtc_str_3(String.valueOf(typeInfoLCSMOAObject.getPtc_bln_1()));
		
							}
                            
                            if (flexTypeName.contains("Finish Attribute MOA")) {
                            	//hbiCylSize
								transObj.setPtc_dbl_1(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_13()));
		
							}
                            if (flexTypeName.contains("Knitting Finishing attributes")) {
                            	//hbiCylinderInt
								transObj.setPtc_dbl_28(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_23()));
		
							}
                            if (flexTypeName.contains("Colorway Placement Table")) {
                            	//hbiPlanPct
								transObj.setPtc_dbl_3(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_1()));
		
							}
                            if (flexTypeName.contains("Assortment Table")) {
                            	//hbiRevisionCode
								transObj.setPtc_dbl_2(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_10()));
		
							}
                            if (flexTypeName.contains("Purchasing Materials MOA")) {
                            	//hbiTieredQty
								transObj.setPtc_dbl_11(new BigDecimal(typeInfoLCSMOAObject.getPtc_lng_1()));
								transObj.setPtc_dbl_12(new BigDecimal(typeInfoLCSMOAObject.getPtc_ref_1().getObject().getPersistInfo().getObjectIdentifier().getId()));
								transObj.setPtc_dbl_13(new BigDecimal(typeInfoLCSMOAObject.getPtc_ref_2().getObject().getPersistInfo().getObjectIdentifier().getId()));
							}
                            
                            
                            transList.addElement(transObj);
                            
                        } else {
                        }
                    }
                } catch (Exception ex) {
                	ex.printStackTrace();
                    etlLogger.debug("Exception in PLMETLTransformer for LCSMOAObject:  "+moaObj);
                    etlLogger.debug(ex);
                    continue;
                }

            }
        }
      //  System.out.println("transList>>>>>>>>>>>>>>>>>>>>>>>>>>>"+transList.size());
        return transList;
    }
}
