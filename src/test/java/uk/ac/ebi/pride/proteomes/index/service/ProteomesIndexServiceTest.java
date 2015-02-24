package uk.ac.ebi.pride.proteomes.index.service;

import org.apache.solr.SolrTestCaseJ4;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.solr.core.SolrTemplate;
import uk.ac.ebi.pride.proteomes.index.model.PeptiForm;
import uk.ac.ebi.pride.proteomes.index.model.SolrPeptiForm;
import uk.ac.ebi.pride.proteomes.index.repository.RepositoryFactory;

import static uk.ac.ebi.pride.proteomes.index.service.TestData.*;

/**
 * @author florian@ebi.ac.uk
 */
public class ProteomesIndexServiceTest extends SolrTestCaseJ4 {

    private SolrServer server;
    private ProteomesIndexService proteomesIndexService;


    @BeforeClass
    public static void initialise() throws Exception {
        initCore("src/main/resources/solr/conf/solrconfig.xml",
                "src/main/resources/solr/conf/schema.xml",
                "src/main/resources/solr",
                "");
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        server = new EmbeddedSolrServer(h.getCoreContainer(), h.getCore().getName());
        RepositoryFactory repositoryFactory = new RepositoryFactory(new SolrTemplate(server));
        proteomesIndexService = new ProteomesIndexService(repositoryFactory.create());

        // make sure we don't have any old data kicking around
        server.deleteByQuery("*:*");

        // force the commit for testing purposes (avoids soft commit delay)
        server.commit();
    }

    @Test
    public void testSaveAndDelete() throws SolrServerException {
        // first insert the test data
        proteomesIndexService.save(createTestPeptiForms());
        // and check if all records are there
        QueryResponse response = server.query(new SolrQuery("*"));
        assertEquals(COUNT_TOTAL_DOCS, response.getResults().getNumFound());

        // do some spot checks
        // search for a specific record by ID
        response = server.query(new SolrQuery(SolrPeptiForm.ID+":"+ ClientUtils.escapeQueryChars(PEPTIDE_1_FORM_1_ID)));
        assertEquals(1, response.getResults().getNumFound());
        assertEquals(PEPTIDE_1_FORM_1_ID, response.getBeans(PeptiForm.class).get(0).getId());
        // delete a specific record by ID
        proteomesIndexService.delete(PEPTIDE_1_FORM_1_ID);
        response = server.query(new SolrQuery("*"));
        assertEquals(COUNT_TOTAL_DOCS-1, response.getResults().getNumFound());
        // search by general keyword
        response = server.query(new SolrQuery(SolrPeptiForm.TEXT+":human"));
        assertEquals(HUMAN_RECORDS-1+HBV_RECORDS, response.getResults().getNumFound());

        // finally remove all records
        proteomesIndexService.deleteAll();
        // and check that the index is empty
        response = server.query(new SolrQuery("*"));
        assertEquals(0, response.getResults().getNumFound());

    }


}
