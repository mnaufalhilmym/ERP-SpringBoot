package id.bengkelinovasi.erp.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WebPaginationResponse {

    private int size;

    private long total;

    private int page;

    private int totalPages;

}
