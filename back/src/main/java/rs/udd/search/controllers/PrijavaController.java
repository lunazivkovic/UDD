package rs.udd.search.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.udd.search.dto.*;
import rs.udd.search.services.PrijavaService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( value = "/api/prijava" )
public class PrijavaController
{

    @Autowired
    PrijavaService service;

    @PostMapping( "/" )
    public  String create( PrijavaDTO prijavaDTO ) throws IOException
    {
        System.out.println(prijavaDTO);
        this.service.create( prijavaDTO );
        return "Ok";

    }

    @PostMapping( "/sear" )
    public  String sear(@RequestBody List<SearchFieldDTO> list)
    {

        System.out.println("gggg");
        return "Ok";

    }


    @PostMapping( "/check" )
    public ResponseEntity< ? > checkPlagiarism( BookDTO book ) throws IOException
    {
        return this.service.plagiarismSimplified( book );

    }


    @GetMapping( "/" )
    public ResponseEntity< ? > retrieve() throws IOException
    {
        return this.service.retrieveAll();

    }


    @PostMapping( "/search" )
    public ResponseEntity<?> search( List<SearchField> fields ) throws IOException
    {
        System.out.println("Usla sammmmm");
        return this.service.search( fields );

    }


    @GetMapping( "/{id}" )
    public ResponseEntity< ? > download( @PathVariable String id ) throws IOException
    {
        return this.service.download( id );

    }

}
