package com.kharybin.prhclient.lib;

import com.kharybin.prhclient.lib.model.CompanyData;

public interface PrhClient {
    CompanyData getCompanyDataById(String businessId);
}
