package uk.ac.ebi.pride.proteomes.index.service;

import org.apache.solr.common.SolrInputDocument;
import uk.ac.ebi.pride.proteomes.index.model.Peptiform;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiform;

import java.util.ArrayList;
import java.util.List;

/**
 * Test data for the unit tests
 *
 * @author florian@ebi.ac.uk
 */
public class TestData {

    public static final String PEPTIDE_1_FORM_1_ID = "[HDCVMPDR|9606|]";
    public static final String PEPTIDE_1_FORM_2_ID = "[HDCVMPDR|9606|(5,15)]";
    public static final String PEPTIDE_1_SEQUENCE = "HDCVMPDR";

    public static final String PEPTIDE_2_FORM_1_ID = "[AAITSYEK|9606|]";
    public static final String PEPTIDE_2_FORM_2_ID = "[AAITSYEK|9606|(6,4)]";
    public static final String PEPTIDE_2_SEQUENCE = "AAITSYEK";

    public static final String PEPTIDE_3_FORM_1_ID = "[ELGAVEK|9606|]";
    public static final String PEPTIDE_3_SEQUENCE = "ELGAVEK";
    public static final List<String> PEPTIDE_3_PROTEINS;
    static {
        PEPTIDE_3_PROTEINS = new ArrayList<String>(2);
        PEPTIDE_3_PROTEINS.add("P12345");
        PEPTIDE_3_PROTEINS.add("P12346");
        PEPTIDE_3_PROTEINS.add("P12347");
    }
    public static final List<String> PEPTIDE_3_UP_GROUPS;
    static {
        PEPTIDE_3_UP_GROUPS = new ArrayList<String>(3);
        PEPTIDE_3_UP_GROUPS.add("P12345");
        PEPTIDE_3_UP_GROUPS.add("P12346");
        PEPTIDE_3_UP_GROUPS.add("P12347");
    }
    public static final String GROUP_DESC_1 = "some description about a group";
    public static final String GROUP_DESC_2 = "for example keywords like kinase, disease";
    public static final String GROUP_DESC_3 = "or common names like albumin, p53";
    public static final List<String> PEPTIDE_3_GENE_GROUPS;
    static {
        PEPTIDE_3_GENE_GROUPS = new ArrayList<String>(3);
        PEPTIDE_3_GENE_GROUPS.add("GENE1");
        PEPTIDE_3_GENE_GROUPS.add("GENE2");
    }


    public static final String PEPTIDE_4_FORM_1_ID = "[EDAANNYAR|9606|]";
    public static final String PEPTIDE_4_FORM_2_ID = "[EDAANNYAR|9606|(1,4)(2,8)(9,15)]";
    public static final String PEPTIDE_4_FORM_3_ID = "[EDAANNYAR|10090|]";
    public static final String PEPTIDE_4_SEQUENCE = "EDAANNYAR";
    public static final List<String> PEPTIDE_4_MODS;
    static {
        PEPTIDE_4_MODS = new ArrayList<String>(3);
        PEPTIDE_4_MODS.add("Phosphorylation");
        PEPTIDE_4_MODS.add("Deamidation");
        PEPTIDE_4_MODS.add("Oxidation");
    }

    public static final String PEPTIDE_5_FORM_1_ID = "[EDSQLASMQHK|10090|(8,15)]";
    public static final String PEPTIDE_5_SEQUENCE = "EDSQLASMQHK";
    public static final List<String> PEPTIDE_5_PROTEINS;
    static {
        PEPTIDE_5_PROTEINS = new ArrayList<String>(2);
        PEPTIDE_5_PROTEINS.add("P12345");
        PEPTIDE_5_PROTEINS.add("P12344");
    }
    public static final List<String> PEPTIDE_5_UP_GROUPS;
    static {
        PEPTIDE_5_UP_GROUPS = new ArrayList<String>(2);
        PEPTIDE_5_UP_GROUPS.add("P12345");
        PEPTIDE_5_UP_GROUPS.add("P12344");
    }

