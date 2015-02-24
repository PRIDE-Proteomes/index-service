package uk.ac.ebi.pride.proteomes.index.service;

import org.apache.solr.common.SolrInputDocument;
import uk.ac.ebi.pride.proteomes.index.model.PeptiForm;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiForm;

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

    public static final String PEPTIDE_4_FORM_1_ID = "[EDAANNYAR|9606|]";
    public static final String PEPTIDE_4_FORM_2_ID = "[EDAANNYAR|9606|(1,20)(2,20)(9,20)]";
    public static final String PEPTIDE_4_FORM_3_ID = "[EDAANNYAR|10090|]";
    public static final String PEPTIDE_4_SEQUENCE = "EDAANNYAR";

    public static final String PEPTIDE_5_FORM_1_ID = "[EDSQLASMQHK|10090|(8,15)]";
    public static final String PEPTIDE_5_SEQUENCE = "EDSQLASMQHK";
    public static final List<String> PEPTIDE_5_PROTEINS;
    static {
        PEPTIDE_5_PROTEINS = new ArrayList<String>(2);
        PEPTIDE_5_PROTEINS.add("P12345");
        PEPTIDE_5_PROTEINS.add("P12344");
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
        doc.addField(SolrPeptiForm.ID, id);
        doc.addField(SolrPeptiForm.PEPTIFORM_SEQUENCE, sequence);
        doc.addField(SolrPeptiForm.PEPTIFORM_TAXID, taxid);
        doc.addField(SolrPeptiForm.PEPTIFORM_SPECIES, species);
        if (proteins != null) {
            doc.addField(SolrPeptiForm.PROTEINS, proteins);
            doc.addField(SolrPeptiForm.NUM_PROTEINS, proteins.size());
        } else { // default values
            doc.addField(SolrPeptiForm.PROTEINS, null);
            doc.addField(SolrPeptiForm.NUM_PROTEINS, 0);
        }
        return doc;
    }

    public static List<SolrInputDocument> createTestDocs() {
        List<SolrInputDocument> solrDocuments = new ArrayList<SolrInputDocument>();
        solrDocuments.add(TestData.createDoc(PEPTIDE_1_FORM_2_ID, PEPTIDE_1_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_1_FORM_1_ID, PEPTIDE_1_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_2_FORM_1_ID, PEPTIDE_2_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_2_FORM_2_ID, PEPTIDE_2_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_3_FORM_1_ID, PEPTIDE_3_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, PEPTIDE_3_PROTEINS));
        solrDocuments.add(TestData.createDoc(PEPTIDE_4_FORM_1_ID, PEPTIDE_4_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_4_FORM_2_ID, PEPTIDE_4_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_4_FORM_3_ID, PEPTIDE_4_SEQUENCE, TAXID_MOUSE, SPECIES_MOUSE, null));
        solrDocuments.add(TestData.createDoc(PEPTIDE_5_FORM_1_ID, PEPTIDE_5_SEQUENCE, TAXID_MOUSE, SPECIES_MOUSE, PEPTIDE_5_PROTEINS));
        solrDocuments.add(TestData.createDoc(PEPTIDE_6_FORM_1_ID, PEPTIDE_6_SEQUENCE, TAXID_HBV,   SPECIES_HBV,   PEPTIDE_6_PROTEINS));
        return solrDocuments;
    }

    private static PeptiForm createPeptiForm(String id, String sequence, int taxid, String species, List<String> proteins) {
        PeptiForm peptiForm = new PeptiForm();
        peptiForm.setId(id);
        peptiForm.setSequence(sequence);
        peptiForm.setTaxid(taxid);
        peptiForm.setSpecies(species);
        if (proteins != null) {
            peptiForm.setProteins(proteins);
            peptiForm.setNumProteins(proteins.size());
        } else {
            peptiForm.setProteins(null);
            peptiForm.setNumProteins(0);
        }
        return peptiForm;
    }

    public static List<PeptiForm> createTestPeptiForms() {
        List<PeptiForm> peptiForms = new ArrayList<PeptiForm>(10);
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_1_FORM_1_ID, PEPTIDE_1_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_1_FORM_2_ID, PEPTIDE_1_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_2_FORM_1_ID, PEPTIDE_2_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_2_FORM_2_ID, PEPTIDE_2_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_3_FORM_1_ID, PEPTIDE_3_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, PEPTIDE_3_PROTEINS));
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_4_FORM_1_ID, PEPTIDE_4_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_4_FORM_2_ID, PEPTIDE_4_SEQUENCE, TAXID_HUMAN, SPECIES_HUMAN, null));
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_4_FORM_3_ID, PEPTIDE_4_SEQUENCE, TAXID_MOUSE, SPECIES_MOUSE, null));
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_5_FORM_1_ID, PEPTIDE_5_SEQUENCE, TAXID_MOUSE, SPECIES_MOUSE, PEPTIDE_5_PROTEINS));
        peptiForms.add(TestData.createPeptiForm(PEPTIDE_6_FORM_1_ID, PEPTIDE_6_SEQUENCE, TAXID_HBV, SPECIES_HBV,     PEPTIDE_6_PROTEINS));
        return peptiForms;
    }

}
