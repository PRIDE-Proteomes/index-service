package uk.ac.ebi.pride.proteomes.index.model;

/**
 * Field names of the PeptiForm Solr document.
 *
 * @author florian@ebi.ac.uk
 */
public interface SolrPeptiformFields {

    String ID = "id";
    String PEPTIFORM_SEQUENCE = "peptiform_sequence";
    String PEPTIFORM_TAXID = "peptiform_taxid";
    String PEPTIFORM_SPECIES = "peptiform_species";
    String TEXT = "text";
    String NUM_PROTEINS = "num_proteins";
    String PROTEIN_ACCESSION = "protein_accession";
    String PROTEIN_NAME = "protein_name";
    String PROTEIN_DESCRIPTION = "protein_description";

    String MOD = "mod";

    String NUM_GENE_GROUPS = "num_gene_groups";
    String GENE_GROUP = "gene_group";
    String GENE_GROUP_DESCRIPTION = "gene_group_description";
}
