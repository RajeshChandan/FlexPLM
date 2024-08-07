package com.lowes.model;

import com.lcs.wc.flextype.FlexTyped;
import wt.util.WTException;

import java.util.Collection;

public interface GenericModel {

    public Collection<FlexTyped> getObject(Object obj, String objectName) throws WTException;
}
