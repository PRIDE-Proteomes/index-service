package uk.ac.ebi.pride.proteomes.index.service;


import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiform;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiformFields;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.*;
import static uk.ac.ebi.pride.proteomes.index.service.TestData.*;

/**
 * @author florian@ebi.ac.uk
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestContext.class})
public class ProteomesSearchServiceTest {

    @Resource
    SolrTemplate solrOperations;

    @Resource
    private ProteomesSearchService proteomesSearchService;

    private static final List<SolrInputDocument> docs = createTestDocs();

    private SolrServer server;

    @Before
    public void setUp() throws Exception {

        server = solrOperations.getSolrServer();

        // insert test data
        solrOperations.saveBeans(docs);

        // force the commit for testing purposes (avoids soft commit delay)
        solrOperations.commit();
    }

    @After
    public void tearDown() {
        solrOperations.delete(new SimpleQuery(new SimpleStringCriteria("*:*")));
        solrOperations.commit();
    }


    @Test
    public void testEmptyIndex() throws SolrServerException, IOException {

        server.deleteByQuery("*:*");
        //make sure the changes are committed
        server.commit();

        // query for everything (e.g. *)
        QueryResponse response = server.query(new SolrQuery("*"));

        assertEquals(0, response.getResults().size());
    }

    @Test
    public void testThatAllResultsAreReturned() throws SolrServerException {
        SolrParams params = new SolrQuery("*");
        QueryResponse response = server.query(params);
        assertEquals(COUNT_TOTAL_DOCS, response.getResults().getNumFound());
    }

    @Test
    public void testThatNoResultsAreReturned() throws SolrServerException {
        SolrParams params = new SolrQuery("text that is not found");
        QueryResponse response = server.query(params);
        assertEquals(0, response.getResults().getNumFound());
    }


    @Test
    public void testQueryByAccession() throws Exception {
        // we need to escape the peptiform IDs as they contain reserved special characters
        SolrParams params = new SolrQuery(SolrPeptiformFields.ID + ":" + ClientUtils.escapeQueryChars(PEPTIDE_1_FORM_1_ID));
        QueryResponse response = server.query(params);
        // we expect exactly one result for a query by ID
        assertEquals(1, response.getResults().getNumFound());
        assertEquals(PEPTIDE_1_FORM_1_ID, response.getResults().get(0).get(SolrPeptiformFields.ID));
    }

    @Test
    public void testQueryBySequence() throws SolrServerException {
        SolrParams params = new SolrQuery(SolrPeptiformFields.PEPTIFORM_SEQUENCE + ":" + PEPTIDE_1_SEQUENCE);
        QueryResponse response = server.query(params);
        // we expect two documents, since we have two PeptiForms with that sequence
        assertEquals(2, response.getResults().getNumFound());
        SolrDocument one = response.getResults().get(0);
        SolrDocument two = response.getResults().get(1);
        // both results need to have the sequence we have queried for
        assertEquals(PEPTIDE_1_SEQUENCE, one.get(SolrPeptiformFields.PEPTIFORM_SEQUENCE));
        assertEquals(PEPTIDE_1_SEQUENCE, two.get(SolrPeptiformFields.PEPTIFORM_SEQUENCE));
        // both IDs need to contain the sequence we have queried for
        assertTrue(one.get(SolrPeptiformFields.ID).toString().contains(PEPTIDE_1_SEQUENCE));
        assertTrue(two.get(SolrPeptiformFields.ID).toString().contains(PEPTIDE_1_SEQUENCE));
    }

    @Test
    public void testQueryByTaxid() throws SolrServerException {
        SolrParams params = new SolrQuery(SolrPeptiformFields.PEPTIFORM_TAXID + ":" + TAXID_HUMAN);
        QueryResponse response = server.query(params);
        // we expect 7 documents, since we have 7 PeptiForms with that taxid
        assertEquals(HUMAN_RECORDS, response.getResults().getNumFound());
        params = new SolrQuery(SolrPeptiformFields.PEPTIFORM_TAXID + ":" + TAXID_MOUSE);
        response = server.query(params);
        // we expect two documents, since we have two PeptiForms with that taxid
        assertEquals(MOUSE_RECORDS, response.getResults().getNumFound());
    }

    @Test
    public void testQueryBySpeciesName() throws SolrServerException {
        SolrParams params = new SolrQuery(SolrPeptiformFields.PEPTIFORM_SPECIES + ":" + SPECIES_HUMAN);
        QueryResponse response = server.query(params);
        // service terms are tokenized, so we expect 8 documents,
        // we have 7 (human) and 1 (human louse) PeptiForms that match the service terms
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, response.getResults().getNumFound());
        // same results if we only use (the matching) service token
        params = new SolrQuery(SolrPeptiformFields.PEPTIFORM_SPECIES + ":human");
        response = server.query(params);
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, response.getResults().getNumFound());

        params = new SolrQuery(SolrPeptiformFields.PEPTIFORM_SPECIES + ":" + SPECIES_MOUSE);
        response = server.query(params);
        // we expect two documents, since we have two PeptiForms with that species name
        assertEquals(MOUSE_RECORDS, response.getResults().getNumFound());
    }

    @Test
    public void testQueryByText() throws SolrServerException {
        SolrParams params = new SolrQuery("text:*human*");
        QueryResponse response = server.query(params);
        // we expect 8 documents, since we have 7 human and 1 human louse PeptiForms
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, response.getResults().getNumFound());


        params = new SolrQuery("text:P12345");
        response = server.query(params);
        // we expect 2 documents, since we have 2 peptiforms linked to this protein
        assertEquals(2, response.getResults().getNumFound());
    }

    @Test
    public void testFindAll() {
        // request a page that is big enough to contain all records
        Page<SolrPeptiform> result = proteomesSearchService.findAll(new PageRequest(0, COUNT_TOTAL_DOCS + 2));
        assertEquals(COUNT_TOTAL_DOCS, result.getTotalElements());
    }

    @Test
    public void testFindById() {
        SolrPeptiform result = proteomesSearchService.findById(PEPTIDE_1_FORM_1_ID);
        assertNotNull(result);
        assertEquals(PEPTIDE_1_FORM_1_ID, result.getId());
    }

    @Test
    public void testFindBySequence() {
        Page<SolrPeptiform> page = proteomesSearchService.findBySequence(PEPTIDE_1_SEQUENCE, new PageRequest(0, 10));
        assertEquals(2, page.getTotalElements());
        // we convert toUpperCase internally, so we expect the same results independent of capitalisation
        page = proteomesSearchService.findBySequence(PEPTIDE_1_SEQUENCE.toLowerCase(), new PageRequest(0, 10));
        assertEquals(2, page.getTotalElements());
        String sequence = page.getContent().get(0).getSequence();
        assertEquals(PEPTIDE_1_SEQUENCE, sequence);

        page = proteomesSearchService.findBySequence(PEPTIDE_1_SEQUENCE.toUpperCase(), new PageRequest(0, 10));
        assertEquals(2, page.getTotalElements());
        sequence = page.getContent().get(0).getSequence();
        assertEquals(PEPTIDE_1_SEQUENCE, sequence);

        // we only find exact matches, no match if the query is super sequence
        page = proteomesSearchService.findBySequence(PEPTIDE_1_SEQUENCE + "A", new PageRequest(0, 10));
        assertEquals(0, page.getTotalElements());

        // we only find exact matches, no match if the query is partial sequence
        page = proteomesSearchService.findBySequence(PEPTIDE_1_SEQUENCE.substring(2), new PageRequest(0, 10));
        assertEquals(0, page.getTotalElements());
    }

    @Test
    public void testFindByTaxid() {
        Page<SolrPeptiform> page = proteomesSearchService.findByTaxid(TAXID_HUMAN, new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByTaxid(TAXID_MOUSE, new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByTaxid(TAXID_HBV, new PageRequest(0, 10));
        assertEquals(HBV_RECORDS, page.getTotalElements());

        // non-existent taxid
        page = proteomesSearchService.findByTaxid(1009, new PageRequest(0, 10));
        assertEquals(0, page.getTotalElements());
    }

    @Test
    public void testFindBySpecies() {
        // 9606 and 121225 contain the same keyword/token 'human', so we expect to find both records
        Page<SolrPeptiform> page = proteomesSearchService.findBySpecies(SPECIES_HUMAN, new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findBySpecies(SPECIES_HBV, new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, page.getTotalElements());

        // no other species contains the same keywords/token as mouse, so we expect only those records
        page = proteomesSearchService.findBySpecies(SPECIES_MOUSE, new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS, page.getTotalElements());

        // partial strings are supported, so we expect more results than only for 9606!
        // Homo sapiens and human louse are matching
        page = proteomesSearchService.findBySpecies("human", new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, page.getTotalElements());

        // we don't expect to match partial words/tokens
        page = proteomesSearchService.findBySpecies("ouse", new PageRequest(0, 10));
        assertEquals(0, page.getTotalElements());

        // service terms with wildcards

        // human louse and mouse match the following service term
        page = proteomesSearchService.findBySpecies("*ouse", new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS + HBV_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findBySpecies("?ouse", new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS + HBV_RECORDS, page.getTotalElements());
    }

    @Test
    public void testFindBySpeciesCaseSensitivity() {
        Page<SolrPeptiform> page = proteomesSearchService.findBySpecies(SPECIES_MOUSE, new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS, page.getTotalElements());
        page = proteomesSearchService.findBySpecies(SPECIES_MOUSE.toUpperCase(), new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS, page.getTotalElements());
        page = proteomesSearchService.findBySpecies(SPECIES_MOUSE.toLowerCase(), new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS, page.getTotalElements());
    }

    @Test
    public void testFindByProtein() {
        List<SolrPeptiform> list = proteomesSearchService.findAllByProtein("P12345");
        assertEquals(2, list.size());
        for (SolrPeptiform solrPeptiform : list) {
            assertTrue(solrPeptiform.getProteins().contains("P12345"));
        }
        // same with paged query (restrict to one result)
        list = proteomesSearchService.findByProtein("P12345", new PageRequest(0, 1)).getContent();
        assertEquals(1, list.size());

        list = proteomesSearchService.findAllByProtein("P12347");
        assertEquals(1, list.size());
        assertEquals(PEPTIDE_3_FORM_1_ID, list.get(0).getId());
        // same with paged query
        list = proteomesSearchService.findByProtein("P12347", new PageRequest(0, 10)).getContent();
        assertEquals(1, list.size());
        assertEquals(PEPTIDE_3_FORM_1_ID, list.get(0).getId());

        list = proteomesSearchService.findAllByProtein("P98765");
        assertEquals(1, list.size());
        SolrPeptiform solrPeptiform = list.get(0);
        assertFalse(solrPeptiform.getProteins().isEmpty());
        assertTrue(solrPeptiform.getProteins().contains("P98765"));

    }

    @Test
    public void testFindByMod() {
        String mod = "Oxidation";
        Page<SolrPeptiform> page = proteomesSearchService.findByMod(mod, new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());

        SolrPeptiform solrPeptiform = page.getContent().get(0);
        assertTrue(solrPeptiform.getMods().contains(mod));
        assertEquals(PEPTIDE_4_FORM_2_ID, solrPeptiform.getId());
    }

    @Test
    public void testFindByGroup() {
        List<SolrPeptiform> list = proteomesSearchService.findAllByUpGroup("P12345");
        assertEquals(2, list.size());
        for (SolrPeptiform solrPeptiform : list) {
            assertTrue(solrPeptiform.getUpGroups().contains("P12345"));
            assertTrue(solrPeptiform.getProteins().contains("P12345"));
        }
        // same with paged query (restricted to one result)
        list = proteomesSearchService.findByUpGroup("P12345", new PageRequest(0, 1)).getContent();
        assertEquals(1, list.size());
        assertTrue(list.get(0).getUpGroups().contains("P12345"));
        assertTrue(list.get(0).getProteins().contains("P12345"));

        list = proteomesSearchService.findAllByGeneGroup("GENE1");
        assertEquals(1, list.size());
        assertTrue(list.get(0).getGeneGroups().contains("GENE1"));

        // same with paged query
        list = proteomesSearchService.findByGeneGroup("GENE1", new PageRequest(0, 1)).getContent();
        assertEquals(1, list.size());
        list = proteomesSearchService.findByGeneGroup("GENE1", new PageRequest(1, 1)).getContent();
        assertEquals(0, list.size());
    }


    @Test
    public void testFindByQuery() {
        // service term(s) contains 'human' which matches 8 records
        Page<SolrPeptiform> page = proteomesSearchService.findByQuery(SPECIES_HUMAN, new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByQuery("human", new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByQuery("homo", new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByQuery("9606", new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByQuery("Mouse", new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByQuery("?ouse", new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS + HBV_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByQuery("10090", new PageRequest(0, 10));
        assertEquals(MOUSE_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByQuery(PEPTIDE_6_SEQUENCE, new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());

        page = proteomesSearchService.findByQuery("P12347", new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());

        page = proteomesSearchService.findByQuery("P????5", new PageRequest(0, 10));
        assertEquals(3, page.getTotalElements());

        page = proteomesSearchService.findByQuery("*", new PageRequest(0, 10));
        assertEquals(COUNT_TOTAL_DOCS, page.getTotalElements());

        page = proteomesSearchService.findByQuery("", new PageRequest(0, 10));
        assertEquals(COUNT_TOTAL_DOCS, page.getTotalElements());

        page = proteomesSearchService.findByQuery(null, new PageRequest(0, 10));
        assertEquals(COUNT_TOTAL_DOCS, page.getTotalElements());

        page = proteomesSearchService.findByQuery("GENE1", new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());
        page = proteomesSearchService.findByQuery("Gene1", new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());

        page = proteomesSearchService.findByQuery("kinase", new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());
        page = proteomesSearchService.findByQuery("description", new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());
    }

    @Test
    public void testFindNot() {
        Page<SolrPeptiform> page = proteomesSearchService.findByQueryNot("Mouse", new PageRequest(0, 10));
        assertEquals(COUNT_TOTAL_DOCS - MOUSE_RECORDS, page.getTotalElements());
    }

    @Test
    public void testFindByNumProteins() {
        // there is only one peptide with more than 2 proteins
        Page<SolrPeptiform> page = proteomesSearchService.findByNumProteinsGreaterThan(2, new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals(PEPTIDE_3_FORM_1_ID, page.getContent().get(0).getId());

        // > 0 : at least one protein
        page = proteomesSearchService.findByNumProteinsGreaterThan(0, new PageRequest(0, 10));
        assertEquals(3, page.getTotalElements());

        // > 100 : none has that many proteins
        page = proteomesSearchService.findByNumProteinsGreaterThan(100, new PageRequest(0, 10));
        assertEquals(0, page.getTotalElements());

        // < 0 : nothing has less than 0
        page = proteomesSearchService.findByNumProteinsLessThan(0, new PageRequest(0, 10));
        assertEquals(0, page.getTotalElements());

        // < 1 : no proteins
        page = proteomesSearchService.findByNumProteinsLessThan(1, new PageRequest(0, 10));
        assertEquals(7, page.getTotalElements());

        // < 100 : all
        page = proteomesSearchService.findByNumProteinsLessThan(100, new PageRequest(0, 10));
        assertEquals(COUNT_TOTAL_DOCS, page.getTotalElements());

        // < 100 : all (second page has to be empty)
        page = proteomesSearchService.findByNumProteinsLessThan(100, new PageRequest(1, 10));
        assertEquals(0, page.getNumberOfElements());
    }

    @Test
    public void testFindByQueryAndFilterTaxId() {
        List<Integer> species = new ArrayList<Integer>();
        species.add(TAXID_HUMAN);
        Page<SolrPeptiform> page = proteomesSearchService.findByQueryAndFilterTaxid("human", species, new PageRequest(0, 10));
        assertEquals(7, page.getTotalElements());
        // we only expect Homo sapiens (9606) records
        for (SolrPeptiform solrPeptiform : page) {
            assertEquals(TAXID_HUMAN, solrPeptiform.getTaxid());
        }

        // nothing is annotated with multiple species
        species.add(TAXID_HBV);
        page = proteomesSearchService.findByQueryAndFilterTaxid("human", species, new PageRequest(0, 10));
        assertEquals(0, page.getTotalElements());

        species.clear();
        species.add(TAXID_HBV);
        page = proteomesSearchService.findByQueryAndFilterTaxid("human", species, new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());
        SolrPeptiform result = page.getContent().get(0);
        assertEquals(PEPTIDE_6_FORM_1_ID, result.getId());

        // without species filter we expect the same result as a normal search
        long countFilter = proteomesSearchService.findByQueryAndFilterTaxid("human", null, new PageRequest(0, 10)).getTotalElements();
        long count = proteomesSearchService.findByQuery("human", new PageRequest(0, 10)).getTotalElements();
        assertEquals(countFilter, count);
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, count);

        species.clear();

        page = proteomesSearchService.findByQueryAndFilterTaxid("P12347", species, new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());

        species.add(TAXID_HUMAN);
        page = proteomesSearchService.findByQueryAndFilterTaxid("*", species, new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByQueryAndFilterTaxid("", species, new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS, page.getTotalElements());

        page = proteomesSearchService.findByQueryAndFilterTaxid(null, species, new PageRequest(0, 10));
        assertEquals(HUMAN_RECORDS, page.getTotalElements());
    }


    @Test
    public void testCountAll() {
        long count = proteomesSearchService.countAll();
        assertEquals(COUNT_TOTAL_DOCS, count);
    }

    @Test
    public void testIfExists() {
        SolrPeptiform result = proteomesSearchService.findById(PEPTIDE_1_FORM_1_ID);
        assertNotNull(result);
    }

    @Test
    public void testCountBySequence() {
        long count = proteomesSearchService.countBySequence(PEPTIDE_1_SEQUENCE);
        assertEquals(2, count);
    }

    @Test
    public void testCountByTaxid() {
        long count = proteomesSearchService.countByTaxid(TAXID_HUMAN);
        assertEquals(HUMAN_RECORDS, count);
    }

    @Test
    public void testCountBySpecies() {
        long count = proteomesSearchService.countBySpecies(SPECIES_HUMAN);
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, count);

        count = proteomesSearchService.countBySpecies("human");
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, count);

        count = proteomesSearchService.countBySpecies("mouse");
        assertEquals(MOUSE_RECORDS, count);

        count = proteomesSearchService.countBySpecies("?ouse");
        assertEquals(MOUSE_RECORDS + HBV_RECORDS, count);
    }

    @Test
    public void testCountByProtein() {
        long count = proteomesSearchService.countByProtein("P12345");
        assertEquals(2, count);
        // wildcards are not supported
        count = proteomesSearchService.countByProtein("P????5");
        assertEquals(0, count);
    }

    @Test
    public void testCountByMod() {
        Page<SolrPeptiform> page = proteomesSearchService.findByMod("Oxidation", new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());

        page = proteomesSearchService.findByMod("Phosphorylation", new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());

        page = proteomesSearchService.findByMod("Deamidation", new PageRequest(0, 10));
        assertEquals(1, page.getTotalElements());

        // non-existing modification, no results!
        page = proteomesSearchService.findByMod("foo", new PageRequest(0, 10));
        assertEquals(0, page.getTotalElements());

        // now wildcards allowed
        page = proteomesSearchService.findByMod("*", new PageRequest(0, 10));
        assertEquals(0, page.getTotalElements());

        // if an empty mod term is given we throw an exception
        boolean failed = false;
        try {
            proteomesSearchService.findByMod("", new PageRequest(0, 10));
        } catch (Exception e) {
            failed = true;
        }
        assertTrue(failed);

        // if no mod term is given at all we throw an exception
        failed = false;
        try {
            proteomesSearchService.findByMod(null, new PageRequest(0, 10));
        } catch (Exception e) {
            failed = true;
        }
        assertTrue(failed);
    }

    @Test
    public void testCountByGroup() {
        long count = proteomesSearchService.countByUpGroup("P12345");
        assertEquals(2, count);

        count = proteomesSearchService.countByGeneGroup("GENE1");
        assertEquals(1, count);
    }

    @Test
    public void testCount() {
        // service term(s) contains 'human' which matches 8 records
        long count = proteomesSearchService.countByQuery(SPECIES_HUMAN);
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, count);

        count = proteomesSearchService.countByQuery("human");
        assertEquals(HUMAN_RECORDS + HBV_RECORDS, count);

        count = proteomesSearchService.countByQuery("homo");
        assertEquals(HUMAN_RECORDS, count);

        count = proteomesSearchService.countByQuery("9606");
        assertEquals(HUMAN_RECORDS, count);

        count = proteomesSearchService.countByQuery("Mouse");
        assertEquals(MOUSE_RECORDS, count);

        count = proteomesSearchService.countByQuery("?ouse");
        assertEquals(MOUSE_RECORDS + HBV_RECORDS, count);

        count = proteomesSearchService.countByQuery("10090");
        assertEquals(MOUSE_RECORDS, count);

        count = proteomesSearchService.countByQuery(PEPTIDE_6_SEQUENCE);
        assertEquals(1, count);

        count = proteomesSearchService.countByQuery("P12345");
        assertEquals(2, count);

        count = proteomesSearchService.countByQuery("P????5");
        assertEquals(3, count);

        count = proteomesSearchService.countByQuery("gene1");
        assertEquals(1, count);

        count = proteomesSearchService.countByQuery("GENE1");
        assertEquals(1, count);

        count = proteomesSearchService.countByQuery("kinase");
        assertEquals(1, count);
        count = proteomesSearchService.countByQuery("description");
        assertEquals(1, count);
    }

    @Test
    public void testCountNot() {
        long count = proteomesSearchService.countByQueryNot("Mouse");
        assertEquals(COUNT_TOTAL_DOCS - MOUSE_RECORDS, count);

        count = proteomesSearchService.countByQueryNot("9606");
        assertEquals(COUNT_TOTAL_DOCS - HUMAN_RECORDS, count);

        count = proteomesSearchService.countByQueryNot("somenonsense");
        assertEquals(COUNT_TOTAL_DOCS, count);
    }

    @Test
    public void testCountByNumProteins() {
        // <2 and >1 = all
        long less = proteomesSearchService.countByNumProteinsLessThan(2);
        long more = proteomesSearchService.countByNumProteinsGreaterThan(1);
        assertEquals(COUNT_TOTAL_DOCS, less + more);

        // we expect the same counts as for the corresponding find methods
        // > 0 : at least one protein
        long count = proteomesSearchService.countByNumProteinsGreaterThan(0);
        assertEquals(3, count);

        // > 100 : none has that many proteins
        count = proteomesSearchService.countByNumProteinsGreaterThan(100);
        assertEquals(0, count);

        // < 0 : nothing has less than 0
        count = proteomesSearchService.countByNumProteinsLessThan(0);
        assertEquals(0, count);

        // < 1 : no proteins
        count = proteomesSearchService.countByNumProteinsLessThan(1);
        assertEquals(7, count);

        // < 100 : all
        count = proteomesSearchService.countByNumProteinsLessThan(100);
        assertEquals(COUNT_TOTAL_DOCS, count);
    }

    @Test
    public void testCountByQueryAndFilterTaxId() {
        List<Integer> species = new ArrayList<Integer>();
        species.add(TAXID_HUMAN);
        long count = proteomesSearchService.countByQueryAndFilterTaxid("human", species);
        assertEquals(7, count);

        // nothing is annotated with multiple species
        species.add(TAXID_HBV);
        count = proteomesSearchService.countByQueryAndFilterTaxid("human", species);
        assertEquals(0, count);

        species.clear();
        species.add(TAXID_HBV);
        count = proteomesSearchService.countByQueryAndFilterTaxid("human", species);
        assertEquals(1, count);
    }


    @Test
    public void testFindByQueryFacetAndFilterTaxid() {
        Map<Integer, Long> facets = proteomesSearchService.getTaxidFacetsByQuery("human");
        assertEquals(2, facets.size());
        Long humanCount = facets.get(TAXID_HUMAN);
        assertTrue(humanCount != null);
        assertEquals(new Long(HUMAN_RECORDS), humanCount);
        Long hbvCount = facets.get(TAXID_HBV);
        assertTrue(hbvCount != null);
        assertEquals(new Long(HBV_RECORDS), hbvCount);


        facets = proteomesSearchService.getTaxidFacetsByQuery("");
        assertNotNull(facets);
        assertEquals(3, facets.size());
        humanCount = facets.get(TAXID_HUMAN);
        assertTrue(humanCount != null);
        assertEquals(new Long(HUMAN_RECORDS), humanCount);
        hbvCount = facets.get(TAXID_HBV);
        assertTrue(hbvCount != null);
        assertEquals(new Long(HBV_RECORDS), hbvCount);
        Long mouseCount = facets.get(TAXID_MOUSE);
        assertTrue(mouseCount != null);
        assertEquals(new Long(MOUSE_RECORDS), mouseCount);
    }


    @Test
    public void testGetProteinCounts() {
        Page<FacetFieldEntry> proteinCounts = proteomesSearchService.getProteinCounts(0, 10, false);
        // we have only 5 proteins, even if we have more PeptiForms
        assertEquals(5, proteinCounts.getContent().size());

        for (FacetFieldEntry entry : proteinCounts.getContent()) {
            if (entry.getValue().equalsIgnoreCase("P12345")) {
                // only that protein has two PeptiForms
                assertEquals(2, entry.getValueCount());
            } else {
                // all other proteis only have one PeptiForm
                assertEquals(1, entry.getValueCount());
            }
        }

        // we have only 5 different proteins, so the second page of size 5 has to be empty
        proteinCounts = proteomesSearchService.getProteinCounts(1, 5, false);
        assertEquals(0, proteinCounts.getContent().size());
    }

    @Test
    public void testGetProteinCountsBySpecies() {
        Collection<Integer> taxids = new ArrayList<Integer>();
        taxids.add(TAXID_HUMAN);
        Page<FacetFieldEntry> proteinCounts = proteomesSearchService.getProteinCountsBySpecies(taxids, 0, 10, false);
        assertEquals(3, proteinCounts.getContent().size());

        for (FacetFieldEntry entry : proteinCounts) {
            assertEquals(1, entry.getValueCount());
        }
        taxids.add(TAXID_MOUSE);
        proteinCounts = proteomesSearchService.getProteinCountsBySpecies(taxids, 0, 10, false);
        assertEquals(4, proteinCounts.getContent().size());

        taxids.add(TAXID_HBV);
        proteinCounts = proteomesSearchService.getProteinCountsBySpecies(taxids, 0, 10, false);
        assertEquals(5, proteinCounts.getContent().size());
    }

    @Test
    public void testGetUPGroupCounts() {
        Page<FacetFieldEntry> proteinCounts = proteomesSearchService.getUPGroupCounts(0, 10, false);
        // we have only 4 proteins, even if we have more PeptiForms
        assertEquals(4, proteinCounts.getContent().size());

        for (FacetFieldEntry entry : proteinCounts.getContent()) {
            if (entry.getValue().equalsIgnoreCase("P12345")) {
                // only that protein has two PeptiForms
                assertEquals(2, entry.getValueCount());
            } else {
                // all other proteis only have one PeptiForm
                assertEquals(1, entry.getValueCount());
            }
        }

        // we have only 4 different proteins, so the second page of size 4 has to be empty
        proteinCounts = proteomesSearchService.getUPGroupCounts(1, 4, false);
        assertEquals(0, proteinCounts.getContent().size());
    }

    @Test
    public void testGetUPGroupCountsBySpecies() {
        Collection<Integer> taxids = new ArrayList<Integer>();
        taxids.add(TAXID_HUMAN);
        Page<FacetFieldEntry> proteinCounts = proteomesSearchService.getUPGroupCountsBySpecies(taxids, 0, 10, false);
        assertEquals(3, proteinCounts.getContent().size());

        for (FacetFieldEntry entry : proteinCounts.getContent()) {
            assertEquals(1, entry.getValueCount());
        }

        taxids.clear();
        taxids.add(TAXID_MOUSE);
        proteinCounts = proteomesSearchService.getUPGroupCountsBySpecies(taxids, 0, 10, false);
        assertEquals(2, proteinCounts.getContent().size());
    }

    @Test
    public void testGetGeneGroupCounts() {
        Page<FacetFieldEntry> proteinCounts = proteomesSearchService.getGeneGroupCounts(0, 10, false);
        assertEquals(2, proteinCounts.getContent().size());

        for (FacetFieldEntry entry : proteinCounts.getContent()) {
            assertEquals(1, entry.getValueCount());
        }

        proteinCounts = proteomesSearchService.getGeneGroupCounts(1, 2, false);
        assertEquals(0, proteinCounts.getContent().size());
    }

    @Test
    public void testGetGeneGroupCountsBySpecies() {
        Collection<Integer> taxids = new ArrayList<Integer>();
        taxids.add(TAXID_HUMAN);
        Page<FacetFieldEntry> proteinCounts = proteomesSearchService.getGeneGroupCountsBySpecies(taxids, 0, 10, false);
        assertEquals(2, proteinCounts.getContent().size());

        for (FacetFieldEntry entry : proteinCounts.getContent()) {
            assertEquals(1, entry.getValueCount());
        }

        taxids.clear();
        taxids.add(TAXID_MOUSE);
        proteinCounts = proteomesSearchService.getGeneGroupCountsBySpecies(taxids, 0, 10, false);
        assertEquals(0, proteinCounts.getContent().size());
    }

}
