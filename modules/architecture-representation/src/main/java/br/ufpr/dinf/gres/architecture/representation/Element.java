package br.ufpr.dinf.gres.architecture.representation;

import br.ufpr.dinf.gres.architecture.exceptions.ConcernNotFoundException;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.uml2.uml.Interface;

import java.io.Serializable;
import java.util.*;

/**
 * @author edipofederle<edipofederle @ gmail.com>
 */
public abstract class Element implements Serializable {

    private static final long serialVersionUID = 4736685073967472613L;

    protected String id;
    private String name;
    private VariationPoint variationPoint;
    private Variant variant;
    private Set<Concern> concerns = new HashSet<Concern>();
    private Architecture architecture;
    private String typeElement;
    private String namespace;
    private boolean belongsToGeneralization;
    private String comments = "";
    private boolean freezedByCluster = false;

    public Element(String name, Variant variant, String typeElement, String namespace, String id) {
        setId(id);
        setName(name);
        setVariant(variant);
        setTypeElement(typeElement);
        setNamespace(namespace);
    }

    public abstract Collection<Concern> getAllConcerns();

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Generates Unique Integer Id according to the namespace:elementType:elementName
     *
     * @return Unique Integer Id
     */
    public float getNumberId() {
        int minValue = Integer.MIN_VALUE;
        int maxValue = Integer.MAX_VALUE;
        int numberId = HashCodeBuilder.reflectionHashCode(this.getNamespace() + ":" + this.getTypeElement() + ":" + this.getName(), true);
        return (numberId - minValue) / (maxValue - minValue);
    }

    /**
     * @param id the id to set
     */
    private void setId(String id) {
        this.id = id;
    }

    public String getTypeElement() {
        return this.typeElement;
    }

    private void setTypeElement(String typeElement) {
        this.typeElement = typeElement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isVariationPoint() {
        return this.getVariationPoint() != null;
    }

    public Variant getVariant() {
        return variant;
    }

    public void setVariant(Variant variant) {
        this.variant = variant;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Retorna apenas os interesses pertencentes a este elemento.<br />
     *
     * @return List<{ @ link Concern }>
     */
    public Set<Concern> getOwnConcerns() {
        if (concerns == null || concerns.isEmpty())
            return Collections.emptySet();
        return concerns;
    }

    public boolean containsConcern(Concern concern) {
        for (Concern conc : getOwnConcerns()) {
            if (conc.getName().equalsIgnoreCase(concern.getName()))
                return true;
        }
        return false;
    }

    public void addConcerns(List<String> concernsNames) throws ConcernNotFoundException {
        for (String name : concernsNames)
            addConcern(name);
    }

    /**
     * Adiciona um interesse a classe.<br/>
     * <p>
     * Somente irá incluir o interesse se o mesmo estive presente no arquivo de
     * perfil que contêm os interesses</br>
     *
     * @param concernName
     * @throws ConcernNotFoundException
     */
    public void addConcern(String concernName) throws ConcernNotFoundException {
        Concern concern = ConcernHolder.INSTANCE.getOrCreateConcern(concernName);
        concerns.add(concern);
    }

    public void removeConcern(String concernName) {
        Concern concern = ConcernHolder.INSTANCE.getConcernByName(concernName);
        concerns.remove(concern);
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Architecture getArchitecture() {
        return architecture;
    }

    /**
     * @return the variationPoint
     */
    public VariationPoint getVariationPoint() {
        return variationPoint;
    }

    /**
     * @param variationPoint the variationPoint to set
     */
    public void setVariationPoint(VariationPoint variationPoint) {
        this.variationPoint = variationPoint;
    }

    public boolean belongsToGeneralization() {
        return this.belongsToGeneralization;
    }

    public void setBelongsToGeneralization(boolean belongsToGeneralization) {
        this.belongsToGeneralization = belongsToGeneralization;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public boolean isFreezeByDM() {
        return this.comments != null && this.comments.contains("freeze");
    }

    public boolean unsetFreezeFromDM() {
        this.comments = this.comments.replaceAll("freeze", "");
        return this.isFreezeByDM();
    }

    public boolean setFreezeFromDM() {
        if (!this.comments.contains("freeze")) {
            this.comments += "freeze";
        }
        return isFreezeByDM();
    }

    public boolean setFreezeFromDM(boolean bool) {
        return bool ? setFreezeFromDM() : unsetFreezeFromDM();
    }

    public boolean setFreezeFromDM(double bool) {
        return bool > 0 ? setFreezeFromDM() : unsetFreezeFromDM();
    }

    public boolean isFreezedByCluster() {
        return freezedByCluster;
    }

    public void setFreezedByCluster(boolean freezedByCluster) {
        this.freezedByCluster = freezedByCluster;
    }

    public void setFreezedByCluster() {
        this.freezedByCluster = true;
    }

    public boolean isTotalyFreezed() {
        return this.isFreezeByDM() || this.isFreezedByCluster();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        String objClass = obj.getClass().toString();
        if (this == obj)
            return true;
        if (!getClass().toString().equals(objClass.toString()))
            return false;
        Element other = (Element) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        return true;
    }

    public boolean totalyEquals(Element other) {
        boolean isEquals = this.getNumberId() == other.getNumberId();
        if (this instanceof Class && other instanceof Class) {
            isEquals &= ((Class) this).getAllAttributes().equals(((Class) other).getAllAttributes());
            isEquals &= ((Class) this).getAllMethods().equals(((Class) other).getAllMethods());
        } else if (this instanceof Interface && other instanceof Interface) {
            isEquals &= ((Interface) this).getOperations().equals(((Interface) other).getOperations());
        } else if (this instanceof Package && other instanceof Package) {
            Set<Element> collect = ((Package) this).getElements();
            Set<Element> collect1 = ((Package) other).getElements();
            if (collect.size() != collect1.size()) return false;
            for (Element element : collect) {
                isEquals &= element.totalyEquals(other);
            }
        }
        return isEquals;

    }

}