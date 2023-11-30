
package com.custom.wc.exportimport;

import java.io.PrintWriter;

/**
 * Tools to write out an XML file from a FlexObject.
 * 
 * @author David Boissey
 */
    public class ExportFile {

        public String fileName;
//        public PrintWriter xmlStream;
        public int recordCount;
        public String xmlTag;
        public String outputBuffer = "";

        /**
         * Close the File.
         * @author David Boissey
         * @throws Exception 
         */
//        public void close() throws Exception {
//            if (this.recordCount > 0) {
//                //this.writeXmlFile("</" + this.xmlTag + ">");
//                this.xmlStream.close();
//            }
//
//        }
/**
 * Add the closing XML tag.
 * @author David Boissey
 * @throws Exception 
 */
        public void closeTag() throws Exception {
            this.writeXmlFile("\n</" + this.xmlTag + ">");
        }
/**
 * Write the XML record.
 * @author David Boissey
 * @param xmlData
 * @throws Exception 
 */
        public void writeXmlFile(String xmlData) throws Exception {
            //this.xmlStream.println(xmlData);
            this.outputBuffer += xmlData;
        }
/**
 * Initialize the XML file with the xml tag.
 * @author David Boissey
 * @throws Exception 
 */
        public void initXmlFile() throws Exception {
            this.writeXmlFile("<?xml version=\"1.0\" encoding=\"UTF-16\"?>\n<" + this.xmlTag + ">");
        }
/**
 * Move the FlexObject string to the file.
 * @author David Boissey
 * @param xmlData
 * @throws Exception 
 */
        public void FlexObjectToXmlFile(String xmlData) throws Exception {

            if (this.recordCount == 0) {
                this.initXmlFile();
            }
            xmlData = deleteXmlTagLine(xmlData);
            this.writeXmlFile(xmlData);
            this.recordCount++;
        }

    /**
     * Remove the xml tag so it doesn't interfere with the first one in the file.
     * @author David Boissey
     * @param xmlData
     * @return
     * @throws Exception 
     */
     public  String deleteXmlTagLine(String xmlData) throws Exception {
        //int startPoint = xmlData.indexOf("<COLLECTION>") - 1;

        return xmlData.substring(39 /* startPoint */);
    }

    }
