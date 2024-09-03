package id.bengkelinovasi.erp.model.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetUserByIdRequest {

    private UUID id;

}
