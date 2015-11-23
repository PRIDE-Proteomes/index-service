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

    @Field(PROTEIN_ACCESSION)
    private List<String> proteinAccession;

    @Field(PROTEIN_NAME)
    private List<String> proteinName;

    @Field(PROTEIN_DESCRIPTION)
    private List<String> proteinDescription;

    @Field(MOD)
    private List<String> mod;

    @Field(NUM_GENE_GROUPS)
    private int numGeneGroups;

    @Field(GENE_GROUP)
    private List<String> geneGroup;

    @Field(GENE_GROUP_DESCRIPTION)
    private List<String> geneGroupDescription;
    private List<String> proteinGeneSymbol;
    private List<String> proteinEvidence;
    private List<String> proteinAltName;


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

    public List<String> getProteinAccession() {
        return proteinAccession;
    }

    public void setProteinAccession(List<String> proteinAccession) {
        this.proteinAccession = proteinAccession;
    }

    public List<String> getProteinName() {
        return proteinName;
    }

    public void setProteinName(List<String> proteinName) {
        this.proteinName = proteinName;
    }

    public List<String> getProteinDescription() {
        return proteinDescription;
    }

    public void setProteinDescription(List<String> proteinDescription) {
        this.proteinDescription = proteinDescription;
    }

    public List<String> getMod() {
        return mod;
    }

    public void setMod(List<String> mod) {
        this.mod = mod;
    }

    public int getNumGeneGroups() {
        return numGeneGroups;
    }

    public void setNumGeneGroups(int numGeneGroups) {
        this.numGeneGroups = numGeneGroups;
    }

    public List<String> getGeneGroup() {
        return geneGroup;
    }

    public void setGeneGroup(List<String> geneGroup) {
        this.geneGroup = geneGroup;
    }

    public List<String> getGeneGroupDescription() {
        return geneGroupDescription;
    }

    public void setGeneGroupDescription(List<String> geneGroupDescription) {
        this.geneGroupDescription = geneGroupDescription;
    }

    public void setProteinGeneSymbol(List<String> proteinGeneSymbol) {
        this.proteinGeneSymbol = proteinGeneSymbol;
    }

    public List<String> getProteinGeneSymbol() {
        return proteinGeneSymbol;
    }

    public void setProteinEvidence(List<String> proteinEvidence) {
        this.proteinEvidence = proteinEvidence;
    }

    public List<String> getProteinEvidence() {
        return proteinEvidence;
    }

    public void setProteinAltName(List<String> proteinAltName) {
        this.proteinAltName = proteinAltName;
    }

    public List<String> getProteinAltName() {
        return proteinAltName;
    }
}
