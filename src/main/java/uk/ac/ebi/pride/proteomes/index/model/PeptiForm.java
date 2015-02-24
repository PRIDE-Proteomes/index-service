package uk.ac.ebi.pride.proteomes.index.model;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

/**
 * Java bean for the PeptiForm Solr document.
 *
 * @author florian@ebi.ac.uk
 */
public class PeptiForm implements SolrPeptiForm {


    @Field(ID)
    private String id;

    @Field(PEPTIFORM_SEQUENCE)
    private String sequence;

    @Field(PEPTIFORM_TAXID)
    private int taxid;

    @Field(PEPTIFORM_SPECIES)
    private String species;

    @Field(NUM_PROTEINS)
    private int numProteins;

    @Field(PROTEINS)
    private List<String> proteins;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getTaxid() {
        return taxid;
    }

    public void setTaxid(int taxid) {
        this.taxid = taxid;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getNumProteins() {
        return numProteins;
    }

    public void setNumProteins(int numProteins) {
        this.numProteins = numProteins;
    }

    public List<String> getProteins() {
        return proteins;
    }

    public void setProteins(List<String> proteins) {
        this.proteins = proteins;
    }
}
