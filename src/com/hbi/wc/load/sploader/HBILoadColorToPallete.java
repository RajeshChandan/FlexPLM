package com.hbi.wc.load.sploader;

import com.lcs.wc.color.LCSColor;
import com.lcs.wc.color.LCSColorHelper;
import com.lcs.wc.color.LCSColorLogic;
import com.lcs.wc.color.LCSPalette;
import com.lcs.wc.color.LCSPaletteLogic;
import com.lcs.wc.color.LCSPaletteQuery;
import com.lcs.wc.load.LoadCommon;
import com.lcs.wc.util.FormatHelper;
import com.lcs.wc.util.LCSProperties;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.fc.WTReference;
import wt.util.WTException;

public class HBILoadColorToPallete {
	protected static LCSPaletteLogic PALETTE_LOGIC = new LCSPaletteLogic();
	protected static LCSColorLogic COLOR_LOGIC = new LCSColorLogic();
	public static final String imageURL = LCSProperties.get("com.lcs.wc.content.imageURL", "/Windchill/images");
	private static final int addMaterialOrColorFromParentPaletteMode = LCSProperties
			.get("com.lcs.wc.color.LCSPaletteLogic.addMaterialOrColorFromParentPalette.mode", 0);

	public static boolean addColorToPalette(Hashtable<String, Object> dataValues, Hashtable<String, Object> commandLine,
			Vector<?> returnObjects) {
		return addColorToPalette(dataValues, LoadCommon.getValue(commandLine, "FileName", false));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static boolean addColorToPalette(Hashtable<String, Object> dataValues, String fileName) {
		try {
			LCSPalette palette = (LCSPalette) LoadCommon.searchForObject(fileName, "Palette", dataValues);
			String paletteOid = FormatHelper.getObjectId(palette);
			if (palette == null) {
				return false;
			} else {
				LCSColor color = (LCSColor) LoadCommon.searchForObject(fileName, "Color", dataValues);

				if (color == null) {
					return false;
				} else {
					LoadCommon.display("Adding LCSColor To LCSPalette With Values '" + dataValues + " ...");
					LoadCommon.display(
							"addMaterialOrColorFromParentPaletteMode=" + addMaterialOrColorFromParentPaletteMode);

					if (2 == addMaterialOrColorFromParentPaletteMode) {

						if (palette != null) {
							LoadCommon.display(
									"#############Begining of calling LCSPaletteQuery.findParentPalettes###################");
							LoadCommon.display("findParentPalettes by childPaletteId: " + paletteOid);
							Collection<String> parentPalettesIdColl = LCSPaletteQuery.findParentPalettes(paletteOid);
							parentPalettesIdColl.add(FormatHelper.getObjectId(palette));
							LoadCommon.display("parentPalettesIdList" + parentPalettesIdColl);
							LoadCommon.display(
									"#############End of calling LCSPaletteQuery.findParentPalettes###################");

							Iterator parentPalettesIt = parentPalettesIdColl.iterator();

							while (parentPalettesIt.hasNext()) {
								String parentPallentId = (String) parentPalettesIt.next();
								
								LCSColorHelper.service.addColorToPalette(color,(LCSPalette) findByOid(parentPallentId));
								LoadCommon.display("Completed adding color to parentPallet" + parentPallentId);
								
							}
						}
					} else {
						palette = (LCSPalette) findByOid(paletteOid);
						LCSColorHelper.service.addColorToPalette(color, palette);
					}
					// LCSPaletteToColorLink paletteToColor =
					// PALETTE_LOGIC.addColorToPalette(color, palette);

					// LoadCommon.putCache(fileName, "CURRENT_FLEXTYPED",
					// paletteToColor);
					return true;
				}
			}
		} catch (WTException var5) {
			LoadCommon.display("\n#WTException : " + var5.getLocalizedMessage());
			return false;
		}
	}

	/**
	 * @param oid
	 * @return
	 * @throws WTException
	 */
	public static WTObject findByOid(String oid) throws WTException {
		ReferenceFactory palette = new ReferenceFactory();
		WTReference ref = palette.getReference(oid);
		return (WTObject) ref.getObject();
	}
}