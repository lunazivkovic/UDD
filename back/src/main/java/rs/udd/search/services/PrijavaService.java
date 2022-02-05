package rs.udd.search.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import com.google.gson.Gson;


import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;


import rs.udd.search.dto.BookDTO;
import rs.udd.search.dto.LogicalOperation;
import rs.udd.search.dto.PrijavaDTO;
import rs.udd.search.dto.SearchField;
import rs.udd.search.handlers.DocumentHandler;
import rs.udd.search.handlers.PDFHandler;
import rs.udd.search.handlers.TextDocHandler;
import rs.udd.search.handlers.Word2007Handler;
import rs.udd.search.handlers.WordHandler;
import rs.udd.search.models.PrijavaZaPosao;

@Service
public class PrijavaService
{

    final private static String[] FETCH_FIELDS =
    { "id", "ime", "prezime", "email", "adresa", "stepenObrazovanja", "url" , "textContent"};

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * @param book
     * @return IndexResponse
     * @throws IOException
     */
    public ResponseEntity< ? > create( PrijavaDTO book ) throws IOException
    {
        PrijavaZaPosao newPrijava = _createNew( book );

        IndexRequest indexRequest = new IndexRequest( PrijavaZaPosao.INDEX );
        indexRequest.id( newPrijava.getId() );

        String json = new Gson().toJson( newPrijava );

        indexRequest.source( json, XContentType.JSON );

        IndexResponse index = restHighLevelClient.index( indexRequest, RequestOptions.DEFAULT );

        return new ResponseEntity< IndexResponse >( index, HttpStatus.CREATED );

    }


    private PrijavaZaPosao _createNew( PrijavaDTO book ) throws IOException
    {
        DocumentHandler handler = getHandler( book.getFile().getOriginalFilename() );

        PrijavaZaPosao newPrijava = new PrijavaZaPosao();

        newPrijava.setIme( book.getIme() );
        newPrijava.setPrezime( book.getPrezime() );
        newPrijava.setEmail( book.getEmail() );
        newPrijava.setId( book.getFile().getOriginalFilename() );
        newPrijava.setAdresa( book.getAdresa() );
        newPrijava.setStepenObrazovanja( book.getStepenObrazovanja() );
        newPrijava.setUrl( newPrijava.getUrl() + newPrijava.getId() );

        File convertedFile = convert( book.getFile() );
        newPrijava.setTextContent( handler.getText( convertedFile ) );

        return newPrijava;

    }


    private BoolQueryBuilder createBoolQueryBuilder( List< SearchField > searchFields )
    {

        if ( searchFields.isEmpty() )
        {
            return QueryBuilders.boolQuery().must( QueryBuilders.matchAllQuery() );
        }

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        searchFields.forEach( sf ->
        {
            String field = sf.getField();
            String value = sf.getValue();
            Boolean phrase = sf.getPhraseQuery();

            // switch ( sf.getLogic() )
            // {
            // case AND:
            // boolQuery.must( phrase ? QueryBuilders.matchPhraseQuery( field, value ) :
            // QueryBuilders.matchQuery( field, value ) );
            // break;
            // case OR:
            // boolQuery.should( phrase ? QueryBuilders.matchPhraseQuery( field, value ) :
            // QueryBuilders.matchQuery( field, value ) );
            // break;
            // default:
            // break;
            // }

            if ( sf.getLogic() == LogicalOperation.AND )
            {
                if ( phrase )
                {
                    boolQuery.must( QueryBuilders.matchPhrasePrefixQuery( field, value ) );
                    // boolQuery.must( QueryBuilders.matchPhraseQuery( field, value ) );

                }
                else
                {
                    boolQuery.must( QueryBuilders.matchQuery( field, value ) );
                }
            }
            else
            {
                if ( phrase )
                {
                    boolQuery.should( QueryBuilders.matchPhrasePrefixQuery( field, value ) );
                    // boolQuery.should( QueryBuilders.matchPhraseQuery( field, value ) );

                }
                else
                {
                    boolQuery.should( QueryBuilders.matchQuery( field, value ) );
                }
            }

        } );

        return boolQuery;

    }


    public ResponseEntity< ? > search( List< SearchField > fields ) throws IOException
    {

        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder createBoolQueryBuilder = createBoolQueryBuilder( fields );

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field( "textContent" );
        highlightContent.highlighterType( "unified" );

        highlightBuilder.field( highlightContent );

        searchSourceBuilder.query( createBoolQueryBuilder ).fetchSource( FETCH_FIELDS, null ).highlighter( highlightBuilder );

        searchRequest.indices( PrijavaZaPosao.INDEX );
        searchRequest.source( searchSourceBuilder );

        SearchResponse search = restHighLevelClient.search( searchRequest, RequestOptions.DEFAULT );

        ArrayList< PrijavaZaPosao > returnValues = new ArrayList<>();
        search.getHits().forEach( hit ->
        {
            Map< String, HighlightField > highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get( "textContent" );

            PrijavaZaPosao book = new Gson().fromJson( hit.getSourceAsString(), PrijavaZaPosao.class );

            if ( Optional.ofNullable( highlightField ).isPresent() )
            {
                Text[] fragments = highlightField.fragments();
                String contentHighlight = "";
                for ( Text text : fragments )
                {
                    contentHighlight = contentHighlight + text.toString() + "\n";
                }
                book.setTextContent( contentHighlight );
            }
            else
            {
                book.setTextContent( book.getTextContent().substring( 0, 200 ) + " ..." );
            }
            returnValues.add( book );

        } );

        return new ResponseEntity< List< PrijavaZaPosao > >( returnValues, HttpStatus.OK );

    }


