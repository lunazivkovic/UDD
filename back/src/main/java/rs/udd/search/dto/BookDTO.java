package rs.udd.search.dto;

import org.springframework.web.multipart.MultipartFile;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookDTO
{

    private MultipartFile file;

    private String ime;

    private String prezime;

    private String email;

    private String adresa;

    private Float stepenObrazovanja;

}
