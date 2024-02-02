package com.custom.wc.exportimport;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.regexp.*;

import com.lcs.wc.carewash.*;
import com.lcs.wc.util.*;
import com.lcs.wc.client.*;
import com.lcs.wc.db.*;
import com.lcs.wc.flextype.*;
import com.lcs.wc.foundation.*;
import com.lcs.wc.moa.*;

import wt.fc.*;
import wt.introspection.*;
import wt.method.*;
import wt.util.*;
/**
 * writer class for XML
 * @Class name HBIXMLWriter.java
 * @author Dushyant Pathak Created on 11/19/2013
 *
 */
public class HBIXMLWriter extends XMLWriter {

    private static final String TAG_OBJECT                  = "object";
    private static final String TAG_ATTRIBUTE               = "flexAttribute";
    
    private static final String TAG_VALUE                   = "value";
    private static final String TAG_DISPLAYVALUE            = "displayValue";

    private static final String TAG_USERID                  = "webId";
    private static final String TAG_USEREMAIL               = "emailAddress";
    private static final String TAG_USERFULLNAME            = "fullName";
    private static final String TAG_COLORINHEX              = "colorInHex";
    
    private static final String XML_ENCODING       = XMLHelper.DEFAULT_ENCODING;
    private Object obj = null;
    private Document xmlDoc        = null;
    private boolean expandReferences = false;
    private boolean includeMetaData = false;
    private Element rootObject = null;
    private Element parentItem = null;
    private String   xmlStr        = null;
    private String  scope = null;
    
    
    public HBIXMLWriter() {
    }
	public Document getXMLDoc() {
     return xmlDoc;
   }
   
    public void toXml(Object obj, boolean expandReferences, Document xmlDoc, Element parentItem,boolean includeMetaData) {


        this.obj = obj;


        if (xmlDoc != null) {
            this.xmlDoc = xmlDoc;
        }
        this.rootObject = parentItem;
        this.expandReferences = expandReferences;
		this.includeMetaData = includeMetaData;

        this.generateXMLDocument();
    }
   private void generateXMLDocument() {

        if (this.obj instanceof FlexTyped) {
            flextypedToXml();
        } 
    }


