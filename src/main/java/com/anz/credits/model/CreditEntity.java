package com.anz.credits.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The CreditEntity Node data model to represent CreditEntity in memory.
 *
 * @author : Joby Job
 */
public class CreditEntity {

    private CreditEntity parent;

    //this field is used to do final reconciliation/fixing parent record if they appear later in data fetch order
    private String parentName;
    private String creditEntityName;
    private Double limit=0.0, utilization=0.0;

    private Double cumulativeUtilization;

    private List<CreditEntity> children;

    public CreditEntity(String creditEntityName, String parentName, CreditEntity parentCreditEntity, Double limit , Double utilization){
        this.creditEntityName = creditEntityName;
        this.parentName = parentName;
        this.parent = parentCreditEntity;
        this.limit = limit;
        this.utilization = utilization;
        //Initialize cumulative utilization as current node utilization
        this.cumulativeUtilization = utilization;
    }

    /**
     * Add the input child entity node into this child list,
     *  and calculate the cumulative Utilization propagated upwards in the hierarchy.
     *
     * @param creditEntity
     */
    public void addChild(CreditEntity creditEntity){
        if (children == null){
            children = new ArrayList<CreditEntity>();
        }
        children.add(creditEntity);
        creditEntity.parent = this;
        propagateCumulativeUtilization(creditEntity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditEntity creditEntity = (CreditEntity) o;
        return creditEntityName.equals(creditEntity.creditEntityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(creditEntityName);
    }

    public CreditEntity getParent() {
        return parent;
    }

    public void setParent(CreditEntity parent) {
        this.parent = parent;
    }

    public String getCreditEntityName() {
        return creditEntityName;
    }

    public void setCreditEntityName(String creditEntityName) {
        this.creditEntityName = creditEntityName;
    }

    public String getParentName() {
        return parentName;
    }
    public Double getLimit() {
        return limit;
    }

    public void setLimit(Double limit) {
        this.limit = limit;
    }

    public Double getUtilization() {
        return utilization;
    }

    public void setUtilization(Double utilization) {
        this.utilization = utilization;
    }

    public List<CreditEntity> getChildren() {
        return children;
    }

    public void setChildren(List<CreditEntity> children) {
        this.children = children;
    }

    public Double getCumulativeUtilization() {
        return cumulativeUtilization;
    }

    @Override
    public String toString() {
        return "Node{" +
                "parent=" + (parent != null ? parent.getCreditEntityName() :null)+
                ", nodeName='" + creditEntityName + '\'' +
                ", limit=" + limit +
                ", utilization=" + utilization +
                ", cumulativeUtilization=" + cumulativeUtilization +
                '}';
    }

    /**
     * Propagate cumulative utlization upwards in the hierarchy
     * @param creditEntity
     */
    private void propagateCumulativeUtilization(CreditEntity creditEntity){
        this.cumulativeUtilization += creditEntity.getUtilization();

        CreditEntity parent = this;
        while ( (parent = parent.getParent()) != null){
            parent.propagateCumulativeUtilization(creditEntity);
        }
    }
}
