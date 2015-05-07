package uk.ac.ebi.pride.proteomes.index.service;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.pride.proteomes.index.model.Peptiform;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiform;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static uk.ac.ebi.pride.proteomes.index.service.TestData.*;

/**
 * @author florian@ebi.ac.uk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class})
public class ProteomesIndexServiceTest {

    @Resource
    private ProteomesIndexService proteomesIndexService;

    @Resource
    private ProteomesSearchService proteomesSearchService;

    @Resource
    SolrTemplate solrOperations;

//    @Before
//    @After
//    public void deleteAll() throws SolrServerException, IOException {
//        // make sure we don't have any old data kicking around
//        server.deleteByQuery("*:*");
//
//        // force the commit for testing purposes (avoids soft commit delay)
//        server.commit();
//    }

    @After
    public void tearDown() {
        solrOperations.delete(new SimpleQuery(new SimpleStringCriteria("*:*")));
        solrOperations.commit();
    }
    /**
     * Simple save and delete test. Uses the ProteomesIndexService to create
     * and save records and verifies that with a direct query on to the Solr
     * server. Then the same is done with a delete.
     *
     * @throws SolrServerException
     */
    @Test
    public void testSaveAndDelete() throws SolrServerException {

        SolrServer server = solrOperations.getSolrServer();

        // first insert the test data
        proteomesIndexService.save(createTestPeptiForms());
        // and check if all records are there
        QueryResponse response = server.query(new SolrQuery("*"));
        assertEquals(COUNT_TOTAL_DOCS, response.getResults().getNumFound());

        // do some spot checks
        // search for a specific record by ID
        response = server.query(new SolrQuery(SolrPeptiform.ID + ":" + ClientUtils.escapeQueryChars(PEPTIDE_1_FORM_1_ID)));
        assertEquals(1, response.getResults().getNumFound());
        assertEquals(PEPTIDE_1_FORM_1_ID, response.getBeans(Peptiform.class).get(0).getId());
        // delete a specific record by ID
        proteomesIndexService.delete(PEPTIDE_1_FORM_1_ID);
        response = server.query(new SolrQuery("*"));
        assertEquals(COUNT_TOTAL_DOCS-1, response.getResults().getNumFound());
        // search by general keyword
        response = server.query(new SolrQuery(SolrPeptiform.TEXT+":human"));
        assertEquals(HUMAN_RECORDS-1+HBV_RECORDS, response.getResults().getNumFound());

        // finally remove all records
        proteomesIndexService.deleteAll();
        // and check that the index is empty
        response = server.query(new SolrQuery("*"));
        assertEquals(0, response.getResults().getNumFound());

    }

    /**
     * We assume that document identity is given by the document ID and documents are overwritten
     * if a document is saved to the index and a document with that ID already exists there.
     *
     * This should be true if a document if first retrieved from the index and then saved back
     */
    @Test
    public void testFindAndUpdate() {

        // first insert the test data
        proteomesIndexService.save(createTestPeptiForms());
        assertEquals(new Long(COUNT_TOTAL_DOCS), proteomesSearchService.countAll());

        // retrieve a specific record
        Peptiform peptiform = proteomesSearchService.findById(PEPTIDE_1_FORM_1_ID);
        assertNotNull(peptiform);
        // check the existing record we want to change
        int taxid = peptiform.getTaxid();
        assertEquals(TAXID_HUMAN, taxid);

        // now we manipulate the record...
        taxid += 1000;
        peptiform.setTaxid(taxid);
        // and save it back
        proteomesIndexService.save(peptiform);

        // check that we still have the same number of records
        assertEquals(new Long(COUNT_TOTAL_DOCS), proteomesSearchService.countAll());

        peptiform = proteomesSearchService.findById(PEPTIDE_1_FORM_1_ID);
        assertNotNull(peptiform);
        assertEquals(TAXID_HUMAN + 1000, peptiform.getTaxid());
    }

    /**
     * We assume that document identity is given by the document ID and documents are overwritten
     * if a document is saved to the index and a document with that ID already exists there.
     *
     * This should also be true if a document is created outside the index and then saved
     * to it with an existing ID.
     */
    @Test
    public void testOverwriteUpdate() {

        // first insert the test data
        proteomesIndexService.save(createTestPeptiForms());
        assertEquals(new Long(COUNT_TOTAL_DOCS), proteomesSearchService.countAll());
        // we have two proteins with this sequence and we will change one of them
        assertEquals(new Long(2), proteomesSearchService.countBySequence(PEPTIDE_1_SEQUENCE));

        // recreate the records (different objects) and pick one (not load from index)
        Peptiform peptiform = new Peptiform();
        // use an existing ID, but otherwise different data
        peptiform.setId(PEPTIDE_1_FORM_1_ID);
        peptiform.setTaxid(1000);
        peptiform.setSpecies("A new species");
        peptiform.setSequence("UNKNOWN");
        // and save it back (overwriting the existing record)
        proteomesIndexService.save(peptiform);

        // check that we still have the same number of records
        assertEquals(new Long(COUNT_TOTAL_DOCS), proteomesSearchService.countAll());

        peptiform = proteomesSearchService.findById(PEPTIDE_1_FORM_1_ID);
        assertNotNull(peptiform);
        assertEquals(1000, peptiform.getTaxid());

        assertEquals(new Long(1), proteomesSearchService.countByTaxid(1000));
        assertEquals(new Long(1), proteomesSearchService.countBySequence("UNKNOWN"));
        assertEquals(new Long(1), proteomesSearchService.countBySequence(PEPTIDE_1_SEQUENCE));


    }

}
