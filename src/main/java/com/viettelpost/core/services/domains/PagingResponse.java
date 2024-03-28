package com.viettelpost.core.services.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagingResponse {
    List<Object> mainData;
    Object sideData;
}
