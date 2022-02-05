package rs.udd.search.handlers;


import java.io.File;
import java.io.FileInputStream;


import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import rs.udd.search.models.PrijavaZaPosao;


public class Word2007Handler extends DocumentHandler {
    @Override
    public PrijavaZaPosao getIndexUnit(File file) {
        return null;
    }

    @Override
    public String getText(File file) {
        String text = null;
        try {
            XWPFDocument wordDoc = new XWPFDocument(new FileInputStream(file));
            XWPFWordExtractor we = new XWPFWordExtractor(wordDoc);
            text = we.getText();
            we.close();
        }catch (Exception e) {
            System.out.println("Problem pri parsiranju docx fajla");
        }
        return text;
    }
}
