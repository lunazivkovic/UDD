package rs.udd.search.handlers;

import java.io.File;
import java.io.IOException;


import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.text.PDFTextStripper;


import rs.udd.search.models.PrijavaZaPosao;

public class PDFHandler extends DocumentHandler
{

    @Override
    public PrijavaZaPosao getIndexUnit(File file )
    {
        return null;

    }


    @Override
    public String getText( File file )
    {
        try
        {
            PDFParser parser = new PDFParser( new RandomAccessFile( file, "r" ) );

            parser.parse();
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText( parser.getPDDocument() );
            return text;
        }
        catch ( IOException e )
        {
            System.out.println( "Error while converting document to PDF" );
        }
        return null;

    }


    public String getText( PDFParser parser )
    {
        try
        {
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText( parser.getPDDocument() );
            return text;
        }
        catch ( IOException e )
        {
            System.out.println( "Error while converting document to PDF" );
        }
        return null;

    }

}
