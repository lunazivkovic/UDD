package rs.udd.search.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchFieldDTO
{

    private String field;

    private String value;

    private Boolean phraseQuery;

    private Boolean logic;

}
