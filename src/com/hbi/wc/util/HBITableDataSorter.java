package com.hbi.wc.util;

import com.lcs.wc.client.web.TableData;
import com.lcs.wc.util.LCSProperties;
import java.io.PrintStream;
import java.util.*;
import com.lcs.wc.util.FormatHelper;


public class HBITableDataSorter implements Comparator 
{

    Hashtable valsets;
    Collection sortedCollection;
	Hashtable specificSortingOrder = null;
	Collection numericSort;
	public boolean floatSort;

    public HBITableDataSorter() {
        valsets = new Hashtable();
        sortedCollection = new Vector();		
    }
	
	public HBITableDataSorter(Hashtable sortOrder) {
        valsets = new Hashtable();
        sortedCollection = new Vector();
		specificSortingOrder = sortOrder;		
    }	

    public HBITableDataSorter(boolean flag)
    {
        floatSort = flag;
    }

    public int compare(Object obj, Object obj1)
    {
        boolean flag = false;
		if(obj instanceof java.lang.String && obj1 instanceof java.lang.String) {
			String s = (String) obj;
			String s1 = (String) obj1;
			if(floatSort) {
				Float float1 = new Float(FormatHelper.parseFloat(s));			
				Float float2 = new Float(FormatHelper.parseFloat(s1));
				if(float1 == null && float2 != null) {
					return -1;
				}
				if(float2 == null && float1 != null) {
					return 1;
				}
				if(float1 == null && float2 == null) {
					return 0;
				} else	{
					int i = float1.compareTo(float2);
					return i;
				}
			} else {
				//future use
				return 0;
			}
		} else {
			//future use
			return 0;
		}
    }
	
	public void setNumericSort(Collection numberSort) {
		numericSort = numberSort;		
	}
	
	public Collection getNumericSort() {
		return numericSort;		
	}

    public Collection sortTableDataObjects(Collection collection, Vector vector) {
        sortedCollection = new Vector();
        if(vector == null || vector.size() < 1)  {
            return collection;
        }
        System.out.println("sortList: " + vector);
 
        valsets = buildUniqueValueTable(collection, vector);
        sortTableDataObjects(collection, vector, 0);
        return sortedCollection;
    }

    private void sortTableDataObjects(Collection collection, Vector vector, int i) {
        if(collection.size() < 1)  {
            return;
        }
        String s = (String)vector.elementAt(i);
        Vector vector1 = new Vector();
        if(valsets.get(s) != null)  {
            vector1 = new Vector((Collection)valsets.get(s));
        }
        
		// override OOTB sorting algorithm
		if(specificSortingOrder != null && specificSortingOrder.size() > 0 &&
			specificSortingOrder.containsKey(s)) {
			Collections.sort(vector1);
			Vector sortOrder = (Vector) specificSortingOrder.get(s);
			Iterator sortIt = sortOrder.iterator();
			Vector newVector1 = new Vector();
			while(sortIt.hasNext()) {
				Object sortObj = sortIt.next();
				if(vector1.contains(sortObj)) {
					newVector1.add(sortObj);
				}
			}			
			//add data if not present in specificSortingOrder vector
			sortIt = vector1.iterator();
			while(sortIt.hasNext()) {
				Object sortObj = sortIt.next();
				if(!newVector1.contains(sortObj)) {
					newVector1.add(sortObj);
				}
			}
			vector1 = newVector1;
		} else if(numericSort != null && numericSort.size() > 0 &&
			numericSort.contains(s)) {
			try{
				Iterator sortIt = vector1.iterator();
				Vector newVector1 = new Vector();
				while(sortIt.hasNext()) {
					String sortObj = (String) sortIt.next();					
					newVector1.add(sortObj.trim());					
				}
				vector1 = newVector1;
				HBITableDataSorter floatcomparator = new HBITableDataSorter(true);				
				Collections.sort(vector1, floatcomparator);
			} catch(Exception e) {
				//System.out.println("vector1:" + vector1);				
				System.out.println("Could not perform numeric sort. Using default sorting...");
				//e.printStackTrace();
				Collections.sort(vector1);
			}
		} else {
			Collections.sort(vector1);
		}		
		
        Iterator iterator = vector1.iterator();
        String s1 = "";
        while(iterator.hasNext()) {
            Object obj = iterator.next();
            Collection collection1 = getObjsMatchingCriteria(collection, s, obj);
            if(i == vector.size() - 1)  {
                sortedCollection.addAll(collection1);
            } else  {
                sortTableDataObjects(collection1, vector, i + 1);
            }
        }
        Collection collection2 = getObjsMatchingCriteria(collection, s, null);
        if(i == vector.size() - 1)  {
            sortedCollection.addAll(collection2);
        } else  {
            sortTableDataObjects(collection2, vector, i + 1);
        }
    }

    public Hashtable buildUniqueValueTable(Collection collection, Collection collection1) {
        Hashtable hashtable = new Hashtable();
        Iterator iterator = collection1.iterator();
        String s = "";
        while(iterator.hasNext())  {
            String s1 = (String)iterator.next();
            Iterator iterator1 = collection.iterator();
            while(iterator1.hasNext()) {
                TableData tableDataObjs = (TableData)iterator1.next();
                if(tableDataObjs.getData(s1) != null) {
                    Object obj = tableDataObjs.getData(s1);
                    Object obj1 = (Collection)hashtable.get(s1);
                    if(obj1 == null)  {
                        obj1 = new Vector();
                    }
                    if(!((Collection) (obj1)).contains(obj))  {
                        ((Collection) (obj1)).add(obj);
                    }
                    hashtable.put(s1, obj1);
                }
            }
        }
        return hashtable;
    }

    public Collection getObjsMatchingCriteria(Collection collection, String s, Object obj)  {
        Vector vector = new Vector();
        Iterator iterator = collection.iterator();
        do {
            if(!iterator.hasNext()) {
                break;
            }
            TableData tableDataObjs = (TableData)iterator.next();
            Object obj1 = tableDataObjs.getData(s);
            if(obj1 != null && obj1.equals(obj) || obj == null && obj1 == null)  {
                vector.add(tableDataObjs);
            }
        } while(true);
        return vector;
    }

}