    public static final String PEPTIDE_6_FORM_1_ID = "[TESTTEST|121225|(8,15)]";
    public static final String PEPTIDE_6_SEQUENCE = "TESTTEST";
    public static final List<String> PEPTIDE_6_PROTEINS;
    static {
        PEPTIDE_6_PROTEINS = new ArrayList<String>(2);
        PEPTIDE_6_PROTEINS.add("P98765");
    }

    public static final int TAXID_HUMAN = 9606;
    public static final String SPECIES_HUMAN = "Homo sapiens (human)";
    public static final int TAXID_MOUSE = 10090;
    public static final String SPECIES_MOUSE = "Mus musculus (mouse)";
    public static final int TAXID_HBV = 121225;
    public static final String SPECIES_HBV = "Pediculus humanus (human louse)";

    public static final int HUMAN_RECORDS = 7;
    public static final int MOUSE_RECORDS = 2;
    public static final int HBV_RECORDS = 1;

    public static final int COUNT_TOTAL_DOCS = HUMAN_RECORDS + MOUSE_RECORDS + HBV_RECORDS;

    private static SolrInputDocument createDoc(String id, String sequence, int taxid, String species, List<String> proteins) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField(SolrPeptiform.ID, id);
        doc.addField(SolrPeptiform.PEPTIFORM_SEQUENCE, sequence);
        doc.addField(SolrPeptiform.PEPTIFORM_TAXID, taxid);
        doc.addField(SolrPeptiform.PEPTIFORM_SPECIES, species);
        if (proteins != null) {
            doc.addField(SolrPeptiform.PROTEINS, proteins);
            doc.addField(SolrPeptiform.NUM_PROTEINS, proteins.size());
        } else { // default values
            doc.addField(SolrPeptiform.PROTEINS, null);
            doc.addField(SolrPeptiform.NUM_PROTEINS, 0);
        }
        return doc;
    }

    public static List<SolrInputDocument> createTestDocs() {
        List<SolrInputDocument> solrDocuments = new ArrayList<SolrInputDocument>();
        solrDocuments.add(TestData.createDoc(PEPTIDE_1_FORM_1_ID, PEPTIDE_1_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_1_FORM_2_ID, PEPTIDE_1_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_2_FORM_1_ID, PEPTIDE_2_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_2_FORM_2_ID, PEPTIDE_2_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        SolrInputDocument peptide3 = TestData.createDoc(PEPTIDE_3_FORM_1_ID, PEPTIDE_3_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, PEPTIDE_3_PROTEINS);
        peptide3.addField(SolrPeptiform.NUM_UP_GROUPS, PEPTIDE_3_UP_GROUPS.size());
        peptide3.addField(SolrPeptiform.UP_GROUPS, PEPTIDE_3_UP_GROUPS);
        peptide3.addField(SolrPeptiform.NUM_GENE_GROUPS, PEPTIDE_3_GENE_GROUPS.size());
        peptide3.addField(SolrPeptiform.GENE_GROUPS, PEPTIDE_3_GENE_GROUPS);
        peptide3.addField(SolrPeptiform.GROUP_DESCS, GROUP_DESC_1 + "\t" + GROUP_DESC_2 + "\t" + GROUP_DESC_3);
        solrDocuments.add(peptide3);
        solrDocuments.add(TestData.createDoc(PEPTIDE_4_FORM_1_ID, PEPTIDE_4_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        SolrInputDocument peptide4_2 = TestData.createDoc(PEPTIDE_4_FORM_2_ID, PEPTIDE_4_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null);
        peptide4_2.addField(SolrPeptiform.MODS, PEPTIDE_4_MODS);
        solrDocuments.add(peptide4_2);
        solrDocuments.add(TestData.createDoc(PEPTIDE_4_FORM_3_ID, PEPTIDE_4_SEQUENCE, TAXID_MOUSE, SPECIES_MOUSE, null));
        SolrInputDocument peptide5 = TestData.createDoc(PEPTIDE_5_FORM_1_ID, PEPTIDE_5_SEQUENCE, TAXID_MOUSE, SPECIES_MOUSE, PEPTIDE_5_PROTEINS);
        peptide5.addField(SolrPeptiform.NUM_UP_GROUPS, PEPTIDE_5_UP_GROUPS.size());
        peptide5.addField(SolrPeptiform.UP_GROUPS, PEPTIDE_5_UP_GROUPS);
        solrDocuments.add(peptide5);
        solrDocuments.add(TestData.createDoc(PEPTIDE_6_FORM_1_ID, PEPTIDE_6_SEQUENCE, TAXID_HBV,   SPECIES_HBV,   PEPTIDE_6_PROTEINS));
        return solrDocuments;
    }

    private static Peptiform createPeptiForm(String id, String sequence, int taxid, String species, List<String> proteins) {
        Peptiform peptiform = new Peptiform();
        peptiform.setId(id);
        peptiform.setSequence(sequence);
        peptiform.setTaxid(taxid);
        peptiform.setSpecies(species);
        if (proteins != null) {
            peptiform.setProteins(proteins);
            peptiform.setNumProteins(proteins.size());
        } else {
            peptiform.setProteins(null);
            peptiform.setNumProteins(0);
        }
        return peptiform;
    }

    public static List<Peptiform> createTestPeptiForms() {
        List<Peptiform> peptiforms = new ArrayList<Peptiform>(10);
        peptiforms.add(TestData.createPeptiForm(PEPTIDE_1_FORM_1_ID, PEPTIDE_1_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        peptiforms.add(TestData.createPeptiForm(PEPTIDE_1_FORM_2_ID, PEPTIDE_1_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        peptiforms.add(TestData.createPeptiForm(PEPTIDE_2_FORM_1_ID, PEPTIDE_2_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        peptiforms.add(TestData.createPeptiForm(PEPTIDE_2_FORM_2_ID, PEPTIDE_2_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        Peptiform peptide3 = TestData.createPeptiForm(PEPTIDE_3_FORM_1_ID, PEPTIDE_3_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, PEPTIDE_3_PROTEINS);
        peptide3.setUpGroups(PEPTIDE_3_UP_GROUPS);
        peptide3.setNumUpGroups(PEPTIDE_3_UP_GROUPS.size());
        peptide3.setGeneGroups(PEPTIDE_3_GENE_GROUPS);
        peptide3.setNumGeneGroups(PEPTIDE_3_GENE_GROUPS.size());
        peptide3.setGroupDescs(GROUP_DESC_1 + "\t" + GROUP_DESC_2 + "\t" + GROUP_DESC_3);
        peptiforms.add(peptide3);
        peptiforms.add(TestData.createPeptiForm(PEPTIDE_4_FORM_1_ID, PEPTIDE_4_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        Peptiform peptide4_2 = TestData.createPeptiForm(PEPTIDE_4_FORM_2_ID, PEPTIDE_4_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null);
        peptide4_2.setMods(PEPTIDE_4_MODS);
        peptiforms.add(peptide4_2);
        peptiforms.add(TestData.createPeptiForm(PEPTIDE_4_FORM_3_ID, PEPTIDE_4_SEQUENCE, TAXID_MOUSE, SPECIES_MOUSE, null));
        Peptiform peptide5 = TestData.createPeptiForm(PEPTIDE_5_FORM_1_ID, PEPTIDE_5_SEQUENCE, TAXID_MOUSE, SPECIES_MOUSE, PEPTIDE_5_PROTEINS);
        peptide5.setUpGroups(PEPTIDE_5_UP_GROUPS);
        peptide5.setNumUpGroups(PEPTIDE_5_UP_GROUPS.size());
        peptiforms.add(peptide5);
        peptiforms.add(TestData.createPeptiForm(PEPTIDE_6_FORM_1_ID, PEPTIDE_6_SEQUENCE, TAXID_HBV, SPECIES_HBV,     PEPTIDE_6_PROTEINS));
        return peptiforms;
    }

}
