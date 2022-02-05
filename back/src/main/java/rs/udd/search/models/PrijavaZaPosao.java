package rs.udd.search.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Data
@NoArgsConstructor
public class PrijavaZaPosao
{

    public static final String INDEX = "prijava";

    private String id;

    private String ime;

    private String prezime;

    private String adresa;

    private String email;

    private String stepenObrazovanja;

    private String textContent;

    private String url = "localhost:8080/api/prijava/";

}
