package uk.ac.ebi.pride.proteomes.index.model;

/**
 * Field names of the PeptiForm Solr document.
 *
 * @author florian@ebi.ac.uk
 */
public interface SolrPeptiformFields {

    public static final String ID = "id";
    public static final String PEPTIFORM_SEQUENCE = "peptiform_sequence";
    public static final String PEPTIFORM_TAXID = "peptiform_taxid";
    public static final String PEPTIFORM_SPECIES = "peptiform_species";
    public static final String TEXT = "text";
    public static final String NUM_PROTEINS = "num_proteins";
    public static final String PROTEIN_ACCESSION = "protein_accession";
    public static final String PROTEIN_NAME = "protein_name";
    public static final String PROTEIN_DESCRIPTION = "protein_description";

    public static final String MOD = "mod";

    public static final String NUM_GENE_GROUPS = "num_gene_groups";
    public static final String GENE_GROUP = "gene_group";
    public static final String GENE_GROUP_DESCRIPTION = "gene_group_description";
}
