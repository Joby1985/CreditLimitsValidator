package com.anz.credits.model;

import java.util.HashSet;
import java.util.Iterator;

/**
 * A customized HashSet for dealing with custom Node data. It help retrieve an existing/already added
 * Node by its name.
 * @author : Joby Job
 *
 * @param <E>
 */
public class CreditEntitySet<E> extends HashSet<E> {
    /**
     * Get a CreditEntity item in the set by its unique name.
     * @param nodeName
     * @return
     */
    public CreditEntity getByName(String nodeName){
        CreditEntity ret = null;
        Iterator<E> iter = iterator();
        boolean isNodeType = false;
        // Loop all elements until we find the item (until ret != null)
        while (iter.hasNext() && ret == null){
            E item = iter.next();
            //All items in the set are of same type. So if we find that one of them is of 'Node' type,
            // then we will need to do  'instanceof' check for max one item.
            if (isNodeType || (isNodeType = (item instanceof CreditEntity))){
                CreditEntity currentItem = (CreditEntity)item;
                if (nodeName.equals(currentItem.getCreditEntityName())){
                    ret = currentItem;
                }
            }
            else{
                break;
            }
        }
        return ret;
    }
}