    /**
     * 
     * @return all documents; only for testing
     * @throws IOException
     */
    public ResponseEntity< ? > retrieveAll() throws IOException
    {

        SearchRequest searchRequest = new SearchRequest();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must( QueryBuilders.matchAllQuery() );

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query( boolQueryBuilder ).fetchSource( FETCH_FIELDS, null );

        searchRequest.indices( PrijavaZaPosao.INDEX );
        searchRequest.source( searchSourceBuilder );

        SearchResponse searchResponse = restHighLevelClient.search( searchRequest, RequestOptions.DEFAULT );

        return new ResponseEntity<>( searchResponse.getHits(), HttpStatus.OK );

    }


    /**
     * 
     * @param fileName
     * @return Document handler based on file's extension
     */
    private DocumentHandler getHandler( String fileName )
    {
        if ( fileName.endsWith( ".txt" ) )
        {
            return new TextDocHandler();
        }
        if ( fileName.endsWith( ".pdf" ) )
        {
            return new PDFHandler();
        }
        if ( fileName.endsWith( ".doc" ) )
        {
            return new WordHandler();
        }
        if ( fileName.endsWith( ".docx" ) )
        {
            return new Word2007Handler();
        }
        return null;

    }


    /**
     * 
     * @param file
     * @return converted file
     * @throws IOException
     */
    private File convert( MultipartFile file ) throws IOException
    {

        File convertedFile = new File( "pdf" + File.separator + file.getOriginalFilename() );
        convertedFile.createNewFile();
        FileOutputStream fos = new FileOutputStream( convertedFile );
        fos.write( file.getBytes() );
        fos.close();
        return convertedFile;

    }


    public ResponseEntity< ? > download( String name ) throws IOException
    {

        Path file = Paths.get( "pdf" ).resolve( name );
        Resource resource = new UrlResource( file.toUri() );

        return ResponseEntity.ok().header( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"" ).body( resource );

    }


    /**
     * @deprecated
     * @param book
     * @return
     * @throws IOException
     */
    public ResponseEntity< ? > plagiarism( @RequestBody PrijavaDTO book ) throws IOException
    {

        PrijavaZaPosao newBook = _createNew( book );
        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery( "textContent", newBook.getTextContent() ).minimumShouldMatch( "50%" );

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field( "textContent" );
        highlightContent.highlighterType( "unified" );

        searchSourceBuilder.query( matchQuery ).fetchSource( FETCH_FIELDS, null ).highlighter( highlightBuilder );

        searchRequest.indices( PrijavaZaPosao.INDEX );
        searchRequest.source( searchSourceBuilder );

        SearchResponse search = restHighLevelClient.search( searchRequest, RequestOptions.DEFAULT );

        ArrayList< PrijavaZaPosao > returnValues = new ArrayList<>();

        search.getHits().forEach( hit ->
        {
            Map< String, HighlightField > highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get( "textContent" );

            PrijavaZaPosao currentBook = new Gson().fromJson( hit.getSourceAsString(), PrijavaZaPosao.class );

            if ( Optional.ofNullable( highlightField ).isPresent() )
            {
                Text[] fragments = highlightField.fragments();
                String contentHighlight = "";
                for ( Text text : fragments )
                {
                    contentHighlight = contentHighlight + text.toString() + "\n";
                }
                currentBook.setTextContent( contentHighlight );
            }
            else
            {
                currentBook.setTextContent( currentBook.getTextContent().substring( 0, 200 ) + " ..." );
            }
            returnValues.add( currentBook );

        } );

        return new ResponseEntity<>( returnValues, HttpStatus.OK );

        // return null;
    }


    public ResponseEntity< ? > plagiarismSimplified( @RequestBody BookDTO book ) throws IOException
    {

        DocumentHandler handler = getHandler( book.getFile().getOriginalFilename() );
        File convertedFile = convert( book.getFile() );
        String textForChecking = handler.getText( convertedFile );

        SearchRequest searchRequest = new SearchRequest();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MatchQueryBuilder matchQuery = QueryBuilders.matchQuery( "textContent", textForChecking ).minimumShouldMatch( "50%" );

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightContent = new HighlightBuilder.Field( "textContent" );
        highlightContent.highlighterType( "unified" );

        searchSourceBuilder.query( matchQuery ).fetchSource( FETCH_FIELDS, null ).highlighter( highlightBuilder );

        searchRequest.indices( PrijavaZaPosao.INDEX );
        searchRequest.source( searchSourceBuilder );

        SearchResponse search = restHighLevelClient.search( searchRequest, RequestOptions.DEFAULT );

        ArrayList< PrijavaZaPosao > returnValues = new ArrayList<>();

        search.getHits().forEach( hit ->
        {
            Map< String, HighlightField > highlightFields = hit.getHighlightFields();
            HighlightField highlightField = highlightFields.get( "textContent" );

            PrijavaZaPosao currentBook = new Gson().fromJson( hit.getSourceAsString(), PrijavaZaPosao.class );

            if ( Optional.ofNullable( highlightField ).isPresent() )
            {
                Text[] fragments = highlightField.fragments();
                String contentHighlight = "";
                for ( Text text : fragments )
                {
                    contentHighlight = contentHighlight + text.toString() + "\n";
                }
                currentBook.setTextContent( contentHighlight );
            }
            else
            {
                currentBook.setTextContent( currentBook.getTextContent().substring( 0, 200 ) + " ..." );
            }
            returnValues.add( currentBook );

        } );

        return new ResponseEntity<>( returnValues, HttpStatus.OK );

        // return null;
    }

}
