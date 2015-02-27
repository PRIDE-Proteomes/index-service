package uk.ac.ebi.pride.proteomes.index.model;

/**
 * Field names of the PeptiForm Solr document.
 *
 * @author florian@ebi.ac.uk
 */
public interface SolrPeptiForm {

    public static final String ID = "id";
    public static final String PEPTIFORM_SEQUENCE = "peptiform_sequence";
    public static final String PEPTIFORM_TAXID = "peptiform_taxid";
    public static final String PEPTIFORM_SPECIES = "peptiform_species";
    public static final String TEXT = "text";
    public static final String NUM_PROTEINS = "num_proteins";
    public static final String PROTEINS = "proteins";
    public static final String MODS = "mods";
    public static final String NUM_UP_GROUPS = "num_up_groups";
    public static final String UP_GROUPS = "up_groups";
    public static final String NUM_GENE_GROUPS = "num_gene_groups";
    public static final String GENE_GROUPS = "gene_groups";
    public static final String GROUP_DESCS = "group_descs";
}
