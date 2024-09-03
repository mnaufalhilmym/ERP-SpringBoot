package id.bengkelinovasi.erp.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WebResponse<T> {

    private WebPaginationResponse pagination;

    private T data;

}
