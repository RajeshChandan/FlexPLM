/**Class Name.
 * CareWashReport.java 
 */
package com.sportmaster.wc.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import wt.util.WTException;

import com.lcs.wc.db.Criteria;
import com.lcs.wc.db.FlexObject;
import com.lcs.wc.db.PreparedQueryStatement;
import com.lcs.wc.db.QueryColumn;
import com.lcs.wc.db.SearchResults;
import com.lcs.wc.flexbom.FlexBOMLink;
import com.lcs.wc.flexbom.FlexBOMPart;
import com.lcs.wc.flexbom.LCSFlexBOMQuery;
import com.lcs.wc.flextype.FlexType;
import com.lcs.wc.flextype.FlexTypeAttribute;
import com.lcs.wc.flextype.FlexTypeCache;
import com.lcs.wc.foundation.LCSQuery;
import com.lcs.wc.material.LCSMaterial;
import com.lcs.wc.material.LCSMaterialColor;
import com.lcs.wc.material.LCSMaterialMaster;
import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.ProductDestination;
import com.lcs.wc.season.LCSProductSeasonLink;
import com.lcs.wc.season.LCSSeason;
import com.lcs.wc.sourcing.LCSSourcingConfig;
import com.lcs.wc.sourcing.LCSSourcingConfigMaster;
import com.lcs.wc.specification.FlexSpecDestination;
import com.lcs.wc.specification.FlexSpecToComponentLink;
import com.lcs.wc.specification.FlexSpecification;
import com.lcs.wc.util.LCSLog;
import com.lcs.wc.util.LCSProperties;
import com.lcs.wc.util.VersionHelper;

/**
 * This class will generate the Care Label Report Based on selected parameters
 * from Season--> Reports--> Care Label Report.
 * 
 * Report is getting generated in background hence there will be no pop ups.
 * 
 * @author 'true' Monu Singh Jangra
 * @version 'true' 1.0 version number
 */
public class CareWashReport {

	/**
	 * This method will convert the comma separated string values to list
	 * object.
	 * 
	 * @param selectedValues
	 *            String
	 * @return list List<String>
	 */
	public List<String> getTheList(String selectedValues) {
		List<String> list = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(selectedValues, "|~*~|");
		while (st.hasMoreElements()) {
			list.add(st.nextToken());
		}
		return list;

	}