    static boolean isReferenceAttr(FlexTypeAttribute attr)
    {
        return HBIXMLHelper.OBJECT_REF.equals(attr.getAttVariableType()) || 
            HBIXMLHelper.OBJECT_REF_LIST.equals(attr.getAttVariableType());
    }
    private void flextypedToXml() {
        FlexTyped obj1 = (FlexTyped)obj;
        Element objItem = null;
        Element attributeItem = null;

        try {
            // Create the root element
            objItem = xmlDoc.createElement(TAG_OBJECT);
            if (obj1.getFlexType() != null){
                objItem.setAttribute("flexTypePath", obj1.getFlexType().getFullName(true));
            } else {
                objItem.setAttribute("flexTypePath", "NA");
            }
            objItem.setAttribute("objClass", obj1.getClass().getName());
            
            
            FlexType type = obj1.getFlexType();
            Collection<FlexTypeAttribute> keyAttributes = type.getAllAttributesUsedBy(obj1);
            Iterator<FlexTypeAttribute> keys = keyAttributes.iterator();
		//	System.out.println("keyAttributes---------->"+keyAttributes.size());
            FlexTypeAttribute att = null;
            while(keys.hasNext()){
                att = (FlexTypeAttribute) keys.next();
				
				
				
				//Added check for hidden attribtues for fixing memory issues	
               	if( !att.isAttEnabled() || 
                    !ACLHelper.hasViewAccess(att)){
                    continue;
                }

                String tag = TAG_ATTRIBUTE;
                
				attributeItem = xmlDoc.createElement(tag);

                attributeItem.setAttribute("type", att.getAttVariableType());
                attributeItem.setAttribute("key", att.getAttKey());
                attributeItem.setAttribute("display", att.getAttDisplay());
                if (isReferenceAttr(att)) {

                    attributeItem = renderObjectReference(obj1, att, attributeItem);

                } else if ("multiobject".equals(att.getAttVariableType()) ||
                        "iteratedmultiobject".equals(att.getAttVariableType()) ||
                        "discussion".equals(att.getAttVariableType()) 
                        ) {

                    attributeItem = renderMOA(obj1, att, attributeItem);

                } else if ("date".equals(att.getAttVariableType())) {

                    attributeItem = renderDate(obj1, att, attributeItem);

                } else if ("careWashImages".equals(att.getAttVariableType())) {

                    attributeItem = renderCarewashImage(obj1, att, attributeItem);

                } else if ("url".equals(att.getAttVariableType())) {

                    attributeItem = renderUrl(obj1, att, attributeItem);

                } else if ("userList".equals(att.getAttVariableType())) {

                    attributeItem = renderUserList(obj1, att, attributeItem);

                } else if ("colorSelect".equals(att.getAttVariableType())) {

                    attributeItem = renderColorSelect(obj1, att, attributeItem);

                } else if ("image".equals(att.getAttVariableType())) {

                    attributeItem = renderImage(obj1, att, attributeItem);

                } else if (att.isComplex()) {

                    if ("moaList".equals(att.getAttVariableType())) {

                        attributeItem = renderMoaList(obj1, att, attributeItem);

                    } else if ("composite".equals(att.getAttVariableType())) {

                        attributeItem = renderComposite(obj1, att, attributeItem);

                    } else {

                        attributeItem = renderMoaEntry(obj1, att, attributeItem);
                    }

                } else if ("choice".equals(att.getAttVariableType())) {

                    attributeItem = renderChoice(obj1, att, attributeItem);

                } else {
		            attributeItem = renderAttribute(obj1, att, attributeItem);

                }
                objItem.appendChild(attributeItem);

            }
			if(includeMetaData){
				objItem = wcMetaDataToXml(objItem);
			}
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (parentItem != null) {
            parentItem.appendChild(objItem);
        } else {
            this.rootObject.appendChild(objItem);
        }
    }
    private Element wcMetaDataToXml(Element root) throws Exception {
        Element objItem = root;
 

        Class objClass = this.obj.getClass();

        ClassInfo classinfo = WTIntrospector.getClassInfo(objClass);

        ColumnDescriptor colDescriptors[] = classinfo.getDatabaseInfo().getBaseTableInfo().getColumnDescriptors();
        String tableName = classinfo.getDatabaseInfo().getBaseTableInfo().getTablename();

        HashMap<String, String> columnMap = new HashMap<String, String>();
        QueryStatement statement = new QueryStatement();
        statement.appendFromTable(tableName);
		statement.addPossibleSearchCriteria(tableName,"IDA2A2",FormatHelper.getNumericObjectIdFromObject((WTObject)this.obj)) ;
        RE pattern = new RE("(^att|^num|^date)[0-9]*[0-9]");
        for(int i = 0; i < colDescriptors.length; i++){
            String columnName = colDescriptors[i].getColumnName();
            String attributeName = colDescriptors[i].getName();
            if (! pattern.match(attributeName)) {

                if (attributeName.startsWith("blob$")) {
                    continue;
                }
                columnMap.put(columnName,attributeName);

                statement.appendSelectColumn(tableName, columnName);
            }
        }

        SearchResults results = LCSQuery.runDirectQuery(statement);

        Iterator o = results.getResults().iterator();
        if (o.hasNext()) {
            FlexObject fo = (FlexObject)o.next();

            for(int i = 0; i < colDescriptors.length; i++){
                String columnName = colDescriptors[i].getColumnName();
                String attributeName = colDescriptors[i].getName();

                if (! pattern.match(attributeName)) {
                    if (attributeName.startsWith("blob$")) {
                        continue;
                    }
                    String value = "";
                    if (colDescriptors[i].getJavaType().equals("boolean")) {
                        value = (String)fo.get(tableName+"."+columnName);
                        if("1".equals(value) || "true".equals(value)){
                            value = "true";
                        } else {
                            value = "false";
                        }
                    } else {
                        value = (String)fo.get(tableName+"."+columnName);
                    }
                    if (columnName.endsWith("IsNull")) {
                        objItem.appendChild(createAttributeItem(columnName, value, FormatHelper.STRING_FORMAT));
                    } else {
                        objItem.appendChild(createAttributeItem((String)columnMap.get(columnName), value, FormatHelper.STRING_FORMAT));
                    }

                }
            }
        }


        return objItem;

    }

    public Element createAttributeItem(String tagname, String value, String format) {
        if (FormatHelper.INT_FORMAT.equals(format) && FormatHelper.hasContent(value)) {
            format = FormatHelper.STRING_FORMAT;
            if (value.indexOf(',') > -1) {
                value = value.replaceAll(",", "");
            }
            int i = (int)Double.parseDouble(value);
            value = i == 0 ? "" : String.valueOf(i);
        }
        return createAttributeItem(tagname, value, format, false);
    }
    public Element createAttributeItem(String tagname, String value, String format, boolean allowZero) {
        return createAttributeItem(tagname, value, format, allowZero, this.xmlDoc);
    }
    private Element createAttributeItem(String tagname, String value, String format, boolean allowZero, Document document) {

        Element attributeItem = document.createElement(tagname);
        String formattedValue = value;

        if (allowZero) {
            if (FormatHelper.hasContentAllowZero(format)) {
                formattedValue = FormatHelper.applyFormat(value, format);
            }
            if (FormatHelper.hasContentAllowZero(formattedValue)) {
                attributeItem.appendChild(document.createTextNode(formattedValue));
            }
        } else {
            if (FormatHelper.hasContent(format)) {
                formattedValue = FormatHelper.applyFormat(value, format);
            }
            if (FormatHelper.hasContent(formattedValue)) {
                attributeItem.appendChild(document.createTextNode(formattedValue));
            }
        }
        return attributeItem;

    }
    public Element createAttributeItem(String tagname, Date value, String format) {

        Element attributeItem = xmlDoc.createElement(tagname);
        String formattedValue = WTStandardDateFormat.format(value,
                    WTStandardDateFormat.LONG_STANDARD_DATE_FORMAT_MINUS_TIME);
        if (FormatHelper.hasContent(formattedValue)) {
            attributeItem.appendChild(xmlDoc.createTextNode(formattedValue));
        }

        return attributeItem;
    }

    public Element createAttributeItem(String tagname, boolean value, String format) {
        Element attributeItem = xmlDoc.createElement(tagname);
        attributeItem.appendChild(xmlDoc.createTextNode("" + value));

        return attributeItem;
    }

    public Element createAttributeItem(String tagname, float value, String format) {
        Element attributeItem = xmlDoc.createElement(tagname);
        String formattedValue = null;
        if (FormatHelper.INT_FORMAT.equals(format) && value != 0f) {
            formattedValue = String.valueOf((int)value);
        } else {
            formattedValue = String.valueOf(value);
        }
        if (FormatHelper.hasContent(formattedValue)) {
            attributeItem.appendChild(xmlDoc.createTextNode(formattedValue));
        }

        return attributeItem;
    }
    private void generateXMLString() {
        this.xmlStr = createXMLString(xmlDoc);
    }
	public String getXMLString() {
		 this.generateXMLString();
		return xmlStr;
	}

    public String createXMLString(Document xmlDoc) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult sr = new StreamResult(baos);
        TransformerFactory tf = TransformerFactory.newInstance();
        DOMSource source = new DOMSource(xmlDoc);
        String str = null;
        try {
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, XML_ENCODING);
            transformer.transform(source, sr);
            byte[] byteArray = baos.toByteArray();
            str = new String(byteArray, XML_ENCODING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
    private Element renderObjectReference(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderObjectReference(obj1, att, attributeItem, null, false);
    }
    private Element renderObjectReference(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException{

        ForiegnKeyDefinition fkDef = att.getRefDefinition();
        if(fkDef.getFlexTypedDisplay() != null) {

            FlexType fkType = FlexTypeCache.getFlexTypeRootByClass(fkDef.getFlexTypeClass());
            attributeItem.setAttribute("flexTypePath", fkType.getFullName(true));
            attributeItem.setAttribute("objClass", fkType.getTypeClass());
//Added for fixing memory issues			
			this.expandReferences=false;
//End
            String currentId = "";
            if (override) {
                currentId = ovrStringValue;
            } else {
                currentId = att.getStringValue(obj1);
            }

            if ("0".equals(currentId)) {
                currentId = "";
            }

            String display = "";
            if (FormatHelper.hasContent(currentId)) {
                WTObject obj2 = null;
                obj2 = (WTObject) FlexTypeQuery.findObjectById(fkDef.getIdPrefix() + currentId);
                if(obj instanceof wt.vc.Mastered){
                    obj2 = VersionHelper.latestIterationOf((wt.vc.Mastered)obj2);
                }

                if(fkDef.getFlexTypedDisplay() != null){
                    display = "" + ((FlexTyped)obj2).getValue(fkDef.getFlexTypedDisplay());
                }

                attributeItem.appendChild(createAttributeItem(TAG_VALUE, currentId, FormatHelper.STRING_FORMAT));
                attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, display, FormatHelper.STRING_FORMAT));
                if (this.expandReferences) {
                    HBIXMLWriter xmlWriter= new HBIXMLWriter();
                    xmlWriter.toXml(obj2, this.expandReferences, this.xmlDoc, attributeItem,this.includeMetaData);
                }
            }
        }
        return attributeItem;
    }
    private Element renderMOA(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException {
        attributeItem.setAttribute("flexTypePath", att.getRefType().getFullName(true));
        attributeItem.setAttribute("objClass", att.getRefType().getTypeClass());

        if (this.expandReferences) {
            Iterator moaItems = LCSMOAObjectQuery.findMOACollection((WTObject)obj1, att).iterator();
            LCSMOAObject moa = null;
            while(moaItems.hasNext()){
                moa = (LCSMOAObject) moaItems.next();
                HBIXMLWriter xmlWriter= new HBIXMLWriter();
                xmlWriter.toXml((Object)moa, this.expandReferences, this.xmlDoc, attributeItem,this.includeMetaData);
            }
        }
        return attributeItem;
    }
    private Element renderDate(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderDate(obj1, att, attributeItem, null, false);
    }
    private Element renderDate(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
        attributeItem.setAttribute("dateFormat", "YYYY/MM/DD");

        String stringValue = "";
        String display = "";
        if (override) {
            stringValue = ovrStringValue;
            display = ovrStringValue;
        } else {
            stringValue = att.getStringValue(obj1);
            display = att.getDisplayValue(obj1);
        }
        attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
        attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, display, FormatHelper.STRING_FORMAT));
        return attributeItem;
    }
    private Element renderCarewashImage(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderCarewashImage(obj1, att, attributeItem, null, false);
    }
    private Element renderCarewashImage(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {

        String stringValue = "";
        if (override) {
            stringValue = ovrStringValue;
        } else {
            stringValue = att.getStringValue(obj1);
        }

        if (FormatHelper.hasContent(stringValue)) {
            String locale = "";
            try{
         	   locale = com.lcs.wc.client.ClientContext.getContext().getLocale().toString();
            }catch(wt.util.WTException we){
         	   locale = "";
            }
            Iterator carewashOrder = CareWashManager.getSymbolOrder().iterator();
            String symbolKey = "";
            String carewashText = "";
            while (carewashOrder.hasNext()) {
                symbolKey = (String) carewashOrder.next();

                carewashText = carewashText + "\n" + CareWashManager.getText(symbolKey, locale, "" + stringValue.charAt(CareWashManager.getIndex(symbolKey)));
            }

            attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
            attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, carewashText, null));

            carewashOrder = CareWashManager.getSymbolOrder().iterator();
            symbolKey = "";
            while (carewashOrder.hasNext()) {
                symbolKey = (String) carewashOrder.next();

                String carewashUrl = CareWashManager.getURL(symbolKey, locale, "" + stringValue.charAt(CareWashManager.getIndex(symbolKey)));
                attributeItem.appendChild(createAttributeItem(symbolKey+"Image", carewashUrl, FormatHelper.STRING_FORMAT));
                carewashText = CareWashManager.getText(symbolKey, locale, "" + stringValue.charAt(CareWashManager.getIndex(symbolKey)));
                attributeItem.appendChild(createAttributeItem(symbolKey+"Text", carewashText, FormatHelper.STRING_FORMAT));
            }
        }
        return attributeItem;
    }

	private Element renderUrl(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderUrl(obj1, att, attributeItem, null, false);
    }
    private Element renderUrl(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
        String stringValue = "";
        if (override) {
            stringValue = ovrStringValue;
        } else {
            stringValue = att.getStringValue(obj1);
        }
    
		attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
        attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, stringValue, FormatHelper.STRING_FORMAT));
        return attributeItem;
    }

	private Element renderUserList(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderUserList(obj1, att, attributeItem, null, false);
    }
    private Element renderUserList(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
	    String deleteParensLabel = WTMessage.getLocalizedMessage( RB.USER, "deletedParens_LBL" , RB.objA );

        attributeItem.setAttribute("flexTypePath", "N/A");
        attributeItem.setAttribute("objClass", "wt.org.WTUser");

        String stringValue = "";
        if (override) {
            stringValue = ovrStringValue;
        } else {
            stringValue = att.getStringValue(obj1);
        }

        if(FormatHelper.hasContent(stringValue)){
            FlexObject user = UserCache.getUser(stringValue);
			if(user != null) {
			   if(user.getBoolean(UserCache.DELETED)) {
				  user.put(UserCache.NAME, user.getString(UserCache.NAME) + deleteParensLabel );
			   }
			}else {
			   user = new FlexObject();
			   user.put(UserCache.NAME, deleteParensLabel );
			}

            attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
            attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, user.getString(UserCache.NAME), FormatHelper.STRING_FORMAT));
            attributeItem.appendChild(createAttributeItem(TAG_USERID, user.getString(UserCache.WEBID), FormatHelper.STRING_FORMAT));
            attributeItem.appendChild(createAttributeItem(TAG_USEREMAIL, user.getString(UserCache.EMAIL), FormatHelper.STRING_FORMAT));
            attributeItem.appendChild(createAttributeItem(TAG_USERFULLNAME, user.getString(UserCache.NAME), FormatHelper.STRING_FORMAT));
        }
        return attributeItem;
    }
    private Element renderColorSelect(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderColorSelect(obj1, att, attributeItem, null, false);
    }
    private Element renderColorSelect(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
        String stringValue = "";
        String display = "";
        if (override) {
            stringValue = ovrStringValue;
            display = stringValue;
        } else {
            stringValue = att.getStringValue(obj1);
            display = att.getDisplayValue(obj1);
        }
        attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
        attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, display, FormatHelper.STRING_FORMAT));

        AttributeValueList list = att.getAttValueList();
        String hexColor = list.get(stringValue, AttributeValueList.COLORINHEX);
        attributeItem.appendChild(createAttributeItem(TAG_COLORINHEX, hexColor, FormatHelper.STRING_FORMAT));
        return attributeItem;
    }
    private Element renderImage(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderImage(obj1, att, attributeItem, null, false);
    }
    private Element renderImage(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
        String stringValue = "";
        String display= "";
        if (override) {
            stringValue = ovrStringValue;
            display = stringValue;
        } else {
            stringValue = att.getStringValue(obj1);
            display = stringValue;
        }
        attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
        attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, display, FormatHelper.STRING_FORMAT));
        return attributeItem;
    }
    private Element renderMoaList(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderMoaList(obj1, att, attributeItem, null, false);
    }
    private Element renderMoaList(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
        String stringValue = "";
        if (override) {
            stringValue = ovrStringValue;
        } else {
            stringValue = att.getStringValue(obj1);
        }
        attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
        String display = "";
        display = MOAHelper.parseOutDelimsLocalized(stringValue, true, att.getAttValueList(), ClientContext.getContext().getLocale());
        attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, display, FormatHelper.STRING_FORMAT));
        return attributeItem;
    }
    private Element renderComposite(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderComposite(obj1, att, attributeItem, null, false);
    }
    private Element renderComposite(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
        String stringValue = "";
        if (override) {
            stringValue = ovrStringValue;
        } else {
            stringValue = att.getStringValue(obj1);
        }

        AttributeValueList list = att.getAttValueList();
        String value = MOAHelper.parseOutDelims(stringValue, true);
        StringTokenizer parser = new StringTokenizer(value, ",");
        String token;
        String per;
        String item;
        String hold = "";
        while(parser.hasMoreElements()){
            token = parser.nextToken();
            int index = token.indexOf("%");
            per = token.substring(0, index + 1);
            item = token.substring(index + 1).trim();
            try{
                item = list.getValue(item, ClientContext.getContext().getLocale());
            } catch(LCSException e) {
                e.printStackTrace();
            }

            if(parser.hasMoreElements()){
                hold = hold + per + " " + item + ",";
            } else {
                hold = hold + per + " " + item;
            }
        }

        String display = hold;
        attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
        attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, display, FormatHelper.STRING_FORMAT));
        return attributeItem;
    }
    private Element renderMoaEntry(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderMoaEntry(obj1, att, attributeItem, null, false);
    }
    private Element renderMoaEntry(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
        String stringValue = "";
        if (override){
            stringValue = ovrStringValue;
        } else {
            stringValue = att.getStringValue(obj1);
        }
        attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));

        String display = MOAHelper.parseOutDelimsLocalized(stringValue, true, att.getAttValueList(), ClientContext.getContext().getLocale());
        attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, display, FormatHelper.STRING_FORMAT));
        return attributeItem;
    }
    private Element renderChoice(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderChoice(obj1, att, attributeItem, null, false);
    }
    private Element renderChoice(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
        String stringValue = "";
        String display = "";
        if (override){
            stringValue = ovrStringValue;
            display = att.getAttValueList().getValue(stringValue, ClientContext.getContext().getLocale());
        } else {
            stringValue = att.getStringValue(obj1);
            display = att.getDisplayValue(obj1);
        }

        attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
        attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, display, FormatHelper.STRING_FORMAT));
        return attributeItem;
    }
    private Element renderAttribute(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem) throws WTException{
        return renderAttribute(obj1, att, attributeItem, null, false);
    }
    private Element renderAttribute(FlexTyped obj1, FlexTypeAttribute att, Element attributeItem, String ovrStringValue, boolean override) throws WTException {
        String stringValue = "";
        String display = "";
        if (override){
            stringValue = ovrStringValue;
            display = FormatHelper.format(stringValue);
        } else {
            stringValue = att.getStringValue(obj1);
            display = att.getDisplayValue(obj1);
        }

        attributeItem.appendChild(createAttributeItem(TAG_VALUE, stringValue, FormatHelper.STRING_FORMAT));
        attributeItem.appendChild(createAttributeItem(TAG_DISPLAYVALUE, display, FormatHelper.STRING_FORMAT));
        return attributeItem;
    }
    
}
