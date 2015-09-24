package uk.ac.ebi.pride.proteomes.index.model;

import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * Java bean for the PeptiForm Solr document.
 *
 * @author florian@ebi.ac.uk
 */
@SuppressWarnings("UnusedDeclaration")
public class SolrPeptiform implements SolrPeptiformFields {

    @Id
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

    @Field(MODS)
    private List<String> mods;

    @Field(NUM_UP_GROUPS)
    private int numUpGroups;

    @Field(UP_GROUPS)
    private List<String> upGroups;

    @Field(NUM_GENE_GROUPS)
    private int numGeneGroups;

    @Field(GENE_GROUPS)
    private List<String> geneGroups;

    @Field(GROUP_DESCS)
    private String groupDescs;


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

    public List<String> getMods() {
        return mods;
    }

    public void setMods(List<String> mods) {
        this.mods = mods;
    }

    public int getNumUpGroups() {
        return numUpGroups;
    }

    public void setNumUpGroups(int numUpGroups) {
        this.numUpGroups = numUpGroups;
    }

    public List<String> getUpGroups() {
        return upGroups;
    }

    public void setUpGroups(List<String> upGroups) {
        this.upGroups = upGroups;
    }

    public int getNumGeneGroups() {
        return numGeneGroups;
    }

    public void setNumGeneGroups(int numGeneGroups) {
        this.numGeneGroups = numGeneGroups;
    }

    public List<String> getGeneGroups() {
        return geneGroups;
    }

    public void setGeneGroups(List<String> geneGroups) {
        this.geneGroups = geneGroups;
    }

    public void setGroupDescs(String groupDescs) {
        this.groupDescs = groupDescs;
    }
}