	/**
	 * 
	 * This method will be responsible for all the logic which will generate the
	 * Excel in background.
	 * 
	 * @param season
	 *            LCSSeason
	 * @param productBranchIds
	 *            List<String>
	 * @param brands
	 *            List<String>
	 * @param ages
	 *            List<String>
	 * @param projects
	 *            List<String>
	 * @param PGs
	 *            List<String>
	 * @param PTS
	 *            List<String>
	 * @param SSS
	 *            List<String>
	 */
	public void generateExcelFromData(LCSSeason season, List<String> productBranchIds, List<String> brands, List<String> ages, List<String> projects, List<String> PGs, List<String> PTS,
			List<String> SSS) {

		final String reportHeader = LCSProperties.get("com.sportmaster.wc.reports.reportHeader");

		final String orderDestinationIN = "smSCDestination";
		final String primaryIN = "smPrimary";
		final String AltPrimaryIN = "smAltPrimary";
		final String componentNameIN = "smComponentName";
		final String OrderDestinationIN = "smSCDestination";
		final String UOMIN = "unitOfMeasure";
		final String washCareIN = "smWashCare";
		final String compositionIN = "vrdContentSearch";
		final String compositionRUIN = "smCompositionRU";
		final String laminationCoatingIN = "smLaminationCoating";
		final String finishIN = "vrdFinish";
		final String additionalCareIN = "smAdditionalCareMC";
		final String quantityIN = "quantity";
		final String materialDescriptionIN = "materialDescription";
		final String brandIN = "vrdBrand";
		final String ageIN = "smAge";
		final String projectIN = "smProject";
		final String productionGroupIN = "smProductionGroup";
		final String producctTechnologistIN = "smProducctTechnologist";
		final String styleStatusIN = "vrdStatus";

		// Declaring Boolean local variables as a flag or check.
		boolean isBrandsSelected = true;
		boolean isAgesSelected = true;
		boolean isProjectsSelected = true;
		boolean isPGsSelected = true;
		boolean isPTsSelected = true;
		boolean isSSsSelected = true;

		// Declaring String local variables. Which will be the output for
		// report.
		String seasonName;
		String productName;
		String colorwayName;
		String orderDestination;
		String bomName;
		String destination;
		String componentName;
		String materialName;
		String uom;
		String composition;
		String compositionRU;
		String coatinLamination;
		String finishing;
		String washCare;
		String additionCare;

		// Declaring Boolean local variables which will be part of report.
		boolean primary = false;
		boolean altPrimary = false;

		// Declaring double local variable. Which will be the output for report.
		double consumption;

		// This will be Excel workbook variable
		Workbook wb;

		// Declaring Row local variables. This will create all the output values
		// for reports.
		Row dataRow;

		// Declaring FlexType variables.
		FlexType productType = null;
		FlexType sourceType = null;
		FlexType productSeasonLinkType = null;
		FlexType APDproductType = null;
		FlexType materialType = null;
		FlexType materialFabricType = null;
		FlexType bomType = null;
		FlexType materialColorType = null;

		// Initializing all FlexTypes.
		try {
			productType = FlexTypeCache.getFlexTypeFromPath(LCSProperties.get("com.sportmaster.wc.reports.productType"));
			sourceType = FlexTypeCache.getFlexTypeFromPath(LCSProperties.get("com.sportmaster.wc.reports.sourceType"));
			productSeasonLinkType = FlexTypeCache.getFlexTypeRootByClass("com.lcs.wc.season.LCSProductSeasonLink");
			APDproductType = FlexTypeCache.getFlexTypeFromPath(LCSProperties.get("com.sportmaster.wc.reports.APDproductType"));
			materialType = FlexTypeCache.getFlexTypeFromPath(LCSProperties.get("com.sportmaster.wc.reports.materialType"));
			materialFabricType = FlexTypeCache.getFlexTypeFromPath(LCSProperties.get("com.sportmaster.wc.reports.materialFabricType"));
			bomType = FlexTypeCache.getFlexTypeFromPath(LCSProperties.get("com.sportmaster.wc.reports.bomType"));
			materialColorType = FlexTypeCache.getFlexTypeFromPath(LCSProperties.get("com.sportmaster.wc.reports.materialColorType"));
		} catch (WTException e1) {
			e1.printStackTrace();
		}

		// Checking for existence of the object. If no product selected this
		// will exit from this method.
		if (productBranchIds != null && productBranchIds.size() != 0) {
			LCSLog.debug("Product Ids are -->" + productBranchIds);
		} else {
			return;
		}

		/**
		 * Checking for other parameters selected or not from the Care Label
		 * Report page.
		 * 
		 * This includes: Brand, Age, Project, Production Group, Technician,
		 * Status-Style.
		 */
		if (brands == null || brands.size() == 0) {
			isBrandsSelected = false;
		}

		if (ages == null || ages.size() == 0) {
			isAgesSelected = false;
		}

		if (projects == null || projects.size() == 0) {
			isProjectsSelected = false;
		}

		if (PGs == null || PGs.size() == 0) {
			isPGsSelected = false;
		}

		if (PTS == null || PTS.size() == 0) {
			isPTsSelected = false;
		}

		if (SSS == null || SSS.size() == 0) {
			isSSsSelected = false;
		}

		/**
		 * Prepared Query Statement and writing the logic to get the desire
		 * result.
		 */
		PreparedQueryStatement statement = new PreparedQueryStatement();
		SearchResults results = null;

		try {
			statement.appendFromTable(LCSProduct.class);
			statement.appendFromTable(LCSProductSeasonLink.class);
			statement.appendFromTable(LCSSourcingConfig.class);
			statement.appendFromTable(LCSSourcingConfigMaster.class);
			statement.appendFromTable(FlexBOMPart.class);
			statement.appendFromTable(FlexSpecification.class);
			statement.appendFromTable(FlexSpecToComponentLink.class);

			statement.appendSelectColumn(new QueryColumn("LCSPRODUCT", "IDA2A2"));
			statement.appendSelectColumn(new QueryColumn("LCSSourcingConfig", "IDA2A2"));
			statement.appendSelectColumn(new QueryColumn("FLEXBOMPART", "IDA2A2"));

			statement.appendJoin(new QueryColumn("LCSPRODUCT", "BRANCHIDITERATIONINFO"), new QueryColumn("LCSProductSeasonLink", "PRODUCTAREVID"));
			statement.appendJoin(new QueryColumn("LCSPRODUCT", "BRANCHIDITERATIONINFO"), new QueryColumn("LCSSourcingConfig", "PRODUCTAREVID"));
			statement.appendJoin(new QueryColumn("LCSPRODUCT", "IDA3MASTERREFERENCE"), new QueryColumn("LCSSourcingConfigMaster", "IDA3A6"));
			statement.appendJoin(new QueryColumn("LCSSourcingConfigMaster", "IDA2A2"), new QueryColumn("FlexSpecification", "IDA3B12"));
			statement.appendJoin(new QueryColumn("LCSSourcingConfigMaster", "IDA2A2"), new QueryColumn("LCSSourcingConfig", "IDA3MASTERREFERENCE"));
			statement.appendJoin(new QueryColumn("FlexSpecification", "IDA3MASTERREFERENCE"), new QueryColumn("FlexSpecToComponentLink", "IDA3A4"));
			statement.appendJoin(new QueryColumn("FlexSpecToComponentLink", "IDA3B4"), new QueryColumn("FlexBOMPart", "IDA3MASTERREFERENCE"));

			statement.appendCriteria(new Criteria(new QueryColumn("LCSPRODUCT", "LATESTITERATIONINFO"), "1", Criteria.EQUALS));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("LCSProductSeasonLink", "EFFECTLATEST"), "1", Criteria.EQUALS));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("LCSProductSeasonLink", "EFFECTOUTDATE"), "", Criteria.IS_NULL));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("LCSSourcingConfig", "LATESTITERATIONINFO"), "1", Criteria.EQUALS));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("FlexSpecification", "LATESTITERATIONINFO"), "1", Criteria.EQUALS));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("FlexSpecification", "VERSIONIDA2VERSIONINFO"), "A", Criteria.EQUALS));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("FlexBOMPart", "LATESTITERATIONINFO"), "1", Criteria.EQUALS));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("FlexBOMPart", "VERSIONIDA2VERSIONINFO"), "A", Criteria.EQUALS));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("FlexSpecToComponentLink", "COMPONENTTYPE"), "BOM", Criteria.EQUALS));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("LCSPRODUCT", "statecheckoutinfo"), "wrk", Criteria.NOT_EQUAL_TO));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("FlexBOMPart", "statecheckoutinfo"), "wrk", Criteria.NOT_EQUAL_TO));
			statement.appendAnd();
			statement.appendCriteria(new Criteria(new QueryColumn("LCSSourcingConfig", "statecheckoutinfo"), "wrk", Criteria.NOT_EQUAL_TO));

			statement.appendInCriteria(new QueryColumn("LCSPRODUCT", "BRANCHIDITERATIONINFO"), productBranchIds);

			/**
			 * Based on selection from Care Label report page these all or some
			 * of these will get enabled and will be used in the query to filter
			 * out the results.
			 */
			if (isBrandsSelected) {
				statement.appendInCriteria(new QueryColumn("LCSPRODUCT", productType.getAttribute(brandIN).getColumnName()), brands);
			}
			if (isAgesSelected) {
				statement.appendInCriteria(new QueryColumn("LCSPRODUCT", productType.getAttribute(ageIN).getColumnName()), ages);
			}
			if (isProjectsSelected) {
				statement.appendInCriteria(new QueryColumn("LCSPRODUCT", APDproductType.getAttribute(projectIN).getColumnName()), projects);
			}
			if (isPGsSelected) {
				statement.appendInCriteria(new QueryColumn("LCSSourcingConfig", productSeasonLinkType.getAttribute(productionGroupIN).getColumnName()), PGs);
			}
			if (isPTsSelected) {
				statement.appendInCriteria(new QueryColumn("LCSSourcingConfig", productSeasonLinkType.getAttribute(producctTechnologistIN).getColumnName()), PTS);
			}
			if (isSSsSelected) {
				statement.appendInCriteria(new QueryColumn("LCSPRODUCT", productType.getAttribute(styleStatusIN).getColumnName()), SSS);
			}

			results = LCSQuery.runDirectQuery(statement);

		} catch (WTException e) {
			e.printStackTrace();
		}

		wb = new HSSFWorkbook();
		Sheet sheet1 = wb.createSheet("Care Label Report");

		Row excelHeader = sheet1.createRow(0);
		List<String> header = getTheList(reportHeader);
		Iterator<String> headerIterator = header.iterator();
		int hCell = 0;
		while (headerIterator.hasNext()) {
			excelHeader.createCell(hCell).setCellValue(headerIterator.next());
			hCell++;
		}

		if (results != null) {
			seasonName = season.getName();
			FlexTypeAttribute componentAttribute = null;
			FlexTypeAttribute orderDestinationAttribute = null;
			FlexTypeAttribute uomAttributes = null;
			FlexTypeAttribute compositionAttributes = null;
			FlexTypeAttribute compositionRUAttributes = null;
			FlexTypeAttribute careLabelAttributes = null;
			FlexTypeAttribute coatingLaminationAttributes = null;
			FlexTypeAttribute finishingAttributes = null;
			FlexTypeAttribute addiCareAttributes = null;

			try {
				componentAttribute = bomType.getAttribute(componentNameIN);
				orderDestinationAttribute = sourceType.getAttribute(OrderDestinationIN);
				uomAttributes = materialType.getAttribute(UOMIN);
				careLabelAttributes = materialType.getAttribute(washCareIN);
				compositionAttributes = materialType.getAttribute(compositionIN);
				compositionRUAttributes = materialFabricType.getAttribute(compositionRUIN);
				coatingLaminationAttributes = materialFabricType.getAttribute(laminationCoatingIN);
				finishingAttributes = materialFabricType.getAttribute(finishIN);
				addiCareAttributes = materialColorType.getAttribute(additionalCareIN);
			} catch (WTException e1) {
				e1.printStackTrace();
			}

			List<?> data = results.getResults();
			if (data.size() > 0) {
				int i = 1;
				for (Object obj : data) {
					LCSProduct prod = null;
					LCSSourcingConfig source = null;
					FlexBOMPart fbp = null;
					FlexObject fo = (FlexObject) obj;
					try {
						prod = (LCSProduct) LCSQuery.findObjectById("OR:com.lcs.wc.product.LCSProduct:" + fo.getData("LCSPRODUCT.IDA2A2"));
						source = (LCSSourcingConfig) LCSQuery.findObjectById("OR:com.lcs.wc.sourcing.LCSSourcingConfig:" + fo.getData("LCSSOURCINGCONFIG.IDA2A2"));
						fbp = (FlexBOMPart) LCSQuery.findObjectById("OR:com.lcs.wc.flexbom.FlexBOMPart:" + fo.getData("FlexBOMPart.IDA2A2"));

						productName = prod.getName();
						colorwayName = "";
						if (orderDestinationAttribute != null) {
							orderDestination = orderDestinationAttribute.getDisplayValue((String) source.getValue(orderDestinationIN));
						} else {
							orderDestination = null;
						}
						bomName = fbp.getName();

						Collection<FlexBOMLink> bomlinks = LCSFlexBOMQuery.getAllFlexBOMLinks(fbp, null, null, null, null, null);

						Iterator<FlexBOMLink> itr = bomlinks.iterator();
						while (itr.hasNext()) {

							compositionRU = null;
							componentName = null;
							materialName = null;
							primary = false;
							altPrimary = false;
							consumption = 0;
							destination = null;
							uom = null;
							composition = null;
							coatinLamination = null;
							finishing = null;
							washCare = null;

							FlexBOMLink bomLink = (FlexBOMLink) itr.next();
							if (bomLink.getValue(primaryIN) != null) {
								primary = (Boolean) bomLink.getValue(primaryIN);
							}
							if (bomLink.getValue(AltPrimaryIN) != null) {
								altPrimary = (Boolean) bomLink.getValue(AltPrimaryIN);
							}
							if (primary == true || altPrimary == true) {
								dataRow = sheet1.createRow(i);
								LCSMaterialMaster materialMaster = bomLink.getChild();
								LCSMaterial material = (LCSMaterial) VersionHelper.latestIterationOf(materialMaster);

								// Getting Desination
								FlexSpecDestination flexDestination = bomLink.getDestinationDimension();

								if (flexDestination != null && flexDestination.toString().contains("com.lcs.wc.product.ProductDestination")) {
									ProductDestination pd = (ProductDestination) LCSQuery.findObjectById("OR:" + flexDestination);
									destination = pd.getName();
								}

								if (destination != null) {
									bomLink = LCSFlexBOMQuery.findTopLevelBranch(bomLink);
									materialMaster = bomLink.getChild();
									material = (LCSMaterial) VersionHelper.latestIterationOf(materialMaster);

								}

								uom = uomAttributes.getDisplayValue((String) material.getValue(UOMIN));
								composition = compositionAttributes.getDisplayValue((String) material.getValue(compositionIN));

								washCare = careLabelAttributes.getDisplayValue((String) material.getValue(washCareIN));
								if (material.getFlexType().isAncestorType(materialFabricType)) {
									compositionRU = compositionRUAttributes.getDisplayValue((String) material.getValue(compositionRUIN));
									coatinLamination = coatingLaminationAttributes.getDisplayValue((String) material.getValue(laminationCoatingIN));
									finishing = finishingAttributes.getDisplayValue((String) material.getValue(finishIN));
								}
								washCare = washCare.replaceAll("<br>", ",");

								materialName = (String) bomLink.getValue(materialDescriptionIN);
								componentName = componentAttribute.getDisplayValue((String) bomLink.getValue(componentNameIN));
								consumption = (Double) bomLink.getValue(quantityIN);

								LCSMaterialColor materialColor = bomLink.getMaterialColor();

								if (materialColor != null) {
									additionCare = addiCareAttributes.getDisplayValue((String) materialColor.getValue(additionalCareIN));
								} else {
									additionCare = null;
								}

								dataRow.createCell(0).setCellValue(seasonName);
								dataRow.createCell(1).setCellValue(productName);
								dataRow.createCell(2).setCellValue(colorwayName);
								dataRow.createCell(3).setCellValue(orderDestination);
								dataRow.createCell(4).setCellValue(bomName);
								dataRow.createCell(5).setCellValue(destination);
								dataRow.createCell(6).setCellValue(componentName);
								dataRow.createCell(7).setCellValue(materialName);
								dataRow.createCell(8).setCellValue(primary);
								dataRow.createCell(9).setCellValue(altPrimary);
								if (consumption == 0) {
									dataRow.createCell(10).setCellValue("");
								} else {
									dataRow.createCell(10).setCellValue(consumption);
								}
								dataRow.createCell(11).setCellValue(uom);
								dataRow.createCell(12).setCellValue(composition);
								dataRow.createCell(13).setCellValue(compositionRU);
								dataRow.createCell(14).setCellValue(coatinLamination);
								dataRow.createCell(15).setCellValue(finishing);
								dataRow.createCell(16).setCellValue(washCare);
								dataRow.createCell(17).setCellValue(additionCare);
								i++;
							}
						}

					} catch (WTException e) {
						e.printStackTrace();
					}

				}
			}
		} else {
		}

		try {
			FileOutputStream fout = new FileOutputStream(new File(LCSProperties.get("com.sportmaster.wc.reports.materialColorType")));
			LCSLog.debug("Report Generated at -> " + fout.getFD());
			wb.write(fout);
			fout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
