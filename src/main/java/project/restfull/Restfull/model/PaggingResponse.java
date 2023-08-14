package project.restfull.Restfull.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaggingResponse {

    private Integer currentPage;

    private Integer totalPage;

    private Integer size;
}
