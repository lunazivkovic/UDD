package rs.udd.search.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SearchDTO {

    private List<SearchFieldDTO> data;
}
