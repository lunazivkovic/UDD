package rs.udd.search.handlers;

import rs.udd.search.models.PrijavaZaPosao;

import java.io.File;



public abstract class DocumentHandler
{

    public abstract PrijavaZaPosao getIndexUnit(File file );

    public abstract String getText( File file );

}
