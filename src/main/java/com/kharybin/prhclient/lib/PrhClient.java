package com.kharybin.prhclient.lib;

import com.kharybin.prhclient.lib.model.CompanyData;

import java.util.Optional;

public interface PrhClient {
    Optional<CompanyData> getCompanyDataById(String businessId);
}
