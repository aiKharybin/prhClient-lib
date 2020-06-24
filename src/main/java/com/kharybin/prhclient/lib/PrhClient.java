package com.kharybin.prhclient.lib;

import com.kharybin.prhclient.lib.model.CompanyData;



public interface PrhClient {
    /**
     * Returns an {@link com.kharybin.prhclient.lib.model.CompanyData}
     * takes String in ddddddd-d format as input
     */
    CompanyData getCompanyDataById(String businessId);
}
