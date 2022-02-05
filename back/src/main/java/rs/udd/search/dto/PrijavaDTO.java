package rs.udd.search.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class PrijavaDTO
{

    private MultipartFile file;

    private String ime;

    private String prezime;

    private String email;

    private String adresa;

    private String stepenObrazovanja;

    @Override
    public String toString() {
        return "PrijavaDTO{" +
                "file=" + file +
                ", ime='" + ime + '\'' +
                ", prezime='" + prezime + '\'' +
                ", email='" + email + '\'' +
                ", adresa='" + adresa + '\'' +
                ", stepenObrazovanja='" + stepenObrazovanja + '\'' +
                '}';
    }
}
