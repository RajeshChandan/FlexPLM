package com.lowes.web.model.product;

import com.lcs.wc.product.LCSProduct;
import com.lcs.wc.product.LCSProductLogic;
import wt.util.WTException;

/****
 * Standard Product model class has below functions
 *  - Search Product
 *  - Delete Product
 *
 * @author Rajesh Chandan Sahu (rajeshchandan.sahu@lowes.com)
 */
public class ProductModel {

    public void deleteProduct(LCSProduct product) throws WTException {

        LCSProductLogic productLogic = new LCSProductLogic();
        productLogic.delete(product,true);
    }
}
